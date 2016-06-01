package profile;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import core.unit.SimpleUnit;
import singleton.IPTablesConf;
import singleton.NetConf;

public class Router extends AProfile {

	public Router() {
		super("router");
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();

		// FWD
		vec.addElement(new SimpleUnit("router_fwd", "proceed",
				"sudo bash -c \"echo net.ipv4.ip_forward=1 >> /etc/sysctl.conf\"; sudo sysctl -p;",
				"grep -v \"#\" /etc/sysctl.conf | grep ip_forward;", "net.ipv4.ip_forward=1", "pass"));

		// NAT
		IPTablesConf.getInstance(server, data.getLabel()).addNatPostrouting("-j MASQUERADE");

		// subnetting
		String[] devs = data.getDeviceLabels();
		int start = 100;
		for (int i = 0; i < devs.length; i++) {
			int subnet = start + i;

			vec.addElement(NetConf.getInstance(server, data.getLabel()).addStaticIface(devs[i] + "_iface",
					Router.getIntIface(server, data),
					"address " + Router.getNet(server, data) + "." + subnet + ".1" + "\nnetmask 255.255.255.0"));

			/*
			IPTablesConf.getInstance(server, data.getLabel())
					.addFilterForward("-s " + Router.getNet(server, data) + "." + subnet + ".2 -j " + devs[i]);
			IPTablesConf.getInstance(server, data.getLabel())
					.addFilterForward("-d " + Router.getNet(server, data) + "." + subnet + ".2 -j " + devs[i]);

			IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i], "-j DROP");
			IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i],
					"-j LOG --log-prefix \\\"iptfwd-" + devs[i] + ": \\\"");
			IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i],
					"-i " + Router.getIntIface(server, data) + " -o " + Router.getIntIface(server, data) + " -s "
							+ Router.getNet(server, data) + "." + subnet + ".2 -j ACCEPT");
			IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i],
					"-i " + Router.getIntIface(server, data) + " -o " + Router.getIntIface(server, data) + " -d "
							+ Router.getNet(server, data) + "." + subnet + ".2 -j ACCEPT");
			IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i],
					"-i " + Router.getIntIface(server, data) + " -o " + Router.getExtIface(server, data) + " -s "
							+ Router.getNet(server, data) + "." + subnet + ".2 -j ACCEPT");
			IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i],
					"-i " + Router.getExtIface(server, data) + " -o " + Router.getIntIface(server, data) + " -d "
							+ Router.getNet(server, data) + "." + subnet + ".2 -j ACCEPT");
			*/
		}

		return vec;
	}

	public static String getIntIface(String server, INetworkData data) {
		return getProperty("int", "router", server, data);
	}

	public static String getExtIface(String server, INetworkData data) {
		return getProperty("ext", "router", server, data);
	}

	public static String getSubnet(String server, INetworkData data) {
		return getProperty("subnet", "router", server, data);
	}

	public static String getNet(String server, INetworkData data) {
		return getProperty("net", "router", server, data);
	}

	public static String getNetrev(String server, INetworkData data) {
		return getProperty("netrev", "router", server, data);
	}

	public static String getPKI(String server, INetworkData data) {
		return getProperty("pki", "router", server, data);
	}

}
