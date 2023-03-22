package com.bilgidoku.rom.pg;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bilgidoku.rom.base.java.min.OrtamInit;
import com.bilgidoku.rom.base.min.Sistem;
import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.min.gorevli.GorevliYonetimi;
import com.bilgidoku.rom.base.min.gorevli.Ortam;
import com.bilgidoku.rom.pg.sqlunit.SqlUnitGorevlisi;

public class SuTestBase {

	@BeforeClass
	public static void setUp() throws KnownError {
		OrtamInit.test();
		Ortam.test().iniFile(false, true, "test.ini");
		SqlUnitGorevlisi.bind(null);
		GorevliYonetimi.tek().basla();
	}
	
	@Test
	public void test() throws KnownError {

//		SqlUnitGorevlisi.tek().load("testing");
//		SqlUnitGorevlisi.tek().runOnDb();
		Sistem.log.outln("Hello world");
//	
	}
	

}
