package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DropController;
import noppes.npcs.controllers.data.DropsTemplate;
import noppes.npcs.entity.data.DropSet;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.Collections;
import java.util.List;

public class SPacketNpcInvDropSetSave extends PacketServerBasic {

    protected static int channelId;
    private final int dropType;
    private final int groupId;
    private final int slot;
    private final CompoundTag compound;

    public SPacketNpcInvDropSetSave(int dropTypeIn, int groupIdIn, int slotIn, CompoundTag compoundIn) {
        dropType = dropTypeIn;
        groupId = groupIdIn;
        slot = slotIn;
        compound = compoundIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_INVENTORY); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketNpcInvDropSetSave msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.dropType);
        buf.writeInt(msg.groupId);
        buf.writeInt(msg.slot);
        buf.writeNbt(msg.compound);
    }

    public static SPacketNpcInvDropSetSave decode(FriendlyByteBuf buf) {
        return new SPacketNpcInvDropSetSave(buf.readInt(), buf.readInt(), buf.readInt(), buf.readAnySizeNbt());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ItemStack stack = ItemStack.of(compound.getCompound("Item"));
        DropsTemplate template = DropController.getInstance().templates.get(npc.inventory.saveDropsName);
        if (stack.isEmpty()) {
            if (dropType == 1) {
                if (template != null) {
                    if (slot < 0) { template.removeGroup(groupId); }
                    else { template.removeDrop(groupId, slot); }
                }
            }
            else { npc.inventory.removeDrop(slot); }
        } // remove
        else if (slot == -1) {
            DropSet drop = null;
            if (dropType == 1) {
                if (template != null) { drop = template.addDropItem(groupId, stack, 85.0d); }
            }
            else { drop = (DropSet) npc.inventory.addDropItem(stack, 1.0d); }
            if (drop != null) { drop.load(compound); }
        } // add new
        else {
            if (dropType == 1) {
                if (template != null && template.groups.containsKey(groupId) && template.groups.get(groupId).containsKey(slot)) {
                    template.groups.get(groupId).get(slot).load(compound);
                }
            }
            else if (npc.inventory.drops.containsKey(slot)) { npc.inventory.drops.get(slot).load(compound); }
        } // change
        npc.updateAI = true;
        npc.updateClient = true;
        if (dropType == 1) {
            DropController.getInstance().sendTo(player);
            DropController.getInstance().save();
        }
        Packets.send(player, new PacketGuiData(npc.inventory.save(new CompoundTag())));
        CustomNpcs.debugData.end("Packets");
    }

}
