package singleton;

import java.util.Hashtable;
import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import core.unit.SimpleUnit;
import unit.fs.FileContainsUnit;

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

	public FileContainsUnit setPrimaryIface(String name, String iface, String rule) {
		String net = "auto " + iface + "\n";
		net += "iface " + iface + " inet static\n";
		net += "pre-up iptables-restore < /etc/iptables/iptables.conf\n";
		net += rule;
		strings.add(0, net);
		return new FileContainsUnit(name, "proceed", net, "/etc/network/interfaces");
	}

	public FileContainsUnit addStaticIface(String name, String iface, String rule) {
		String net = "auto " + iface + "\n";
		net += "iface " + iface + " inet static\n";
		net += rule;
		strings.add(net);
		return new FileContainsUnit(name, "proceed", net, "/etc/network/interfaces");
	}

	public FileContainsUnit addDynamicIface(String name, String iface) {
		String net = "auto " + iface + "\n";
		net += "iface " + iface + " inet dhcp";
		strings.add(net);
		return new FileContainsUnit(name, "proceed", net, "/etc/network/interfaces");
	}

	public FileContainsUnit addPPPIface(String name, String iface) {
		String net = "auto " + iface + "\n";
		net += "iface " + iface + " inet ppp";
		strings.add(net);
		return new FileContainsUnit(name, "proceed", net, "/etc/network/interfaces");
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
