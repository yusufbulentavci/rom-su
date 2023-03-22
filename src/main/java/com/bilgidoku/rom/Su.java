package com.bilgidoku.rom;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.bilgidoku.rom.base.gunluk.GunlukGorevlisi;
import com.bilgidoku.rom.base.java.min.OrtamInit;
import com.bilgidoku.rom.base.min.gorevli.Ortam;
import com.bilgidoku.rom.pg.sqlunit.SqlUnitGorevlisi;

public class Su {

	static Options options = new Options();

	public static void main(String[] args) {
//		args=new String[] {"--inifile","/work/romlib/kampusdb/src/test/resources/test.ini"};
		addOption(true, "i", "inifile", true, "Ini file");
//		addOption(false, "g", "sql", true, "Generate script, do not execute");
//		addOption(false, "f", "outputfile", true, "Generate script to output");
//		addOption(false, "q", "quite", false, "Do not write any unnecessary output");
//		addOption(false, "t", "test", false, "Run as test");
//		System.setProperty("ROM_DB_USER", "yuzrun");
//		System.setProperty("ROM_DB_USER_PASSWORD", "jvl5SSFq8fsemod");
//		System.setProperty("ROM_DB", "hello");
//		

		CommandLineParser parser = new DefaultParser();
		

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			String infi=line.getOptionValue("inifile");
			System.out.println(infi);
			
			//OrtamInit.test();
			OrtamInit.prod();
			
			Ortam.prod().iniFile(true, false, infi);
			
			
			GunlukGorevlisi.bind(null);
			SqlUnitGorevlisi sg = SqlUnitGorevlisi.bind(null);
			OrtamInit.basla();
			
			sg.runOnDb();
			
			
		} catch(Exception exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
			exp.printStackTrace();
			System.exit(1);
		}

	}

	private static void addOption(boolean required, String argName, String longOption, boolean hasArg,
			String description) {
		Option o = Option.builder().hasArg(hasArg).desc(description).argName(argName).longOpt(longOption)
				.required(required).build();
		options.addOption(o);
	}

}
