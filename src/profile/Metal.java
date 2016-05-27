package profile;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AStructuredProfile;
import unit.pkg.InstalledUnit;

public class Metal extends AStructuredProfile {

	public Metal() {
		super("metal");
	}

	public Vector<IProfile> getInstalled(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new InstalledUnit("metal_virtualbox", "virtualbox"));
		return vec;
	}

	public Vector<IProfile> getPersistent(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		return vec;
	}

	public Vector<IProfile> getIpt(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		return vec;
	}

	public Vector<IProfile> getLive(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		return vec;
	}

}
