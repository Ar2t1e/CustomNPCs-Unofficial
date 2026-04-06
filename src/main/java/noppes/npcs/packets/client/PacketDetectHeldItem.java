package noppes.npcs.packets.client;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDetectHeldItem extends PacketBasic {

    protected static int channelId;
    private int slotID;
    private NBTTagCompound data;

    public PacketDetectHeldItem() { }

    public PacketDetectHeldItem(int slotIDIn, NBTTagCompound dataIn) {
        slotID = slotIDIn;
        data = dataIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        slotID = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slotID);
        buf.writeNbt(data);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ItemStack stack = new ItemStack(data);
        if (slotID >= 0) { player.inventory.setInventorySlotContents(slotID, stack); }
        else { player.inventory.setItemStack(stack); }
        CustomNpcs.debugData.end("Packets");
    }

}