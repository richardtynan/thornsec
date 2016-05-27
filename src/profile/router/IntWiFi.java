package profile.router;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.CompoundProfile;
import core.unit.SimpleUnit;
import profile.Router;
import unit.fs.ChildFileUnit;
import unit.pkg.InstalledUnit;
import unit.pkg.RunningUnit;

public class IntWiFi extends SimpleUnit {

	public String getLabel() {
		return "router_int_wifi";
	}

	public Vector<IProfile> getInstalled(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new InstalledUnit("hostapd", "hostapd"));
		return vec;
	}

	public Vector<IProfile> getPersistent(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		CompoundProfile rule = new CompoundProfile("hostapd_persist", "proceed", "echo restart hostapd manually;");
		String defconf = "DAEMON_CONF=\\\"/etc/hostapd/hostapd.conf\\\"";
		rule.addChild(new ChildFileUnit("hostapd_persist", "hostapd_defconf", "hostapd_installed", defconf,
				"/etc/default/hostapd"));

		String conf = "interface=" + Router.getIntIface(server, model) + "\n";
		conf += "driver=nl80211\n";
		conf += "channel=11\n";
		conf += "country_code=US\n";
		conf += "ssid=tempssid\n";
		conf += "hw_mode=g\n";
		conf += "wpa=2\n";
		conf += "wpa_passphrase=justAplaceholder\n";
		conf += "wpa_key_mgmt=WPA-PSK-SHA256";
		rule.addChild(new ChildFileUnit("hostapd_persist", "hostapd_conf", "hostapd_installed", conf,
				"/etc/hostapd/hostapd.conf"));
		vec.addElement(rule);

		return vec;
	}

	public Vector<IProfile> getIpt(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		return vec;
	}

	public Vector<IProfile> getLive(String server, INetworkData model) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new RunningUnit("hostapd", "hostapd", "hostapd"));
		return vec;
	}

}
