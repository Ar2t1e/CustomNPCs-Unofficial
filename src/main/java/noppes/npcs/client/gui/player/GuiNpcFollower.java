package noppes.npcs.client.gui.player;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketFollowerExtend;
import noppes.npcs.packets.server.SPacketFollowerState;
import noppes.npcs.packets.server.SPacketNpcRoleGet;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.util.Util;

public class GuiNpcFollower extends GuiContainerNPCInterface<ContainerNPCFollowerHire>
		implements IGuiData {

	protected final RoleFollower role;

	// New from Unofficial (BetaZavr)
	protected EntityNPCInterface displayNPC;

	public GuiNpcFollower(EntityNPCInterface npc, ContainerNPCFollowerHire container) {
		super(npc, container, Component.empty());
		setBackground("follower.png");
		ySize = 224;

		role = (RoleFollower) npc.role;
		Packets.sendServer(new SPacketNpcRoleGet());
		// New from Unofficial (BetaZavr)
		displayNPC = Util.instance.copyToGUI(npc, player.world, false);
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = guiLeft + 12;
		int y = guiTop - 11;
		if (!role.infiniteDays) {
			for (int i = 0; i < 3; ++i) {
				if (role.rentalItems.getStackInSlot(i).isEmpty()) { continue; }
				addButton(i, x, y += 16, "follower.extend")
						.setSize(60, 13)
						.setHoverTexts("follower.hover.extend");
			}
		}
		if (role.rates.containsKey(3) && role.rentalMoney > 0) {
			addButton(3, x, guiTop + 53, "follower.extend")
					.setSize(60, 13)
					.setHoverTexts("follower.hover.extend");
		}
		x += 52;
		y = guiTop + 105;
		addButton(5, x, y, false, role.isFollowing ? 0 : 1, "follower.waiting", "follower.following")
				.setSize(50, 14)
				.setHoverTexts("follower.hover.move");
		addButton(6, x + 54, y, "follower.fire")
				.setSize(50, 14)
				.setHoverTexts("follower.hover.fire");
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 5: Packets.sendServer(new SPacketFollowerState(true)); break;
			case 6: Packets.sendServer(new SPacketFollowerState(false)); onClose(); break;
			default: Packets.sendServer(new SPacketFollowerExtend(button.id)); break;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		int index = 0;
		if (!role.infiniteDays) {
			for (int slot = 0; slot < role.rentalItems.getSizeInventory(); ++slot) {
				ItemStack itemstack = role.rentalItems.getStackInSlot(slot);
				if (!NoppesUtilServer.isItemStackNull(itemstack)) {
					int days = 1;
					if (role.rates.containsKey(slot)) { days = role.rates.get(slot); }
					int yOffset = index * 16;
					int x = guiLeft + 68;
					int y = guiTop + yOffset + 4;
					GlStateManager.enableRescaleNormal();
					RenderHelper.enableGUIStandardItemLighting();
					itemRender.renderItemAndEffectIntoGUI(itemstack, x + 11, y);
					itemRender.renderItemOverlays(fontRenderer, itemstack, x + 11, y);
					RenderHelper.disableStandardItemLighting();
					GlStateManager.disableRescaleNormal();
					Component daysS = Component.empty()
							.append(" = " + days + " ")
							.append(Component.translatable(days == 1 ? "follower.day": "follower.days"));
					fontRenderer.drawString(daysS.getFormattedText(), x + 27, y + 4,
							CustomNpcResourceListener.DefaultTextColor);
					if (isMouseHover(mouseX, mouseY, x - guiLeft + 11, y - guiTop, 16, 16)) {
						renderToolTip(itemstack, mouseX, mouseY);
					}
					++index;
				}
			}
		}
		int size = role.inventory.getSizeInventory();
		if (size > 0) {
			int s = (size == 2 || size == 4) ? 2 : 3;
			GlStateManager.pushMatrix();
			mc.getTextureManager().bindTexture(background);
			GlStateManager.translate(guiLeft + 172, guiTop + 135, 0.0f);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			drawTexturedModalRect(3, 0, 118, 0, 58, 1);
			drawTexturedModalRect(2, 1, 117, 1, 59, 1);
			drawTexturedModalRect(1, 2, 116, 2, 60, 1);
			drawTexturedModalRect(0, 3, 115, 3, 61, 82);
			drawTexturedModalRect(0, 85, 115, 220, 61, 4);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.translate(guiLeft + 173, guiTop + 141, 0.0f);
			mc.getTextureManager().bindTexture(GuiBasic.RESOURCE_SLOT);
			for (int slotId = 0; slotId < size; slotId++) {
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				drawTexturedModalRect((slotId % s) * 18, (slotId / s) * 18, 0, 0, 18, 18);
			}
			GlStateManager.popMatrix();
		}
		if (role.rates.containsKey(3) && role.rentalMoney > 0) {
			int days = role.rates.get(3);
			Component daysS = Component.empty()
					.append(Util.instance.getTextReducedNumber(role.rentalMoney, true, true, false))
					.append(" " + CustomNpcs.displayCurrencies + " = " + days + " ")
					.append(Component.translatable(days == 1 ? "follower.day": "follower.days"));
			fontRenderer.drawString(daysS.getFormattedText(), guiLeft + 80, guiTop + 56, CustomNpcResourceListener.DefaultTextColor);
		}
		if (displayNPC != null) { drawNpc(displayNPC, 33, 131, 1.0f, 0, 0, 1); }
		else { drawNpc(33, 131); }
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		long time = (System.currentTimeMillis() - role.hiredTime) / 50L;
		fontRenderer.drawString(Component.translatable("follower.health")
				.append(": " + npc.getHealth() + "/" + npc.getMaxHealth()).getFormattedText(), 62, 70, CustomNpcResourceListener.DefaultTextColor);
		if (!role.infiniteDays) {
			fontRenderer.drawString(Component.translatable("follower.daysleft")
					.append(" " + Util.instance.ticksToElapsedTime((role.getDays() * 28800L) - time, false, true, false)).getFormattedText(), 62, 82, CustomNpcResourceListener.DefaultTextColor);
		}
		fontRenderer.drawString(Component.translatable("follower.lastday")
				.append(": " + Util.instance.ticksToElapsedTime(time, false, true, false)).getFormattedText(), 62, 94, CustomNpcResourceListener.DefaultTextColor);
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
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

    @Override
	public void setGuiData(NBTTagCompound compound) {
		npc.role.load(compound);
		initGui();
	}

}
