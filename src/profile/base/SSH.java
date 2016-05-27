package profile.base;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AStructuredProfile;
import core.unit.SimpleUnit;
import profile.Base;
import singleton.IPTablesConf;
import unit.fs.DirUnit;
import unit.fs.FileUnit;
import unit.pkg.InstalledUnit;
import unit.pkg.RunningUnit;

public class SSH extends AStructuredProfile {

	public SSH() {
		super("ssh");
	}

	public Vector<IProfile> getInstalled(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new InstalledUnit("sshd", "openssh-server"));
		return vec;
	}

	public Vector<IProfile> getPersistent(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();

		String sshdconf = "";
		sshdconf += "Port " + Base.getPort(server, data) + "\n";
		sshdconf += "Protocol 2\n";
		sshdconf += "HostKey /etc/ssh/ssh_host_rsa_key\n";
		sshdconf += "HostKey /etc/ssh/ssh_host_dsa_key\n";
		sshdconf += "HostKey /etc/ssh/ssh_host_ecdsa_key\n";
		sshdconf += "HostKey /etc/ssh/ssh_host_ed25519_key\n";
		sshdconf += "UsePrivilegeSeparation yes\n";
		sshdconf += "KeyRegenerationInterval 3600\n";
		sshdconf += "ServerKeyBits 1024\n";
		sshdconf += "MACs hmac-sha2-512\n";
		sshdconf += "Ciphers aes256-ctr,aes256-cbc\n";
		sshdconf += "KexAlgorithms ecdh-sha2-nistp521,ecdh-sha2-nistp256,diffie-hellman-group-exchange-sha256\n";
		sshdconf += "SyslogFacility AUTH\n";
		sshdconf += "LogLevel INFO\n";
		sshdconf += "LoginGraceTime 120\n";
		sshdconf += "PermitRootLogin no\n";
		sshdconf += "StrictModes yes\n";
		sshdconf += "RSAAuthentication yes\n";
		sshdconf += "PubkeyAuthentication yes\n";
		sshdconf += "AuthorizedKeysFile %h/.ssh/authorized_keys\n";
		sshdconf += "IgnoreRhosts yes\n";
		sshdconf += "RhostsRSAAuthentication no\n";
		sshdconf += "HostbasedAuthentication no\n";
		sshdconf += "PermitEmptyPasswords no\n";
		sshdconf += "ChallengeResponseAuthentication no\n";
		sshdconf += "X11Forwarding yes\n";
		sshdconf += "X11DisplayOffset 10\n";
		sshdconf += "PrintMotd no\n";
		sshdconf += "PrintLastLog yes\n";
		sshdconf += "TCPKeepAlive yes\n";
		sshdconf += "AcceptEnv LANG LC_*\n";
		sshdconf += "Subsystem sftp /usr/lib/openssh/sftp-server\n";
		sshdconf += "UsePAM yes";
		vec.addElement(new FileUnit("sshd_config", "proceed", sshdconf, "/etc/ssh/sshd_config"));

		vec.addElement(new SimpleUnit("sshd_rsa", "sshd_config",
				"echo -e \"y\\n\" | sudo ssh-keygen -f /etc/ssh/ssh_host_rsa_key -N \"\" -t rsa -b 4096",
				"ssh-keygen -lf /etc/ssh/ssh_host_rsa_key | awk '{print $1}'", "4096", "pass"));

		vec.addElement(new SimpleUnit("sshd_dsa", "sshd_config",
				"echo -e \"y\\n\" | sudo ssh-keygen -f /etc/ssh/ssh_host_dsa_key -N \"\" -t dsa -b 1024",
				"ssh-keygen -lf /etc/ssh/ssh_host_dsa_key | awk '{print $1}'", "1024", "pass"));

		vec.addElement(new SimpleUnit("sshd_ecdsa", "sshd_config",
				"echo -e \"y\\n\" | sudo ssh-keygen -f /etc/ssh/ssh_host_ecdsa_key -N \"\" -t ecdsa -b 521",
				"ssh-keygen -lf /etc/ssh/ssh_host_ecdsa_key | awk '{print $1}'", "521", "pass"));

		vec.addElement(new DirUnit("sshd_dir", "sshd_config", "~/.ssh"));

		vec.addElement(new FileUnit("sshd_keys", "sshd_dir", Base.getKeys(server, data), "~/.ssh/authorized_keys"));

		return vec;
	}

	public Vector<IProfile> getIpt(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		IPTablesConf.getInstance(server, data.getLabel())
				.addFilterInput("-p tcp --dport " + Base.getPort(server, data) + " -j ACCEPT");
		IPTablesConf.getInstance(server, data.getLabel())
				.addFilterOutput("-p tcp --sport " + Base.getPort(server, data) + " -j ACCEPT");
		return vec;
	}

	public Vector<IProfile> getLive(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new RunningUnit("sshd", "openssh-server", "sshd"));
		return vec;
	}
}
