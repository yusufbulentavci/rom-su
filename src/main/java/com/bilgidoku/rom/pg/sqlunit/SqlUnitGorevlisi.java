package com.bilgidoku.rom.pg.sqlunit;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.bilgidoku.rom.base.java.min.gorevli.GorevliDir;
import com.bilgidoku.rom.base.min.Sistem;
import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.min.gorevli.EnvItem;
import com.bilgidoku.rom.base.min.gorevli.Gorevli;
import com.bilgidoku.rom.base.min.gorevli.Ortam;
import com.bilgidoku.rom.js.JsGorevlisi;
import com.bilgidoku.rom.pg.sqlunit.model.Command;
import com.bilgidoku.rom.pg.sqlunit.rom.RomComp;
import com.bilgidoku.rom.pg.sqlunit.rom.RomCompDao;
import com.bilgidoku.rom.pg.veritabani.VeritabaniGorevlisi;

public class SqlUnitGorevlisi extends GorevliDir {
	public static final int NO = 0;

	private static SqlUnitGorevlisi tek;

	private EnvItem su;
	private EnvItem dryRun;
	
	private VeritabaniGorevlisi vg;
	private JsGorevlisi jg;

	public static SqlUnitGorevlisi bind(Gorevli binder) {
		if (tek == null) {
			synchronized (SqlUnitGorevlisi.class) {
				if (tek == null) {
					tek = new SqlUnitGorevlisi();
				}
			}
		}
		tek.binder(binder);
		return tek;
	}

	@Override
	protected EnvItem[] prepEnvItems() {
		su = EnvItem.create().setEnvName("su__file").setNullable(false)
				.setDescription("Sql Unit file");
		dryRun = EnvItem.create().setEnvName("su__dry_run").setNullable(true).setDefaultValue("false")
				.setDescription("Generate sql scripts. Do not execute them.");
		
		return new EnvItem[] {su, dryRun};
	}

	@Override
	protected void bindDependency() {
		vg = VeritabaniGorevlisi.bind(this);
		jg = JsGorevlisi.bind(this);
	}

	private SqlUnitGorevlisi() {
		super("SqlUnit", NO);
		this.cheapDir = cheapDir();
		this.unitTest = Ortam.tek().isTest();
		executionSus = new LinkedList<String>();
		execSuObj = new LinkedList<SqlUnit>();
		sqlUnits = new HashMap<String, SqlUnit>();
		comps = new HashMap<String, RomComp>();
		newComps = new HashMap<String, RomComp>();

	}

	@Override
	protected void kur() throws KnownError {
		parser = new SqlUnitParser();
		load(su.getValueStr());
		runOnDb();
	}

	class SqlUnitAddr {
		public SqlUnitAddr(String unitName, String path) {
			this.sqlUnitName = unitName;
			this.addr = path;
		}

		String sqlUnitName;
		String addr;
	}

	private final static String SQLUNIT_SUFFIX = ".su";

	private SqlUnitParser parser;
	private final List<String> executionSus;
	private final List<SqlUnit> execSuObj;
	private final Map<String, SqlUnit> sqlUnits;
	private boolean unitTest;
	private Map<String, RomComp> comps;
	private Map<String, RomComp> newComps;

	private String cheapDir;
//	private boolean genScript = false;
	private boolean loaded = false;

	private String sqlUnitName;

	public void load(String suStr) throws KnownError {
		this.sqlUnitName = suStr;
		try {
			this.loadUnit(sqlUnitName, unitTest);
		} catch (IOException | SuException e) {
			throw new KnownError("Loading su:" + sqlUnitName, e);
		}
		this.parser.getRomDb().init();
		this.loaded = true;
	}

	public void runOnDb() throws KnownError {
		if (!loaded) {
			throw new KnownError("sqlUnit load edilmemis");
		}

//		String romDbTest = System.getProperty("ROM.DB.TEST");

		if (Ortam.tek().isProd()) {
			runCommit();
		} else if (!Ortam.tek().integrationTest) {
			runTest();
		}

//		AkilGorevlisi.tek().reset();
//		
//		AynilamaGorevlisi.tek().aynila(parser.getRomDb().getClipsTables());
//		
//		AkilGorevlisi.tek().run();
	}

	private void byPassDb() {
		parser.setByPassDb(true);
	}

