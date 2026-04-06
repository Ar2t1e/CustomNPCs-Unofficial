package noppes.npcs.client.model.animation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ValueUtil;

public class AddedPartConfig {

    public ResourceLocation location;
    public ResourceLocation objUp;
    public ResourceLocation objDown;
    public int parentPart = -1;
    public int id;
    public int textureU = 40;
    public int textureV = 16;
    public boolean isNormal;
    public final float[] pos = new float[] { 0.0f, 0.0f, 0.0f }; // offset position relative to parent [x, y, z]
    public final float[] rot = new float[] { 0.0f, 0.0f, 0.0f }; // base rotation relative to parent [x, y, z]
    public final float[] size = new float[] { 4.0f, 5.5f, 3.5f, 3.0f, 4.0f }; // cuboid size [dx, dy0, dy1, dy2, dz]

    public AddedPartConfig() {
        clear();
    }

    public AddedPartConfig(int parentPartId) {
        parentPart = parentPartId;
        clear();
    }

    public void clear() {
        for (int i = 0; i < 3; i++) {
            pos[i] = 0.0f;
            rot[i] = 0.0f;
        }
        size[0] = 4.0f;
        size[1] = 5.5f;
        size[2] = 3.5f;
        size[3] = 3.0f;
        size[4] = 4.0f;
        isNormal = true;
        textureU = 40;
        textureV = 16;
        location = new ResourceLocation(CustomNpcs.MODID, "textures/entity/humanmale/steve.png");
        objUp = null;
        objDown = null;
    }

    public void load(CompoundTag compound) {
        parentPart = compound.getInt("ParentPart");
        location = new ResourceLocation(compound.getString("Location"));
        if (compound.contains("OBJUpLocation", 8)) { objUp = new ResourceLocation(compound.getString("OBJUpLocation")); }
        if (compound.contains("OBJDownLocation", 8)) { objUp = new ResourceLocation(compound.getString("OBJDownLocation")); }
        for (int i = 0; i < 5; i++) {
            try { size[i] = ValueUtil.correctFloat(compound.getList("BaseSize", 5).getFloat(i), -1.0f, 1.0f); } catch (Exception e) { LogWriter.error(e); }
            if (i > 2) { continue; }
            try { pos[i] = ValueUtil.correctFloat(compound.getList("BasePosition", 5).getFloat(i), -1.0f, 1.0f); } catch (Exception e) { LogWriter.error(e); }
            try { rot[i] = ValueUtil.correctFloat(compound.getList("BaseRotation", 5).getFloat(i), -5.0f, 5.0f); } catch (Exception e) { LogWriter.error(e); }
        }
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("ParentPart", parentPart);
        compound.putString("Location", location.toString());
        if (objUp != null) { compound.putString("OBJUpLocation", objUp.toString()); }
        if (objDown != null) { compound.putString("OBJDownLocation", objDown.toString()); }
        ListTag listPos = new ListTag();
        ListTag listRot = new ListTag();
        ListTag listSize = new ListTag();
        for (int i = 0; i < 5; i++) {
            listSize.add(FloatTag.valueOf(size[i]));
            if (i > 2) { continue; }
            listPos.add(FloatTag.valueOf(pos[i]));
            listRot.add(FloatTag.valueOf(rot[i]));
        }
        compound.put("BasePosition", listPos);
        compound.put("BaseRotation", listRot);
        compound.put("BaseSize", listSize);
        return compound;
    }

}
