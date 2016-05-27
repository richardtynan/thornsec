package core.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import core.iface.IData;

public abstract class AData implements IData {

	private String label;

	public AData(String label) {
		this.label = label;
	}

	public void read(String data) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject config = (JSONObject) parser.parse(data);
			this.read(config);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String getLabel() {
		return label;
	}

	public abstract void read(JSONObject config);

}
