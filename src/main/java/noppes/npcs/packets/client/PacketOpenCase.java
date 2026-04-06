package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.gui.player.GuiOpenCase;
import noppes.npcs.shared.common.PacketBasic;

import java.util.*;

public class PacketOpenCase extends PacketBasic {

    protected static int channelId;
    private final Map<ItemStack, Integer> map = new LinkedHashMap<>();
    private int dealID;

    public PacketOpenCase() { }

    public PacketOpenCase(int dealIDIn, List<ItemStack> inventory) {
        dealID = dealIDIn;
        for (ItemStack stack : inventory) {
            if (NoppesUtilServer.isItemStackNull(stack) || stack.isEmpty()) { continue; }
            boolean found = false;
            for (ItemStack st : map.keySet()) {
                if (NoppesUtilServer.isItemStackNull(st) || st.isEmpty()) { continue; }
                if (NoppesUtilPlayer.compareItems(stack, st, false, false)) {
                    map.put(st, map.get(st) + stack.getCount());
                    found = true;
                    break;
                }
            }
            if (!found) { map.put(stack, stack.getCount()); }
        }
    }

    public PacketOpenCase(int dealIDIn, Map<ItemStack, Integer> mapIn) {
        dealID = dealIDIn;
        map.putAll(mapIn);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        dealID = buf.readInt();
        map.clear();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            NBTTagCompound nbt = buf.readNbt();
            int count = nbt.getInteger("CnpcStackSize");
            nbt.removeTag("CnpcStackSize");
            map.put(new ItemStack(nbt), count);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(dealID);
        buf.writeInt(map.size());
        for (ItemStack stack : map.keySet()) {
            NBTTagCompound nbt = new NBTTagCompound();
            stack.writeToNBT(nbt);
            nbt.setInteger("CnpcStackSize", map.get(stack));
            buf.writeNbt(nbt);
        }
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!map.isEmpty()) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.displayGuiScreen(new GuiOpenCase((GuiContainer) mc.currentScreen, dealID, map));
        }
        CustomNpcs.debugData.end("Packets");
    }

}