package core.data;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import core.iface.INetworkData;

public class NetworkData extends AData implements INetworkData {

	private HashMap<String, ServerData> servers;

	private HashMap<String, DeviceData> devices;

	public NetworkData(String label) {
		super(label);
	}

	public void read(JSONObject config) {
		String include = (String) config.get("include");
		if (include != null) {
			try {
				String text = new String(Files.readAllBytes(Paths.get(include)), StandardCharsets.UTF_8);
				JSONParser parser = new JSONParser();
				JSONObject inc = (JSONObject) parser.parse(text);
				this.read(inc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			JSONObject jsonservers = (JSONObject) config.get("servers");
			servers = new HashMap<String, ServerData>();
			Iterator<?> serverIter = jsonservers.keySet().iterator();
			while (serverIter.hasNext()) {
				String key = (String) serverIter.next();
				ServerData net = new ServerData(key);
				net.read((JSONObject) jsonservers.get(key));
				servers.put(key, net);
			}

			JSONObject jsondevices = (JSONObject) config.get("devices");
			devices = new HashMap<String, DeviceData>();
			Iterator<?> deviceIter = jsondevices.keySet().iterator();
			while (deviceIter.hasNext()) {
				String key = (String) deviceIter.next();
				DeviceData net = new DeviceData(key);
				net.read((JSONObject) jsondevices.get(key));
				devices.put(key, net);
			}
		}
	}

	public String[] getServerLabels() {
		String[] labs = new String[servers.keySet().size()];
		Iterator<?> iter = servers.keySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			String key = (String) iter.next();
			labs[i] = key;
			i++;
		}
		return labs;
	}

	public String[] getDeviceLabels() {
		String[] labs = new String[devices.keySet().size()];
		Iterator<?> iter = devices.keySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			String key = (String) iter.next();
			labs[i] = key;
			i++;
		}
		return labs;
	}

	public String[] getDeviceMacs() {
		String[] labs = new String[devices.keySet().size()];
		Iterator<?> iter = devices.keySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			String key = (String) iter.next();
			labs[i] = devices.get(key).getMac();
			i++;
		}
		return labs;
	}

	public String[] getServerProfiles(String server) {
		return servers.get(server).getProfiles();
	}

	public String getServerData(String server, String label) {
		return servers.get(server).getData(label);
	}

	public String getServerUser(String server) {
		return servers.get(server).getUser();
	}

	public String getServerIP(String server) {
		return servers.get(server).getIP();
	}

	public String getServerPort(String server) {
		return servers.get(server).getPort();
	}

	public String getServerUpdate(String server) {
		return servers.get(server).getUpdate();
	}
}
