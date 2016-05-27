package core.view;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import core.model.OrgsecModel;

public class BlockingFrame {

	private OrgsecModel model;
	private TextAreaOutputStream out;
	private JTabbedPane jtp;

	public BlockingFrame(OrgsecModel model) {
		this.model = model;
		JFrame frame = new JFrame("orgsec");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jtp = new JTabbedPane();

		JTextArea area = new JTextArea();
		out = new TextAreaOutputStream(area);
		JScrollPane areapane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		DefaultCaret caret = (DefaultCaret) area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		areapane.setViewportView(area);
		jtp.add("blocking", areapane);

		frame.setContentPane(jtp);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}

	public void auditBlocking(String net, String server) {
		jtp.setTitleAt(0, "Blocking: " + net + " - " + server);
		model.getNetworkModel(net).auditServerBlock(server, out, System.in);
	}

}
