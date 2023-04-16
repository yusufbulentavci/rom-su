package com.bilgidoku.rom.pg.sqlunit.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.bilgidoku.rom.pg.sqlunit.SqlUnit;
import com.bilgidoku.rom.pg.sqlunit.SuComp;
import com.bilgidoku.rom.pg.sqlunit.rom.RomComp;

public class Run extends SuComp {
	public Run(SqlUnit su, boolean isSql, boolean unitTest, int lineNo) {
		super(su, isSql, unitTest, lineNo, true);
	}

	@Override
	public boolean isLineBased() {
		return false;
	}

	@Override
	public RomComp getComp() {
		return null;
	}

	@Override
	public List<Command> upgrade(Integer dbVer) {
		return new LinkedList<Command>();
	}

	@Override
	public boolean justRun() {
		return true;
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

}
