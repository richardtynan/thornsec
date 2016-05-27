package core.profile;

import java.util.Vector;

import core.iface.INetworkData;
import core.iface.IProfile;

public abstract class AStructuredProfile extends AProfile {

	protected AStructuredProfile(String name) {
		super(name);
	}

	public Vector<IProfile> getUnits(String server, INetworkData data) {
		Vector<IProfile> children = new Vector<IProfile>();
		children.addAll(this.getInstalled(server, data));
		children.addAll(this.getPersistent(server, data));
		children.addAll(this.getIpt(server, data));
		children.addAll(this.getLive(server, data));
		return children;
	}

	protected abstract Vector<IProfile> getInstalled(String server, INetworkData data);

	protected abstract Vector<IProfile> getPersistent(String server, INetworkData data);

	protected abstract Vector<IProfile> getIpt(String server, INetworkData data);

	protected abstract Vector<IProfile> getLive(String server, INetworkData data);

}