	private String generateScript() {
		List<Command> cmds = genCmds();
		StringBuilder sb = new StringBuilder();
		for (Command command : cmds) {
			sb.append(command.toScript());
			sb.append("\n");
		}
		return sb.toString();
	}

	private void runCommit() throws KnownError {
		RomCompDao rcd = new RomCompDao(vg);

		setComps(rcd.getComps());
//		AkilGorevlisi.tek().add(parser.getRomDb().getClipsTemplates());
		List<Command> cmds = genCmds();
		for (Command command : cmds) {
			// if(command.getCommand().length()>=30)
			// syso(command.getCommand().substring(0,29));
			try {
				if (command.isSql()) {
					if (this.dryRun.getValueBoolean()) {
						System.out.println("DRY-RUN:"+command.getCommand());
					}
					rcd.executeCommand(command.getCommand());
					if (command.getCommand().startsWith("create table dict.envo(")) {
						rcd.executeCommand("insert into dict.envo(testing)" + " values(" + Ortam.tek().isTest() + ")");
					}
				}
//				if (command.isClp()) {
//					AkilGorevlisi.tek().add(command.getCommand());
//				}
				if (command.isJs()) {
					jg.eval(command.getCommand());
				}
			} catch (Exception e) {
				throw new KnownError("Error while runCommit; Command:" + command.getCommand(), e);
			}
		}
		if (this.dryRun.getValueBoolean()) {
			System.out.println("DRY-RUN:ROLLBACK");
			rcd.rollback();
			return;
		}
		rcd.setComps(newComps);
		rcd.commit();

	}

	private void genScript() throws KnownError {
		RomCompDao rcd = new RomCompDao(vg);

		setComps(rcd.getComps());
		List<Command> cmds = genCmds();
		for (Command command : cmds) {
			// if(command.getCommand().length()>=30)
			// syso(command.getCommand().substring(0,29));
			rcd.executeCommand(command.getCommand());
			if (command.getCommand().startsWith("create table dict.envo(")) {
				rcd.executeCommand("insert into dict.envo(testing)" + " values(" + Ortam.tek().isTest() + ")");
			}
		}
		System.out.println("========= Db Script ==============");
		for (Command command : cmds) {
			System.out.println(command.toScript());
		}
		System.out.println("========= End Db Script ==============");
		rcd.rollback();
	}

	private void runTest() throws KnownError {
		RomCompDao rcd = new RomCompDao(vg);
		File us = new File(cheapDir + "/upgrade.sql");
		if (us.exists())
			us.delete();
		try (PrintWriter pw = new PrintWriter(us)) {
			setComps(rcd.getTestComps(pw));
			List<Command> cmds = genCmds();
			for (Command command : cmds) {
				String sql = command.getCommand();
				pw.println(sql);
				if (command.isSql()) {
					System.out.println("Sql:");
					System.out.println(sql);
					System.out.println("Sql:");
					
					rcd.executeCommand(sql);
					if (sql.startsWith("create table dict.envo(")) {
						sql = "insert into dict.envo(testing)" + " values(" + Ortam.tek().isTest() + ")";
						pw.println(sql);

						rcd.executeCommand(sql);
					}
				}
//				if (command.isClp()) {
//					AkilGorevlisi.tek().add(command.getCommand());
//				}
				if (command.isJs()) {
					jg.eval(command.getCommand());
				}
			}

			rcd.reportRomComp(comps, '\n');
			rcd.setComps(comps);

			rcd.rollback();
		} catch (FileNotFoundException e) {
			throw new KnownError("db run test", e);
		}
	}

	AtomicInteger savePointId = new AtomicInteger();

