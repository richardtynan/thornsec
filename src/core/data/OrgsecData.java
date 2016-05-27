package core.data;

import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;

public class OrgsecData extends AData {

	private HashMap<String, NetworkData> networks;

	public OrgsecData() {
		super("orgsec");
	}
	
	public void read(JSONObject nets) {
		networks = new HashMap<String, NetworkData>();
		Iterator<?> iter = nets.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			NetworkData net = new NetworkData(key);
			net.read(nets.get(key).toString());
			networks.put(key, net);
		}
	}

	public String[] getNetworkLabels() {
		String[] labs = new String[networks.keySet().size()];
		Iterator<?> iter = networks.keySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			String key = (String) iter.next();
			labs[i] = key;
			i++;
		}
		return labs;
	}

	public NetworkData getNetworkData(String label) {
		return networks.get(label);
	}

}
