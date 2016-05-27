package profile;

import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import profile.base.IPTables;
import profile.base.Net;
import profile.base.SSH;

public class Base extends AProfile {

	public Base() {
		super("base");
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();

		Net net = new Net();
		vec.addElement(net);

		IPTables ipt = new IPTables();
		vec.addElement(ipt);

		SSH ssh = new SSH();
		vec.addElement(ssh);

		return vec;
	}

	public static String getUpdate(String server, INetworkData data) {
		return getProperty("update", "base", server, data);
	}

	public static String getIface(String server, INetworkData data) {
		return getProperty("iface", "base", server, data);
	}

	public static String getPort(String server, INetworkData data) {
		return getProperty("port", "base", server, data);
	}

	public static String getDomain(String server, INetworkData data) {
		return getProperty("domain", "base", server, data);
	}

	public static String getHost(String server, INetworkData data) {
		return getProperty("host", "base", server, data);
	}

	public static String getIP(String server, INetworkData data) {
		return getProperty("ip", "base", server, data);
	}

	public static String getUser(String server, INetworkData data) {
		return getProperty("user", "base", server, data);
	}

	public static String getKeys(String server, INetworkData data) {
		try {
			String keys = getProperty("keys", "base", server, data);
			JSONParser parser = new JSONParser();
			JSONArray config = (JSONArray) parser.parse(keys);
			String keyfile = "";
			for (int i = 0; i < config.size(); i++) {
				keyfile += config.get(i) + "\n";
			}
			return keyfile.trim();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

}
