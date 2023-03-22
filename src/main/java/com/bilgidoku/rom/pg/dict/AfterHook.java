package com.bilgidoku.rom.pg.dict;

import java.util.Set;

import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.min.err.NotInlineMethodException;
import com.bilgidoku.rom.base.min.err.ParameterError;

public abstract class AfterHook {

	protected CGMethod romMethod;
	protected Set<String> interestedFields;
	protected boolean started=false;

	public void initialize(CGMethod method) {
		this.romMethod = method;
	}

	abstract public void hook(HookScope scope, Object resp) throws KnownError, NotInlineMethodException, ParameterError;
	abstract public void undo(HookScope scope) throws KnownError;
	public Set<String> interestedResults(){
		return interestedFields;
	}

	public void start() {
		started=true;
	}

	public boolean getStarted() {
		return started;
	}
}
