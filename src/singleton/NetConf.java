package singleton;

import java.util.Hashtable;
import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import core.unit.SimpleUnit;

public class NetConf extends AProfile {

	Vector<String> strings;

	public boolean isSingleton() {
		return true;
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new SimpleUnit("net_conf_persist", "proceed",
				"echo \"" + getPersistent() + "\" | sudo tee /etc/network/interfaces;", "cat /etc/network/interfaces;",
				getPersistent(), "pass"));
		return vec;
	}

	public SimpleUnit setPrimaryIface(String name, String iface, String rule) {
		String net = "auto " + iface + "\n";
		net += "iface " + iface + " inet static\n";
		net += "pre-up iptables-restore < /etc/iptables/iptables.conf\n";
		net += rule;
		strings.add(0, net);
		return new SimpleUnit(name, "proceed", "echo \\\"handled by singleton\\\";",
				"grep -A1 -B3 \"" + rule.substring(0, rule.indexOf('\n')) + "\" /etc/network/interfaces;", net, "pass");
	}

	public SimpleUnit addStaticIface(String name, String iface, String rule) {
		String net = "auto " + iface + "\n";
		net += "iface " + iface + " inet static\n";
		net += rule;
		strings.add(net);
		return new SimpleUnit(name, "proceed", "echo \\\"handled by singleton\\\";",
				"grep -A1 -B2 \"" + rule.substring(0, rule.indexOf('\n')) + "\" /etc/network/interfaces;", net, "pass");
	}

	public SimpleUnit addDynamicIface(String name, String iface) {
		String net = "auto " + iface + "\n";
		net += "iface " + iface + " inet dhcp";
		strings.add(net);
		return new SimpleUnit(name, "proceed", "echo \\\"handled by singleton\\\";",
				"grep -B1 \"" + net.substring(net.indexOf('\n') + 1, net.length()) + "\" /etc/network/interfaces;", net,
				"pass");
	}

	public SimpleUnit addPPPIface(String name, String iface) {
		String net = "auto " + iface + "\n";
		net += "iface " + iface + " inet ppp";
		strings.add(net);
		return new SimpleUnit(name, "proceed", "echo \\\"handled by singleton\\\";",
				"grep -B1 \"" + net.substring(net.indexOf('\n') + 1, net.length()) + "\" /etc/network/interfaces;", net,
				"pass");
	}

	private String getPersistent() {
		String net = "source /etc/network/interfaces.d/*\n";
		net += "\n";
		net += "auto lo\n";
		net += "iface lo inet loopback\n";
		net += "\n";
		for (int i = 0; i < strings.size(); i++) {
			net += strings.elementAt(i) + "\n\n";
		}
		return net.trim();
	}

	private NetConf() {
		super("net_conf");
		this.strings = new Vector<>();
	}

	private static Hashtable<String, Hashtable<String, NetConf>> networks;

	public static NetConf getInstance(String server, String network) {
		if (networks == null) {
			networks = new Hashtable<>();
		}
		Hashtable<String, NetConf> servers = networks.get(network);
		if (servers == null) {
			servers = new Hashtable<>();
			networks.put(network, servers);
		}
		NetConf inst = servers.get(server);
		if (inst == null) {
			inst = new NetConf();
			servers.put(server, inst);
		}
		return inst;
	}

}
