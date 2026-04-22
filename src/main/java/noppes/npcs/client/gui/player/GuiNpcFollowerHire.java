package noppes.npcs.client.gui.player;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketFollowerHire;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.util.Util;

import java.util.ArrayList;
import java.util.List;

public class GuiNpcFollowerHire extends GuiContainerNPCInterface<ContainerNPCFollowerHire> {

	protected final RoleFollower role;

	public GuiNpcFollowerHire(EntityNPCInterface npc, ContainerNPCFollowerHire cont) {
		super(npc, cont, Component.empty());
		setBackground("followerhire.png");
		closeOnEsc = true;

		role = (RoleFollower) npc.role;
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = guiLeft + 26;
		int y = guiTop - 7;
		for (int i = 0; i < 3; ++i) {
			if (!role.rentalItems.getStackInSlot(i).isEmpty()) {
				addButton(i, x, y += 18, "follower.hire")
						.setSize(50, 14);
			}
		}
		if (role.rates.containsKey(3) && role.rentalMoney > 0) {
			addButton(3, x, guiTop + 65, "follower.hire")
					.setSize(50, 14);
		}
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		Packets.sendServer(new SPacketFollowerHire(button.id));
		onClose();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		int index = 0;
		for (int slot = 0; slot < role.rentalItems.getSizeInventory(); ++slot) {
			ItemStack itemstack = role.rentalItems.getStackInSlot(slot);
			if (!NoppesUtilServer.isItemStackNull(itemstack)) {
				int days = 1;
				if (role.rates.containsKey(slot)) { days = role.rates.get(slot); }
				int yOffset = index * 18;
				int x = guiLeft + 78;
				int y = guiTop + yOffset + 10;
				GlStateManager.enableRescaleNormal();
				RenderHelper.enableGUIStandardItemLighting();
				itemRender.renderItemAndEffectIntoGUI(itemstack, x + 11, y);
				itemRender.renderItemOverlays(fontRenderer, itemstack, x + 11, y);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.disableRescaleNormal();
				Component daysS = Component.empty()
						.append(" = " + days + " ")
						.append(Component.translatable(days == 1 ? "follower.day": "follower.days"));
				fontRenderer.drawString(daysS.getString(), x + 27, y + 4,
						CustomNpcResourceListener.DefaultTextColor);
				if (isMouseHover(mouseX, mouseY, x - guiLeft + 11, y - guiTop, 16, 16)) {
					renderToolTip(itemstack, mouseX, mouseY);
				}
				++index;
			}
		}
		if (role.rates.containsKey(3) && role.rentalMoney > 0) {
			int days = role.rates.get(3);
			Component daysS = Component.empty()
					.append(Util.instance.getTextReducedNumber(role.rentalMoney, true, true, false))
					.append(" " + CustomNpcs.displayCurrencies + " = " + days + " ")
					.append(Component.translatable(days == 1 ? "follower.day": "follower.days"));
			fontRenderer.drawString(daysS.getString(), guiLeft + 90, guiTop + 68, CustomNpcResourceListener.DefaultTextColor);
		}
	}

    @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		for (int i = 0; i < 3; ++i) {
			if (getButton(i) != null) {
				getButton(i).setIsEnabled(player.isCreative() || Util.instance.canRemoveItems(player.inventory.mainInventory, role.rentalItems.getStackInSlot(i), false, false));
			}
		}
		if (getButton(3) != null) {
			getButton(3).setIsEnabled(player.isCreative() || CustomNpcs.proxy.getPlayerData(player).game.getMoney() >= role.rentalMoney);
		}
		for (int i = 0; i < 4; ++i) {
			if (getButton(i) != null && getButton(i).isHoveredOrFocused()) {
				List<Component> hover = new ArrayList<>();
				hover.add(Component.translatable("follower.hover.hire.info"));
				if (role.disableGui) { hover.add(Component.translatable("follower.hover.disable.gui").withStyle(TextFormatting.GRAY)); }
				if (role.infiniteDays) { hover.add(Component.translatable("follower.hover.infinite")); }
				setHoverText(hover);
			}
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
