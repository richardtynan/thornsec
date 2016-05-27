package core;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import core.data.OrgsecData;
import core.model.OrgsecModel;
import core.view.BlockingFrame;
import core.view.FullFrame;
import core.view.SimpleFrame;

public class Main {

	public static void main(String[] args) throws Exception {
		String text = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);

		OrgsecData data = new OrgsecData();
		data.read(text);

		OrgsecModel model = new OrgsecModel();
		model.setData(data);

		if (args[1].equals("cli")) {
			for (int i = 2; i < args.length; i += 2) {
				model.getNetworkModel(args[i]).auditServerBlock(args[i + 1], System.out, System.in);
			}
		} else if (args[1].equals("simple")) {
			SimpleFrame sf = new SimpleFrame(model);
			for (int i = 2; i < args.length; i += 2) {
				sf.audit(args[i], args[i + 1]);
			}
		} else if (args[1].equals("block")) {
			BlockingFrame bf = new BlockingFrame(model);
			for (int i = 2; i < args.length; i += 2) {
				bf.auditBlocking(args[i], args[i + 1]);
			}
		} else if (args[1].equals("full")) {
			new FullFrame(model);
		}
	}

}
