package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.api.wrapper.ScreenSize;

import java.util.*;

public class PlayerOverlayData implements IPlayerData {

    protected static final String dataName = "OverlayData";

    protected Map<RenderGameOverlayEvent.ElementType, Boolean> showElementTypes = new HashMap<>();
    public ScreenSize screenSize = new ScreenSize(0, 0);
    public Set<Integer> keyPress = new HashSet<>();
    public Set<Integer> mousePress = new HashSet<>();
    public boolean isMoved;
    public String currentGUI;
    public boolean updateClient; // ServerTickHandler.cnpcPlayerTick()

    public PlayerOverlayData() {
        for (RenderGameOverlayEvent.ElementType et : RenderGameOverlayEvent.ElementType.values()) { showElementTypes.put(et, true); }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound compound) {
        NBTTagCompound overlayNBT = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (Map.Entry<RenderGameOverlayEvent.ElementType, Boolean> entry : showElementTypes.entrySet()) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("K", entry.getKey().ordinal());
            nbt.setBoolean("V", entry.getValue());
            list.appendTag(nbt);
        }
        overlayNBT.setTag("ShowElementTypes", list);
        int[] iK = overlayNBT.getIntArray("KeyPress");
        int[] iM = overlayNBT.getIntArray("MousePress");
        keyPress.clear();
        mousePress.clear();
        for (int key : iK) { keyPress.add(key); }
        for (int key : iM) { mousePress.add(key); }
        overlayNBT.setDouble("ScreenWidth", screenSize.getWidth());
        overlayNBT.setDouble("ScreenHeight", screenSize.getHeight());
        compound.setTag(dataName, overlayNBT);
        return compound;
    }

    @Override
    public void load(NBTTagCompound compound) {
        if (compound == null || !compound.hasKey(dataName, 10) || !compound.hasKey("HUDData", 10)) { return; }
        NBTTagCompound overlayNBT = compound.hasKey(dataName, 10) ? compound.getCompoundTag(dataName) : compound.getCompoundTag("HUDData");
        int[] iK = overlayNBT.getIntArray("KeyPress");
        int[] iM = overlayNBT.getIntArray("MousePress");
        keyPress.clear();
        mousePress.clear();
        for (int key : iK) { keyPress.add(key); }
        for (int key : iM) { mousePress.add(key); }
        showElementTypes.clear();
        NBTTagList list = compound.getTagList("ShowElementTypes", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound nbt = list.getCompoundTagAt(i);
            int id = nbt.getInteger("K");
            if (id < 0) { id *= -1; }
            showElementTypes.put(RenderGameOverlayEvent.ElementType.values()[id % RenderGameOverlayEvent.ElementType.values().length], nbt.getBoolean("V"));
        }
        updateClient = false;
    }

    public int[] getKeyPressed() {
        int[] ids = new int[keyPress.size()];
        int i = 0;
        for (int key : keyPress) {
            ids[i] = key;
            i++;
        }
        return ids;
    }

    public int[] getMousePressed() {
        int[] ids = new int[mousePress.size()];
        int i = 0;
        for (int key : mousePress) {
            ids[i] = key;
            i++;
        }
        return ids;
    }

    public boolean hasMousePress(int key) {
        for (int k : mousePress) {
            if (k == key) {
                return true;
            }
        }
        return mousePress.contains(key);
    }

    public boolean hasOrKeysPressed(int... keys) {
        for (int key : keys) {
            for (int k : keyPress) {
                if (k == key) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPressedCtrl() { return hasOrKeysPressed(341, 345); }

    public boolean isPressedShift() { return hasOrKeysPressed(54, 42); }

    public ScreenSize getWindowSize() { return screenSize; }

    public boolean isShowElementType(RenderGameOverlayEvent.ElementType type) {
        if (!showElementTypes.get(RenderGameOverlayEvent.ElementType.ALL)) { return false; }
        return showElementTypes.get(type);
    }

}
