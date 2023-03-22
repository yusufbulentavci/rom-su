package com.bilgidoku.rom.pg.dict;

import java.text.ParseException;

import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.min.err.NotInlineMethodException;
import com.bilgidoku.rom.base.min.err.ParameterError;
import com.bilgidoku.rom.pg.dict.types.TypeHolder;
import com.bilgidoku.rom.pg.veritabani.DbThree;


public abstract class ArgMapper {
	protected boolean canBeNull;
	int index;
	protected TypeHolder sqlType;
	
	public ArgMapper(int index,boolean canBeNull){
		this.canBeNull=canBeNull;
		this.index=index;
	}
	
	public String getConversion(){
		return null;
	}
	
	public String callProto(){
		return "?";
	}
	
	public abstract void setValue(CallInteraction request, String self, DbThree ps) throws KnownError, KnownError, NotInlineMethodException, ParseException, KnownError, KnownError, ParameterError; 
	
	public abstract Object getValue(CallInteraction request, String self) throws KnownError, ParseException, KnownError, KnownError, ParameterError;
	public boolean isNullable(){
		return canBeNull;
	}
}
