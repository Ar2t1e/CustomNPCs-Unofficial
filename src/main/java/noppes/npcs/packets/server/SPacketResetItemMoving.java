package noppes.npcs.packets.server;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemNpcMovingPath;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketMenuSave;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.Util;

public class SPacketResetItemMoving extends PacketServerBasic {

    protected static int channelId;

    public SPacketResetItemMoving() { }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketResetItemMoving ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static SPacketResetItemMoving decode(FriendlyByteBuf ignoredBuf) { return new SPacketResetItemMoving(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        CustomNPCsScheduler.runTack(() -> {
            boolean isChanged = false;
            for (int i =0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() instanceof ItemNpcMovingPath && getIsNotFoundNpc(stack)) { isChanged = true; }
            }
            if (isChanged) { player.containerMenu.broadcastChanges(); }
        });
        CustomNpcs.debugData.end("Packets");
    }

    protected boolean getIsNotFoundNpc(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (compound != null && compound.contains("NPCID", 3)) {
            EntityNPCInterface npcIn = null;
            ServerLevel level;
            MinecraftServer server = player.getServer() != null ? player.getServer() : CustomNpcs.Server;
            if (compound.contains("NPCDIM", 8) && server != null) {
                level = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("NPCDIM"))));
            }
            else { level = (ServerLevel) player.level(); }
            if (level != null) {
                if (level.getEntity(compound.getInt("NPCID")) instanceof EntityNPCInterface cnpc) { npcIn = cnpc; }
                else if (Util.instance.getEntityByUUID(compound.getUUID("NPCUUID"), level, true) instanceof EntityNPCInterface cnpc) { npcIn = cnpc; }
            }
            else if (player.level().getEntity(compound.getInt("NPCID")) instanceof EntityNPCInterface cnpc) { npcIn = cnpc; }
            if (npcIn == null) {
                compound.remove("NPCID");
                compound.remove("NPCUUID");
                compound.remove("NPCDIM");
                if (compound.isEmpty()) { stack.setTag(null); }
                return true;
            }
            if (npcIn.getId() != compound.getInt("NPCID") ||
                    level == null ||
                    !npcIn.level().dimension().equals(level.dimension()) ||
                    !npcIn.getUUID().equals(compound.getUUID("NPCUUID"))) {
                compound.putInt("NPCID", npcIn.getId());
                compound.putUUID("NPCUUID", npcIn.getUUID());
                compound.putString("NPCDIM", npcIn.level().dimension().location().toString());
                if (level != null && player.level().dimension().equals(level.dimension())) {
                    Packets.send(player, new PacketMenuSave(npcIn, EnumMenuType.MOVING_PATH));
                }
                return true;
            }
        }
        return false;
    }

}