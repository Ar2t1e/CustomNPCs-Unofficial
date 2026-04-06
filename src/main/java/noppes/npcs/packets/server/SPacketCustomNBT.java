package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomNBT extends PacketServerBasic {

    protected static int channelId;
    private final CompoundTag data;

    public SPacketCustomNBT(CompoundTag dataIn) { data = dataIn; }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    public static void encode(SPacketCustomNBT msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

    public static SPacketCustomNBT decode(FriendlyByteBuf buf) {
        return new SPacketCustomNBT(buf.readAnySizeNbt());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData pd = PlayerData.get(player);
        if (pd != null) {
            EventHooks.onEvent(pd.scriptData, EnumScriptType.PACKAGE_FROM, new PlayerEvent.PlayerPackage(pd.scriptData.getPlayer(), data));
        }
        CustomNpcs.debugData.end("Packets");
    }

}