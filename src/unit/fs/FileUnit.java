package unit.fs;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.commons.lang3.StringEscapeUtils;

import core.unit.SimpleUnit;

public class FileUnit extends SimpleUnit {

	private String text;
	private String path;

	public FileUnit(String name, String precondition, String text, String path) {
		super(name, precondition, "", "cat " + path + ";", text, "pass");
		this.text = text;
		this.path = path;
	}

	public String getConfig() {
		return "echo \"" + getText() + "\" | sudo tee " + path + " > /dev/null;";
	}

	public String getText() {
		return this.text;
	}

	public static void main(String[] args) throws Exception {
		String label = "iptxslt";
		String path = "tpl/iptables.xslt";
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = reader.readLine();
		while (line != null) {
			String escaped = StringEscapeUtils.escapeJava(line.replace("\t", "    ")).replace("$", "\\$");
			System.out.println(label + " += \"" + escaped + "\\n\";");
			line = reader.readLine();
		}
		reader.close();
	}

}