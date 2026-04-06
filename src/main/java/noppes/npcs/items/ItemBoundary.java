package noppes.npcs.items;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemBoundary;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.Zone3D;
import noppes.npcs.packets.server.SPacketGuiOpen;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class ItemBoundary extends Item implements INPCToolItem, IItemBoundary {

    public ItemBoundary() { super((new Item.Properties()).stacksTo(1)); }

    public void leftClick(ItemStack stack, ServerPlayer player) {
        PlayerData data = PlayerData.get(player);
        if (data == null) { return; }
        int id = -1;
        Vec3 vec3d = player.getEyePosition(1.0F);
        Vec3 vec3d1 = player.getViewVector(1.0F);
        Vec3 vec3d2 = vec3d.add(vec3d1.x * 5.0d, vec3d1.y * 5.0d, vec3d1.z * 5.0d);
        BlockHitResult result = player.level().clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        int x = result.getBlockPos().getX();
        int y = result.getBlockPos().getY();
        int z = result.getBlockPos().getZ();
        try {
            switch (result.getDirection()) {
                case UP: y += 1; break;
                case NORTH: z -= 1; break;
                case SOUTH: z += 1; break;
                case WEST: x -= 1; break;
                case EAST: x += 1; break;
                default: y -= 1; break;
            }
        }
        catch (Exception e) { LogWriter.error(e); }
        BlockPos pos = new BlockPos(x, y, z);
        if (stack.getTag() != null && stack.getTag().contains("RegionID", 3)) { id = stack.getTag().getInt("RegionID"); }
        // Shift + LMB = New Region
        if (data.overlay.isPressedShift()) {
            Zone3D reg = BorderController.getInstance().createNew(player.level().dimension().location().toString(), pos);
            BorderController.getInstance().save();
            BorderController.getInstance().update(reg.getId());
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.BoundarySetting, null, new BlockPos(reg.getId(), 0, 0));
            CompoundTag compound = stack.getOrCreateTag();
            compound.putInt("RegionID", reg.getId());
            return;
        }
        // LMB = remove point
        Zone3D reg = BorderController.getInstance().getRegion(id);
        if (reg == null || result.getType() == HitResult.Type.MISS) { return; }
        Point p = reg.points.get(reg.getIdNearestPoint(player.blockPosition()));
        if (p == null || !reg.contains(p.x, p.y)) { return; }
        boolean remove = reg.removePoint(p.x, p.y);
        player.sendSystemMessage(Component.translatable("message.boundary.del.vertex." + remove, "" + p.x, "" + p.y, reg.toString()));
        if (remove) {
            reg.fix();
            BorderController.getInstance().save();
            BorderController.getInstance().update(id);
        }
    }

    public void rightClick(ItemStack stack, ServerPlayer player) {
        PlayerData data = PlayerData.get(player);
        if (data == null) { return; }
        Vec3 vec3d = player.getEyePosition(1.0F);
        Vec3 vec3d1 = player.getViewVector(1.0F);
        Vec3 vec3d2 = vec3d.add(vec3d1.x * 5.0d, vec3d1.y * 5.0d, vec3d1.z * 5.0d);
        BlockHitResult result = player.level().clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        int id = -1;
        if (stack.getTag() != null && stack.getTag().contains("RegionID", 3)) { id = stack.getTag().getInt("RegionID"); }
        Zone3D reg = BorderController.getInstance().getRegion(id);
        int x = result.getBlockPos().getX();
        int y = result.getBlockPos().getY();
        int z = result.getBlockPos().getZ();
        try {
            switch (result.getDirection()) {
                case UP: y += 1; break;
                case NORTH: z -= 1; break;
                case SOUTH: z += 1; break;
                case WEST: x -= 1; break;
                case EAST: x += 1; break;
                default: y -= 1; break;
            }
        } catch (Exception e) { LogWriter.error(e); }
        BlockPos pos = new BlockPos(x, y, z);
        // Shift + RMB = Show Region settings
        if (reg == null || result.getType() == HitResult.Type.MISS || data.overlay.isPressedShift()) {
            if (reg == null && !BorderController.getInstance().regions.isEmpty()) {
                List<Zone3D> list = BorderController.getInstance().getNearestRegions(player.level().dimension().location().toString(), x, y, z, 1);
                if (!list.isEmpty()) {reg = list.get(0); }
            }
            if (reg == null && result.getType() != HitResult.Type.MISS) { reg = BorderController.getInstance().createNew(player.level().dimension().location().toString(), pos); }
            if (reg != null) {
                CompoundTag compound = player.getMainHandItem().getOrCreateTag();
                compound.putInt("RegionID", reg.getId());
                SPacketGuiOpen.sendOpenGui(player, EnumGuiType.BoundarySetting, null, new BlockPos(id, reg.getIdNearestPoint(player.blockPosition()), 0));
            }
            return;
        }
        // RMB = add point
        boolean add = false;
        if (reg.contains(pos.getX(), pos.getZ())) { // Offset Y min/max
            int min = Math.abs(reg.y[0] - pos.getY());
            int max = Math.abs(reg.y[1] - pos.getY());
            if (min <= max) {
                reg.y[0] = pos.getY();
            } else {
                reg.y[1] = pos.getY();
                add = true;
            }
            player.sendSystemMessage(Component.translatable("message.boundary.offset.y." + add, "" + pos.getX(), "" + pos.getY(), "" + pos.getZ(), reg.toString()));
            add = true;
        } else { // add new point
            add = reg.insertPoint(pos.getX(), pos.getY(), pos.getZ(),
                    Objects.requireNonNull(NpcAPI.Instance()).getIPos(player.getX(), player.getY(), player.getZ()));
            player.sendSystemMessage(Component.translatable("message.boundary.add.vertex." + add, "" + pos.getX(),
                    "" + pos.getY(), "" + pos.getZ(), reg.toString()));
        }
        if (add) {
            reg.fix();
            BorderController.getInstance().save();
            BorderController.getInstance().update(reg.getId());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> list, @Nonnull TooltipFlag flagIn) {
        list.add(Component.translatable("info.item.boundary", Component.translatable("tile.npcborder.name").getString()));
        Zone3D reg = null;
        if (stack.getTag() != null && stack.getTag().contains("RegionID", 3)) {
            reg = BorderController.getInstance().getRegion(stack.getTag().getInt("RegionID"));
        }
        if (reg == null) {
            list.add(Component.translatable("info.item.boundary.2"));
            list.add(Component.translatable("info.item.boundary.3"));
            return;
        }
        for (int i = 0; i < 4; i++) {
            list.add(Component.translatable("info.item.boundary." + i));
        }
        list.add(Component.translatable("info.item.boundary.4", "" + reg.getId(), reg.name));
    }

}
