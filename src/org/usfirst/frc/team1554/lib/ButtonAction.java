package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.readonly.ReadOnlyString;
import org.usfirst.frc.team1554.lib.readonly.StringConstant;

public abstract class ButtonAction {

	private final ReadOnlyString name;

	public static ButtonAction as(String name, Runnable action) {
		return new ButtonAction(name) {
			@Override
			public void act() {
				action.run();
			}
		};
	}

	public ButtonAction(String name) {
		this.name = new StringConstant(name);
	}

	public String name() {
		return this.name.get();
	}

	abstract public void act();

}
