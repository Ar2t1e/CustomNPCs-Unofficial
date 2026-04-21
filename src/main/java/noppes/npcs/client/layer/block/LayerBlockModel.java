package noppes.npcs.client.layer.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.ILayerBlockModel;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.BlockWrapper;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class LayerBlockModel implements ILayerBlockModel {

    protected @Nonnull final TileScripted tile;
    // item
    protected ItemStackWrapper itemModel = ItemStackWrapper.AIR;
    // block
    protected BlockWrapper blockModel = BlockWrapper.AIR;
    // obj
    protected ResourceLocation objModel = null;
    protected List<String> objVisibleMeshes = new ArrayList<>();
    protected Map<String, ResourceLocation> objMaterialsReplase = new HashMap<>();
    protected AxisAlignedBB aabb = Block.FULL_BLOCK_AABB;

    protected float[] offsetAxis = new float[] { 0.0f, 0.0f, 0.0f };
    protected float[] scaleAxis = new float[] { 1.0f, 1.0f, 1.0f };
    protected float[] rotateAxis = new float[] { 0.0f, 0.0f, 0.0f };
    protected byte[] isRotate = new byte[] { (byte) 0, (byte) 0, (byte) 0 };
    protected int id = 0;
    protected int rotateSpeed = 1;

    public LayerBlockModel(@Nonnull TileScripted tileIn) { tile = tileIn; }

    public LayerBlockModel(int idIn, ItemStack stack, @Nonnull TileScripted tile) {
        this(tile);
        id = idIn;
        itemModel = (ItemStackWrapper) Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack);
    }

    @Override
    public IItemStack getItemModel() { return itemModel; }

    @Override
    public IBlock getBlockModel() { return blockModel; }

    @Override
    public INbt getNbt() {
        NBTTagCompound nbtLayer = new NBTTagCompound();
        if (!itemModel.isEmpty()) { nbtLayer.setTag("Model", itemModel.getMCItemStack().writeToNBT(new NBTTagCompound())); }
        // OBJ
        if (objModel != null) { nbtLayer.setString("OBJModel", objModel.toString()); }
        NBTTagList ovm = new NBTTagList();
        for (String mesh : objVisibleMeshes) { ovm.appendTag(new NBTTagString(mesh)); }
        nbtLayer.setTag("OBJVisibleMeshes", ovm);
        NBTTagList omr = new NBTTagList();
        for (Map.Entry<String, ResourceLocation> entry : objMaterialsReplase.entrySet()) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString("K", entry.getKey());
            nbt.setString("V", entry.getValue().toString());
            ovm.appendTag(nbt);
        }
        nbtLayer.setTag("OBJMaterialsReplase", omr);
        // box
        NBTTagList box = new NBTTagList();
        box.appendTag(new NBTTagDouble(aabb.minX));
        box.appendTag(new NBTTagDouble(aabb.minY));
        box.appendTag(new NBTTagDouble(aabb.minZ));
        box.appendTag(new NBTTagDouble(aabb.maxX));
        box.appendTag(new NBTTagDouble(aabb.maxY));
        box.appendTag(new NBTTagDouble(aabb.maxZ));
        nbtLayer.setTag("AABB", box);
        // rotate
        NBTTagList ra = new NBTTagList();
        for (float f : rotateAxis) { ra.appendTag(new NBTTagFloat(f)); }
        nbtLayer.setTag("RotateAxis", ra);
        // offset
        NBTTagList oa = new NBTTagList();
        for (float f : offsetAxis) { oa.appendTag(new NBTTagFloat(f)); }
        nbtLayer.setTag("OffsetAxis", oa);
        // scale
        NBTTagList sa = new NBTTagList();
        for (float f : scaleAxis) { sa.appendTag(new NBTTagFloat(f)); }
        nbtLayer.setTag("ScaleAxis", sa);
        // main
        nbtLayer.setByteArray("isRotate", isRotate);
        nbtLayer.setInteger("Id", id);
        nbtLayer.setInteger("Speed", rotateSpeed);
        return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtLayer);
    }

    @Override
    public void setNbt(INbt nbt) {
        NBTTagCompound nbtLayer = nbt.getMCNBT();
        if (nbtLayer.hasKey("Model", 10)) {
            itemModel = (ItemStackWrapper) Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(new ItemStack(nbtLayer.getCompoundTag("Model")));
        }
        // OBJ
        if (nbtLayer.hasKey("OBJModel", 8)) { objModel = new ResourceLocation(nbtLayer.getString("OBJModel")); }
        objVisibleMeshes.clear();
        NBTTagList ovm = nbtLayer.getTagList("OBJVisibleMeshes", 8);
        for (int i = 0; i < ovm.tagCount(); i++) { objVisibleMeshes.add(ovm.getStringTagAt(i)); }
        objMaterialsReplase.clear();
        NBTTagList omr = nbtLayer.getTagList("OBJVisibleMeshes", 10);
        for (int i = 0; i < omr.tagCount(); i++) {
            NBTTagCompound mcNbt = omr.getCompoundTagAt(i);
            objMaterialsReplase.put(mcNbt.getString("K"), new ResourceLocation(NoppesUtilServer.validLocation(mcNbt.getString("V"))));
        }
        // box
        NBTTagList box = nbtLayer.getTagList("AABB", 6);
        aabb = new AxisAlignedBB(box.tagCount() > 0 ? box.getDoubleAt(0) : aabb.minX,
                box.tagCount() > 1 ? box.getDoubleAt(1) : aabb.minY,
                box.tagCount() > 2 ? box.getDoubleAt(2) : aabb.minZ,
                box.tagCount() > 3 ? box.getDoubleAt(3) : aabb.maxX,
                box.tagCount() > 4 ? box.getDoubleAt(4) : aabb.maxY,
                box.tagCount() > 5 ? box.getDoubleAt(5) : aabb.maxZ);
        // rotate
        if (nbtLayer.getTagList("RotateAxis", 5).tagCount() == 3) {
            for (int i = 0; i < nbtLayer.getTagList("RotateAxis", 5).tagCount(); i++) {
                rotateAxis[i] = nbtLayer.getTagList("RotateAxis", 5).getFloatAt(i);
            }
        }
        // offset
        if (nbtLayer.getTagList("OffsetAxis", 5).tagCount() == 3) {
            for (int i = 0; i < nbtLayer.getTagList("OffsetAxis", 5).tagCount(); i++) {
                offsetAxis[i] = nbtLayer.getTagList("OffsetAxis", 5).getFloatAt(i);
            }
        }
        // scale
        if (nbtLayer.getTagList("ScaleAxis", 5).tagCount() == 3) {
            for (int i = 0; i < nbtLayer.getTagList("ScaleAxis", 5).tagCount(); i++) {
                scaleAxis[i] = nbtLayer.getTagList("ScaleAxis", 5).getFloatAt(i);
            }
        }
        // main
        if (nbtLayer.getByteArray("isRotate").length == 3) { isRotate = nbtLayer.getByteArray("isRotate"); }
        id = nbtLayer.hasKey("Pos", 3) ? nbtLayer.getInteger("Pos") : nbtLayer.getInteger("Id");
        setRotateSpeed(nbtLayer.getInteger("Speed"));
    }

    @Override
    public @Nullable String getOBJModel() { return objModel == null ? null : objModel.toString(); }

    @Override
    public List<String> getOBJVisibleMeshes() { return objVisibleMeshes; }

    @Override
    public Map<String, ResourceLocation> getOBJMaterialsReplase() { return objMaterialsReplase; }

    @Override
    public float getOffset(int axis) {
        if (axis < 0) { axis *= -1; }
        return offsetAxis[axis % 3];
    }

    @Override
    public int getId() { return id; }

    public void setId(int newId) {
        id = ValueUtil.onlyPositiveInt(newId, Integer.MAX_VALUE);
        tile.needsClientUpdate = true;
    }

    @Override
    public float getRotation(int axis) {
        if (axis < 0) { axis *= -1; }
        return rotateAxis[axis % 3];
    }

    @Override
    public int getRotateSpeed() { return rotateSpeed; }

    @Override
    public float getScale(int axis) {
        if (axis < 0) { axis *= -1; }
        return scaleAxis[axis % 3];
    }

    @Override
    public boolean isRotate(int axis) {
        if (axis < 0) { axis *= -1; }
        return isRotate[axis % 3] == (byte) 1;
    }

    @Override
    public void setIsRotate(boolean x, boolean y, boolean z) {
        isRotate[0] = x ? (byte) 1 : (byte) 0;
        isRotate[1] = y ? (byte) 1 : (byte) 0;
        isRotate[2] = z ? (byte) 1 : (byte) 0;
        tile.needsClientUpdate = true;
    }

    @Override
    public void setItemModel(IItemStack stack) {
        itemModel = (ItemStackWrapper) stack;
        blockModel = null;
        objModel = null;
        objVisibleMeshes = new ArrayList<>();
        objMaterialsReplase = new HashMap<>();
        tile.needsClientUpdate = true;
    }

    @Override
    public void setBlockModel(IBlock iBlock) {
        itemModel = null;
        blockModel = (BlockWrapper) iBlock;
        objModel = null;
        objVisibleMeshes = new ArrayList<>();
        objMaterialsReplase = new HashMap<>();
        tile.needsClientUpdate = true;
    }

    @Override
    public void setOBJModel(String path) {
        itemModel = null;
        blockModel = null;
        objModel = new ResourceLocation(NoppesUtilServer.validLocation(path));
        objVisibleMeshes = new ArrayList<>();
        objMaterialsReplase = new HashMap<>();
        tile.needsClientUpdate = true;
    }

    @Override
    public void setOBJModel(String path, List<String> meshes,  Map<String, ResourceLocation> materials) {
        itemModel = null;
        blockModel = null;
        objModel = new ResourceLocation(NoppesUtilServer.validLocation(path));
        objVisibleMeshes = meshes;
        objMaterialsReplase = materials;
        tile.needsClientUpdate = true;
    }

    @Override
    public void setOffset(float x, float y, float z) {
        offsetAxis[0] = x;
        offsetAxis[1] = y;
        offsetAxis[2] = z;
        tile.needsClientUpdate = true;
    }

    @Override
    public void setRotation(float x, float y, float z) {
        x %= 360.0f;
        y %= 360.0f;
        z %= 360.0f;
        if (x < 0.0f) { x += 360.0f; }
        if (y < 0.0f) { y += 360.0f; }
        if (z < 0.0f) { z += 360.0f; }
        rotateAxis[0] = ValueUtil.correctFloat(x, 0.0f, 359.9999f);
        rotateAxis[1] = ValueUtil.correctFloat(y, 0.0f, 359.9999f);
        rotateAxis[2] = ValueUtil.correctFloat(z, 0.0f, 359.9999f);
        tile.needsClientUpdate = true;
    }

    @Override
    public void setRotateSpeed(int speed) {
        rotateSpeed = ValueUtil.correctInt(speed, 1, 7);
        tile.needsClientUpdate = true;
    }

    @Override
    public void setScale(float x, float y, float z) {
        scaleAxis[0] = ValueUtil.correctFloat(x, -10.0f, 10.0f);
        scaleAxis[1] = ValueUtil.correctFloat(y, -10.0f, 10.0f);
        scaleAxis[2] = ValueUtil.correctFloat(z, -10.0f, 10.0f);
        tile.needsClientUpdate = true;
    }

    @Override
    public AxisAlignedBB getBoundingBox() { return aabb; }

    @Override
    public void setBoundingBox(AxisAlignedBB newAABB) {
        aabb = newAABB;
        tile.needsClientUpdate = true;
    }

    @Override
    public void setBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        aabb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        tile.needsClientUpdate = true;
    }

}
