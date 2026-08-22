package noppes.npcs.api.block;

import noppes.npcs.api.ILayerBlockModel;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;

import java.util.List;

public interface IBlockScripted extends IBlock {

	String executeCommand(@ParamName("command") String command);

	@SuppressWarnings("unused")
	float getHardness();

	@SuppressWarnings("unused")
	boolean getIsLadder();

	@SuppressWarnings("unused")
	boolean getIsPassible();

	int getLight();

	IItemStack getModel();

	int getRedstonePower();

	float getResistance();

	float getRotationX();

	@SuppressWarnings("unused")
	float getRotationY();

	float getRotationZ();

	float getScaleX();

	float getScaleY();

	float getScaleZ();

	@SuppressWarnings("unused")
	ITextPlane getTextPlane();

	@SuppressWarnings("unused")
	ITextPlane getTextPlane2();

	@SuppressWarnings("unused")
	ITextPlane getTextPlane3();

	@SuppressWarnings("unused")
	ITextPlane getTextPlane4();

	@SuppressWarnings("unused")
	ITextPlane getTextPlane5();

	@SuppressWarnings("unused")
	ITextPlane getTextPlane6();

	ITimers getTimers();

	void setHardness(@ParamName("hardness") float hardness);

	@SuppressWarnings("unused")
	void setIsLadder(@ParamName("bo") boolean bo);

	@SuppressWarnings("unused")
	void setIsPassible(@ParamName("bo") boolean bo);

	void setLight(@ParamName("value") int value);

	void setModel(@ParamName("item") IItemStack item);

	void setModel(@ParamName("name") String name);

	void setModel(@ParamName("blockName") String blockName, @ParamName("meta") int meta);

	@SuppressWarnings("unused")
	void setRedstonePower(@ParamName("strength") int strength);

	void setResistance(@ParamName("resistance") float resistance);

	void setRotation(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);

	void setScale(@ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);

	void trigger(@ParamName("id") int id, @ParamName("arguments") Object... arguments);


	// New from Unofficial (BetaZavr)
	@SuppressWarnings("unused")
	ITextPlane getTextPlane(@ParamName("id") int id);

	void setModel(@ParamName("block") IBlock block);

	void setObjModel(@ParamName("path") String path);

	@SuppressWarnings("unused")
	void setObjModel(@ParamName("path") String path,
					 @ParamName("objVisibleMeshes") String[] objVisibleMeshes,
					 @ParamName("objMaterialsReplase") String[][] objMaterialsReplase);

	ILayerBlockModel getModel(@ParamName("id") int id);

	@SuppressWarnings("unused")
	ILayerBlockModel createLayerModel();

	@SuppressWarnings("unused")
	List<ILayerBlockModel> getLayerModels();

	@SuppressWarnings("unused")
	boolean removeLayerModel(@ParamName("layer") ILayerBlockModel layer);

	@SuppressWarnings("unused")
	boolean removeLayerModel(@ParamName("id") int id);

	@SuppressWarnings("unused")
	void updateModel();

}
