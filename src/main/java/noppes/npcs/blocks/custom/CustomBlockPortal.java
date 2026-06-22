package noppes.npcs.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityPortal;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemNpcBlock;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class CustomBlockPortal extends EndPortalBlock implements ICustomElement {

    protected static final VoxelShape SHAPE_1 = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 12.0D);
    protected static final VoxelShape SHAPE_2 = Block.box(6.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    public static IntegerProperty TYPE = IntegerProperty.create("type", 0, 2);

    protected final @Nonnull CompoundTag nbtData;

    public CustomBlockPortal(@Nonnull Properties properties, @Nonnull CompoundTag nbtBlock) {
        super(properties);
        nbtData = nbtBlock;
    }

    @Override
    public boolean canEntityDestroy(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> stateDefinition) {
        stateDefinition.add(TYPE);
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new CustomTileEntityPortal(pos, state);
    }
    @Override
    public @Nonnull VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return switch (state.getValue(TYPE)) {
            case 1 -> SHAPE_1;
            case 2 -> SHAPE_2;
            default -> SHAPE;
        };
    }

    @SuppressWarnings("deprecation")
    public @Nonnull VoxelShape getCollisionShape(@Nonnull BlockState blockState, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack item) {
        if (level.getBlockEntity(pos) instanceof CustomTileEntityPortal portalTile) {
            if (nbtData.contains("RenderData", 10)) {
                CompoundTag nbtRender = nbtData.getCompound("RenderData");
                if (nbtRender.contains("Transparency", 5)) {
                    portalTile.setAlpha(nbtRender.getFloat("Transparency"));
                }
            }
            CustomTileEntityPortal adjacent = null;
            for (Direction facing : Direction.values()) {
                adjacent = getAdjacentTile(level, pos.relative(facing));
                if (adjacent != null) break;
            }
            if (adjacent != null) {
                portalTile.type = adjacent.type;
                if (adjacent.posTp.getY() > -1) { portalTile.posTp = new BlockPos(adjacent.posTp); }
                if (adjacent.posHomeTp.getY() > -1) { portalTile.posHomeTp = new BlockPos(adjacent.posHomeTp); }
                portalTile.dimensionId = adjacent.dimensionId;
                portalTile.homeDimensionId = adjacent.homeDimensionId;
                portalTile.setAlpha(adjacent.getAlpha());
            }
            else {
                portalTile.type = placer == null || placer.getXRot() < -45 || placer.getXRot() > 45 ? 0 : 1;
                if (portalTile.type == 1 && (placer.getDirection() == Direction.EAST || placer.getDirection() == Direction.WEST)) { portalTile.type = 2; }
            }

            CompoundTag nbt = new CompoundTag();
            portalTile.saveAdditional(nbt);
            level.setBlock(pos, state.setValue(TYPE, portalTile.type), 3);
            BlockEntity newTile = level.getBlockEntity(pos);
            if (newTile instanceof CustomTileEntityPortal newPortal) {
                newPortal.load(nbt);
                newPortal.setChanged();
            }
        }
        super.setPlacedBy(level, pos, state, placer, item);
    }

    @Override
    public void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entityIn) {
        if (level instanceof ServerLevel sLevel && entityIn.canChangeDimensions() && Shapes.joinIsNotEmpty(Shapes.create(entityIn.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ())), state.getShape(sLevel, pos), BooleanOp.AND)) {
            if (entityIn instanceof ServerPlayer player && (player.getMainHandItem().getItem() instanceof INPCToolItem ||
                    (player.getMainHandItem().getItem() instanceof ItemNpcBlock itemBlock &&
                            itemBlock.getBlock() instanceof CustomBlockPortal)))
            { return; }

            ResourceKey<Level> id = Level.OVERWORLD;
            ResourceKey<Level> homeId = Level.OVERWORLD;
            if (nbtData.contains("DimensionID", 8)) {
                id = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbtData.getString("DimensionID")));
            }
            if (nbtData.contains("HomeDimensionID", 8)) {
                homeId = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbtData.getString("HomeDimensionID")));
            }
            BlockEntity blockTile = sLevel.getBlockEntity(pos);
            if (blockTile instanceof CustomTileEntityPortal tile) {
                if (hasDimension(sLevel.getServer(), tile.dimensionId)) { id = tile.dimensionId; }
                if (hasDimension(sLevel.getServer(), tile.homeDimensionId)) { homeId = tile.homeDimensionId; }
            }
            if (hasDimension(sLevel.getServer(), id)) { id = Level.OVERWORLD; }
            if (hasDimension(sLevel.getServer(), homeId)) { homeId = Level.OVERWORLD; }

            boolean isHome = sLevel.dimension().equals(id);
            BlockPos p = null;
            if (blockTile instanceof CustomTileEntityPortal tile) { p = tile.getPosTp(isHome); }
            if (p == null) {
                ServerLevel world = Objects.requireNonNull(sLevel.getServer()).getLevel(isHome ? homeId : id);
                p = Objects.requireNonNull(world).getSharedSpawnPos();
                while (p.getY() < 253 && (!sLevel.isEmptyBlock(p) || !sLevel.isEmptyBlock(p.above()))) { p = p.above(); }
            }
            if (entityIn instanceof ServerPlayer player) {
                PlayerEvent.CustomTeleport event = EventHooks.onPlayerTeleport(player, p, pos, isHome ? homeId : id);
                if (!event.isCanceled()) {
                    ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(NoppesUtilServer.validPath(event.dimension)));
                    if (!hasDimension(sLevel.getServer(), dimension)) { dimension = Level.OVERWORLD; }
                    SPacketDimensionTeleport.teleportPlayer(player, dimension, event.pos.getX() + 0.5d,
                            event.pos.getY(), event.pos.getZ() + 0.5d, entityIn.getYRot(),
                            entityIn.getXRot());
                }
            } else {
                ResourceKey<Level> dimension = isHome ? homeId : id;
                if (entityIn instanceof EntityNPCInterface) {
                    NpcEvent.CustomNpcTeleport event = EventHooks.onNpcTeleport((EntityNPCInterface) entityIn, p, pos, isHome ? homeId : id);
                    if (event.isCanceled() || entityIn.getRemovalReason() != null) { return; }
                    dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(NoppesUtilServer.validPath(event.dimension)));
                    if (!hasDimension(sLevel.getServer(), dimension)) { dimension = Level.OVERWORLD; }
                }
                ServerLevel world = level.getServer().getLevel(dimension);
                if (world != null && !entityIn.level().equals(world)) { entityIn = entityIn.changeDimension(world); }
                if (entityIn != null) { entityIn.setPos(p.getX() + 0.5d, p.getY(), p.getZ() + 0.5d); }
            }
        }
    }

    @Override
    public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource rndSource) {
        if (nbtData.contains("RenderData", 10)) {
            CompoundTag compound = nbtData.getCompound("RenderData");
            float f0 = compound.contains("ChanceParticle", 3) ?
                    ValueUtil.correctInt(compound.getInt("ChanceParticle"), 0, 100) / 100.0f : 0.1f;
            if (Math.random() < f0) {
                double d0 = (double) pos.getX() + rndSource.nextDouble();
                double d1 = (double) pos.getY() + 0.8D;
                double d2 = (double) pos.getZ() + rndSource.nextDouble();
                SimpleParticleType particle = ParticleTypes.CRIT;
                ParticleType<?> ept = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(compound.getString("SpawnParticle")));
                if (ept instanceof SimpleParticleType p) { particle = p; }
                level.addParticle(particle, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }

        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(this);
    }

    private boolean hasDimension(MinecraftServer server, ResourceKey<Level> dimensionId) {
        return dimensionId != null && ((server != null && server.getLevel(dimensionId) != null) || DimensionController.has(dimensionId.location()));
    }

    private @Nullable CustomTileEntityPortal getAdjacentTile(Level level, BlockPos pos) {
        BlockEntity t = level.getBlockEntity(pos);
        Block block = level.getBlockState(pos).getBlock();
        if (t instanceof CustomTileEntityPortal tile && block instanceof CustomBlockPortal pBlock && pBlock.getCustomName().equals(getCustomName())) {
            return tile;
        }
        return null;
    }

    @Override
    public String getCustomName() { return nbtData.getString("RegistryName"); }

    @Override
    public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

    @Override
    public int getElementType() {
        if (nbtData.contains("BlockType", 1)) { return nbtData.getByte("BlockType"); }
        return 1;
    }

    @Override
    public boolean showInCreative() { return !nbtData.contains("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative"); }


}
