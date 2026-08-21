package noppes.npcs.client.gui.player.tabs;

import io.netty.buffer.Unpooled;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;

public class InventoryTabFactions extends AbstractTab {

	public InventoryTabFactions() {
		super(1, 0, 0, new ItemStack(Items.BANNER, 1, 1), Component.translatable("menu.factions"));
	}

	@Override
	public void onTabClicked() {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeInt(1);
		CustomNpcs.proxy.openGui(null, EnumGuiType.QuestLog, buf);
	}

	@Override
	public boolean shouldAddToList(){ return true; }
}
