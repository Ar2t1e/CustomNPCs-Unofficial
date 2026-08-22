package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.item.ISpecBuilder;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.BuilderData;

import java.util.List;

public class SPacketSetBuildData extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound compound;

    public SPacketSetBuildData() { }

    public SPacketSetBuildData(NBTTagCompound compoundIn) { compound = compoundIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

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
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack.getItem() instanceof ISpecBuilder &&
                compound.hasKey("ID", 3) &&
                compound.hasKey("BuilderType", 3)) {
            int id = compound.getInteger("ID");
            BuilderData builder = SyncController.dataBuilder.get(id);
            if (id < 0 || builder == null) {
                if (id < 0 && builder != null) { SyncController.dataBuilder.remove(id); }
                id = 0;
                while (SyncController.dataBuilder.containsKey(id)) { id ++; }
                builder = new BuilderData(id, compound.getInteger("BuilderType"));
                compound.setInteger("ID", id);
            }
            builder.read(compound);
            SyncController.dataBuilder.put(id, builder);
            NBTTagCompound nbtStack = builder.getNbt();
            if (compound.getInteger("BuilderType") != ((ISpecBuilder) stack.getItem()).getType()) {
                switch (compound.getInteger("BuilderType")) {
                    case 1: stack = new ItemStack(CustomItems.npcbuilder); break;
                    case 2: stack = new ItemStack(CustomItems.npcreplacer); break;
                    case 3: stack = new ItemStack(CustomItems.npcplacer); break;
                    case 4: stack = new ItemStack(CustomItems.npcsaver); break;
                    default: stack = new ItemStack(CustomItems.npcremover); break;
                }
            }
            else { stack = stack.copy(); }
            stack.setTagCompound(nbtStack);
            player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
            player.openContainer.detectAndSendChanges();
            Packets.send(player, new PacketSyncUpdate(id, 7, nbtStack));
        }
        CustomNpcs.debugData.end("Packets");
    }

}