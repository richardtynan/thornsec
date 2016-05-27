package core.view;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import core.model.OrgsecModel;

public class SimpleFrame {

	private JTabbedPane jtp;
	private OrgsecModel model;

	public SimpleFrame(OrgsecModel model) {
		this.model = model;
		JFrame frame = new JFrame("orgsec");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jtp = new JTabbedPane();

		frame.setContentPane(jtp);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}

	private void addTab(String title, JTextArea area) {
		JScrollPane areapane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		DefaultCaret caret = (DefaultCaret) area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		areapane.setViewportView(area);
		jtp.add(title, areapane);
	}

	public void audit(String net, String server) {
		JTextArea area = new JTextArea();
		TextAreaOutputStream out = new TextAreaOutputStream(area);
		this.addTab(net + " - " + server, area);
		model.getNetworkModel(net).auditServer(server, out, System.in);
	}

}
