package singleton;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import core.unit.SimpleUnit;

public class IPTablesConf extends AProfile {

	Hashtable<String, Hashtable<String, Vector<String>>> tables;

	public SimpleUnit addFilterInput(String name, String rule) {
		return add(name, "filter", "INPUT", rule);
	}

	public SimpleUnit addFilterForward(String name, String rule) {
		return add(name, "filter", "FORWARD", rule);
	}

	public SimpleUnit addFilterOutput(String name, String rule) {
		return add(name, "filter", "OUTPUT", rule);
	}

	public SimpleUnit addFilter(String name, String chain, String rule) {
		return add(name, "filter", chain, rule);
	}

	public SimpleUnit addNatPostrouting(String name, String rule) {
		return add(name, "nat", "POSTROUTING", rule);
	}

	private SimpleUnit add(String name, String table, String chain, String rule) {
		System.out.println("add " + name + " " + table + " " + chain + " " + rule);
		this.getChain(table, chain).add(rule);
		return new SimpleUnit(name, "proceed", "echo \\\"handled by singleton\\\";",
				"cat /etc/iptables/iptables.conf | iptables-xml | " + "xsltproc --stringparam table " + table
						+ " /etc/iptables/iptables.xslt - | " + "grep " + chain + " | grep \""
						+ rule.replaceAll("-", "\\\\-") + "\"",
				"-A " + chain + " " + rule, "pass");
	}

	private String getPersistent() {
		String ipt = "*nat\n";
		ipt += ":PREROUTING ACCEPT [0:0]\n";
		ipt += ":INPUT ACCEPT [0:0]\n";
		ipt += ":OUTPUT ACCEPT [0:0]\n";
		ipt += ":POSTROUTING ACCEPT [0:0]\n";
		ipt += getNat();
		ipt += "COMMIT\n";
		ipt += "*filter\n";
		ipt += getFilter();
		ipt += "COMMIT";
		return ipt;
	}

	private String getNat() {
		Vector<String> chain = this.getChain("nat", "POSTROUTING");
		String nat = "";
		for (int i = 0; i < chain.size(); i++) {
			nat += "-A POSTROUTING " + chain.elementAt(chain.size() - 1 - i) + "\n";
		}
		return nat;
	}

	private String getFilter() {
		String policy = "";
		String filter = "";
		policy += ":INPUT ACCEPT [0:0]\n";
		policy += ":FORWARD ACCEPT [0:0]\n";
		policy += ":OUTPUT ACCEPT [0:0]\n";
		Hashtable<String, Vector<String>> tab = tables.get("filter");
		Iterator<String> iter = tab.keySet().iterator();
		while (iter.hasNext()) {
			String val = iter.next();
			if (!val.equals("INPUT") && !val.equals("FORWARD") && !val.equals("OUTPUT"))
				policy += ":" + val + " - [0:0]\n";
			Vector<String> chain = this.getChain("filter", val);
			for (int j = 0; j < chain.size(); j++) {
				filter += "-A " + val + " " + chain.elementAt(chain.size() - j - 1) + "\n";
			}
		}
		return policy + filter;
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new SimpleUnit("iptables_conf_persist", "iptables_dir",
				"echo \"" + getPersistent() + "\" | sudo tee /etc/iptables/iptables.conf;",
				"cat /etc/iptables/iptables.conf;", getPersistent(), "pass"));
		return vec;
	}

	private Vector<String> getChain(String table, String chain) {
		Hashtable<String, Vector<String>> tab = tables.get(table);
		if (tab == null) {
			tables.put(table, new Hashtable<>());
		}
		tab = tables.get(table);
		Vector<String> ch = tab.get(chain);
		if (ch == null) {
			tab.put(chain, new Vector<>());
		}
		return tables.get(table).get(chain);
	}

	public boolean isSingleton() {
		return true;
	}

	private IPTablesConf() {
		super("iptables_conf");
		this.tables = new Hashtable<>();
	}

	public static IPTablesConf getInstance(String server, String network) {
		if (networks == null) {
			networks = new Hashtable<>();
		}
		Hashtable<String, IPTablesConf> servers = networks.get(network);
		if (servers == null) {
			servers = new Hashtable<>();
			networks.put(network, servers);
		}
		IPTablesConf inst = servers.get(server);
		if (inst == null) {
			inst = new IPTablesConf();
			servers.put(server, inst);
		}
		return inst;
	}

	private static Hashtable<String, Hashtable<String, IPTablesConf>> networks;

}
