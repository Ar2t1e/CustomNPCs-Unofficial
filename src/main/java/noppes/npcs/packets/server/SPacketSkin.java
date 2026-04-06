package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.PlayerSkinController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketSkin extends PacketServerBasic {

    protected static int channelId;
    private final CompoundTag data;

    public SPacketSkin(CompoundTag stackNBT) { data = stackNBT; }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    public static void encode(SPacketSkin msg, FriendlyByteBuf buf) {buf.writeNbt(msg.data); }

    public static SPacketSkin decode(FriendlyByteBuf buf) { return new SPacketSkin(buf.readNbt(new NbtAccounter(Long.MAX_VALUE))); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerSkinController.getInstance().loadPlayerSkin(data);
        PlayerSkinController.getInstance().sendToAll(player.getUUID());
        CustomNpcs.debugData.end("Packets");
    }

}