package com.bilgidoku.rom.pg.sqlunit.model;

import org.junit.Ignore;
import org.junit.Test;

import com.bilgidoku.rom.pg.sqlunit.SqlUnit;

@Ignore
public class DataTest {

	@Test
	public void test() {
		SqlUnit su=new SqlUnit("mydatasu","com.bilgidoku.rom.pg.sqlunit.model");
		Data d=new Data(su, true, true, 5);
		d.setName("mydata");
		d.setTable("myschema.mytable");
		d.run();
	
	}

}
