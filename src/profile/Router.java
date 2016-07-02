package profile;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import core.unit.SimpleUnit;
import profile.router.DHCP;
import profile.router.DNS;
import profile.router.PKI;
import profile.router.Subnets;
import singleton.IPTablesConf;

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
		vec.addElement(
				IPTablesConf.getInstance(server, data.getLabel()).addNatPostrouting("router_nat", "-j MASQUERADE"));

		// Subnets
		Subnets subnets = new Subnets();
		vec.addAll(subnets.getUnits(server, data));

		// DHCP
		DHCP dhcp = new DHCP();
		vec.addAll(dhcp.getUnits(server, data));

		// DNS
		DNS dns = new DNS();
		vec.addAll(dns.getUnits(server, data));

		// PKI
		PKI pki = new PKI();
		vec.addAll(pki.getUnits(server, data));
		
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterOutput("base_debian1", "-o " + Router.getExtIface(server, data) + " -d 78.129.164.123 -p tcp --dport 80 -j ACCEPT"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterOutput("base_debian2", "-o " + Router.getExtIface(server, data) + " -d 212.211.132.250 -p tcp --dport 80 -j ACCEPT"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterOutput("base_debian3", "-o " + Router.getExtIface(server, data) + " -d 212.211.132.32 -p tcp --dport 80 -j ACCEPT"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterOutput("base_debian4", "-o " + Router.getExtIface(server, data) + " -d 195.20.242.89 -p tcp --dport 80 -j ACCEPT"));

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
	
	public static String getDNS(String server, INetworkData data) {
		return getProperty("dns", "router", server, data);
	}
	
	public static String getExtDHCP(String server, INetworkData data) {
		return getProperty("extdhcp", "router", server, data);
	}

}
