package noppes.npcs.shared.client.gui.listeners;

import java.util.Map;
import java.util.Vector;

public interface IScrollData {

	void setData(Vector<String> dataList, Map<String, Integer> dataMap);

	void setSelected(String select);

}
