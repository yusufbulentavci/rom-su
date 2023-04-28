package com.bilgidoku.rom.pg.sqlunit.model;

import java.util.ArrayList;
import java.util.List;

import com.bilgidoku.rom.pg.sqlunit.SqlUnit;
import com.bilgidoku.rom.pg.sqlunit.SuComp;
import com.bilgidoku.rom.pg.sqlunit.rom.RomComp;

public class Data extends SuComp {


	private String table;
	private String name;

	public Data(SqlUnit su, boolean isSql, boolean unitTest, int lineNo) {
		super(su, isSql, unitTest, lineNo, true);
	}
	
	@Override
	public boolean isLineBased() {
		return false;
	}

	
	public RomComp getComp() {
		return new RomComp("data", "public", table + "_" + name, this.getVersion(), true, null, null, null, null, null);
	}

	@Override
	public boolean justRun() {
		return false;
	}

	public List<Command> getCommands() {
		if(!this.isUnitTest())
			return super.getCommands();
		
		List<Command> ret = new ArrayList<Command>();

		Command going = null;
		for (Command command : super.getCommands()) {
//			System.err.println(">>>"+command.getCommand());
			String cmd = command.getCommand().trim();
			if (going == null) {
				going = command;
			}

			if (cmd.endsWith(";")) {
				if (going != command) {
					going.appendCommand(cmd);
				}
				ret.add(going);
				going = null;
			}else if (going != command) {
				going.appendCommand(cmd);
			}
		}

		return ret;
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
}
