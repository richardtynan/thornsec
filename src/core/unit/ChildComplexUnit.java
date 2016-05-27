package core.unit;

import core.iface.IChildUnit;

public class ChildComplexUnit extends ComplexUnit implements IChildUnit {

	protected String parent;

	protected ChildComplexUnit() {
	}

	public ChildComplexUnit(String parent, String name, String precondition, String config, String audit) {
		super(name, precondition, config + parent + "_unchanged=0;\n", audit);
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