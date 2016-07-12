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

		Vector<String> serverNames = new Vector<String>();
		Vector<String> serverSubnets = new Vector<String>();
		String[] servers = data.getServerLabels();
		for (int i = 0; i < servers.length; i++) {
			if (!servers[i].equals(server)) {
				serverNames.addElement(servers[i]);
				serverSubnets.addElement(Base.getSubnet(servers[i], data));
			}
		}

		Vector<String> deviceNames = new Vector<String>();
		Vector<String> deviceSubnets = new Vector<String>();
		String[] devs = data.getDeviceLabels();
		int start = 100;
		for (int i = 0; i < devs.length; i++) {
			int subnet = start + i;
			deviceNames.addElement(devs[i]);
			deviceSubnets.addElement(Router.getNet(server, data) + "." + subnet);
		}

		Vector<String> names = new Vector<String>();
		Vector<String> subnets = new Vector<String>();

		names.addAll(serverNames);
		subnets.addAll(serverSubnets);
		names.addAll(deviceNames);
		subnets.addAll(deviceSubnets);

		for (int i = 0; i < names.size(); i++) {
			vec.addElement(NetConf.getInstance(server, data.getLabel()).addStaticIface(names.elementAt(i) + "_iface",
					Router.getIntIface(server, data),
					"address " + subnets.elementAt(i) + ".1" + "\nnetmask 255.255.255.0"));
		}

		for (int i = 0; i < deviceNames.size(); i++) {

			// jump to device chain
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterForward(
					deviceNames.elementAt(i) + "_ipt_src",
					"-s " + deviceSubnets.elementAt(i) + ".2 -j " + deviceNames.elementAt(i)));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterForward(
					deviceNames.elementAt(i) + "_ipt_dst",
					"-d " + deviceSubnets.elementAt(i) + ".2 -j " + deviceNames.elementAt(i)));

			// setup device chain
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
					.addChain(deviceNames.elementAt(i) + "_ipt_chain", "filter", deviceNames.elementAt(i)));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
					.addFilter(deviceNames.elementAt(i) + "_ipt_chain_drop", deviceNames.elementAt(i), "-j DROP"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(
					deviceNames.elementAt(i) + "_ipt_chain_log", deviceNames.elementAt(i),
					"-j LOG --log-prefix \\\"iptfwd-" + deviceNames.elementAt(i) + ": \\\""));
			vec.addElement(
					IPTablesConf.getInstance(server, data.getLabel()).addFilter(deviceNames.elementAt(i) + "_ipt_ext1",
							deviceNames.elementAt(i), "-i " + Router.getIntIface(server, data) + " -o "
									+ Router.getExtIface(server, data) + " -j " + deviceNames.elementAt(i) + "_ext"));
			vec.addElement(
					IPTablesConf.getInstance(server, data.getLabel()).addFilter(deviceNames.elementAt(i) + "_ipt_ext2",
							deviceNames.elementAt(i), "-i " + Router.getExtIface(server, data) + " -o "
									+ Router.getIntIface(server, data) + " -j " + deviceNames.elementAt(i) + "_ext"));
			vec.addElement(
					IPTablesConf.getInstance(server, data.getLabel()).addFilter(deviceNames.elementAt(i) + "_ipt_int",
							deviceNames.elementAt(i), "-i " + Router.getIntIface(server, data) + " -o "
									+ Router.getIntIface(server, data) + " -j " + deviceNames.elementAt(i) + "_int"));

			// setup external chain
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addChain(
					deviceNames.elementAt(i) + "_ipt_ext_chain", "filter", deviceNames.elementAt(i) + "_ext"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(
					deviceNames.elementAt(i) + "_ipt_chain_ext", deviceNames.elementAt(i) + "_ext", "-j ACCEPT"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(
					deviceNames.elementAt(i) + "_ipt_chain_established", deviceNames.elementAt(i) + "_ext",
					"-m conntrack --ctstate ESTABLISHED -j ACCEPT"));

			// setup internal chain
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addChain(
					deviceNames.elementAt(i) + "_ipt_int_chain", "filter", deviceNames.elementAt(i) + "_int"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(
					deviceNames.elementAt(i) + "_ipt_chain_int", deviceNames.elementAt(i) + "_int", "-j ACCEPT"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(
					deviceNames.elementAt(i) + "_ipt_chain_int_log", deviceNames.elementAt(i) + "_int",
					"-j LOG --log-prefix \\\"iptfwd-int-" + deviceNames.elementAt(i) + ": \\\""));

			// setup internal chain rules for servers
			for (int j = 0; j < serverNames.size(); j++) {
				vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(
						deviceNames.elementAt(i) + "_ipt_chain_int_1_" + serverNames.elementAt(j),
						deviceNames.elementAt(i) + "_int", "-s " + serverSubnets.elementAt(j) + ".2 -j ACCEPT"));
				vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter(
						deviceNames.elementAt(i) + "_ipt_chain_int_2_" + serverNames.elementAt(j),
						deviceNames.elementAt(i) + "_int", "-d " + serverSubnets.elementAt(j) + ".2 -j ACCEPT"));
			}

		}
		
		for (int i = 0; i < serverNames.size(); i++) {

			// jump to server chain
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterForward(
					serverNames.elementAt(i) + "_ipt_src",
					"-s " + serverSubnets.elementAt(i) + ".2 -j " + serverNames.elementAt(i)));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterForward(
					serverNames.elementAt(i) + "_ipt_dst",
					"-d " + serverSubnets.elementAt(i) + ".2 -j " + serverNames.elementAt(i)));

			// setup device chain
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
					.addChain(serverNames.elementAt(i) + "_ipt_chain", "filter", serverNames.elementAt(i)));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
					.addFilter(serverNames.elementAt(i) + "_ipt_chain_drop", serverNames.elementAt(i), "-j ACCEPT"));
		}
		return vec;
	}

}
