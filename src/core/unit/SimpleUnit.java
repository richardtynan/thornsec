package core.unit;

public class SimpleUnit extends ComplexUnit {

	protected String test;
	protected String result;

	protected SimpleUnit() {
	}

	public SimpleUnit(String name, String precondition, String config, String audit, String test, String result) {
		super(name, precondition, config, audit);
		this.test = test;
		this.result = result;
	}

	protected String getAudit() {
		String auditString = "out=$(" + super.getAudit() + ");\n";
		auditString += "test=\"" + getTest() + "\";\n";
		
		if (getResult().equals("fail"))
			auditString += "if [ \"$out\" = \"$test\" ] ; then\n";
		else
			auditString += "if [ \"$out\" != \"$test\" ] ; then\n";
		auditString += "\t" + getLabel() + "=0;\n";
		auditString += "else\n";
		auditString += "\t" + getLabel() + "=1;\n";
		auditString += "fi ;\n";
		return auditString;
	}

	protected String getTest() {
		return this.test;
	}

	protected String getResult() {
		return this.result;
	}

}