	private List<Command> genCmds() {
		List<Command> allCmds = new ArrayList<Command>();
		for (SqlUnit su : this.execSuObj) {
			try {
				// boolean runUnitTest = unitTest &&
				// su.named.equals(sqlUnitName);
				for (SuComp comp : su.getComps()) {

					if (comp.justRun()) {
						if (comp.isUnitTest()) {
							if (Ortam.tek().isTest() && !Ortam.tek().integrationTest) {
								String savePointLabel = "rsp_" + savePointId.getAndIncrement();
								allCmds.add(new Command("savepoint " + savePointLabel + ";", 0));
								allCmds.addAll(comp.getCommands());
								allCmds.add(new Command("rollback to savepoint " + savePointLabel + ";", 0));
							}
						} else {
							allCmds.addAll(comp.getCommands());
						}
						continue;
					}

					RomComp currentComp = comp.getComp();
					String id = currentComp == null ? null : currentComp.getId();

					RomComp oldy = comps.get(id);
					if (oldy == null) {
						List<Command> cmds = comp.beforeRun();
						if (cmds != null && cmds.size() > 0)
							allCmds.addAll(cmds);

						if (comp.isAbstractFunction()) {
							cmds = comp.genAbstract();
						} else {
							cmds = comp.run();
						}

						if (cmds != null && cmds.size() > 0)
							allCmds.addAll(cmds);

						cmds = comp.afterRun();
						if (cmds != null && cmds.size() > 0)
							allCmds.addAll(cmds);

//						if (!comp.isNoRes() && !comp.isNoRom() && currentComp.compType.equals("table")) {
//							allCmds.add(createGetResourceScript(currentComp.schemaName, currentComp.named));
//						}
					} else if (oldy.ver != comp.getVersion()) {
						List<Command> cmds = comp.upgrade(oldy.ver);
						if (cmds != null && cmds.size() > 0)
							allCmds.addAll(cmds);
					}
					if (id != null)
						newComps.put(id, currentComp);
				}
			} catch (Exception e) {
				Sistem.errln("Error in su:" + su.getNamed());
				throw e;
			}
		}
		for (String compId : comps.keySet()) {
			if (newComps.containsKey(compId)) {
				continue;
			}
			RomComp comp = comps.get(compId);
			if (comp.compType.equals("table")) {
				allCmds.add(dropGetResourceScript(comp.schemaName, comp.named));
			}
			List<Command> cmds = comp.drop();
			if (cmds != null && cmds.size() > 0)
				allCmds.addAll(cmds);
		}
		return allCmds;
	}

	private Command createGetResourceScript(String schemaName, String named) {
		String c = "create function " + schemaName + ".g_" + named + "(integer, text) returns rom.resources as $$ "
				+ "select ri,host_id,uri,container,html_file,modified_date,creation_date,delegated,ownercid,gid,relatedcids,mask,nesting,dbfs,weight,null::tsvector,rtags,aa "
				+ "from " + schemaName + "." + named + " where host_id=$1 and uri=$2; " + "$$ language sql;";

		return new Command(c, 0);
	}

	private Command dropGetResourceScript(String schemaName, String named) {
		String c = "drop function  if exists " + schemaName + ".g_" + named + "(integer,text);";

		// String c = "select format('drop function
		// %s.%s(%s);',n.nspname,proname,pg_get_function_identity_arguments(p.oid))"
		// + "from pg_proc p,pg_namespace n where n.nspname="
		// + "'"+schemaName+"'"
		// + "and proname="
		// + "'"+named+"'" + " and pronamespace=n.oid";

		return new Command(c, 0);
	}

	private void loadUnit(String sqlUnitName, boolean unitTest) throws IOException, SuException {
		buildExecutionList(sqlUnitName, 0);
	}

