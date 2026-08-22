package noppes.npcs.client.gui.player.tabs;

import io.netty.buffer.Unpooled;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.constants.EnumGuiType;

public class InventoryTabQuests extends AbstractTab {

	public InventoryTabQuests() {
		super(2, 0, 0, new ItemStack(Items.BOOK), Component.translatable("quest.quests")
				.append(" (").append(GameSettings.getKeyDisplayString(ClientProxy.QuestLog.getKeyCode()))
				.append(")"));
	}

	@Override
	public void onTabClicked() {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeInt(0);
		CustomNpcs.proxy.openGui(null, EnumGuiType.QuestLog, buf);
	}

	@Override
	public boolean shouldAddToList() { return true; }

}
