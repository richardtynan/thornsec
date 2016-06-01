package profile.router;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import profile.Router;
import singleton.NetConf;

public class ExtDHClient extends AProfile {

	public ExtDHClient() {
		super("router_ext_dhcp");
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(NetConf.getInstance(server, data.getLabel()).addDynamicIface("router_ext_dhcp_iface",
				Router.getExtIface(server, data)));
		/*
		 * IPTablesConf.getInstance(server,
		 * data.getLabel()).addFilter("dhclient", "-j ACCEPT");
		 * IPTablesConf.getInstance(server,
		 * data.getLabel()).addFilter("dhclient",
		 * "-j LOG --log-prefix \\\"ipt-dhclient: \\\"");
		 * IPTablesConf.getInstance(server, data.getLabel()) .addFilterInput(
		 * "-i " + Router.getExtIface(server, data) +
		 * " -p udp --sport 67 -j dhclient"); IPTablesConf.getInstance(server,
		 * data.getLabel()) .addFilterOutput("-o " + Router.getExtIface(server,
		 * data) + " -p udp --dport 67 -j dhclient");
		 * 
		 * IPTablesConf.getInstance(server,
		 * data.getLabel()).addFilter("dhclient", "-i " +
		 * Router.getExtIface(server, data) +
		 * " -s 10.240.96.1 -p udp --sport 67 --dport 68 -j ACCEPT");
		 */
		return vec;
	}

}
