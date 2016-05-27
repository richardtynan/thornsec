package core.data;

import org.json.simple.JSONObject;

public class DeviceData extends AData {

	private String mac;

	public DeviceData(String label) {
		super(label);
	}
	
	public void read(JSONObject config) {
		mac = (String) config.get("mac");
	}

	public String getMac() {
		return mac;
	}

}
