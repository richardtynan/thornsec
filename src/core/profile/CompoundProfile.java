package core.profile;

import java.util.Vector;

import core.iface.IChildUnit;
import core.iface.INetworkData;
import core.iface.IProfile;
import core.unit.ComplexUnit;

public class CompoundProfile extends AProfile {

	private String precondition;
	private String config;
	private Vector<IProfile> children;

	public CompoundProfile(String name, String precondition, String config) {
		super(name);
		this.precondition = precondition;
		this.config = config;
		this.children = new Vector<IProfile>();
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> rules = new Vector<IProfile>();
		rules.add(new ComplexUnit(getLabel() + "_compound", precondition, "",
				getLabel() + "_unchanged=1;\n" + getLabel() + "_compound=1;\n"));
		rules.addAll(this.children);
		rules.add(new ComplexUnit(getLabel(), precondition, config + "\n" + getLabel() + "_unchanged=1;\n",
				getLabel() + "=$" + getLabel() + "_unchanged;\n"));
		return rules;
	}

	public void addChild(IChildUnit rule) {
		this.children.addElement(rule);
	}

}
