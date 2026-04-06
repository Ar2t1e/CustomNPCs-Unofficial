package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketEmotionChange extends PacketServerBasic {

    protected static int channelId;
    private int type;
    private NBTTagCompound data;

    public SPacketEmotionChange() { }

    /**
     * @param typeIn - 0: clear; 1: delete; 2: load
     * @param dataIn - emotion nbt tags
     */
    public SPacketEmotionChange(int typeIn, NBTTagCompound dataIn) {
        type = typeIn;
        data = dataIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        buf.writeNbt(data);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        type = buf.readInt();
        data = buf.readAnySizeNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        AnimationController aData = AnimationController.getInstance();
        switch (type) {
            case 0: {
                aData.clearEmotions();
                aData.sendEmotionToAll(-1);
                break;
            } // clear
            case 1: {
                aData.removeEmotion(data.getInteger("ID"));
                aData.sendEmotionToAll(data.getInteger("ID"));
                break;
            } // delete
            case 2: {
                aData.loadEmotion(data);
                aData.sendEmotionToAll(data.getInteger("ID"));
                break;
            } // load
        }
        CustomNpcs.debugData.end("Packets");
    }

}
