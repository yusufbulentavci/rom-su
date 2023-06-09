package com.bilgidoku.rom.pg.dict.types;

import java.text.ParseException;

import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.java.min.json.JSONWriter;
import com.bilgidoku.rom.pg.veritabani.DbThree;

public interface TypeAdapter {
	public boolean isPrimitive();
	public boolean isJavaType();
	public String getJavaType();
	public void setValue(DbThree three, String obj) throws KnownError;
	public String callProtoPart();
	public Object getValue(DbThree db3) throws KnownError;

	public Object[] getArrayOf(int size);
	public String getSqlName();
	public String[] getSqlNames();
	public boolean needSqlConversion();
	public boolean isToQuote();
	public boolean needCast();
	public Object[] getArrayValue(DbThree db3) throws KnownError;
	public void writeValue(DbThree db3, JSONWriter writer) throws KnownError, KnownError;
	public void writeArrayValue(DbThree db3,  JSONWriter writer) throws KnownError, KnownError;
	public Object fromString(String str) throws ParseException;
	public void writeJson(JSONWriter writer, Object value) throws KnownError;
}
