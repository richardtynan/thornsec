package unit.fs;

import core.unit.SimpleUnit;

public class DirPermsUnit extends SimpleUnit {

	public DirPermsUnit(String name, String precondition, String dir, String perms) {
		super(name, precondition, "sudo chmod -R " + perms + " " + dir + ";", "sudo stat -c %%a " + dir + ";", perms,
				"pass");
	}

}
