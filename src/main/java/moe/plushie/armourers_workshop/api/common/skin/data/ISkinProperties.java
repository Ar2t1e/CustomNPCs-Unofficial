package moe.plushie.armourers_workshop.api.common.skin.data;

@SuppressWarnings("unused")
public interface ISkinProperties {

	Object getProperty(String key, Object defaultValue);

	Boolean getPropertyBoolean(String key, Boolean defaultValue);

	double getPropertyDouble(String key, double defaultValue);

	int getPropertyInt(String key, int defaultValue);

	String getPropertyString(String key, String defaultValue);

	boolean haveProperty(String key);

	void removeProperty(String key);

	void setProperty(String key, Object value);

}
