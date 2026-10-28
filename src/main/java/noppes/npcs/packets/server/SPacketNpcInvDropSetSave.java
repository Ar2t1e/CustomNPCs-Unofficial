package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.data.DropSet;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.Collections;
import java.util.List;

public class SPacketNpcInvDropSetSave extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound compound;

    public SPacketNpcInvDropSetSave() { }

    public SPacketNpcInvDropSetSave(NBTTagCompound compoundIn) { compound = compoundIn; }

    @Override
    public boolean requiresNpc() { return true; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_INVENTORY); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(compound); }

    @Override
    public void decode(FriendlyByteBuf buf) { compound = buf.readNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        int slot = compound.getInteger("Slot");
        ItemStack stack = new ItemStack(compound.getCompoundTag("Item"));
        if (stack.isEmpty()) { npc.inventory.removeDrop(slot); } // remove
        else if (slot == -1) {
            DropSet drop = (DropSet) npc.inventory.addDropItem(stack, 1.0d);
            if (drop != null) { drop.load(compound); }
        } // add new
        else if (npc.inventory.drops.containsKey(slot)) { npc.inventory.drops.get(slot).load(compound); } // change
        npc.updateAI = true;
        npc.updateClient = true;
        Packets.send(player, new PacketGuiData(npc.inventory.save(new NBTTagCompound())));
        CustomNpcs.debugData.end("Packets");
    }

}
