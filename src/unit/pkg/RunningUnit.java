package unit.pkg;

import core.unit.SimpleUnit;

public class RunningUnit extends SimpleUnit {

	public RunningUnit(String name, String pkg, String grep) {
		super(name + "_running", name + "_installed", "sudo systemctl restart " + pkg + ";",
				"ps aux | grep -v grep | grep " + grep + ";", "", "fail");
	}

}
