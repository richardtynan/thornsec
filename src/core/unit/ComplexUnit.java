package core.unit;

public class ComplexUnit extends ASingleUnit {

	protected ComplexUnit() {
	}

	public ComplexUnit(String label, String precondition, String config, String audit) {
		super(label, precondition, config, audit);
	}

	protected String getAudit() {
		return this.audit;
	}

	protected String getPrecondition() {
		return precondition;
	}

	protected String getConfig() {
		return config;
	}

	protected String getDryRun() {
		return "";
	}

}
