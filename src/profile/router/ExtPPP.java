package profile.router;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AStructuredProfile;
import profile.Router;
import singleton.NetConf;
import unit.pkg.InstalledUnit;
import unit.pkg.RunningUnit;

public class ExtPPP extends AStructuredProfile {

	public ExtPPP() {
		super("ext_ppp");
	}

	public Vector<IProfile> getInstalled(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new InstalledUnit("ext_ppp", "ppp"));
		return vec;
	}

	public Vector<IProfile> getPersistent(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();

		vec.addElement(NetConf.getInstance(server, data.getLabel()).addPPPIface("ext_ppp_iface",
				Router.getExtIface(server, data)));

		return vec;
	}

	public Vector<IProfile> getIpt(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		return vec;
	}

	public Vector<IProfile> getLive(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new RunningUnit("ext_ppp", "pppd", "pppd"));
		return vec;
	}

}
