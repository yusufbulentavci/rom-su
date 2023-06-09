package com.bilgidoku.rom.pg.dict.types;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.java.min.json.JSONWriter;
import com.bilgidoku.rom.pg.veritabani.DbThree;

public class TimeType implements TypeAdapter {

	ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>();

	@Override
	public void setValue(DbThree three, String obj) throws KnownError {
		if (obj == null) {
			three.setNull(java.sql.Types.TIME);
			return;
		}
		Date d;
		try {
			d = this.getDateFormat().parse(obj);
		} catch (ParseException e) {
			throw new KnownError(e);
		}
		three.setTime(new java.sql.Time(d.getTime()));
	}

	public String toString() {
		return "time";
	}

	@Override
	public String[] getSqlNames() {
		final String[] s = { "time" };
		return s;
	}

	private DateFormat getDateFormat() {
		DateFormat d = dateFormat.get();
		if (d == null) {
			d = new SimpleDateFormat();
			dateFormat.set(d);
		}
		return d;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof TimeType);
	}

	@Override
	public String getJavaType() {
		return "Time";
	}

	@Override
	public Object[] getArrayOf(int size) {
		return new Time[size];
	}

	@Override
	public String getSqlName() {
		return "time";
	}

	@Override
	public boolean needSqlConversion() {
		return false;
	}

	@Override
	public boolean isToQuote() {
		return true;
	}

	@Override
	public boolean needCast() {
		return false;
	}

	@Override
	public Object getValue(DbThree db3) throws KnownError {
		java.sql.Time b = db3.getTime();
		if (db3.wasNull()) {
			return null;
		}
		return b;
	}

	@Override
	public Object[] getArrayValue(DbThree db3) throws KnownError {
		return (java.sql.Time[]) db3.getArray();
	}

	@Override
	public void writeValue(DbThree db3, JSONWriter writer) throws KnownError,
			KnownError {
		writer.value(getValue(db3));
	}

	@Override
	public void writeArrayValue(DbThree db3, JSONWriter writer)
			throws KnownError, KnownError {
		writer.value(getArrayValue(db3));
	}

	@Override
	public Object fromString(String str) throws ParseException {
		Date d = getDateFormat().parse(str);
		return new Time(d.getTime());
	}

	@Override
	public void writeJson(JSONWriter writer, Object value) throws KnownError {
		writer.value(value.toString());
	}

	@Override
	public boolean isJavaType() {
		return true;
	}
	
	@Override
	public String callProtoPart() {
		return "?";
	}
	@Override
	public boolean isPrimitive() {
		return true;
	}
}
