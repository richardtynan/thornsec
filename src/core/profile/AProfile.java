package core.profile;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import core.iface.INetworkData;
import core.iface.IProfile;

public abstract class AProfile implements IProfile {

	protected String name;

	protected AProfile(String name) {
		this.name = name;
	}

	public String getLabel() {
		return name;
	}

	public boolean isSingleton() {
		return false;
	}

	public static String getProperty(String prop, String prof, String server, INetworkData data) {
		try {
			String val = data.getServerData(server, prof);
			JSONParser parser = new JSONParser();
			JSONObject config = (JSONObject) parser.parse(val);
			return config.get(prop).toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

}
