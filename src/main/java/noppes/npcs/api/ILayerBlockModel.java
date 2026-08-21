package noppes.npcs.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;

import java.util.List;
import java.util.Map;

public interface ILayerBlockModel {

	IItemStack getItemModel();

	@SuppressWarnings("unused")
	IBlock getBlockModel();

	@SuppressWarnings("unused")
	String getOBJModel();

	@SuppressWarnings("unused")
	List<String> getOBJVisibleMeshes();

	@SuppressWarnings("unused")
	Map<String, ResourceLocation> getOBJMaterialsReplase();

	INbt getNbt();

	float getOffset(@ParamName("axis") int axis);

	int getId();

	@SuppressWarnings("unused")
	float getRotation(@ParamName("axis") int axis);

	int getRotateSpeed();

	float getScale(@ParamName("axis") int axis);

	@SuppressWarnings("unused")
	boolean isRotate(@ParamName("axis") int axis);

	@SuppressWarnings("unused")
	void setIsRotate(@ParamName("x") boolean x, @ParamName("y") boolean y, @ParamName("z") boolean z);

	@SuppressWarnings("unused")
	void setItemModel(@ParamName("stack") IItemStack stack);

	@SuppressWarnings("unused")
	void setBlockModel(@ParamName("block") IBlock block);

	void setNbt(@ParamName("nbt") INbt nbt);

	@SuppressWarnings("unused")
	void setOBJModel(@ParamName("path") String path);

	@SuppressWarnings("unused")
	void setOBJModel(@ParamName("path") String path,
					 @ParamName("objVisibleMeshes") List<String> objVisibleMeshes,
					 @ParamName("objMaterialsReplase") Map<String, ResourceLocation> objMaterialsReplase);

	void setOffset(@ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);

	@SuppressWarnings("unused")
	void setRotation(@ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);

	void setRotateSpeed(@ParamName("speed") int speed);

	void setScale(@ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);

	AxisAlignedBB getBoundingBox();

	void setBoundingBox(AxisAlignedBB newAABB);

	void setBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

}
