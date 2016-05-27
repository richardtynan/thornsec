package core.exec;

import java.io.OutputStream;

public class ManageExec {

	private String login;
	private String user;
	private String ip;
	private String port;
	private String cmd;
	private OutputStream out;

	public ManageExec(String login, String user, String ip, String port, String cmd, OutputStream out) {
		this.login = login;
		this.user = user;
		this.ip = ip;
		this.port = port;
		this.cmd = cmd;
		this.out = out;
	}

	public ProcessExec manage() {
		try {
			ProcessExec exec1 = new ProcessExec("ssh -o ConnectTimeout=3 -p " + this.port + " " + this.user + "@"
					+ this.ip + " cat > script.sh; chmod +x script.sh;", out, System.err);
			exec1.writeAllClose(this.cmd.getBytes());
			exec1.waitFor();
			ProcessExec exec2 = new ProcessExec("ssh -t -t -o ConnectTimeout=3 -p " + this.port + " " + this.user + "@"
					+ this.ip + " ./script.sh; rm -rf script.sh; exit;", out, System.err);
			Thread.sleep(500);
			PasswordExec pwd = new PasswordExec(login);
			String pass = pwd.getPassword() + "\n";
			exec2.writeAllOpen(pass.getBytes());
			return exec2;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public void manageBlock() {
		ProcessExec exec = manage();
		exec.waitFor();
	}

}
