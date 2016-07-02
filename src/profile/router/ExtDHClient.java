package profile.router;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import profile.Router;
import singleton.IPTablesConf;
import singleton.NetConf;
import unit.fs.FileUnit;

public class ExtDHClient extends AProfile {

	public ExtDHClient() {
		super("router_ext_dhcp");
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(NetConf.getInstance(server, data.getLabel()).addDynamicIface("router_ext_dhcp_iface",
				Router.getExtIface(server, data)));

		String dhclient = "option rfc3442-classless-static-routes code 121 = array of unsigned integer 8;\n";
		dhclient += "send host-name = gethostname();\n";
		dhclient += "supersede domain-name-servers 8.8.8.8, 8.8.4.4;\n";
		dhclient += "request subnet-mask, broadcast-address, time-offset, routers,\n";
		dhclient += "	domain-name, domain-name-servers, domain-search, host-name,\n";
		dhclient += "	dhcp6.name-servers, dhcp6.domain-search,\n";
		dhclient += "	netbios-name-servers, netbios-scope, interface-mtu,\n";
		dhclient += "	rfc3442-classless-static-routes, ntp-servers;";
		vec.addElement(new FileUnit("router_ext_dhcp_persist", "proceed", dhclient, "/etc/dhcp/dhclient.conf"));

		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterInput("router_ext_dhcp_ipt_in", "-i "
				+ Router.getExtIface(server, data) + " -d 255.255.255.255 -p udp --dport 68 --sport 67 -j ACCEPT"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterOutput("router_ext_dhcp_ipt_out",
				"-o " + Router.getExtIface(server, data) + " -d " + Router.getExtDHCP(server, data)
						+ " -p udp --dport 67 --sport 68 -j ACCEPT"));

		return vec;
	}

}
