package noppes.npcs.blocks.custom.tiles;

import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.blocks.custom.CustomBlockPortal;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

public class CustomTileEntityPortal extends TheEndPortalBlockEntity {

    protected ResourceLocation SKY_TEXTURE;
    protected ResourceLocation PORTAL_TEXTURE;
    public BlockPos posTp = new BlockPos(0, -1, 0);
    public BlockPos posHomeTp = new BlockPos(0, -1, 0);
    public ResourceKey<Level> dimensionId = Level.OVERWORLD;
    public ResourceKey<Level> homeDimensionId = Level.OVERWORLD;
    public float alpha = 0.5f;
    public int type;

    public CustomTileEntityPortal(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        super(CustomBlocks.tile_custom_portal, pos, state);
        type = state.getValue(CustomBlockPortal.TYPE);
        if (state.getBlock() instanceof CustomBlockPortal portal) {
            SKY_TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/environment/" + portal.getCustomName() + "_sky.png");
            PORTAL_TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/entity/" + portal.getCustomName() + "_portal.png");
        }
    }

    public @Nonnull ResourceLocation getPortalTexture() {
        if (PORTAL_TEXTURE == null && level != null) {
            BlockState state = level.getBlockState(worldPosition);
            if (state.getBlock() instanceof CustomBlockPortal portal) {
                PORTAL_TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/entity/" + portal.getCustomName() + "_portal.png");
            }
        }
        return PORTAL_TEXTURE != null ? PORTAL_TEXTURE : TheEndPortalRenderer.END_PORTAL_LOCATION;
    }

    public @Nonnull ResourceLocation getSkyTexture() {
        if (SKY_TEXTURE == null && level != null) {
            BlockState state = level.getBlockState(worldPosition);
            if (state.getBlock() instanceof CustomBlockPortal portal) {
                SKY_TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/environment/" + portal.getCustomName() + "_sky.png");
            }
        }
        return SKY_TEXTURE != null ? SKY_TEXTURE : TheEndPortalRenderer.END_SKY_LOCATION;
    }

    public BlockPos getPosTp(boolean isHome) {
        BlockPos pos = null;
        ServerLevel sLevel = null;
        MinecraftServer server = level != null ? level.getServer() : CustomNpcs.Server;
        if (isHome) {
            if (hasDimension(server, homeDimensionId)) {
                pos = new BlockPos(posHomeTp);
                if (server != null) { sLevel = server.getLevel(homeDimensionId); }
            }
        }
        else if (hasDimension(server, dimensionId)) {
            pos = new BlockPos(posTp);
            if (server != null) { sLevel = server.getLevel(dimensionId); }
        }
        if (pos == null) { pos  = new BlockPos(0, -1, 0); }
        if (pos.getY() < 0 && sLevel != null) { pos = new BlockPos(sLevel.getSharedSpawnPos()); }
        if (pos.getY() < 0) { pos.above(70 - pos.getY()); }
        return NoppesUtilServer.getSafeTpPos(sLevel, pos, 253, 1);
    }

    private boolean hasDimension(MinecraftServer server, ResourceKey<Level> dimensionId) {
        return dimensionId != null && ((server != null && server.getLevel(dimensionId) != null) || DimensionController.has(dimensionId.location()));
    }

    public void updateToClient() {
        if (level != null && !level.isClientSide()) {
            CompoundTag compound = new CompoundTag();
            saveAdditional(compound);
            Packets.sendAll(new SPacketTileEntitySave(compound));
        }
    }

    @Override
    public boolean shouldRenderFace(@Nonnull Direction facing) {
        return switch (type) {
            case 1 -> facing == Direction.SOUTH || facing == Direction.NORTH;
            case 2 -> facing == Direction.WEST || facing == Direction.EAST;
            default -> facing == Direction.UP || facing == Direction.DOWN;
        };
    }

    @Override
    public void load(@Nonnull CompoundTag compound) {
        super.load(compound);
        if (!compound.contains("DimensionID", 3)) {
            updateToClient();
            return;
        }
        dimensionId = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("DimensionID")));
        homeDimensionId = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("HomeDimensionID")));
        posHomeTp = BlockPos.of(compound.getLong("HomePosition"));
        posTp = BlockPos.of(compound.getLong("TpPosition"));
        alpha = ValueUtil.correctFloat(compound.getFloat("Alpha"), 0.15f, 1.0f);
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putString("DimensionID", dimensionId.location().toString());
        compound.putString("HomeDimensionID", homeDimensionId.location().toString());
        compound.putFloat("Alpha", alpha);
        compound.putLong("HomePosition", posHomeTp.asLong());
        compound.putLong("TpPosition", posTp.asLong());
    }

}
