package unit.fs;

import core.unit.SimpleUnit;

public class FileContainsUnit extends SimpleUnit {

	public FileContainsUnit(String name, String precondition, String text, String path) {
		super(name, precondition, "echo handled by singleton;", "grep '" + text + "' " + path + ";", text, "pass");
	}

}
