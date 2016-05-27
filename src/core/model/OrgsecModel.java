package core.model;

import java.util.HashMap;

import core.data.OrgsecData;

public class OrgsecModel {

	private OrgsecData data;

	private HashMap<String, NetworkModel> networks;

	public void setData(OrgsecData data) {
		this.data = data;
		networks = new HashMap<String, NetworkModel>();
		String[] netlabs = data.getNetworkLabels();
		for (int i = 0; i < netlabs.length; i++) {
			NetworkModel mod = new NetworkModel();
			mod.setData(data.getNetworkData(netlabs[i]));
			networks.put(netlabs[i], mod);
		}
	}

	public String[] getNetworkLabels() {
		return data.getNetworkLabels();
	}

	public NetworkModel getNetworkModel(String label) {
		return networks.get(label);
	}

}
