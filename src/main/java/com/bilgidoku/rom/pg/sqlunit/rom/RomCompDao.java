package com.bilgidoku.rom.pg.sqlunit.rom;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import com.bilgidoku.rom.base.java.min.util.FromResource;
import com.bilgidoku.rom.base.min.Sistem;
import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.pg.veritabani.DbThree;
import com.bilgidoku.rom.pg.veritabani.VeritabaniGorevlisi;

/**
 * 
 * create table is not exists romcomp( comptype text not null, schemaname text
 * not null, named not null, ver int not null, primary key
 * (comptype,schemaname,named) )
 * 
 * @author bulent.avci
 * 
 */
public class RomCompDao {

	final DbThree db3;

	public RomCompDao(VeritabaniGorevlisi vg) throws KnownError {
		db3 = new DbThree(vg, "create table if not exists public.romcomp(" + "comptype text not null,"
				+ "schemaname text not null,"
				+ "named text not null, ver int not null, norom boolean not null, uzanti text, dokun text, primary key (comptype,schemaname,named)"
				+ ")");
		db3.setTransactional();
	}

	public Map<String, RomComp> getComps() throws KnownError {
		Map<String, RomComp> comps = new Hashtable<String, RomComp>();

		db3.execute();

		try {
			String df = FromResource.loadString("dropfunc.sql");
			db3.replaceQuery(df);
			db3.execute();
		} catch (IOException e) {
			throw new KnownError("Coulnd find /dropfunc.sql", e);
		}

		db3.replaceQuery(
				"create table if not exists public.romcomp(" + "comptype text not null," + "schemaname text not null,"
						+ "named text not null," + "ver int not null,norom boolean not null, uzanti text, dokun text,"
						+ "primary key (comptype,schemaname,named)" + ")");
		db3.execute();

		db3.replaceQuery("select * from romcomp");

		db3.executeQuery();
		while (db3.next()) {

			RomComp rc = new RomComp(db3.getString(), db3.getString(), db3.getString(), db3.getInt(), db3.getBoolean(),
					db3.getString(), db3.getString());
			comps.put(rc.getId(), rc);
		}
		return comps;
	}

	public Map<String, RomComp> getTestComps(PrintWriter pw) throws KnownError {
		Map<String, RomComp> comps = new Hashtable<String, RomComp>();

		db3.execute();

		try {
			String df = FromResource.loadString("/dropfunc.sql");
			pw.println(df);
			db3.replaceQuery(df);
			db3.execute();
		} catch (IOException e) {
			throw new KnownError("Coulnd find /dropdfunc.sql", e);
		}

//		String sqld = "drop table if exists public.romcomp;";
//		pw.println(sqld);
//		db3.replaceQuery(sqld);
//		db3.execute();

		String sql = "create table if not exists public.romcomp(" + "comptype text not null,"
				+ "schemaname text not null," + "named text not null,"
				+ "ver int not null,norom boolean not null, uzanti text,dokun text,"
				+ "primary key (comptype,schemaname,named)" + ")";
		pw.println(sql);
		db3.replaceQuery(sql);
		db3.execute();

		pw.println("select * from romcomp");
		db3.replaceQuery("select * from romcomp");

		db3.executeQuery();
		while (db3.next()) {

			RomComp rc = new RomComp(db3.getString(), db3.getString(), db3.getString(), db3.getInt(), db3.getBoolean(),
					db3.getString(), db3.getString());
			comps.put(rc.getId(), rc);
		}
		return comps;
	}

	public void setComps(Map<String, RomComp> comps) throws KnownError {
		db3.replaceQuery("delete from romcomp");
		db3.execute();

		Sistem.log.addToSet("romdb-comps", reportRomComp(comps, ';'));

		db3.replaceQuery("insert into romcomp (comptype,schemaname,named,ver,norom,uzanti) " + "values (?,?,?,?,?,?)");

		for (Entry<String, RomComp> entry : comps.entrySet()) {
			db3.setString(entry.getValue().compType);
			db3.setString(entry.getValue().schemaName);
			db3.setString(entry.getValue().named);
			db3.setInt(entry.getValue().ver);
			db3.setBoolean(entry.getValue().noRom);
			db3.setString(entry.getValue().uzanti);
			db3.execute();
			db3.closeResultSet();
		}

		db3.replaceQuery("select count(assertcompexists(comptype,schemaname,named)) from romcomp;");

		db3.execute();

	}

	public String reportRomComp(Map<String, RomComp> comps, char nextLine) throws KnownError {
		StringBuilder sb = new StringBuilder();
		for (RomComp r : comps.values()) {
			sb.append(r.toString());
			sb.append(nextLine);
		}
		return sb.toString();
	}

	public void commit() throws KnownError {
		db3.commit();
		Sistem.log.addToSet("romdb-end", "COMMIT");
	}

	public void rollback() throws KnownError {
		db3.rollback();
		Sistem.log.addToSet("romdb-end", "ROLLBACK");
	}

	public void executeCommand(String command) throws KnownError {
		db3.replaceQuery(command);
		Sistem.log.addToSet("romdb-change", command.length() < 500 ? command : command.substring(0, 499));
		System.out
				.println("-------------------------------------------------------------------------------------------");
		System.out.println(command);
		System.out
				.println("------------------------------------------------------------------------------------------");

		db3.execute();
	}

}
