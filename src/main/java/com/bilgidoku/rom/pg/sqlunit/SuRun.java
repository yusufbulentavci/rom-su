package com.bilgidoku.rom.pg.sqlunit;

import java.io.IOException;
import java.sql.SQLException;

import com.bilgidoku.rom.base.min.err.KnownError;
public class SuRun {

	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws KnownError 
	 * @throws IOException 
	 * @throws SuException 
	 * @throws KnownError 
	 */
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, SuException, KnownError {
	System.out.println("We got here");
	for(int i=0; i< args.length; i++){
		System.out.println("here:"+args[i]);
	}
//		Sistem.outln("Running on database:"+args[0]+" su:"+args[1]);
//		SqlUnitManager sum=new SqlUnitManager(false);
//		sum.load(args[1]);
//		sum.runOnDb(args[0]);
//		Sistem.outln("Done");
	}

}
