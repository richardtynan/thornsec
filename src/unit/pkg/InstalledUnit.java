package unit.pkg;

import core.unit.SimpleUnit;

public class InstalledUnit extends SimpleUnit {

	public InstalledUnit(String name, String pkg) {
		super(name + "_installed", "proceed", "sudo apt-get update; sudo apt-get install --assume-yes " + pkg + ";",
				"dpkg-query --status " + pkg + " | grep \"Status:\";", "Status: install ok installed", "pass");
	}

}