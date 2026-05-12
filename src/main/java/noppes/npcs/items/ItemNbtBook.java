package noppes.npcs.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiOpen;

public class ItemNbtBook extends Item implements INPCToolItem {

	public ItemNbtBook() {
		this.setRegistryName(CustomNpcs.MODID, "nbt_book");
		this.setUnlocalizedName("nbt_book");
		this.maxStackSize = 1;
		this.setCreativeTab(CustomTabs.TOOLS);
	}

	public void blockEvent(EntityPlayerMP player, BlockPos pos) {
		if (player == null || pos == null) { return; }
		IBlockState state = player.world.getBlockState(pos);
		if (state.getBlock().isAir(state, player.world, pos)) { return; }
		Packets.send(player, new PacketGuiOpen(EnumGuiType.NbtBook, pos));
		NBTTagCompound data = new NBTTagCompound();
		TileEntity tile = player.world.getTileEntity(pos);
		if (tile != null) { tile.writeToNBT(data); }
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("Data", data);
		Packets.send(player, new PacketGuiData(compound));
	}

	public void entityEvent(EntityPlayerMP player, Entity target) {
		if (player == null || target == null) { return; }
		Packets.send(player, new PacketGuiOpen(EnumGuiType.NbtBook, BlockPos.ORIGIN));
		NBTTagCompound data = new NBTTagCompound();
		target.writeToNBTAtomically(data);
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("EntityId", target.getEntityId());
		compound.setTag("Data", data);
		Packets.send(player, new PacketGuiData(compound));
	}

	// New from Unofficial (BetaZavr)
	public void itemEvent(EntityPlayerMP player) {
		if (player == null) { return; }
		Packets.send(player, new PacketGuiOpen(EnumGuiType.NbtBook, player.getPosition()));
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("Item", true);
		compound.setTag("Data", player.getHeldItemOffhand().writeToNBT(new NBTTagCompound()));
		Packets.send(player, new PacketGuiData(compound));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> list, @Nonnull ITooltipFlag flagIn) {
		list.add(new TextComponentTranslation("info.item.nbt.book").getFormattedText());
	}


}
