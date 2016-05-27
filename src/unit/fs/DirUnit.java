package unit.fs;

import core.unit.SimpleUnit;

public class DirUnit extends SimpleUnit {

	public DirUnit(String name, String precondition, String dir) {
		super(name, precondition, "sudo mkdir " + dir + ";", "sudo [ -d " + dir + " ] && echo pass;", "pass", "pass");
	}

}
