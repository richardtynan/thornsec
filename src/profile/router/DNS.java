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

public class DNS extends AStructuredProfile {

	public DNS() {
		super("dns");
	}

	public Vector<IProfile> getInstalled(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new InstalledUnit("dns", "bind9"));
		return vec;
	}

	public Vector<IProfile> getPersistent(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		CompoundProfile rule = new CompoundProfile("dns_persist", "proceed", "sudo systemctl restart bind9;");
		vec.addElement(rule);

		String options = "options {\n";
		options += "directory \\\"/var/cache/bind\\\";\n";
		options += "dnssec-validation auto;\n";
		options += "auth-nxdomain no;\n";
		options += "listen-on { " + Base.getIP(server, model) + "; };\n";
		options += "listen-on-v6 { none; };\n";
		options += "forwarders {\n";
		options += "8.8.8.8;\n";
		options += "};\n";
		options += "};\n";
		options += "controls {};";
		rule.addChild(new ChildFileUnit("dns_persist", "dns_options_persist", "dns_installed", options,
				"/etc/bind/named.conf.options"));

		String local = "zone \\\"" + Base.getDomain(server, model) + "\\\" {\n";
		local += "type master;\n";
		local += "file \\\"/etc/bind/db." + Base.getDomain(server, model) + "\\\";\n";
		local += "};\n";
		local += "\n";
		local += "zone \\\"" + Router.getNetrev(server, model) + ".in-addr.arpa\\\" {\n";
		local += "type master;\n";
		local += "file \\\"/etc/bind/db." + Base.getDomain(server, model) + ".rev\\\";\n";
		local += "};\n";
		local += "\n";
		local += "zone \\\"" + Router.getPKI(server, model) + "\\\" {\n";
		local += "type master;\n";
		local += "file \\\"/etc/bind/db." + Router.getPKI(server, model) + "\\\";\n";
		local += "};";
		rule.addChild(new ChildFileUnit("dns_persist", "dns_local_persist", "dns_installed", local,
				"/etc/bind/named.conf.local"));

		// todo: modify serial number
		String common = "\\$TTL 604800\n";
		common += "@ IN SOA ns." + Base.getDomain(server, model) + ". admin." + Base.getDomain(server, model) + ". (\n";
		common += "3 ; Serial\n";
		common += "604800 ; Refresh\n";
		common += "86400 ; Retry\n";
		common += "2419200 ; Expire\n";
		common += "604800 )   ; Negative Cache TTL\n";
		common += "@ IN NS ns." + Base.getDomain(server, model) + ".\n";

		String fwd = common + "ns." + Base.getDomain(server, model) + ". IN A " + Base.getIP(server, model) + "\n";
		fwd += Base.getHost(server, model) + " IN A " + Base.getIP(server, model) + "\n";
		fwd += "pki IN A " + Base.getIP(server, model) + "\n";
		String rev = common;
		String[] devs = model.getDeviceLabels();
		int start = 100;
		for (int i = 0; i < devs.length; i++) {
			int subnet = start + i;
			fwd += devs[i] + " IN A " + Router.getNet(server, model) + "." + subnet + ".2\n";
			rev += Router.getNet(server, model) + "." + subnet + ".2 IN PTR " + devs[i] + "."
					+ Base.getDomain(server, model) + ".\n";
		}

		rule.addChild(new ChildFileUnit("dns_persist", "dns_fwd_persist", "dns_installed", fwd.trim(),
				"/etc/bind/db." + Base.getDomain(server, model)));
		rule.addChild(new ChildFileUnit("dns_persist", "dns_rev_persist", "dns_installed", rev.trim(),
				"/etc/bind/db." + Base.getDomain(server, model) + ".rev"));

		String crl = "\\$TTL 604800\n";
		crl += "@ IN SOA ns." + Router.getPKI(server, model) + ". admin." + Router.getPKI(server, model) + ". (\n";
		crl += "3 ; Serial\n";
		crl += "604800 ; Refresh\n";
		crl += "86400 ; Retry\n";
		crl += "2419200 ; Expire\n";
		crl += "604800 )   ; Negative Cache TTL\n";
		crl += "@ IN NS ns." + Router.getPKI(server, model) + ".\n";
		crl += "ns." + Router.getPKI(server, model) + ". IN A " + Base.getIP(server, model) + "\n";
		crl += "pki IN A " + Base.getIP(server, model);
		rule.addChild(new ChildFileUnit("dns_persist", "dns_crl_persist", "dns_installed", crl,
				"/etc/bind/db." + Router.getPKI(server, model)));
		return vec;
	}

	public Vector<IProfile> getIpt(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterInput("dns_ipt_in", "-i " + Router.getIntIface(server, data) + " -p udp --dport 53 -j ACCEPT"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterOutput("dns_ipt_out", "-o " + Router.getIntIface(server, data) + " -p udp --sport 53 -j ACCEPT"));

		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter("dns_ext", "dnsd", "-j ACCEPT"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()).addFilter("dns_ext_log", "dnsd", "-j LOG --log-prefix \\\"ipt-dnsd: \\\""));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterInput("dns_ext_in", "-i " + Router.getExtIface(server, data) + " -p udp --sport 53 -j dnsd"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterOutput("dns_ext_out", "-o " + Router.getExtIface(server, data) + " -p udp --dport 53 -j dnsd"));
		
		return vec;
	}

	public Vector<IProfile> getLive(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new RunningUnit("dns", "bind9", "named"));
		return vec;
	}

}
