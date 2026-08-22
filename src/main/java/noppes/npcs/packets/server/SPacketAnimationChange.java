package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketAnimationChange extends PacketServerBasic {

    protected static int channelId;
    private int type;
    private NBTTagCompound data;

    public SPacketAnimationChange() { }

    /**
     * @param typeIn - 0: clear; 1: delete; 2: save all; 3: load
     * @param dataIn - animation nbt tags
     */
    public SPacketAnimationChange(int typeIn, NBTTagCompound dataIn) {
        type = typeIn;
        data = dataIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        buf.writeNbt(data);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        type = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        AnimationController aData = AnimationController.getInstance();
        switch (type) {
            case 0: {
                aData.clearAnimations();
                aData.sendAnimationToAll(-1);
                break;
            } // clear
            case 1: {
                aData.removeAnimation(data.getInteger("ID"));
                aData.sendAnimationToAll(data.getInteger("ID"));
                break;
            } // delete
            case 2: {
                aData.loadAnimation(data);
                aData.sendAnimationToAll(data.getInteger("ID"));
                break;
            } // load
            case 3: {
                aData.save();
                break;
            } // save all
        }
        CustomNpcs.debugData.end("Packets");
    }

}
