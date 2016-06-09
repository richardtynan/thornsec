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

public class PKI extends AStructuredProfile {

	public PKI() {
		super("pki");
	}

	public Vector<IProfile> getInstalled(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new InstalledUnit("nginx", "nginx-light"));
		return vec;
	}

	public Vector<IProfile> getPersistent(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();

		CompoundProfile rule = new CompoundProfile("nginx_persist", "proceed", "sudo systemctl restart nginx;");
		vec.addElement(rule);

		String nginxconf = "server {\n";
		nginxconf += "listen " + Base.getIP(server, data) + ":80;\n";
		nginxconf += "root /var/www/html;\n";
		nginxconf += "index index.html;\n";
		nginxconf += "location / {\n";
		nginxconf += "autoindex on;\n";
		nginxconf += "}\n";
		nginxconf += "server_name pki." + Base.getDomain(server, data) + ";\n";
		nginxconf += "}";
		rule.addChild(new ChildFileUnit("nginx_persist", "nginx_conf_persist", "nginx_installed", nginxconf,
				"/etc/nginx/sites-enabled/default"));

		return vec;
	}

	public Vector<IProfile> getIpt(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();

		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterInput("pki_ipt_in", "-i " + Router.getIntIface(server, data) + " -p tcp --dport 80 -j ACCEPT"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel())
				.addFilterOutput("pki_ipt_out", "-o " + Router.getIntIface(server, data) + " -p tcp --sport 80 -j ACCEPT"));

		return vec;
	}

	public Vector<IProfile> getLive(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new RunningUnit("nginx", "nginx-light", "nginx"));
		return vec;
	}

}
