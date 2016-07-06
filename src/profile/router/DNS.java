package profile.router;

import java.util.StringTokenizer;
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

public class DNS extends AStructuredProfile {

	public DNS() {
		super("dns");
	}

	public Vector<IProfile> getInstalled(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new InstalledUnit("dns", "bind9"));
		return vec;
	}

	public Vector<IProfile> getPersistent(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		CompoundProfile rule = new CompoundProfile("dns_persist", "proceed", "sudo systemctl restart bind9;");
		vec.addElement(rule);

		String options = "options {\n";
		options += "directory \\\"/var/cache/bind\\\";\n";
		options += "dnssec-validation auto;\n";
		options += "auth-nxdomain no;\n";
		options += "listen-on { " + Base.getIP(server, data) + "; };\n";
		options += "listen-on-v6 { none; };\n";
		options += "forwarders {\n";
		options += Router.getDNS(server, data) + "\n";
		options += "};\n";
		options += "};\n";
		options += "controls {};";
		rule.addChild(new ChildFileUnit("dns_persist", "dns_options_persist", "dns_installed", options,
				"/etc/bind/named.conf.options"));

		String local = "zone \\\"" + Base.getDomain(server, data) + "\\\" {\n";
		local += "type master;\n";
		local += "file \\\"/etc/bind/db." + Base.getDomain(server, data) + "\\\";\n";
		local += "};\n";
		local += "\n";
		local += "zone \\\"" + Router.getNetrev(server, data) + ".in-addr.arpa\\\" {\n";
		local += "type master;\n";
		local += "file \\\"/etc/bind/db." + Base.getDomain(server, data) + ".rev\\\";\n";
		local += "};\n";
		local += "\n";
		local += "zone \\\"" + Router.getPKI(server, data) + "\\\" {\n";
		local += "type master;\n";
		local += "file \\\"/etc/bind/db." + Router.getPKI(server, data) + "\\\";\n";
		local += "};";
		rule.addChild(new ChildFileUnit("dns_persist", "dns_local_persist", "dns_installed", local,
				"/etc/bind/named.conf.local"));

		// todo: modify serial number
		String common = "\\$TTL 604800\n";
		common += "@ IN SOA ns." + Base.getDomain(server, data) + ". admin." + Base.getDomain(server, data) + ". (\n";
		common += "3 ; Serial\n";
		common += "604800 ; Refresh\n";
		common += "86400 ; Retry\n";
		common += "2419200 ; Expire\n";
		common += "604800 )   ; Negative Cache TTL\n";
		common += "@ IN NS ns." + Base.getDomain(server, data) + ".\n";

		String fwd = common + "ns." + Base.getDomain(server, data) + ". IN A " + Base.getIP(server, data) + "\n";
		fwd += Base.getHost(server, data) + " IN A " + Base.getIP(server, data) + "\n";
		fwd += "pki IN A " + Base.getIP(server, data) + "\n";
		String rev = common;
		
		String[] servs = data.getServerLabels();
		for (int i = 0; i < servs.length; i++) {
			if (!servs[i].equals(server)) {
				fwd += servs[i] + " IN A " + Base.getIP(servs[i], data) + "\n";
				rev += Base.getIP(servs[i], data) + " IN PTR " + servs[i] + "."
					+ Base.getDomain(server, data) + ".\n";
			}
		}
		
		String[] devs = data.getDeviceLabels();
		int start = 100;
		for (int i = 0; i < devs.length; i++) {
			int subnet = start + i;
			fwd += devs[i] + " IN A " + Router.getNet(server, data) + "." + subnet + ".2\n";
			rev += Router.getNet(server, data) + "." + subnet + ".2 IN PTR " + devs[i] + "."
					+ Base.getDomain(server, data) + ".\n";
		}
		
		fwd += Router.getFwd(server, data);
		
		rule.addChild(new ChildFileUnit("dns_persist", "dns_fwd_persist", "dns_installed", fwd.trim(),
				"/etc/bind/db." + Base.getDomain(server, data)));
		rule.addChild(new ChildFileUnit("dns_persist", "dns_rev_persist", "dns_installed", rev.trim(),
				"/etc/bind/db." + Base.getDomain(server, data) + ".rev"));

		String crl = "\\$TTL 604800\n";
		crl += "@ IN SOA ns." + Router.getPKI(server, data) + ". admin." + Router.getPKI(server, data) + ". (\n";
		crl += "3 ; Serial\n";
		crl += "604800 ; Refresh\n";
		crl += "86400 ; Retry\n";
		crl += "2419200 ; Expire\n";
		crl += "604800 )   ; Negative Cache TTL\n";
		crl += "@ IN NS ns." + Router.getPKI(server, data) + ".\n";
		crl += "ns." + Router.getPKI(server, data) + ". IN A " + Base.getIP(server, data) + "\n";
		crl += "pki IN A " + Base.getIP(server, data);
		rule.addChild(new ChildFileUnit("dns_persist", "dns_crl_persist", "dns_installed", crl,
				"/etc/bind/db." + Router.getPKI(server, data)));
		return vec;
	}

	public Vector<IProfile> getIpt(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();

		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterInput("dns_ipt_in_udp",
				"-i " + Router.getIntIface(server, data) + " -p udp --dport 53 -j ACCEPT"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterOutput("dns_ipt_out_udp",
				"-o " + Router.getIntIface(server, data) + " -p udp --sport 53 -j ACCEPT"));
		
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterInput("dns_ipt_in_tcp",
				"-i " + Router.getIntIface(server, data) + " -p tcp --dport 53 -j ACCEPT"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterOutput("dns_ipt_out_tcp",
				"-o " + Router.getIntIface(server, data) + " -p tcp --sport 53 -j ACCEPT"));

		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addChain("dns_ipt_chain", "filter", "dnsd"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter("dns_ext", "dnsd", "-j DROP"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter("dns_ext_log", "dnsd",
				"-j LOG --log-prefix \\\"ipt-dnsd: \\\""));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterInput("dns_ext_in",
				"-i " + Router.getExtIface(server, data) + " -p udp --sport 53 -j dnsd"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilterOutput("dns_ext_out",
				"-o " + Router.getExtIface(server, data) + " -p udp --dport 53 -j dnsd"));

		int count = 1;
		StringTokenizer str = new StringTokenizer(Router.getDNS(server, data));
		while (str.hasMoreTokens()) {
			String ip = str.nextToken(";");
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter("dns_ext_server_in_" + count,
					"dnsd", "-i " + Router.getExtIface(server, data) + " -s " + ip + " -p udp --sport 53 -j ACCEPT"));
			vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter("dns_ext_server_out_" + count,
					"dnsd", "-o " + Router.getExtIface(server, data) + " -d " + ip + " -p udp --dport 53 -j ACCEPT"));
			count++;
		}

		return vec;
	}

	public Vector<IProfile> getLive(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new RunningUnit("dns", "bind9", "named"));
		return vec;
	}

}
