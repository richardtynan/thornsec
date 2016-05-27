package unit.fs;

import core.unit.SimpleUnit;

public class DirOwnUnit extends SimpleUnit {

	public DirOwnUnit(String name, String precondition, String dir, String user) {
		super(name, precondition, "sudo chown -R " + user + ":" + user + " " + dir + ";",
				"sudo stat -c %%U " + dir + ";", user, "pass");
	}

}
