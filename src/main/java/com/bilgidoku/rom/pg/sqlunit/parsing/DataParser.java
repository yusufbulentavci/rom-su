package com.bilgidoku.rom.pg.sqlunit.parsing;

import java.util.ArrayList;
import java.util.List;

import com.bilgidoku.rom.pg.dict.types.TypeAdapter;
import com.bilgidoku.rom.pg.sqlunit.RomDb;
import com.bilgidoku.rom.pg.sqlunit.SqlUnit;
import com.bilgidoku.rom.pg.sqlunit.SuComp;
import com.bilgidoku.rom.pg.sqlunit.SuException;
import com.bilgidoku.rom.pg.sqlunit.model.Data;
import com.bilgidoku.rom.pg.sqlunit.model.DataColumn;
import com.bilgidoku.rom.pg.sqlunit.model.Table;

public class DataParser extends CompParser {

	public DataParser(RomDb romDb) {
		super(romDb);
	}

	private static final String KEYWORD_DATA = "--@DATA";

	public String getKeyword() {
		return KEYWORD_DATA;
	}

	@Override
	public SuComp parse(SqlUnit su, String trimmedLine, String nextLine, int lineNo) throws SuException {
		int version = 0;
		String table = "";
		String name = "";
		trimmedLine = trimmedLine.toLowerCase();
		List<DataColumn> dcs = new ArrayList<DataColumn>();
		boolean utest = false;
		Table tbl=null;
		String[] methodTokens = trimmedLine.split("\\s+");
		for (String string : methodTokens) {
			if (string.startsWith("ver=")) {
				String hm = string.substring("ver=".length());
				version = Integer.parseInt(hm);
			} else if (string.startsWith("table=")) {
				table = string.substring("table=".length());
				TypeAdapter mbl = romDb.getAnyType(table);
				if(!(mbl instanceof Table)){
					throw new SuException(su.getNamed(), 0, "Type:"+table+" should be instance of table but not SU:"+su.named);
				}
				tbl=(Table) mbl;
			} else if (string.startsWith("name=")) {
				name = string.substring("name=".length());
			} else if (string.equals("utest")) {
				utest = true;
			}
			
		}
		Data data = new Data(su, true, utest, lineNo);
		data.setColumns(dcs);
		data.setName(name);
		data.setTable(table);
		data.setVersion(version);
		return data;
	}
}
