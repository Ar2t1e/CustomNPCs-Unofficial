package noppes.npcs.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.*;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.GuiNpcMobSpawnerAdd;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCloneSet;
import noppes.npcs.packets.server.SPacketGuiOpen;
import noppes.npcs.packets.server.SPacketToolMobSpawner;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

public class ItemNpcCloner extends Item implements INPCToolItem {

	public ItemNpcCloner() {
		setRegistryName(CustomNpcs.MODID, "npcmobcloner");
		setUnlocalizedName("npcmobcloner");
		setFull3D();
		maxStackSize = 1;
		setCreativeTab(CustomTabs.TOOLS);
	}

	public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			PlayerData data = CustomNpcs.proxy.getPlayerData(player);
			boolean summon = false;
			ItemStack stackCloner = player.getHeldItemMainhand();
			if (data != null && data.overlay.isPressedShift()) {
				NBTTagCompound nbt = stackCloner.getTagCompound();
				if (nbt != null && nbt.hasKey("Settings", 10)) {
					NBTTagCompound nbtData = nbt.getCompoundTag("Settings");
					if (nbtData.getBoolean("isServerClone")) {
						Packets.sendServer(new SPacketToolMobSpawner(true, false, pos.up(),
								nbtData.getString("Name"), nbtData.getInteger("Tab"), new NBTTagCompound()));
					} else {
						Packets.sendServer(new SPacketToolMobSpawner(false, false,
								pos.up(), "", -1, nbtData.getCompoundTag("EntityNBT")));
					}
					summon = true;
				}
			}
			if (!summon) {
				Entity rayTraceEntity = Util.instance.getLookEntity(player, 4.0d, false);
				if (rayTraceEntity instanceof EntityNPCInterface) {
					NBTTagCompound compound = new NBTTagCompound();
					if (!rayTraceEntity.writeToNBTAtomically(compound)) { return EnumActionResult.FAIL; }
					String s = compound.getString("id");
					if (s.equals("minecraft:customnpcs.customnpc") || s.equals("minecraft:customnpcs:customnpc")) {
						compound.setString("id", CustomNpcs.MODID + ":customnpc");
					}
					ServerCloneController.Instance.cleanTags(compound);
					try {
						Packets.sendServer(new SPacketCloneSet(compound));
						NoppesUtil.openGUI(player, new GuiNpcMobSpawnerAdd(compound));
					} catch (Exception e) { LogWriter.error("Error send data:", e); }
					return EnumActionResult.FAIL;
				}
				Packets.sendServer(new SPacketGuiOpen(EnumGuiType.MobSpawner, pos.up()));
			}
		}
		return EnumActionResult.SUCCESS;
	}

	// New from Unofficial (BetaZavr)
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> list, @Nonnull ITooltipFlag flagIn) {
		list.add(Component.translatable("info.item.cloner").getFormattedText());
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null || !nbt.hasKey("Settings", 10)) {
			list.add(Component.translatable("info.item.cloner.empty.0").getFormattedText());
			list.add(Component.translatable("info.item.cloner.empty.1").getFormattedText());
		} else {
			list.add(Component.translatable("info.item.cloner.set.0",
					nbt.getCompoundTag("Settings").getString("Name")).getFormattedText());
			list.add(Component.translatable("info.item.cloner.set.1").getFormattedText());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(@Nonnull ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		return super.hasEffect(stack) || (nbt != null && nbt.hasKey("Settings", 10)
				&& !nbt.getCompoundTag("Settings").getString("Name").isEmpty());
	}

}
