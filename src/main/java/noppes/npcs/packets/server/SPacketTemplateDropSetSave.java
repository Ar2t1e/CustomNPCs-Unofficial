package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DropController;
import noppes.npcs.controllers.data.DropsTemplate;
import noppes.npcs.entity.data.DropSet;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.shared.common.util.LogWriter;

import java.util.Collections;
import java.util.List;

public class SPacketTemplateDropSetSave extends PacketServerBasic {

    protected static int channelId;
    private String templateId;
    private String groupId;
    private NBTTagCompound compound;

    public SPacketTemplateDropSetSave() { }

    public SPacketTemplateDropSetSave(String templateIdIn, String groupIdIn, NBTTagCompound compoundIn) {
        templateId = templateIdIn;
        groupId = groupIdIn;
        compound = compoundIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_DROP_TEMPLATE); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(templateId);
        buf.writeUtf(groupId);
        buf.writeNbt(compound);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        templateId = buf.readUtf();
        groupId = buf.readUtf();
        compound = buf.readNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (templateId == null || templateId.isEmpty()) {
            LogWriter.warn("SaveDropsName is empty for template drop type, ignoring");
        } // Validate saveDropsName for template drops
        else {
            DropController dData = DropController.getInstance();
            DropsTemplate template = dData.templates.get(templateId);
            if (template != null) {
                int slot = compound.getInteger("Slot");
                ItemStack stack = new ItemStack(compound.getCompoundTag("Item"));
                if (stack.isEmpty()) {
                    if (slot < 0) { template.removeGroup(groupId); }
                    else { template.removeDrop(groupId, slot); }
                } // remove
                else if (slot == -1) {
                    DropSet drop = template.addDropItem(groupId, stack, 85.0d);
                    if (drop != null) { drop.load(compound); }
                } // add new
                else if (template.groups.containsKey(groupId) && template.groups.get(groupId).containsKey(slot)) {
                    template.groups.get(groupId).get(slot).load(compound);
                } // change
                dData.save();
                dData.sendTo(player);
            }
        }
        CustomNpcs.debugData.end("Packets");
    }
    
}
