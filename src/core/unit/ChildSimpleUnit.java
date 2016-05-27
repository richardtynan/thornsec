package core.unit;

import core.iface.IChildUnit;

public class ChildSimpleUnit extends SimpleUnit implements IChildUnit {

	protected String parent;

	protected ChildSimpleUnit() {
	}

	public ChildSimpleUnit(String parent, String name, String precondition, String config, String audit, String test,
			String result) {
		super(name, precondition, config + parent + "_unchanged=0;\n", audit, test, result);
		this.parent = parent;
	}

	protected String getDryRun() {
		return "\t" + getParent() + "_unchanged=0;\n";
	}

	protected String getConfig() {
		return config + getParent() + "_unchanged=0;\n";
	}

	public String getParent() {
		return parent;
	}
}
