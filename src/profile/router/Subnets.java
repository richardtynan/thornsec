package profile.router;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import profile.Router;
import singleton.IPTablesConf;
import singleton.NetConf;

public class Subnets extends AProfile {

	public Subnets() {
		super("subnets");
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();

		String[] devs = data.getDeviceLabels();
		int start = 100;
		for (int i = 0; i < devs.length; i++) {
			int subnet = start + i;

			vec.addElement(NetConf.getInstance(server, data.getLabel()).addStaticIface(devs[i] + "_iface",
					Router.getIntIface(server, data),
					"address " + Router.getNet(server, data) + "." + subnet + ".1" + "\nnetmask 255.255.255.0"));

			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterForward(devs[i],
					"-s " + Router.getNet(server, data) + "." + subnet + ".2 -j " + devs[i]));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterForward(devs[i],
					"-d " + Router.getNet(server, data) + "." + subnet + ".2 -j " + devs[i]));

			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i] + "_ipt", devs[i],
					"-j ACCEPT"));

			/*
			 * vec.addElement(IPTablesConf.getInstance(server,
			 * data.getLabel()).addFilter(devs[i], devs[i],
			 * "-j LOG --log-prefix \\\"iptfwd-" + devs[i] + ": \\\""));
			 * vec.addElement(IPTablesConf.getInstance(server,
			 * data.getLabel()).addFilter(devs[i], devs[i], "-i " +
			 * Router.getIntIface(server, data) + " -o " +
			 * Router.getIntIface(server, data) + " -s " + Router.getNet(server,
			 * data) + "." + subnet + ".2 -j ACCEPT"));
			 * vec.addElement(IPTablesConf.getInstance(server,
			 * data.getLabel()).addFilter(devs[i], devs[i], "-i " +
			 * Router.getIntIface(server, data) + " -o " +
			 * Router.getIntIface(server, data) + " -d " + Router.getNet(server,
			 * data) + "." + subnet + ".2 -j ACCEPT"));
			 * vec.addElement(IPTablesConf.getInstance(server,
			 * data.getLabel()).addFilter(devs[i], devs[i], "-i " +
			 * Router.getIntIface(server, data) + " -o " +
			 * Router.getExtIface(server, data) + " -s " + Router.getNet(server,
			 * data) + "." + subnet + ".2 -j ACCEPT"));
			 * vec.addElement(IPTablesConf.getInstance(server,
			 * data.getLabel()).addFilter(devs[i], devs[i], "-i " +
			 * Router.getExtIface(server, data) + " -o " +
			 * Router.getIntIface(server, data) + " -d " + Router.getNet(server,
			 * data) + "." + subnet + ".2 -j ACCEPT"));
			 */
		}
		return vec;
	}

}
