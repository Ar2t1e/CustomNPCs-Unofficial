package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.gui.player.GuiOpenCase;
import noppes.npcs.shared.common.PacketBasic;

import java.util.*;

public class PacketOpenCase extends PacketBasic {

    protected static int channelId;
    private final Map<ItemStack, Integer> map = new LinkedHashMap<>();
    private final int dealID;

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

    public static void encode(PacketOpenCase msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.dealID);
        buf.writeInt(msg.map.size());
        for (ItemStack stack : msg.map.keySet()) {
            CompoundTag nbt = new CompoundTag();
            stack.save(nbt);
            nbt.putInt("CnpcStackSize", msg.map.get(stack));
            buf.writeNbt(nbt);
        }
    }

    public static PacketOpenCase decode(FriendlyByteBuf buf) {
        int dealID = buf.readInt();
        int size = buf.readInt();
        Map<ItemStack, Integer> mapIn = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            CompoundTag nbt = buf.readNbt();
            if (nbt != null) {
                int count = nbt.getInt("CnpcStackSize");
                nbt.remove("CnpcStackSize");
                mapIn.put(ItemStack.of(nbt), count);
            }
        }
        return new PacketOpenCase(dealID, mapIn);
    }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!map.isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new GuiOpenCase(mc.screen, dealID, map));
        }
        CustomNpcs.debugData.end("Packets");
    }

}