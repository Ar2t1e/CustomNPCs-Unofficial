package noppes.npcs.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.api.item.ISpecBuilder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiOpen;
import noppes.npcs.packets.server.SPacketGetBuildData;
import noppes.npcs.util.BuilderData;

public class ItemPlacer extends Item implements ISpecBuilder {

	public ItemPlacer() {
		setRegistryName(CustomNpcs.MODID, "npcplacer");
		setUnlocalizedName("npcplacer");
		maxStackSize = 1;
		setCreativeTab(CustomTabs.TOOLS);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> list, @Nonnull ITooltipFlag flagIn) {
        BuilderData builder = ItemBuilder.getBuilder(stack, null);
		list.add(new TextComponentTranslation("info.item.builder.main.0").getFormattedText());
		list.add(new TextComponentTranslation("info.item.builder.main.1").getFormattedText());
		if (builder != null) {
			list.add(new TextComponentTranslation("info.item.placer").getFormattedText());
			for (int i = 4; i <= 5; i++) {
				list.add(new TextComponentTranslation("info.item.builder.main." + i).getFormattedText());
			}
			list.add(new TextComponentTranslation("info.item.builder.range.1", "" + builder.region[0], "" + builder.region[1], "" + builder.region[2]).getFormattedText());
		} else {
			list.add(new TextComponentTranslation("info.item.builder.main.2").getFormattedText());
			if (stack.hasTagCompound() && stack.getTagCompound() != null  && stack.getTagCompound().hasKey("ID", 3) && stack.getTagCompound().hasKey("BuilderType", 3)) {
				Packets.sendServerDelayed(new SPacketGetBuildData(stack.getTagCompound().getInteger("ID"), stack.getTagCompound().getInteger("BuilderType")), stack, 2000);
			}
		}
	}

	@Override
	public void leftClick(ItemStack stack, EntityPlayerMP player, BlockPos pos) {
		if (pos == null) { return; }
		PlayerData data = PlayerData.get(player);
		BuilderData builder = ItemBuilder.getBuilder(stack, player);
		if (data == null || !stack.hasTagCompound() || builder == null || builder.getID() == -1) {
			Packets.send(player, new PacketGuiOpen(getGUIType(), new BlockPos(-1, getType(), 0)));
			return;
		}
		if (data.overlay.isPressedCtrl()) {
			builder.undo();
        }
	}

	@Override
	public void rightClick(ItemStack stack, EntityPlayerMP player, BlockPos pos) {
		if (pos == null) { return; }
		PlayerData data = PlayerData.get(player);
		BuilderData builder = ItemBuilder.getBuilder(stack, player);
		if (data == null || !stack.hasTagCompound() || builder == null || builder.getID() == -1) {
			Packets.send(player, new PacketGuiOpen(getGUIType(), new BlockPos(-1, getType(), 0)));
			return;
		}
		if (data.overlay.isPressedCtrl()) {
			builder.redo();
			return;
		}
		builder.work(pos, player);
	}

	@Override
	public int getType() { return 3; }

	@Override
	public EnumGuiType getGUIType() { return EnumGuiType.PlacerTool; }
	
}
