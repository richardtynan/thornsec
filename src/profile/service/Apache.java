package profile.service;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import unit.pkg.InstalledUnit;
import unit.pkg.RunningUnit;

public class Apache extends AProfile {

	public Apache() {
		super("apache");
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new InstalledUnit("apache", "apache2"));
		vec.addElement(new RunningUnit("apache", "apache2", "apache2"));
		return vec;
	}

}
