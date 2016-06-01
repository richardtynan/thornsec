package core.unit;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.iface.IUnit;

public abstract class ASingleUnit implements IUnit {

	protected String label;
	protected String precondition;
	protected String config;
	protected String audit;

	protected ASingleUnit() {
	}

	public ASingleUnit(String label, String precondition, String config, String audit) {
		this.label = label;
		this.precondition = precondition;
		this.config = config;
		this.audit = audit;
	}

	public String getLabel() {
		return label;
	}

	public boolean isSingleton() {
		return false;
	}

	public String genAudit() {
		String auditString = getLabel() + "=0;\n";
		auditString += this.getAudit();
		auditString += "if [ \"$" + getLabel() + "\" = \"1\" ] ; then\n";
		auditString += "\techo pass " + getLabel() + "\n";
		auditString += "\t" + "((pass++))\n";
		auditString += "else\n";
		auditString += "\techo fail " + getLabel() + "\n";
		auditString += "\t" + "((fail++))\n";
		auditString += "\t" + "fail_string=\"$fail_string\n" + getLabel() + "\"\n";
		auditString += "fi ;";
		return auditString;
	}

	public String genConfig() {
		String configString = this.getAudit();
		configString += "if [ \"$" + getLabel() + "\" != \"1\" ] ; then\n";
		configString += "if [ \"$" + getPrecondition() + "\" = \"1\" ] ; then\n";
		configString += "\t" + "echo 'fail " + getLabel() + " CONFIGURING'\n";
		configString += "\t" + getConfig() + "\n";
		configString += "\t" + "echo 'fail " + getLabel() + " RETESTING'\n";
		configString += this.genAudit();
		configString += "else\n";
		configString += "\t" + getLabel() + "=0;\n";
		configString += "\t" + "echo 'fail " + getLabel() + " PRECONDITION FAILED " + getPrecondition() + "'\n";
		configString += "fi ;\n";
		configString += "else\n";
		configString += "\techo pass " + getLabel() + "\n";
		configString += "\t" + "((pass++))\n";
		configString += "fi ;\n";
		return configString;
	}

	public String genDryRun() {
		String dryrunString = this.getAudit();
		dryrunString += "if [ \"$" + getLabel() + "\" != \"1\" ] ; then\n";
		dryrunString += "\t" + "echo 'fail " + getLabel() + " DRYRUN'\n";
		dryrunString += "\t" + "echo '" + getConfig() + "';\n";
		dryrunString += this.getDryRun();
		dryrunString += "\t" + "((fail++))\n";
		dryrunString += "\t" + "fail_string=\"$fail_string\n" + getLabel() + "\"\n";
		dryrunString += "else\n";
		dryrunString += "\techo pass " + getLabel() + "\n";
		dryrunString += "\t" + "((pass++))\n";
		dryrunString += "fi ;\n";
		return dryrunString;
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		return null;
	}

	protected abstract String getAudit();

	protected abstract String getPrecondition();

	protected abstract String getConfig();

	protected abstract String getDryRun();
}
