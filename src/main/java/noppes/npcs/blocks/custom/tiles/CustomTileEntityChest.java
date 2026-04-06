package noppes.npcs.blocks.custom.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.custom.CustomChest;
import noppes.npcs.containers.ContainerChestCustom;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

public class CustomTileEntityChest extends ChestBlockEntity implements LidBlockEntity {

    protected @Nonnull SoundEvent sound_open;
    protected @Nonnull SoundEvent sound_close;
    protected String name;
    protected String blockName;
    public final CustomChest block;
    public ResourceLocation chestTexture;
    public int guiColor;
    public int[] guiColorArr;
    public boolean isChest;

    private NonNullList<ItemStack> items;
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state) { playSound(level, pos, state, sound_open); }

        @Override
        protected void onClose(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state) { playSound(level, pos, state, sound_close); }

        @Override
        protected void openerCountChanged(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, int type, int variant) { }

        @Override
        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ContainerChestCustom container) {
                return container.customChest == CustomTileEntityChest.this;
            }
            return false;
        }
    };
    private final ChestLidController chestLidController = new ChestLidController();

    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, CustomTileEntityChest tile) {
        tile.chestLidController.tickLid();
    }

    static void playSound(Level level, BlockPos pos, BlockState state, SoundEvent sound) {
        double d0 = (double) pos.getX() + 0.5D;
        double d1 = (double) pos.getY() + 0.5D;
        double d2 = (double) pos.getZ() + 0.5D;
        level.playSound(null, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    public static int getOpenCount(@Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        if (blockstate.hasBlockEntity()) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof CustomTileEntityChest tile) {
                return tile.openersCounter.getOpenerCount();
            }
        }
        return 0;
    }

    public static void swapContents(CustomTileEntityChest tile, CustomTileEntityChest nextTile) {
        NonNullList<ItemStack> nonnulllist = tile.getItems();
        tile.setItems(nextTile.getItems());
        nextTile.setItems(nonnulllist);
    }

    public CustomTileEntityChest(BlockPos pos, BlockState state) {
        super(CustomBlocks.tile_custom_chest, pos, state);
        block = ((CustomChest) state.getBlock());
        isChest = block.isChest;
        blockName = block.getCustomName();
        CompoundTag nbtData = block.getCustomNbt().getMCNBT();
        chestTexture = nbtData.isEmpty() ? null
                : new ResourceLocation(CustomNpcs.MODID,
                "textures/entity/chest/" + blockName + ".png");
        if (nbtData.contains("Name", 8)) { name = nbtData.getString("Name"); }
        else { name = "custom.chest." + blockName; }
        if (nbtData.contains("GUIColor", 3)) { guiColor = nbtData.getInt("GUIColor"); }
        else { guiColor = -1; }
        if (nbtData.contains("GUIColor", 11)) {
            guiColor = -1;
            guiColorArr = nbtData.getIntArray("GUIColor");
        }
        else { guiColorArr = null; }
        items = NonNullList.withSize(nbtData.contains("Size", 3) ? ValueUtil.correctInt(nbtData.getInt("Size"), 1, 189) : 9,
                ItemStack.EMPTY);
        SoundEvent soundOpen = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(nbtData.getString("SoundOpen")));
        if (soundOpen == null) { sound_open = SoundEvents.CHEST_OPEN; }
        else { sound_open = soundOpen; }
        SoundEvent soundClose = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(nbtData.getString("SoundClose")));
        if (soundClose == null) { sound_close = SoundEvents.CHEST_CLOSE; }
        else { sound_close = soundClose; }
    }

    @Override
    public int getContainerSize() { return items.size(); }

    @Override
    protected @Nonnull Component getDefaultName() {
        if (name.isEmpty()) { return Component.translatable("custom.chest.chestexample"); }
        return Component.translatable(name);
    }

    @Override
    public void load(@Nonnull CompoundTag compound) {
        super.load(compound);
        if (!compound.contains("Items", 9)) {
            if (level != null && !level.isClientSide()) {
                CompoundTag nbt = new CompoundTag();
                saveAdditional(nbt);
                Packets.sendAll(new SPacketTileEntitySave(nbt));
            }
            return;
        }
        isChest = compound.getBoolean("IsChest");
        guiColor = -1;
        guiColorArr = null;
        name = compound.getString("CustomName");
        if (compound.contains("Texture", 8)) { chestTexture = new ResourceLocation(compound.getString("Texture")); }
        else { chestTexture = null; }
        if (compound.contains("GUIColor", 11)) { guiColorArr = compound.getIntArray("GUIColor"); }
        else if (compound.contains("GUIColor", 3)) { guiColor = compound.getInt("GUIColor"); }
        SoundEvent soundOpen = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(compound.getString("SoundOpen")));
        if (soundOpen == null) { sound_open = SoundEvents.CHEST_OPEN; }
        else { sound_open = soundOpen; }
        SoundEvent soundClose = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(compound.getString("SoundClose")));
        if (soundClose == null) { sound_close = SoundEvents.CHEST_CLOSE; }
        else { sound_close = soundClose; }
        int size = compound.contains("Size", 3) ? ValueUtil.correctInt(compound.getInt("Size"), 1, 189) : 9;
        if (size != items.size()) { items = NonNullList.withSize(size, ItemStack.EMPTY); }
        items.clear();
        if (!tryLoadLootTable(compound)) { ContainerHelper.loadAllItems(compound, items); }

    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("IsChest", isChest);
        compound.putInt("Size", items.size());
        compound.putString("CustomName", name.isEmpty() ? "custom.chest.chestexample" : name);
        if (chestTexture != null) { compound.putString("Texture", chestTexture.toString()); }
        if (guiColor != -1) { compound.putInt("GUIColor", guiColor); }
        if (guiColorArr != null) { compound.putIntArray("GUIColor", guiColorArr); }
        compound.putString("SoundOpen", sound_open.getLocation().toString());
        compound.putString("SoundClose", sound_close.getLocation().toString());
        if (!trySaveLootTable(compound)) { ContainerHelper.saveAllItems(compound, items); }

    }

    @Override
    public boolean triggerEvent(int type, int variant) {
        if (type == 1) {
            chestLidController.shouldBeOpen(variant > 0);
            return true;
        }
        return false;
    }

    @Override
    public void startOpen(@Nonnull Player player) {
        if (!remove && !player.isSpectator() && level != null) {
            openersCounter.incrementOpeners(player, level, getBlockPos(), getBlockState());
        }
    }

    @Override
    public void stopOpen(@Nonnull Player player) {
        if (!remove && !player.isSpectator() && level != null) {
            openersCounter.decrementOpeners(player, level, getBlockPos(), getBlockState());
        }
    }

    @Override
    protected @Nonnull NonNullList<ItemStack> getItems() { return items; }

    @Override
    protected void setItems(@Nonnull NonNullList<ItemStack> newItems) { items = newItems; }

    @Override
    public float getOpenNess(float openness) { return chestLidController.getOpenness(openness); }

    @Override
    protected @Nonnull AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory inv) {
        return new ContainerChestCustom(containerId, inv, this);
    }

    private LazyOptional<net.minecraftforge.items.IItemHandlerModifiable> chestHandler;

    @Override
    public void setBlockState(@Nonnull BlockState state) {
        super.setBlockState(state);
        if (chestHandler != null) {
            LazyOptional<?> oldHandler = chestHandler;
            chestHandler = null;
            oldHandler.invalidate();
        }
    }

    @Override
    public @Nonnull <T> LazyOptional<T> getCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<T> cap, @Nonnull Direction side) {
        if (cap == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && !remove) {
            if (chestHandler == null)
                chestHandler = LazyOptional.of(this::createHandler);
            return chestHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private net.minecraftforge.items.IItemHandlerModifiable createHandler() {
        return new net.minecraftforge.items.wrapper.InvWrapper(this);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (chestHandler != null) {
            chestHandler.invalidate();
            chestHandler = null;
        }
    }

    @Override
    public void recheckOpen() {
        if (!remove && level != null) {
            openersCounter.recheckOpeners(level, getBlockPos(), getBlockState());
        }
    }

    @Override
    protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int type, int variant) {
        if (isChest) {
            level.blockEvent(pos, state.getBlock(), 1, variant);
        }
    }

}
