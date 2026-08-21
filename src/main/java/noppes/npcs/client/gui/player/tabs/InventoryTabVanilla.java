package noppes.npcs.client.gui.player.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.play.client.CPacketCloseWindow;

public class InventoryTabVanilla extends AbstractTab {

	public InventoryTabVanilla() {
		super(0, 0, 0, new ItemStack(Blocks.CRAFTING_TABLE), Component.translatable("stats.rarity.normal")
				.append(" (")
				.append(GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()))
				.append(")"));
	}

	@Override
	public void onTabClicked() {
		Minecraft mc = Minecraft.getMinecraft();
		mc.player.connection.sendPacket(new CPacketCloseWindow(mc.player.openContainer.windowId));
		GuiInventory inventory = new GuiInventory(mc.player);
		mc.displayGuiScreen(inventory);
	}

	@Override
	public boolean shouldAddToList(){ return true; }

}
