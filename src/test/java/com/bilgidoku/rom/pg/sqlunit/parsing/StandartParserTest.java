package com.bilgidoku.rom.pg.sqlunit.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bilgidoku.rom.pg.sqlunit.SqlUnit;
import com.bilgidoku.rom.pg.sqlunit.SuComp;
import com.bilgidoku.rom.pg.sqlunit.SuException;
import com.bilgidoku.rom.pg.sqlunit.rom.RomComp;

public class StandartParserTest {

	@Test
	public void test() throws SuException {
		SqlUnit su=new SqlUnit("hede", "hedepack");
		StandartParser s=new StandartParser(null, "@TABLE", "table", null, true);
		SuComp ret = s.parse(su, 
				"prefix=/hello/world", 
				"create table sema.tablo(adi int);" ,
				1);
		RomComp c = ret.getComp();
		assertEquals("sema", c.schemaName);
		assertEquals("tablo", c.named);
		
		System.out.println(c);
	}

}
