package com.bilgidoku.rom.pg.veritabani;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.postgresql.PGConnection;
import org.postgresql.util.PGobject;

import com.bilgidoku.rom.base.java.min.gorevli.GorevliDir;
import com.bilgidoku.rom.base.java.min.json.JSONObject;
import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.min.gorevli.EnvItem;
import com.bilgidoku.rom.base.min.gorevli.Gorevli;
import com.bilgidoku.rom.base.min.gorevli.Ortam;

public class VeritabaniGorevlisi extends GorevliDir {
	public static final int NO = 4;

	private EnvItem host;
	private EnvItem port;
	private EnvItem db;
	private EnvItem user;
	private EnvItem pwd;

	@Override
	protected EnvItem[] prepEnvItems() {
		host = EnvItem.create().setEnvName("db__server").setDefaultValue("localhost").setNullable(false)
				.setDescription("Postgresql server hostname or ip to connect");
		port = EnvItem.create().setEnvName("db__port").setDefaultValue("5432").setNullable(false)
				.setDescription("Postgresql server port to connect");
		db = EnvItem.create().setEnvName("db__name").setNullable(false).setRequired(true)
				.setDescription("Postgresql db to connect");
		user = EnvItem.create().setEnvName("db__user").setNullable(false).setRequired(true)
				.setDescription("Postgresql user to connect");
		pwd = EnvItem.create().setEnvName("db__user_password").setNullable(false).setRequired(true)
				.setDescription("Postgresql user password to connect");
		return new EnvItem[] { host, port, db, user, pwd };
	}

	private static VeritabaniGorevlisi tek;

	public static VeritabaniGorevlisi bind(Gorevli binder) {
		if (tek == null) {
			synchronized (VeritabaniGorevlisi.class) {
				if (tek == null) {
					tek = new VeritabaniGorevlisi();
				}
			}
		}
		tek.binder(binder);
		return tek;
	}

	private VeritabaniGorevlisi() {
		super("Veritabani", NO);
		this.minIdle = 1;
		this.maxActive = 5;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Postgresql driver not found", e);
		}
	}

	@Override
	protected void kur() throws KnownError {
		// password = "jvl5SSFq8fsemod";
		this.testDb = db.getValueStr() + "test";
		this.url = genUrl(false);

		if (Ortam.tek().isTest()) {
			recreateTestDb();
		}
	}

	private int minIdle;
	private int maxActive;
	private String url;
	private String testDb;

	private void recreateTestDb() throws KnownError {
		Connection c = getConnection(true);
		Statement s;
		try {
			s = c.createStatement();
			s.execute("drop database if exists " + testDb + ";");
			s.execute("create database " + testDb);
		} catch (SQLException e) {
			throw new KnownError("Could not recreate testdb:" + testDb, e);
		}
		Connection cc = getConnection(false);
		Statement ss;
		try {
			ss = cc.createStatement();
			ss.execute("create extension if not exists pgcrypto;");
			ss.execute("create extension if not exists ltree;");
		} catch (SQLException e) {
			throw new KnownError("Could not recreate testdb:" + testDb, e);
		}
	}
	
	public DbThree dbThree(String sql) throws KnownError {
		return new DbThree(this, sql);
	}

	@Override
	protected void bitir(boolean dostca) {
		// TODO Auto-generated method stub
		super.bitir(dostca);
	}

	public JSONObject konus(JSONObject in) throws KnownError {

		try (Connection c = getConnection();) {

			PreparedStatement p = c.prepareStatement("select ?::text");
			PGobject jsonObject = new PGobject();
			jsonObject.setType("_LIBRARY_PATH\"));\n" + "//		 ProcessBuilder pb = new ProcessBuilder();\n"
					+ "//	     Map<String, String> env = pb.environment(json");
			jsonObject.setValue(in.toString());
			p.setObject(1, jsonObject);
			ResultSet rs = p.executeQuery();
			if (!rs.next()) {
				return null;
			}
			String ret = rs.getString(1);
			return new JSONObject(ret);
		} catch (SQLException | KnownError e) {
			throw new KnownError(e);
		}

	}

	private String genUrl(boolean initial) {
		if (Ortam.tek().isTest()) {
			if (initial) {
				return "jdbc:postgresql://" + host.getValueStr() + ":" +port.getValue() + "/postgres";

			}
			return "jdbc:postgresql://" + host.getValueStr() + ":" +port.getValue() +"/" + testDb+"?ApplicationName=rom-test";
		}
		return "jdbc:postgresql://" + host.getValueStr() + ":" +port.getValue() + "/" + db.getValueStr();
	}

//	public PGConnection getReplicationConnection() throws KnownError {
//		try {
//			Properties props = new Properties();
////			PGProperty.USER.set(props, user);
////			PGProperty.PASSWORD.set(props, password);
////			PGProperty.REPLICATION.set(props, "database");
////			PGProperty.PREFER_QUERY_MODE.set(props, "simple");
//
//			Connection con = DriverManager.getConnection(url, props);
//			con.setAutoCommit(false);
//			PGConnection replConnection = con.unwrap(PGConnection.class);
//			replConnection.getReplicationAPI().createReplicationSlot().logical().withTemporaryOption()
//					.withSlotName("demo_logical_slot").withOutputPlugin("test_decoding").make();
//
//			return replConnection;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new KnownError(e);
//		}
//	}

