package core.exec;

public class PasswordExec {

	private String password;

	public PasswordExec(String password) {
		this.password = password;
	}

	public String getPassword() {
		String value = "";
		try {
			Process proc3 = Runtime.getRuntime().exec("security find-generic-password -l " + password + " -w");
			int read = proc3.getInputStream().read();
			while (read != -1) {
				value += (char) read;
				read = proc3.getInputStream().read();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return value;
	}
}
