package noppes.npcs.blocks.custom.tiles;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.custom.CustomChest;
import noppes.npcs.containers.ContainerChestCustom;
import noppes.npcs.mixin.util.ISoundEventMixin;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CustomTileEntityChest extends TileEntityLockableLoot implements ITickable {

	protected @Nonnull SoundEvent sound_open = SoundEvents.BLOCK_CHEST_OPEN;
	protected @Nonnull SoundEvent sound_close = SoundEvents.BLOCK_CHEST_CLOSE;

	public float lidAngle;
	public float prevLidAngle;
	public NonNullList<ItemStack> items;
	public @Nullable ResourceLocation chestTexture;
	private int numPlayersUsing = 0;
	public int guiColor = -1;
	public boolean isChest;
	private String name;
	private String blockName;
	public int[] guiColorArr;

	public CustomTileEntityChest() {
		setBlock(new CustomChest(Material.WOOD, new NBTTagCompound()));
	}

	@Override
	public void closeInventory(@Nonnull EntityPlayer player) {
		if (world == null) {
			return;
		}
		--numPlayersUsing;
		world.addBlockEvent(pos, getBlockType(), 1, numPlayersUsing);
		world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
	}

	public CustomTileEntityChest copy() {
		CustomTileEntityChest ctec = new CustomTileEntityChest();
		ctec.readFromNBT(writeToNBT(new NBTTagCompound()));
		return ctec;
	}

	@Override
	public @Nonnull Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
		fillWithLoot(playerIn);
		return new ContainerChestCustom(playerInventory, this, playerIn);
	}

	@Override
	public @Nonnull String getGuiID() { return blockName; }

	@Override
	public int getInventoryStackLimit() { return 64; }

	@Override
	protected @Nonnull NonNullList<ItemStack> getItems() { return items; }

	@Override
	public @Nonnull String getName() {
		if (name.isEmpty()) { return "custom.chest.chestexample"; }
		return name;
	}

	@Override
	public int getSizeInventory() { return items.size(); }

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : items) {
			if (!stack.isEmpty()) { return false; }
		}
		return true;
	}

	@Override
	public void openInventory(@Nonnull EntityPlayer player) {
		if (items != null && world != null) {
			++numPlayersUsing;
			world.addBlockEvent(pos, getBlockType(), 1, numPlayersUsing);
			world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
		}
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (!compound.hasKey("Items", 9)) { return; }
		isChest = compound.getBoolean("IsChest");
		guiColor = -1;
		guiColorArr = null;
		name = compound.getString("CustomName");
		if (compound.hasKey("Texture", 8)) { chestTexture = new ResourceLocation(compound.getString("Texture")); }
		else { chestTexture = null; }
		if (compound.hasKey("GUIColor", 11)) { guiColorArr = compound.getIntArray("GUIColor"); }
		else if (compound.hasKey("GUIColor", 3)) { guiColor = compound.getInteger("GUIColor"); }
		SoundEvent soundOpen = SoundEvent.REGISTRY.getObject(new ResourceLocation(compound.getString("SoundOpen")));
		if (soundOpen == null) { sound_open = SoundEvents.BLOCK_CHEST_OPEN; }
		else { sound_open = soundOpen; }
		SoundEvent soundClose = SoundEvent.REGISTRY.getObject(new ResourceLocation(compound.getString("SoundClose")));
		if (soundClose == null) { sound_close = SoundEvents.BLOCK_CHEST_CLOSE; }
		else { sound_close = soundClose; }
		int size = compound.hasKey("Size", 3) ? ValueUtil.correctInt(compound.getInteger("Size"), 1, 189) : 9;
		if (size != items.size()) { items = NonNullList.withSize(size, ItemStack.EMPTY); }
		items.clear();
		ItemStackHelper.loadAllItems(compound, items);
	}

	@Override
	public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("IsChest", isChest);
		compound.setInteger("Size", items.size());
		compound.setString("CustomName", name.isEmpty() ? "custom.chest.chestexample" : name);
		if (chestTexture != null) { compound.setString("Texture", chestTexture.toString()); }
		if (guiColor != -1) { compound.setInteger("GUIColor", guiColor); }
		if (guiColorArr != null) { compound.setIntArray("GUIColor", guiColorArr); }
		compound.setString("SoundOpen", ((ISoundEventMixin) sound_open).getSoundName().toString());
		compound.setString("SoundClose", ((ISoundEventMixin) sound_close).getSoundName().toString());
		ItemStackHelper.saveAllItems(compound, items);
		return compound;
	}

	public void setBlock(CustomChest block) {
		blockType = block;
		isChest = block.isChest;
		blockName = block.getCustomName();
		NBTTagCompound nbtData = block.getCustomNbt().getMCNBT();
		chestTexture = nbtData.hasNoTags() ? null : new ResourceLocation(CustomNpcs.MODID, "textures/entity/chest/" + blockName + ".png");
		if (nbtData.hasKey("Name", 8)) { name = nbtData.getString("Name"); }
		else { name = "custom.chest." + blockName; }
		if (nbtData.hasKey("GUIColor", 3)) { guiColor = nbtData.getInteger("GUIColor"); }
		else { guiColor = -1; }
		if (nbtData.hasKey("GUIColor", 11)) {
			guiColor = -1;
			guiColorArr = nbtData.getIntArray("GUIColor");
		}
		else { guiColorArr = null; }
		items = NonNullList.withSize(nbtData.hasKey("Size", 3) ? ValueUtil.correctInt(nbtData.getInteger("Size"), 1, 189) : 9,
				ItemStack.EMPTY);
		SoundEvent soundOpen = SoundEvent.REGISTRY.getObject(new ResourceLocation(nbtData.getString("SoundOpen")));
		if (soundOpen == null) { sound_open = SoundEvents.BLOCK_CHEST_OPEN; }
		else { sound_open = soundOpen; }
		SoundEvent soundClose = SoundEvent.REGISTRY.getObject(new ResourceLocation(nbtData.getString("SoundClose")));
		if (soundClose == null) { sound_close = SoundEvents.BLOCK_CHEST_CLOSE; }
		else { sound_close = soundClose; }
	}

	@Override
	public void update() {
		if (numPlayersUsing < 0) { numPlayersUsing = 0; }
        if (world != null && !world.isRemote && numPlayersUsing != 0
				&& (world.getTotalWorldTime() + pos.getX() + pos.getY() + pos.getZ()) % 20 == 0) {
			numPlayersUsing = 0;
			for (EntityPlayer entityplayer : world.getEntitiesWithinAABB(EntityPlayer.class,
					new AxisAlignedBB((float) pos.getX() - 5.0F,
                            (float) pos.getY() - 5.0F,
							(float) pos.getZ() - 5.0F,
                            (float) (pos.getX() + 1) + 5.0F,
                            (float) (pos.getY() + 1) + 5.0F,
                            (float) (pos.getZ() + 1) + 5.0F))) {
				if (entityplayer.openContainer instanceof ContainerChestCustom) {
					if (((ContainerChestCustom) entityplayer.openContainer).getPos().equals(pos)) {
						++numPlayersUsing;
					}
				}
			}
		}
		prevLidAngle = lidAngle;
		if (numPlayersUsing > 0 && lidAngle == 0.0F) {
			double d1 = (double) pos.getX() + 0.5D;
			double d2 = (double) pos.getZ() + 0.5D;
			world.playSound(null, d1, (double) pos.getY() + 0.5D, d2, sound_open,
					SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		}
		if (numPlayersUsing == 0 && lidAngle > 0.0F || numPlayersUsing > 0 && lidAngle < 1.0F) {
			float f2 = lidAngle;
			if (numPlayersUsing > 0) { lidAngle += 0.1F; }
			else { lidAngle -= 0.1F; }
			if (lidAngle > 1.0F) { lidAngle = 1.0F; }
			if (lidAngle < 0.5F && f2 >= 0.5F) {
				double d3 = (double) pos.getX() + 0.5D;
				double d0 = (double) pos.getZ() + 0.5D;
				world.playSound(null, d3, (double) pos.getY() + 0.5D, d0, sound_close, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			}
			if (lidAngle < 0.0F) { lidAngle = 0.0F; }
		}
	}

}
