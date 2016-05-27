package core.data;

import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ServerData extends AData {

	private String user, ip, port, update;

	private String[] profiles;
	
	private HashMap<String, String> data;

	public ServerData(String label) {
		super(label);
	}

	public void read(JSONObject config) {
		JSONObject mgt = (JSONObject) config.get("mgt");
		this.user = (String) mgt.get("user");
		this.ip = (String) mgt.get("ip");
		this.port = (String) mgt.get("port");
		this.update = (String) mgt.get("update");
		JSONArray profilejson = (JSONArray) config.get("profiles");
		profiles = new String[profilejson.size()];
		for (int i = 0; i < profilejson.size(); i++) {
			profiles[i] = (String) profilejson.get(i);
		}
		this.data = new HashMap<>();
		Iterator<?> iter = config.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (!key.equals("label") && !key.equals("mgt") && !key.equals("profiles")) {
				data.put(key, config.get(key).toString());
			}
		}

	}

	public String[] getProfiles() {
		return profiles;
	}

	public String getData(String label) {
		return this.data.get(label);
	}

	public String getUser() {
		return user;
	}

	public String getIP() {
		return ip;
	}

	public String getPort() {
		return port;
	}

	public String getUpdate() {
		return update;
	}

}
