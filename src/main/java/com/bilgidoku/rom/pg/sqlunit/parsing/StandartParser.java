package com.bilgidoku.rom.pg.sqlunit.parsing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bilgidoku.rom.pg.sqlunit.RomDb;
import com.bilgidoku.rom.pg.sqlunit.SqlUnit;
import com.bilgidoku.rom.pg.sqlunit.SuComp;
import com.bilgidoku.rom.pg.sqlunit.SuException;
import com.bilgidoku.rom.pg.sqlunit.model.Standart;

public class StandartParser extends CompParser {
	private final String KEYWORD;
	private final Pattern pattern;
	private String type;
	private String reg;
	private boolean noRom;

	public StandartParser(RomDb romDb, String key, String type, String[] options, boolean noRom) {
		super(romDb);
		KEYWORD = key;
		this.type = type;
		this.noRom = noRom;
		String optionStr = "";
		if (options != null)
			for (String string : options) {
				optionStr = optionStr + "(?:" + string + "[\\s]+" + ")?";
			}
		reg = "^[\\s]*create[\\s]+" + optionStr + type + "[\\s]+([a-z_\\.]+)";
		pattern = Pattern.compile(reg);
	}

	public String getKeyword() {
		return KEYWORD;
	}

	@Override
	public SuComp parse(SqlUnit su, String header, String definition, int lineNo) throws SuException {
		if (definition == null) {
			throw new SuException(su.getNamed(), 0, type + " defined but create " + type + " not found at next line.\n"
					+ type + " definition:" + header);
		}
		definition = definition.toLowerCase();
		/**
		 * First of all we need names, go with nextLine
		 */
		Matcher matchFunc = pattern.matcher(definition);
		if (!matchFunc.find()) {
			throw new SuException(su.getNamed(), 0, type + " parsing failed:" + definition + "\n pattern:" + reg);
		}

		boolean unitTest = (header.indexOf(" utest") > 0);
		boolean noRom = (header.indexOf(" norom") > 0);
		Standart em = new Standart(su, this.type, matchFunc.group(1), unitTest, lineNo, noRom);

		header = header.toLowerCase();
		String[] methodTokens = header.split("\\s+");
		for (String string : methodTokens) {
			if (string.startsWith("ver=")) {
				String hm = string.substring("ver=".length());
				em.setVersion(Integer.parseInt(hm));
			} else if (string.startsWith("uzanti=")) {
				String uzanti = string.substring("uzanti=".length()).trim();
				em.setUzanti(uzanti);
			}
		}
		return em;
	}
	
	public static void main(String[] args) {
	}
}
