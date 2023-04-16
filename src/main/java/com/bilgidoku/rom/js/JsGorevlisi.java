package com.bilgidoku.rom.js;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.bilgidoku.rom.base.java.min.gorevli.GorevliDir;
import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.min.gorevli.EnvItem;
import com.bilgidoku.rom.base.min.gorevli.Gorevli;

public class JsGorevlisi extends GorevliDir {
	public static final int NO = 51;

	private ScriptEngine engine;

	public static JsGorevlisi bind(Gorevli gorevli) {
		if (tek != null)
			return tek;

		synchronized (JsGorevlisi.class) {
			if (tek == null) {
				tek = new JsGorevlisi();
			}
		}
		return tek;
	}

	private static JsGorevlisi tek;

	@Override
	protected EnvItem[] prepEnvItems() {
		return null;
	}

	@Override
	protected void bindDependency() {
	}

	@Override
	public void kur() {
		engine = new ScriptEngineManager().getEngineByName("javascript");
	//	Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
	//	bindings.put("stdout", System.out);
	}
	
	private JsGorevlisi() {
		super("Js", NO);
	}

	public void eval(String code) throws KnownError {
		try {
			engine.eval(code);
		} catch (ScriptException e) {
			throw new KnownError("Js error", e);
		}
	}

//	public void dene() {
//		System.out.println("HEYYY");
//	}
//	
//	public static void main(String[] args) throws KnownError {
//		JsGorevlisi.tek().eval("com.bilgidoku.rom.js.JsGorevlisi.tek().dene();");
//	}
}
