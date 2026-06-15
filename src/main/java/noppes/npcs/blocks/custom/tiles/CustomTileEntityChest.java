package noppes.npcs.blocks.custom.tiles;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.custom.CustomChest;
import noppes.npcs.containers.ContainerChestCustom;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

public class CustomTileEntityChest extends BlockEntity implements net.minecraft.world.Container, net.minecraft.world.MenuProvider {

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

    // For chest animation
    public float lidAngle;
    public float prevLidAngle;
    private int numPlayersUsing;

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
            playSound(level, pos, state, sound_open);
        }

        @Override
        protected void onClose(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
            playSound(level, pos, state, sound_close);
        }

        @Override
        protected void openerCountChanged(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, int count, int openCount) {
            if (isChest) {
                level.blockEvent(pos, state.getBlock(), 1, openCount);
            }
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ContainerChestCustom container) {
                return container.getTileEntity() == CustomTileEntityChest.this;
            }
            return false;
        }
    };

    static void playSound(Level level, BlockPos pos, BlockState ignoredState, SoundEvent sound) {
        double d0 = (double) pos.getX() + 0.5D;
        double d1 = (double) pos.getY() + 0.5D;
        double d2 = (double) pos.getZ() + 0.5D;
        level.playSound(null, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    public CustomTileEntityChest(BlockPos pos, BlockState state) {
        super(CustomBlocks.tile_custom_chest, pos, state);
        this.block = ((CustomChest) state.getBlock());
        this.isChest = block.isChest;
        this.blockName = block.getCustomName();
        CompoundTag nbtData = block.getCustomNbt().getMCNBT();

        this.chestTexture = nbtData.isEmpty() ? null
                : new ResourceLocation(CustomNpcs.MODID,
                "textures/entity/chest/" + blockName + ".png");

        if (nbtData.contains("Name", 8)) {
            this.name = nbtData.getString("Name");
        } else {
            this.name = "custom.chest." + blockName;
        }

        if (nbtData.contains("GUIColor", 3)) {
            this.guiColor = nbtData.getInt("GUIColor");
        } else {
            this.guiColor = -1;
        }

        if (nbtData.contains("GUIColor", 11)) {
            this.guiColor = -1;
            this.guiColorArr = nbtData.getIntArray("GUIColor");
        } else {
            this.guiColorArr = null;
        }

        int size = nbtData.contains("Size", 3) ? ValueUtil.correctInt(nbtData.getInt("Size"), 1, 189) : 9;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);

        SoundEvent soundOpen = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(nbtData.getString("SoundOpen")));
        this.sound_open = soundOpen != null ? soundOpen : SoundEvents.CHEST_OPEN;

        SoundEvent soundClose = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(nbtData.getString("SoundClose")));
        this.sound_close = soundClose != null ? soundClose : SoundEvents.CHEST_CLOSE;
    }

    // Client tick for lid animation
    public static void clientTick(Level ignoredLevel, BlockPos ignoredPos, BlockState ignoredState, CustomTileEntityChest tile) {
        tile.prevLidAngle = tile.lidAngle;
        // if (tile.numPlayersUsing > 0 && tile.lidAngle == 0.0F) { } // Sound is handled by ContainerOpenersCounter
        if (tile.numPlayersUsing == 0 && tile.lidAngle > 0.0F || tile.numPlayersUsing > 0 && tile.lidAngle < 1.0F) {
            if (tile.numPlayersUsing > 0) {
                tile.lidAngle += 0.1F;
            } else {
                tile.lidAngle -= 0.1F;
            }
            if (tile.lidAngle > 1.0F) {
                tile.lidAngle = 1.0F;
            }
            // if (tile.lidAngle < 0.5F && f2 >= 0.5F) { } // Close sound handled by counter
            if (tile.lidAngle < 0.0F) {
                tile.lidAngle = 0.0F;
            }
        }
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @Nonnull ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public @Nonnull ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public @Nonnull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr((double) worldPosition.getX() + 0.5D, (double) worldPosition.getY() + 0.5D, (double) worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public @Nonnull Component getDisplayName() {
        if (name.isEmpty()) { return Component.translatable("custom.chest.chestexample"); }
        return Component.translatable(name);
    }

    @Override
    public void load(@Nonnull CompoundTag compound) {
        super.load(compound);
        if (!compound.contains("Items", 9)) {
            return;
        }
        this.isChest = compound.getBoolean("IsChest");
        this.guiColor = -1;
        this.guiColorArr = null;
        this.name = compound.getString("CustomName");

        if (compound.contains("Texture", 8)) {
            this.chestTexture = new ResourceLocation(compound.getString("Texture"));
        } else {
            this.chestTexture = null;
        }

        if (compound.contains("GUIColor", 11)) {
            this.guiColorArr = compound.getIntArray("GUIColor");
        } else if (compound.contains("GUIColor", 3)) {
            this.guiColor = compound.getInt("GUIColor");
        }

        SoundEvent soundOpen = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(compound.getString("SoundOpen")));
        this.sound_open = soundOpen != null ? soundOpen : SoundEvents.CHEST_OPEN;

        SoundEvent soundClose = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(compound.getString("SoundClose")));
        this.sound_close = soundClose != null ? soundClose : SoundEvents.CHEST_CLOSE;

        int size = compound.contains("Size", 3) ? ValueUtil.correctInt(compound.getInt("Size"), 1, 189) : 9;
        if (size != items.size()) {
            items = NonNullList.withSize(size, ItemStack.EMPTY);
        }
        items.clear();
        ContainerHelper.loadAllItems(compound, items);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("IsChest", this.isChest);
        compound.putInt("Size", this.items.size());
        compound.putString("CustomName", this.name.isEmpty() ? "custom.chest.chestexample" : this.name);
        if (this.chestTexture != null) {
            compound.putString("Texture", this.chestTexture.toString());
        }
        if (this.guiColor != -1) {
            compound.putInt("GUIColor", this.guiColor);
        }
        if (this.guiColorArr != null) {
            compound.putIntArray("GUIColor", this.guiColorArr);
        }
        compound.putString("SoundOpen", this.sound_open.getLocation().toString());
        compound.putString("SoundClose", this.sound_close.getLocation().toString());
        ContainerHelper.saveAllItems(compound, this.items);
    }

    @Override
    public boolean triggerEvent(int type, int variant) {
        if (type == 1) {
            this.numPlayersUsing = variant;
            return true;
        }
        return super.triggerEvent(type, variant);
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
    public @Nonnull AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory inv, @Nonnull Player player) {
        return new ContainerChestCustom(containerId, inv, this);
    }

    // Lock support
    private String lockCode = "";

    public boolean isLocked() {
        return !lockCode.isEmpty();
    }

    public boolean canUnlock(Player player) {
        return lockCode.isEmpty() || lockCode.contains(player.getName().getString()) || player.isCreative();
    }

    @SuppressWarnings("unused")
    public void setLockCode(String code) {
        this.lockCode = code;
    }

    public String getLockCode() {
        return lockCode;
    }

    public float getOpenNess(float partialTicks) {
        return prevLidAngle + (lidAngle - prevLidAngle) * partialTicks;
    }

}