package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemNpcMovingPath;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketMenuSave;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.CustomNPCsScheduler;

import java.util.List;
import java.util.UUID;

public class SPacketResetItemMoving extends PacketServerBasic {

    protected static int channelId;

    public SPacketResetItemMoving() { }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        CustomNPCsScheduler.runTack(() -> {
            boolean isChanged = false;
            for (int i =0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack.getItem() instanceof ItemNpcMovingPath && getIsNotFoundNpc(stack)) { isChanged = true; }
            }
            if (isChanged) { player.inventoryContainer.detectAndSendChanges(); }
        });
        CustomNpcs.debugData.end("Packets");
    }

    protected boolean getIsNotFoundNpc(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey("NPCID", 3)) {
            EntityNPCInterface npcIn = null;
            WorldServer world;
            MinecraftServer server = player.getServer() != null ? player.getServer() : CustomNpcs.Server;
            if (compound.hasKey("NPCDIM", 3) && server != null) { world = server.getWorld(compound.getInteger("NPCDIM")); }
            else { world = (WorldServer) player.world; }
            if (world != null) {
                Entity entity = world.getEntityByID(compound.getInteger("NPCID"));
                if (entity instanceof EntityNPCInterface) { npcIn = (EntityNPCInterface) entity; }
                else {
                    UUID uuid = compound.getUniqueId("NPCUUID");
                    for (Entity e : world.loadedEntityList) {
                        if (e.getUniqueID().equals(uuid) && e instanceof EntityNPCInterface) {
                            npcIn = (EntityNPCInterface) e;
                            break;
                        }
                    }
                }
            }
            else {
                @SuppressWarnings("ConstantConditions")
                Entity entity = player.world.getEntityByID(compound.getInteger("NPCID"));
                if (entity instanceof EntityNPCInterface) { npcIn = (EntityNPCInterface) entity; }
            }
            if (npcIn == null) {
                compound.removeTag("NPCID");
                compound.removeTag("NPCUUID");
                compound.removeTag("NPCDIM");
                if (compound.hasNoTags()) { stack.setTagCompound(null); }
                return true;
            }
            if (npcIn.getEntityId() != compound.getInteger("NPCID") ||
                    world == null ||
                    npcIn.world.provider.getDimension() != world.provider.getDimension() ||
                    !npcIn.getUniqueID().equals(compound.getUniqueId("NPCUUID"))) {
                compound.setInteger("NPCID", npcIn.getEntityId());
                compound.setUniqueId("NPCUUID", npcIn.getUniqueID());
                compound.setInteger("NPCDIM", npcIn.world.provider.getDimension());
                if (world != null && player.world.provider.getDimension() == world.provider.getDimension()) {
                    Packets.send(player, new PacketMenuSave(npcIn, EnumMenuType.MOVING_PATH));
                }
                return true;
            }
        }
        return false;
    }

}