	private void runJavaCommand(Connection connection, Command javaClassName)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// log.debug("Running java command:" + javaClassName.getCommand());
		Class<JavaCommand> cls = (Class<JavaCommand>) classLoader.loadClass(javaClassName.getCommand());
		JavaCommand cmd = cls.newInstance();
		cmd.run(connection, null);
	}

	// public void runSqlUnitTests() throws IOException,
	// SQLException, ClassNotFoundException, InstantiationException,
	// IllegalAccessException {
	// // //log.info("Starting sql unit tests..");
	// List<SqlUnitAddr> sqlUnits = getSqlUnits();
	// SqlUnitParser parser = new SqlUnitParser();
	// for (SqlUnitAddr sqlUnitName : sqlUnits) {
	// // if (startWith != null && !sqlUnitName.startsWith(startWith)) {
	// // continue;
	// // }
	// this.sqlUnits.clear();
	// Connection conn = dbService.getConnection(this.dataSourceName);
	// conn.setAutoCommit(false);
	// try {
	//
	// log.debug("======>" + sqlUnitName.sqlUnitName + "<======");
	// SqlUnit su = parser.go(toPath(sqlUnitName.sqlUnitName),
	// this.getPackageOfSqlUnit(sqlUnitName.sqlUnitName));
	// if (su.hasUnitTest()) {
	// log.debug(" FOUND. Testing...");
	// runSqlUnit(conn, sqlUnitName.sqlUnitName, true);
	// log.debug(" Success");
	// } else {
	// log.debug(" No unit tests, passing...");
	// }
	// } finally {
	// conn.rollback();
	// dbService.free(conn);
	// }
	// }
	// }

	private void buildExecutionList(String sqlUnitName, int deep) throws IOException, SuException {
		System.out.println(">>" + sqlUnitName);
		List<String> dpndSu = parser.parseDependencies(toPath(sqlUnitName), this.getPackageOfSqlUnit(sqlUnitName));
		if (deep > 15) {
			throw new RuntimeException("Too deep:" + deep);
		}

		for (String suname : dpndSu) {
			if (!this.sqlUnits.containsKey(suname)) {
				buildExecutionList(suname, deep + 1);
			}
		}

		// syso("parsing:"+sqlUnitName);
		SqlUnit su = parser.go(toPath(sqlUnitName), this.getPackageOfSqlUnit(sqlUnitName));

		if (deep == 0) {
			// log.debug(su.toString());
		}

		this.sqlUnits.put(sqlUnitName, su);

		if (!this.executionSus.contains(sqlUnitName)) {
			this.executionSus.add(sqlUnitName);
			this.execSuObj.add(su);
		}
		System.out.println("<<" + sqlUnitName);
	}

	private String toPath(String sqlUnitName) {
		return "su/" + sqlUnitName.replace('.', '/') + SQLUNIT_SUFFIX;
	}

	private final FilenameFilter suFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".su");
		}
	};

	private final FileFilter dirFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory();
		}
	};

	private void scanFiles(String packName, File dir, List<SqlUnitAddr> found) {
		String[] sus = dir.list(suFilter);
		for (String string : sus) {
			String withoutExtension = string.substring(0, string.length() - 3);
			SqlUnitAddr sua = new SqlUnitAddr(packName == null ? withoutExtension : packName + "." + withoutExtension,
					dir.getAbsolutePath() + File.separator + string);
			found.add(sua);
		}
		File[] dirs = dir.listFiles(dirFilter);
		for (File file : dirs) {
			scanFiles(packName == null ? file.getName() : packName + "." + file.getName(), file, found);
		}
	}

	private List<SqlUnitAddr> getSqlUnits() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;

		List<SqlUnitAddr> found = new ArrayList<SqlUnitAddr>();
		try {
			Enumeration<URL> resources = classLoader.getResources("");
			// log.debug("Test directories:");
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				if (resource.getProtocol().equals("file")) {
					scanFiles(null, new File(resource.getPath()), found);
				}
				// log.debug(resource.toString());
			}
			// log.debug("------");
			// log.debug("Found sqlunits:");
			// for (SqlUnitAddr sua : found) {
			// log.debug(sua.sqlUnitName);
			// }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Sistem.printStackTrace(e);
		}
		return found;
	}

	private String getPackageOfSqlUnit(String sqlUnitName) {
		int li = sqlUnitName.lastIndexOf('.');
		if (li < 0)
			return null;
		return sqlUnitName.substring(0, li);
	}

	private Map<String, RomComp> getComps() {
		return comps;
	}

	private void setComps(Map<String, RomComp> comps) {
		this.comps = comps;
	}

	public RomDb getRomDbForCodeGen() {
		return parser.getRomDb();
	}

	// private void runSqlCommand(Connection connection, Command sqlCommand)
	// throws KnownError {
	// Statement st = null;
	// try {
	// st = connection.createStatement();
	// if (onlyFunctions) {
	// String l = sqlCommand.getCommand().toLowerCase();
	// if (!l.startsWith("create function") &&
	// l.startsWith("create or replace function")) {
	// return;
	// }
	// }
	// log.debug("SqlCommand->" + sqlCommand.getCommand());
	// st.execute(sqlCommand.getCommand());
	// } catch (SQLException e) {
	// mc.c("Error-->" + sqlCommand.toString());
	// throw e;
	// } finally {
	// dbService.free(st);
	// }
	//
	// }

}
