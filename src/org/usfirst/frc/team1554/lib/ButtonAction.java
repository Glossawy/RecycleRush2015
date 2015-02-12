package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.readonly.ReadOnlyString;
import org.usfirst.frc.team1554.lib.readonly.StringConstant;

public abstract class ButtonAction {

	private final ReadOnlyString name;
	
	public ButtonAction(String name) {
		this.name = new StringConstant(name);
	}
	
	public String name() {
		return name.get();
	}
	
	abstract public void act();
	
}
