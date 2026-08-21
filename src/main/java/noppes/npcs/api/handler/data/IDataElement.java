package noppes.npcs.api.handler.data;

import noppes.npcs.api.interfaces.ParamName;

import javax.annotation.Nonnull;

public interface IDataElement {

	int getId();

	String getInfo();

	@Nonnull String getName();

	String getObfuscatedName();

	@Nonnull Object getObject();

	@Nonnull Class<?> getParentClass();

	int getType();

	Object getValue();

	Object invoke(@ParamName("values") Object[] values);

	boolean isBelong(@ParamName("clazz") Class<?> clazz);

	boolean setValue(@ParamName("value") Object value);

}
