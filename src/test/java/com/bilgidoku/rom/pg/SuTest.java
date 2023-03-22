package com.bilgidoku.rom.pg;

import com.bilgidoku.rom.base.java.min.OrtamInit;
import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.min.gorevli.GorevliYonetimi;
import com.bilgidoku.rom.base.min.gorevli.Ortam;
import com.bilgidoku.rom.pg.sqlunit.SqlUnitGorevlisi;

public class SuTest {

	public static void main(String[] args) throws KnownError {
		OrtamInit.test();
		Ortam.test().iniFile(false, true, "test.ini");
		SqlUnitGorevlisi.bind(null);
		GorevliYonetimi.tek().basla();
	}

}
