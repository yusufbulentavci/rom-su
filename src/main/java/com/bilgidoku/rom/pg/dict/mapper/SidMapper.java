package com.bilgidoku.rom.pg.dict.mapper;

import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.pg.veritabani.DbThree;
import com.bilgidoku.rom.pg.dict.ArgMapper;
import com.bilgidoku.rom.pg.dict.CallInteraction;

public class SidMapper extends ArgMapper {
	public SidMapper(short i, boolean canBeNull) {
		super(i, canBeNull);
	}

	public String toString() {
		return "SidMapper:";
	}

	@Override
	public void setValue(CallInteraction request, String self, DbThree ps) throws KnownError, KnownError {
		throw new KnownError().notImplemented();
	}

	@Override
	public Object getValue(CallInteraction request, String self) {
		return request.getSession().getSid();
	}

}
