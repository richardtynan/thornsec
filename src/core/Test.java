package core;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import core.data.OrgsecData;
import core.model.OrgsecModel;

public class Test {

	public static void main(String[] args) throws Exception {
		String text = new String(Files.readAllBytes(Paths.get("/Users/richie/Desktop/orgsec.json")),
				StandardCharsets.UTF_8);

		OrgsecData data = new OrgsecData();
		data.read(text);

		OrgsecModel model = new OrgsecModel();
		model.setData(data);

		model.getNetworkModel("home").dryrunServerBlock("router", System.out, System.in);
		//model.getNetworkModel("pi").auditServerBlock("router", System.out, System.in);

	}

}
