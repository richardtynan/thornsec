package profile.router;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.unit.SimpleUnit;
import profile.Router;
import singleton.NetConf;
import unit.pkg.InstalledUnit;
import unit.pkg.RunningUnit;

public class ExtPPP extends SimpleUnit {

	public String getLabel() {
		return "ext_ppp";
	}

	public Vector<IProfile> getInstalled(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new InstalledUnit("ext_ppp", "ppp"));
		return vec;
	}

	public Vector<IProfile> getPersistent(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		String ppp = "pre-up /sbin/ifconfig " + Router.getExtIface(server, data) + " up\n";
		ppp += "provider provider";
		vec.addElement(
				NetConf.getInstance(server, data.getLabel()).addStaticIface("ext_ppp_iface", "dsl-provider", ppp));
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
