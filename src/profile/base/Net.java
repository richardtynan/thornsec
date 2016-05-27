package profile.base;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import profile.Base;
import singleton.NetConf;

public class Net extends AProfile {

	public Net() {
		super("net");
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(NetConf.getInstance(server, data.getLabel()));
		vec.addElement(NetConf.getInstance(server, data.getLabel()).setPrimaryIface("net_primary_iface", Base.getIface(server, data),
				"address " + Base.getIP(server, data) + "\nnetmask 255.255.255.0"));
		return vec;
	}

}
