package profile.base;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;
import core.profile.AProfile;
import singleton.IPTablesConf;
import unit.fs.DirUnit;
import unit.fs.FileUnit;
import unit.pkg.InstalledUnit;

public class IPTables extends AProfile {

	public IPTables() {
		super("iptables");
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> vec = new Vector<IProfile>();
		vec.addElement(new InstalledUnit("iptables", "iptables"));
		vec.addElement(new InstalledUnit("xsltproc", "xsltproc"));
		vec.addElement(new DirUnit("iptables_dir", "proceed", "/etc/iptables"));
		vec.addElement(IPTablesConf.getInstance(server, data.getLabel()));

		String iptxslt = "<?xml version=\\\"1.0\\\" encoding=\\\"ISO-8859-1\\\"?>\n";
		iptxslt += "<xsl:transform version=\\\"1.0\\\" xmlns:xsl=\\\"http://www.w3.org/1999/XSL/Transform\\\">\n";
		iptxslt += "  <xsl:output method = \\\"text\\\" />\n";
		iptxslt += "  <xsl:strip-space elements=\\\"*\\\" />\n";
		iptxslt += "    <xsl:param name=\\\"table\\\" />\n";
		iptxslt += "  \n";
		iptxslt += "  <xsl:template match=\\\"iptables-rules/table/chain/rule/conditions/*\\\">\n";
		iptxslt += "    <xsl:if test=\\\"name() != &quot;match&quot;\\\">\n";
		iptxslt += "      <xsl:text> -m </xsl:text><xsl:value-of select=\\\"name()\\\"/>\n";
		iptxslt += "    </xsl:if>\n";
		iptxslt += "    <xsl:apply-templates select=\\\"node()\\\"/>\n";
		iptxslt += "  </xsl:template>\n";
		iptxslt += "\n";
		iptxslt += "  <xsl:template match=\\\"iptables-rules/table/chain/rule/actions|table/chain/rule/conditions\\\">\n";
		iptxslt += "    <xsl:apply-templates select=\\\"*\\\"/>\n";
		iptxslt += "  </xsl:template>\n";
		iptxslt += "\n";
		iptxslt += "  <xsl:template match=\\\"iptables-rules/table/chain/rule/actions/goto\\\">\n";
		iptxslt += "    <xsl:text> -g </xsl:text>\n";
		iptxslt += "    <xsl:apply-templates select=\\\"*\\\"/>\n";
		iptxslt += "    <xsl:text>&#xA;</xsl:text>\n";
		iptxslt += "  </xsl:template>\n";
		iptxslt += "  <xsl:template match=\\\"iptables-rules/table/chain/rule/actions/call\\\">\n";
		iptxslt += "    <xsl:text> -j </xsl:text>\n";
		iptxslt += "    <xsl:apply-templates select=\\\"*\\\"/>\n";
		iptxslt += "    <xsl:text>&#xA;</xsl:text>\n";
		iptxslt += "  </xsl:template>\n";
		iptxslt += "  <xsl:template match=\\\"iptables-rules/table/chain/rule/actions/*\\\">\n";
		iptxslt += "    <xsl:text> -j </xsl:text><xsl:value-of select=\\\"name()\\\"/>\n";
		iptxslt += "    <xsl:apply-templates select=\\\"*\\\"/>\n";
		iptxslt += "    <xsl:text>&#xA;</xsl:text>\n";
		iptxslt += "  </xsl:template>\n";
		iptxslt += "\n";
		iptxslt += "  <xsl:template match=\\\"iptables-rules/table/chain/rule/actions//*|iptables-rules/table/chain/rule/conditions//*\\\" priority=\\\"0\\\">\n";
		iptxslt += "    <xsl:if test=\\\"@invert=1\\\"><xsl:text> !</xsl:text></xsl:if>\n";
		iptxslt += "    <xsl:text> -</xsl:text>\n";
		iptxslt += "    <xsl:if test=\\\"string-length(name())&gt;1\\\">\n";
		iptxslt += "      <xsl:text>-</xsl:text>\n";
		iptxslt += "    </xsl:if>\n";
		iptxslt += "    <xsl:value-of select=\\\"name()\\\"/>\n";
		iptxslt += "    <xsl:text> </xsl:text>\n";
		iptxslt += "    <xsl:apply-templates select=\\\"node()\\\"/>\n";
		iptxslt += "  </xsl:template>\n";
		iptxslt += "\n";
		iptxslt += "  <xsl:template match=\\\"iptables-rules/table/chain/rule/actions/call/*|iptables-rules/table/chain/rule/actions/goto/*\\\">\n";
		iptxslt += "    <xsl:value-of select=\\\"name()\\\"/>\n";
		iptxslt += "    <xsl:apply-templates select=\\\"node()\\\"/>\n";
		iptxslt += "  </xsl:template>\n";
		iptxslt += "\n";
		iptxslt += "  <xsl:template name=\\\"rule-head\\\">\n";
		iptxslt += "    <xsl:text>-A </xsl:text><!-- a rule must be under a chain -->\n";
		iptxslt += "    <xsl:value-of select=\\\"../@name\\\" />\n";
		iptxslt += "    <xsl:apply-templates select=\\\"conditions\\\"/>\n";
		iptxslt += "  </xsl:template>\n";
		iptxslt += "\n";
		iptxslt += "  <xsl:template match=\\\"iptables-rules/table/chain/rule\\\">\n";
		iptxslt += "    <xsl:choose>\n";
		iptxslt += "      <xsl:when test=\\\"count(actions/*)&gt;0\\\">\n";
		iptxslt += "        <xsl:for-each select=\\\"actions/*\\\">\n";
		iptxslt += "          <xsl:for-each select=\\\"../..\\\">\n";
		iptxslt += "            <xsl:call-template name=\\\"rule-head\\\"/>\n";
		iptxslt += "          </xsl:for-each>\n";
		iptxslt += "          <xsl:apply-templates select=\\\".\\\"/>\n";
		iptxslt += "        </xsl:for-each>\n";
		iptxslt += "      </xsl:when>\n";
		iptxslt += "      <xsl:otherwise>\n";
		iptxslt += "        <xsl:call-template name=\\\"rule-head\\\"/>\n";
		iptxslt += "        <xsl:text>&#xA;</xsl:text>\n";
		iptxslt += "      </xsl:otherwise>\n";
		iptxslt += "    </xsl:choose>\n";
		iptxslt += "  </xsl:template>\n";
		iptxslt += "\n";
		iptxslt += "  <xsl:template match=\\\"iptables-rules/table\\\">\n";
		iptxslt += "    <xsl:if test=\\\"@name=\\$table\\\">\n";
		iptxslt += "            <xsl:apply-templates select=\\\"node()\\\"/>\n";
		iptxslt += "    </xsl:if>\n";
		iptxslt += "  </xsl:template>\n";
		iptxslt += "\n";
		iptxslt += "  <xsl:template match=\\\"@*|node()\\\">\n";
		iptxslt += "    <xsl:copy>\n";
		iptxslt += "      <xsl:apply-templates select=\\\"@*\\\"/>\n";
		iptxslt += "      <xsl:apply-templates select=\\\"node()\\\"/>\n";
		iptxslt += "    </xsl:copy>\n";
		iptxslt += "  </xsl:template>\n";
		iptxslt += "\n";
		iptxslt += "</xsl:transform>";
	
		vec.addElement(new FileUnit("iptables_xlst", "proceed", iptxslt, "/etc/iptables/iptables.xslt"));

		//IPTablesConf.getInstance(server, data.getLabel()).addFilterInput("-j DROP");
		//IPTablesConf.getInstance(server, data.getLabel()).addFilterInput("-j LOG --log-prefix \\\"iptin: \\\"");
		//IPTablesConf.getInstance(server, data.getLabel()).addFilterForward("-j DROP");
		//IPTablesConf.getInstance(server, data.getLabel()).addFilterForward("-j LOG --log-prefix \\\"iptfwd: \\\"");
		//IPTablesConf.getInstance(server, data.getLabel()).addFilterOutput("-j DROP");
		//IPTablesConf.getInstance(server, data.getLabel()).addFilterOutput("-j LOG --log-prefix \\\"iptout: \\\"");
		return vec;
	}

}
