package core.iface;

import java.util.Vector;

public interface IProfile {

	public String getLabel();

	public boolean isSingleton();

	public Vector<IProfile> getUnits(String server, INetworkData data);

}