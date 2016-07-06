package profile.router;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import profile.Base;
import profile.Router;
import singleton.IPTablesConf;
import singleton.NetConf;

public class Subnets extends AProfile {

	public Subnets() {
		super("subnets");
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();

		Vector<String> names = new Vector<String>();
		Vector<String> subnets = new Vector<String>();

		String[] servers = data.getServerLabels();
		for (int i = 0; i < servers.length; i++) {
			if (!servers[i].equals(server)) {
				names.addElement(servers[i]);
				subnets.addElement(Base.getSubnet(servers[i], data));
			}
		}

		String[] devs = data.getDeviceLabels();
		int start = 100;
		for (int i = 0; i < devs.length; i++) {
			int subnet = start + i;
			names.addElement(devs[i]);
			subnets.addElement(Router.getNet(server, data) + "." + subnet);
		}

		for (int i = 0; i < names.size(); i++) {

			vec.addElement(NetConf.getInstance(server, data.getLabel()).addStaticIface(names.elementAt(i) + "_iface",
					Router.getIntIface(server, data),
					"address " + subnets.elementAt(i) + ".1" + "\nnetmask 255.255.255.0"));

			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterForward(
					names.elementAt(i) + "_ipt_src",
					"-s " + subnets.elementAt(i) + ".2 -j " + names.elementAt(i)));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterForward(
					names.elementAt(i) + "_ipt_dst",
					"-d " + subnets.elementAt(i) + ".2 -j " + names.elementAt(i)));

			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addChain(names.elementAt(i) + "_ipt_chain",
					"filter", names.elementAt(i)));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
					.addFilter(names.elementAt(i) + "_ipt_chain_drop", names.elementAt(i), "-j DROP"));
			vec.addElement(
					IPTablesConf.getInstance(server, data.getLabel()).addFilter(names.elementAt(i) + "_ipt_chain_log",
							names.elementAt(i), "-j LOG --log-prefix \\\"iptfwd-" + names.elementAt(i) + ": \\\""));

			vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
					.addChain(names.elementAt(i) + "_ipt_int_chain", "filter", names.elementAt(i) + "_int"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
					.addFilter(names.elementAt(i) + "_ipt_chain_int", names.elementAt(i) + "_int", "-j ACCEPT"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(names.elementAt(i) + "_ipt_int",
					names.elementAt(i), "-i " + Router.getIntIface(server, data) + " -o "
							+ Router.getIntIface(server, data) + " -j " + names.elementAt(i) + "_int"));

			vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
					.addChain(names.elementAt(i) + "_ipt_ext_chain", "filter", names.elementAt(i) + "_ext"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
					.addFilter(names.elementAt(i) + "_ipt_chain_ext", names.elementAt(i) + "_ext", "-j ACCEPT"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(names.elementAt(i) + "_ipt_ext1",
					names.elementAt(i), "-i " + Router.getIntIface(server, data) + " -o "
							+ Router.getExtIface(server, data) + " -j " + names.elementAt(i) + "_ext"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(names.elementAt(i) + "_ipt_ext2",
					names.elementAt(i), "-i " + Router.getExtIface(server, data) + " -o "
							+ Router.getIntIface(server, data) + " -j " + names.elementAt(i) + "_ext"));

		}
		return vec;
	}

}
