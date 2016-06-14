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

			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterForward(devs[i] + "_ipt_src",
					"-s " + Router.getNet(server, data) + "." + subnet + ".2 -j " + devs[i]));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterForward(devs[i] + "_ipt_dst",
					"-d " + Router.getNet(server, data) + "." + subnet + ".2 -j " + devs[i]));

			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i] + "_ipt_chain", devs[i],
					"-j ACCEPT"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i] + "_ipt_chain_log", devs[i],
					"-j LOG --log-prefix \\\"iptfwd-" + devs[i] + ": \\\""));
			
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i] + "_ipt_int", devs[i],
					"-i " + Router.getIntIface(server, data) + " -o " + Router.getIntIface(server, data) + " -j "
							+ devs[i] + "_int"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i] + "_ipt_ext1", devs[i],
					"-i " + Router.getIntIface(server, data) + " -o " + Router.getExtIface(server, data) + " -j "
							+ devs[i] + "_ext"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i] + "_ipt_ext2", devs[i],
					"-i " + Router.getExtIface(server, data) + " -o " + Router.getIntIface(server, data) + " -j "
							+ devs[i] + "_ext"));
			
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i] + "_ipt_chain_int",
					devs[i] + "_int", "-j ACCEPT"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(devs[i] + "_ipt_chain_ext",
					devs[i] + "_ext", "-j ACCEPT"));

		}
		return vec;
	}

}
