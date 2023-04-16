package com.bilgidoku.rom.pg.sqlunit.model;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.bilgidoku.rom.base.java.min.util.FromResource;
import com.bilgidoku.rom.pg.sqlunit.SqlUnit;
import com.bilgidoku.rom.pg.sqlunit.SuComp;
import com.bilgidoku.rom.pg.sqlunit.rom.RomComp;

public class Data extends SuComp {

	private List<DataColumn> columns;

	private String table;
	private String name;

	public Data(SqlUnit su, boolean isSql, boolean unitTest, int lineNo) {
		super(su, isSql, unitTest, lineNo, false);
	}

	@Override
	public boolean isLineBased() {
		return true;
	}

	@Override
	public List<Command> run() {
		List<Command> cmds = new ArrayList<Command>();
		CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter('|').withFirstRecordAsHeader();
		String csvName = this.getSu().getPackAsPath() + "/" + name + ".csv";
		Reader stream = FromResource.readerStream(csvName);
		try (CSVParser reader = new CSVParser(stream, csvFormat);) {
			List<CSVRecord> rec = reader.getRecords();
			List<String> hn = reader.getHeaderNames();
			String[] names = new String[hn.size()];
			boolean[] isStr = new boolean[hn.size()];
			for (int i = 0; i < hn.size(); i++) {
				String n = hn.get(i);
				if (n.endsWith("+")) {
					names[i] = n.substring(0, n.length() - 1);
					isStr[i] = true;
				} else {
					names[i] = n;
					isStr[i] = false;
				}
			}
			StringBuilder basla = new StringBuilder();
			basla.append("insert into ");
			basla.append(table);
			basla.append(" (");
			boolean ilk = true;
			for (String n : names) {
				if (ilk) {
					ilk = false;
				} else {
					basla.append(",");
				}
				basla.append(n);
			}
			basla.append(") values (");
			for (CSVRecord csvRecord : rec) {
				StringBuilder row = new StringBuilder(basla.toString());
				ilk = true;
				for (int i = 0; i < names.length; i++) {
					if (ilk) {
						ilk = false;
					} else {
						row.append(",");
					}
					if (isStr[i]) {
						row.append("'");
						row.append(csvRecord.get(i).replaceAll("'", "''"));
						row.append("'");
					} else {
						row.append(csvRecord.get(i));
					}
				}
				row.append(");");
				System.out.println(row.toString());
				cmds.add(new Command(row.toString(), 0));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cmds;
	}

	public List<Command> upgrade(Integer dbVer) {
		List<Command> ret = new LinkedList<Command>();
		ret.addAll(super.upgrade(dbVer));
		ret.addAll(run());
		return ret;
	}

	@Override
	public RomComp getComp() {
		return new RomComp("data", "public", table + "_" + name, this.getVersion(), true, null, null, null, null, null);
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DataColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DataColumn> columns) {
		this.columns = columns;
	}

}
