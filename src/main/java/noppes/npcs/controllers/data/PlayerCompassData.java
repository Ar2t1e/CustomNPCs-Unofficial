package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.IPos;
import noppes.npcs.api.gui.ICompassData;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.constants.EnumQuestTask;
import noppes.npcs.util.ValueUtil;

public class PlayerCompassData implements ICompassData, IPlayerData {

    protected static final String dataName = "CompassData";

    public String npc = "";
    public String  name = "";
    public String  title = "";
    public BlockPos pos = BlockPos.ORIGIN;
    public boolean showQuestName = true;
    public boolean showTaskProgress = true;
    public boolean showOfPlayer = true;
    public boolean showDial = true;
    public boolean isCustomPoint = false;
    public boolean questLogIsFast = false;
    public boolean isFlat = false;
    public int dimension = 0;
    public int questID;
    private int range = 5;
    private int type = 0;
    public final float[] screenPos = new float[] { 0.15f, 0.765f };
    public float scale = 1.0f;
    public float  incline = 0.0f;
    public float  rot = 0.0f;

    @Override
    public NBTTagCompound save(NBTTagCompound compound) {
        NBTTagCompound compassNbt = new NBTTagCompound();
        compassNbt.setString("Name", name);
        compassNbt.setString("Title", title);
        compassNbt.setString("NPCName", npc);
        compassNbt.setInteger("DimensionID", dimension);
        compassNbt.setIntArray("BlockPos", new int[] { pos.getX(), pos.getY(), pos.getZ() });
        compassNbt.setInteger("Range", range);
        compassNbt.setInteger("Type", type);
        compassNbt.setBoolean("ShowOfPlayer", showOfPlayer);
        compassNbt.setBoolean("IsCustomPoint", isCustomPoint);
        compassNbt.setBoolean("QuestLogIsFast", questLogIsFast);
        compassNbt.setBoolean("IsShowDial", showDial);
        compassNbt.setBoolean("IsFlat", isFlat);
        compassNbt.setFloat("Scale", scale);
        compassNbt.setFloat("Rotation", rot);
        compassNbt.setFloat("Incline", incline);
        compassNbt.setByteArray("Showed", new byte[] { (byte) (showQuestName ? 1 : 0), (byte) (showTaskProgress ? 1 : 0) });
        NBTTagList scP = new NBTTagList();
        scP.appendTag(new NBTTagFloat(screenPos[0]));
        scP.appendTag(new NBTTagFloat(screenPos[1]));
        compassNbt.setTag("ScreenPos", scP);
        compound.setTag(dataName, compassNbt);
        return compound;
    }

    @Override
    public void load(NBTTagCompound compound) {
        if (compound == null || !compound.hasKey(dataName, 10)) { return; }
        NBTTagCompound compassNbt = compound.getCompoundTag(dataName);
        name = compassNbt.getString("Name");
        title = compassNbt.getString("Title");
        npc = compassNbt.getString("NPCName");
        int[] p = compassNbt.getIntArray("BlockPos");
        if (p.length >= 3) { pos = new BlockPos(p[0], p[1], p[2]); }
        setDimensionID(compassNbt.getInteger("DimensionID"));
        setRange(compassNbt.getInteger("Range"));
        setType(compassNbt.getInteger("Type"));
        isCustomPoint = compassNbt.getBoolean("IsCustomPoint");
        questLogIsFast = compassNbt.getBoolean("QuestLogIsFast");
        isFlat = compassNbt.getBoolean("IsFlat");
        if (compassNbt.hasKey("IsShowDial", 1)) { showDial = compassNbt.getBoolean("IsShowDial"); }
        if (compassNbt.hasKey("ShowOfPlayer", 1)) { showOfPlayer = compassNbt.getBoolean("ShowOfPlayer"); }
        scale = compassNbt.getFloat("Scale");
        rot = compassNbt.getFloat("Rotation");
        incline = compassNbt.getFloat("Incline");
        NBTTagList list = compassNbt.getTagList("ScreenPos", 5);
        if (list.tagCount() >= 2) {
            screenPos[0] = list.getFloatAt(0);
            screenPos[1] = list.getFloatAt(1);
        }
        byte[] s = compassNbt.getByteArray("Showed");
        if (s.length >= 2) {
            showQuestName = s[0] == (byte) 1;
            showTaskProgress = s[1] == (byte) 1;
        }
    }

    @Override
    public int getDimensionID() { return dimension; }

    @Override
    public String getName() { return name; }

    @Override
    public String getNPCName() { return npc; }

    @Override
    public IPos getPos() { return new BlockPosWrapper(pos); }

    @Override
    public int getRange() { return range; }

    @Override
    public String getTitle() { return title; }

    @Override
    public int getType() { return type; }

    @Override
    public boolean isCustomPoint() { return isCustomPoint; }

    @Override
    public boolean getShowOfPlayer() { return showOfPlayer; }

    @Override
    public boolean isShowDial() { return showDial; }

    @Override
    public boolean isFlat() { return isFlat; }

    @Override
    public void setDimensionID(int dimensionId) {
        if (CustomNpcs.Server == null) { dimension = dimensionId; }
        else {
            for (WorldServer world : CustomNpcs.Server.worlds) {
                if (world.provider.getDimension() == dimensionId) {
                    dimension = dimensionId;
                    break;
                }
            }
        }
    }

    @Override
    public void setName(String nameIn) {
        if (nameIn != null) { name = nameIn; }
    }

    @Override
    public void setNPCName(String npcName) {
        if (npcName != null) { npc = npcName; }
    }

    @Override
    public void setPos(int x, int y, int z) { pos = new BlockPos(x, y, z); }

    @Override
    public void setPos(IPos posIn) { pos = posIn.getMCBlockPos(); }

    @Override
    public void setRange(int rangeIn) {
        if (rangeIn < 0) { rangeIn *= -1; }
        range = ValueUtil.correctInt(rangeIn, 3, 64);
    }

    @Override
    public void setIsCustomPoint(boolean show) { isCustomPoint = show; }

    @Override
    public void setShowOfPlayer(boolean show) { showOfPlayer = show; }

    @Override
    public void setShowDial(boolean show) { showDial = show; }

    @Override
    public void setIsFlat(boolean flat) { isFlat = flat; }

    @Override
    public void setTitle(String titleIn) {
        if (titleIn != null) { title = titleIn; }
    }

    @Override
    public void setType(int typeIn) {
        if (typeIn < 0) { typeIn *= -1; }
        type = typeIn % EnumQuestTask.values().length;
    }

}
