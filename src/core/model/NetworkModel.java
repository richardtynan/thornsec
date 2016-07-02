package core.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Vector;

import core.exec.ManageExec;
import core.iface.INetworkData;
import core.iface.IProfile;
import core.iface.IUnit;
import core.unit.SimpleUnit;

public class NetworkModel {

	private INetworkData data;

	private HashMap<String, Vector<IProfile>> rules;

	public void setData(INetworkData data) {
		this.data = data;
		rules = new HashMap<>();
		String[] serverLabs = data.getServerLabels();
		for (int i = 0; i < serverLabs.length; i++) {
			Vector<IProfile> serverRules = new Vector<IProfile>();
			String[] profiles = data.getServerProfiles(serverLabs[i]);
			for (int j = 0; j < profiles.length; j++) {
				try {
					IProfile profile = (IProfile) Class.forName("profile." + profiles[j]).newInstance();
					serverRules.add(profile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			this.expandProfiles(serverLabs[i], serverRules);
			this.expandSingletons(serverLabs[i], serverRules);
			rules.put(serverLabs[i], serverRules);
		}
	}

	private void expandProfiles(String server, Vector<IProfile> serverRules) {
		boolean allSimple = true;
		do {
			allSimple = true;
			for (int j = 0; j < serverRules.size(); j++) {
				if (!serverRules.elementAt(j).isSingleton()) {
					IProfile prof = serverRules.elementAt(j);
					Vector<IProfile> units = prof.getUnits(server, data);
					if (units != null) {
						serverRules.removeElementAt(j);
						serverRules.addAll(j, units);
						j = serverRules.size();
						allSimple = false;
					}
				}
			}
		} while (!allSimple);
	}

	private void expandSingletons(String server, Vector<IProfile> rules) {
		boolean allSimple = true;
		do {
			allSimple = true;
			for (int j = 0; j < rules.size(); j++) {
				if (rules.elementAt(j).isSingleton()) {
					IProfile prof = rules.elementAt(j);
					Vector<IProfile> units = prof.getUnits(server, data);
					if (units != null) {
						rules.removeElementAt(j);
						rules.addAll(j, units);
						j += units.size() - 1;
						allSimple = false;
					}
				}
			}
		} while (!allSimple);
	}

	public String getLabel() {
		return data.getLabel();
	}

	public String[] getServerLabels() {
		return data.getServerLabels();
	}

	public String[] getDeviceLabels() {
		return data.getDeviceLabels();
	}

	public String[] getDeviceMacs() {
		return data.getDeviceMacs();
	}

	public String[] getServerProfiles(String server) {
		return data.getServerProfiles(server);
	}

	public int getUnitCount(String server) {
		return this.rules.get(server).size();
	}
	
	public void auditDummy(String server, OutputStream out, InputStream in) {
		String line = getAction("audit", server);
		System.out.println(line);
	}

	public void dryrunDummy(String server, OutputStream out, InputStream in) {
		String line = getAction("dryrun", server);
		System.out.println(line);
	}

	public void configDummy(String server, OutputStream out, InputStream in) {
		String line = getAction("config", server);
		System.out.println(line);
	}

	public void auditServer(String server, OutputStream out, InputStream in) {
		String line = getAction("audit", server);
		ManageExec exec = new ManageExec("testlogin", data.getServerUser(server), data.getServerIP(server),
				data.getServerPort(server), line, out);
		exec.manage();
	}

	public void dryrunServer(String server, OutputStream out, InputStream in) {
		String line = getAction("dryrun", server);
		ManageExec exec = new ManageExec("testlogin", data.getServerUser(server), data.getServerIP(server),
				data.getServerPort(server), line, out);
		exec.manage();
	}

	public void configServer(String server, OutputStream out, InputStream in) {
		String line = getAction("config", server);
		ManageExec exec = new ManageExec("testlogin", data.getServerUser(server), data.getServerIP(server),
				data.getServerPort(server), line, out);
		exec.manage();
	}

	public void auditServerBlock(String server, OutputStream out, InputStream in) {
		String line = getAction("audit", server);
		System.out.println(line);
		ManageExec exec = new ManageExec("testlogin", data.getServerUser(server), data.getServerIP(server),
				data.getServerPort(server), line, out);
		exec.manageBlock();
	}

	public void dryrunServerBlock(String server, OutputStream out, InputStream in) {
		String line = getAction("dryrun", server);
		//System.out.println(line);
		ManageExec exec = new ManageExec("testlogin", data.getServerUser(server), data.getServerIP(server),
				data.getServerPort(server), line, out);
		exec.manageBlock();
	}

	public void configServerBlock(String server, OutputStream out, InputStream in) {
		String line = getAction("config", server);
		//System.out.println(line);
		ManageExec exec = new ManageExec("testlogin", data.getServerUser(server), data.getServerIP(server),
				data.getServerPort(server), line, out);
		exec.manageBlock();
	}
	
	private String getAction(String action, String server) {
		System.out.println("=======================" + this.getLabel() + ":" + server + "==========================");
		String line = this.getHeader(server, action) + "\n";
		Vector<IProfile> serverRules = this.rules.get(server);
		
		String configcmd = "";
		if (data.getServerUpdate(server).equals("true")) {
			configcmd = "sudo apt-get --assume-yes upgrade;";
		} else {
			configcmd = "echo \"$out\";";
		}
		serverRules.insertElementAt(new SimpleUnit("update", "proceed", configcmd,
				"sudo apt-get update > /dev/null; sudo apt-get --assume-no upgrade | grep \"0 upgraded, 0 newly installed, 0 to remove and 0 not upgraded.\";",
				"0 upgraded, 0 newly installed, 0 to remove and 0 not upgraded.", "pass"), 0);
		
		for (int i = 0; i < serverRules.size(); i++) {
			IUnit unit = (IUnit) serverRules.elementAt(i);
			System.out.println(unit.getLabel());
			line += "#============ " + serverRules.elementAt(i).getLabel() + " =============\n";
			line += getText(action, unit) + "\n";
		}
		line += this.getFooter(server, action);
		return line;
	}
	
	private String getText(String action, IUnit unit) {
		String line = "";
		if (action.equals("audit")) {
			line = unit.genAudit();
		} else if (action.equals("config")) {
			line = unit.genConfig();
		} else if (action.equals("dryrun")) {
			line = unit.genDryRun();
		}
		return line;
	}

	private String getHeader(String server, String action) {
		String line = "#!/bin/bash\n";
		line += "\n";
		line += "hostname=$(hostname);\n";
		line += "proceed=1;\n";
		line += "\n";
		line += "echo \"Started " + action + " $hostname with config label: " + server + "\"\n";
		line += "pass=0; fail=0; fail_string=;";
		return line;
	}

	private String getFooter(String server, String action) {
		String line = "echo \"pass=$pass fail=$fail\"\n";
		line += "echo \"failed=$fail_string\"\n";
		line += "\n";
		line += "echo \"Finished " + action + " $hostname with config label: " + server + "\"";
		return line;
	}

}