//	public static void main(String[] args) throws KnownError {
////		System.out.println(Shell.exec("echo", "$LD_LIBRARY_PATH"));
////		 ProcessBuilder pb = new ProcessBuilder();
////	     Map<String, String> env = pb.environment();
//		VeritabaniGorevlisi.tek().getReplicationConnection();
//	}

	public Connection getConnection() throws KnownError {
		return this.getConnection(false);
	}

	private Connection getConnection(boolean superAccess) throws KnownError {
		destur();
		String uri;
		if(superAccess) {
			uri=genUrl(true);
		}else {
			if(url==null) {
				url=genUrl(false);
			}
			uri=url;
		}

		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++=");
		System.out.println(uri);
		
		Connection connection;
		try {
			connection = DriverManager.getConnection(uri, user.getValueStr(), pwd.getValueStr());
			return connection;
		} catch (SQLException e) {
			throw cantGetConnection(e);
		}
	}

	public void free1(Connection con, Statement ps) {
		if (ps != null)
			try {
				ps.close();
			} catch (SQLException e) {
			}
		if (con != null)
			try {
				con.close();
			} catch (SQLException e) {
			}
	}

	public void free2(Statement ps) {
		if (ps != null)
			try {
				ps.close();
			} catch (SQLException e) {
			}
	}

	public void free3(Connection con) {
		if (con != null)
			try {
				con.close();
			} catch (SQLException e) {
			}
	}

	// @Override
	// public DbThree prepareStatement(String query) throws SQLException {
	// destur();
	//
	// DbThree dbThree = new DbThree(this,query);
	//
	// return dbThree;
	//
	// }

	private KnownError cantGetConnection(SQLException e) {
		return new KnownError("SqlError/cant get connection", e).internalError();
	}

	public KnownError errorOccured(Connection connection, String query) {
		return new KnownError("SqlError:" + query);
	}

	public KnownError errExecute(Connection connection2, String query, SQLException e) {
		String errorStr="";
		String msg = e.getMessage();
		if (msg != null) {
			int pos = msg.indexOf("Position: ");
			String s = msg.substring(pos + 10);
			try {
				int qp = Integer.parseInt(s);
				int start = qp - 30;
				int end = qp + 30;
				if (start < 0) {
					start = 0;
				}
				if (end >= query.length() - 1) {
					end = query.length() - 1;
				}
				errorStr=query.substring(start, end);
			} catch (Exception ee) {
			}
//			
//			if (pos > 0) {
//				int codepos = msg.indexOf("Code:", pos);
//				if (codepos > 0) {
//				}
//			}
		}
		return new KnownError("SqlError/execute:errorStr:>>>"+errorStr+"<<<\n   query:"  + query, e);
	}

	public KnownError errorSet(Connection connection2, String query, SQLException e) {
		return new KnownError("SqlError/set: " + query, e);
	}

	public KnownError errorGet(Connection connection2, String query, SQLException e) {
		return new KnownError("SqlError/get:" + query, e);
	}

	public KnownError errGeneric(Connection connection2, String query, Exception e) {
		return new KnownError("SqlError/generic:" + query, e);
	}

//
	public void executeCommands(List<String> commands) throws KnownError {
		execCommandImpl(new DbThree(this, commands.get(0)), commands);
	}

	protected void execCommandImpl(DbThree db3, List<String> commands) throws KnownError {
		if (commands.size() == 0)
			return;
		db3.setTransactional();
		db3.execute();
		for (int i = 1; i < commands.size(); i++) {
			db3.replaceQuery(commands.get(i));
			db3.execute();
		}
		db3.commit();
	}

//
//	public void starting(boolean upgrade, Version fromVersion, Version toVersion) throws KnownError {
//		runsu();
//	}
//
//	void runsu() throws KnownError {
//		sum = new SqlUnitManager(false, this, safeCheapDir().getPath(), justScipt);
//		try {
//			sum.load("tepeweb.templates.ready");
//			if (!Mode.integrationTest)
//				sum.runOnDb("default");
//		} catch (Exception e) {
//			throw new KnownError("Failed createtlosdb", e).fatal();
//		}
//
//		if (!justScipt)
//			sum = null;
//
//	}
//
//	public void scenario(String named) throws KnownError {
//		if (named == null) {
//			named = "defaultscene.sql";
//		}
//		try {
//
//			String str = FromResource.loadString(named);
//			String[] cmds = str.split("--##");
//			ArrayList<String> cl = new ArrayList<String>();
//			for (String string : cmds) {
//				cl.add(string);
//			}
//			executeCommands(cl);
//		} catch (IOException e) {
//			throw new KnownError(e);
//		}
//	}
//
//	public void genScript() throws KnownError {
//		sum.genScript("default");
//	}

}
