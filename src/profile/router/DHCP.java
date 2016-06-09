package profile.router;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AStructuredProfile;
import core.profile.CompoundProfile;
import profile.Base;
import profile.Router;
import singleton.IPTablesConf;
import unit.fs.ChildFileUnit;
import unit.pkg.InstalledUnit;
import unit.pkg.RunningUnit;

public class DHCP extends AStructuredProfile {

	public DHCP() {
		super("dhcp");
	}

	public Vector<IProfile> getInstalled(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new InstalledUnit("dhcp", "isc-dhcp-server"));
		return vec;
	}

	public Vector<IProfile> getPersistent(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();

		CompoundProfile rule = new CompoundProfile("dhcp_persist", "proceed",
				"sudo systemctl restart isc-dhcp-server;");

		String defiface = "INTERFACES=\\\"" + Router.getIntIface(server, data) + "\\\"";
		rule.addChild(new ChildFileUnit("dhcp_persist", "dhcp_defiface", "dhcp_installed", defiface,
				"/etc/default/isc-dhcp-server"));

		String dhcpconf = "ddns-update-style none;\n";
		dhcpconf += "option domain-name \\\"" + profile.Base.getDomain(server, data) + "\\\";\n";
		dhcpconf += "option domain-name-servers " + Base.getIP(server, data) + ";\n";
		dhcpconf += "default-lease-time 600;\n";
		dhcpconf += "max-lease-time 7200;\n";
		dhcpconf += "authoritative;\n";
		dhcpconf += "log-facility local7;\n";
		dhcpconf += "\n";
		dhcpconf += "shared-network sharednet {\n";
		dhcpconf += "subnet " + Router.getSubnet(server, data) + ".0 netmask 255.255.255.0 {\n";
		dhcpconf += "}\n";
		String[] devs = data.getDeviceLabels();
		String[] macs = data.getDeviceMacs();
		int start = 100;
		for (int i = 0; i < devs.length; i++) {
			int subnet = start + i;
			dhcpconf += "subnet " + Router.getNet(server, data) + "." + subnet + ".0 netmask 255.255.255.0 {\n";
			dhcpconf += "host " + devs[i] + " {\n";
			dhcpconf += "hardware ethernet " + macs[i] + ";\n";
			dhcpconf += "fixed-address " + Router.getNet(server, data) + "." + subnet + ".2" + ";\n";
			dhcpconf += "option routers " + Router.getNet(server, data) + "." + subnet + ".1" + ";\n";
			dhcpconf += "}\n";
			dhcpconf += "}\n";
		}
		dhcpconf += "}";
		rule.addChild(
				new ChildFileUnit("dhcp_persist", "dhcp_conf", "dhcp_installed", dhcpconf, "/etc/dhcp/dhcpd.conf"));

		vec.addAll(rule.getUnits(server, data));
		return vec;
	}

	public Vector<IProfile> getIpt(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterInput("dhcp_ipt_in", "-i " + Router.getIntIface(server, data) + " -p udp --dport 67 -j ACCEPT"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterOutput("dhcp_ipt_in", "-o " + Router.getIntIface(server, data) + " -p udp --sport 67 -j ACCEPT"));
		return vec;
	}

	public Vector<IProfile> getLive(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new RunningUnit("dhcp", "isc-dhcp-server", "dhcpd"));
		return vec;
	}

}
