package noppes.npcs.api.handler;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.handler.data.IDataElement;

import java.util.List;

@SuppressWarnings("unused")
public interface IDataObject {

	IDataElement getConstructor(@ParamName("index") Object index);

	IDataElement getClazz(@ParamName("index") Object index);

	IDataElement getField(@ParamName("index") Object index);

	IDataElement getMethod(@ParamName("index") Object index);

	List<IDataElement> getConstructors();

	List<IDataElement> getClasses();

	List<IDataElement> getFields();

	List<IDataElement> getMethods();

	String getConstructorsInfo();

	String getClassesInfo();

	String getFieldsInfo();

	String getMethodsInfo();

	String getInfo();

}
