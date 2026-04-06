package noppes.npcs.client.gui.player;

import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.Container;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.ConfirmScreen;
import noppes.npcs.client.gui.global.GuiNpcManagePlayerData;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPlayerData;
import noppes.npcs.containers.NpcMiscInventory;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.mixin.client.gui.inventory.IGuiContainerMixin;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.*;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;
import org.lwjgl.input.Mouse;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.global.SubGuiEditBankAccess;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class GuiNPCBankChest extends GuiContainerNPCInterface<ContainerNPCBank> {

	protected final ResourceLocation bg = new ResourceLocation(CustomNpcs.MODID, "textures/gui/extrasmallbg.png");
	public static final ResourceLocation INVENTORY_ITEMS = new ResourceLocation(CustomNpcs.MODID, "textures/gui/bank/inventory.png");
	public static final ResourceLocation INVENTORY_EMPTY = new ResourceLocation(CustomNpcs.MODID, "textures/gui/bank/empty.png");
	public static final ResourceLocation SAFE = new ResourceLocation(CustomNpcs.MODID, "textures/gui/bank/safe.png");

	public static int startXMouse = 0;
	public static int startYMouse = 0;

	protected final boolean isMany;
	protected boolean isOwner;
	public boolean isWait;
	public int ceilsUpdate;
	public int ceilPos;

	//scrolling
	protected boolean isScrolling = false;
	protected int scrollBarY;
	public int scrollMax = 0;
	public int scrollY;

	// currency to open or upgrade
	protected boolean canUpgrade;
	protected ItemStack stack;
	protected int money;
	protected int donat;

	@Nullable
	protected Slot hoverSlot;
	public ContainerNPCBank menu;

	public GuiNPCBankChest(EntityNPCInterface npc, ContainerNPCBank container) {
		super(npc, container, Component.empty());
		hoverIsGame = true;
		if (container.items.getSizeInventory() > 0) {
			background = INVENTORY_ITEMS;
			xSize = 190;
			ySize = 238;
		} else {
			background = INVENTORY_EMPTY;
			xSize = 178;
			ySize = 143;
		}
		menu = container;
		isMany = container.items.getSizeInventory() > 45;
		if (isMany) { scrollMax = (int) (Math.max(0.0d, Math.ceil((double) container.items.getSizeInventory() / 9.0d)) * 18.0d - 90.0d); }
		scrollY = menu.scrollY;
		ceilPos = ValueUtil.correctInt(menu.ceilPos, 0, menu.data.bank.ceilSettings.size() - 5);
		ceilsUpdate = menu.ceilsUpdate;
		resetRow(0);
	}

	@Override
	public void initGui() {
		super.initGui();
		int color = CustomNpcs.MainColor.getRGB();
		// name
		Component title = Component.translatable("bank.name" + (menu.data.bank.isPublic ? ".public" : ""), ": ");
		if (ContainerNPCBank.editPlayerBankData != null && ContainerNPCBank.editPlayerBankData.isEmpty()) {
			title = Component.translatable("bank.name.player", ContainerNPCBank.editPlayerBankData, ": ");
		}
		title.append(Component.translatable(menu.data.bank.name).withStyle(TextFormatting.BOLD));
		addLabel(0, guiLeft + 5, guiTop + 5, title)
				.setSize(xSize - 10, 10)
				.setColor(color);
		// currency to open or upgrade
		stack = ItemStack.EMPTY;
		money = 0;
		donat = 0;
		Bank.CeilSettings cs = menu.data.bank.ceilSettings.get(menu.ceil);
		isOwner = player.isCreative() || !menu.data.bank.isPublic || menu.data.bank.owner.isEmpty() || player.getName().equals(menu.data.bank.owner);
		canUpgrade = false;
		if (menu.items.getSizeInventory() == 0) {
			stack = cs.openStack;
			money = cs.openMoney;
			donat = cs.openDonat;
			canUpgrade = false;
		}
		else if (menu.items.getSizeInventory() < cs.maxCells && !cs.upgradeStack.isEmpty()) {
			stack = cs.upgradeStack;
			money = cs.upgradeMoney;
			donat = cs.upgradeDonat;
			canUpgrade = true;
		}
		// open and upgrade buttons
		Slot slot = menu.items.getSizeInventory() < menu.inventorySlots.size() ? menu.getSlot(menu.items.getSizeInventory()) : null;
		int u = (width - xSize) / 2 - 8;
		int v = (height - ySize) / 2;
		if (slot != null) {
			// not owner
			if (isOwner && (!stack.isEmpty() || money > 0 || donat > 0)) {
				int x = u + slot.xPos + 61 + (stack.isEmpty() ? 0 : 21);
				int y = v + slot.yPos - 22; // upgrade button up + right or center
				boolean showButton = player.isCreative() || canUpgrade ? menu.items.getSizeInventory() < cs.maxCells : menu.ceil < menu.data.bank.ceilSettings.size();
				addButton(0, x, y, canUpgrade ? "bank.upgrade" : "bank.unlock")
						.setSize(51, 18)
						.setIsEnabled(showButton);
				// size -> open / upgrade
				if (canUpgrade) {
					int s = cs.maxCells - menu.items.getSizeInventory();
					if (s > 0) {
						int p = 0;
						List<Object> list = new ArrayList<>();
						list.add("1");
						list.add("5");
						list.add("10");
						list.add("20");
						list.add("gui.max");
						if (ceilsUpdate == 5) { p = 1; }
						else if (ceilsUpdate == 10) { p = 2; }
						else if (ceilsUpdate == 20) { p = 3; }
						else if (ceilsUpdate != 1) { p = 4; ceilsUpdate = s; }
						addButton(14, x + 52, y, true, p , list.toArray(new Object[0]))
								.setSize(37, 18)
								.setIsEnabled(showButton)
								.setIsVisible(cs.maxCells - menu.items.getSizeInventory() > 0);
					}
				}
				if (money > 0) {
					addLabel(1, guiLeft + 21, y + (donat > 0 ? -1 : 4), Util.instance.getTextReducedNumber(money, true, true, false) + CustomNpcs.displayCurrencies)
							.setSize(38, 10)
							.setColor(color);
				}
				if (donat > 0) {
					addLabel(2, guiLeft + 21, y + (money > 0 ? 11 : 4), Util.instance.getTextReducedNumber(money, true, true, false) + CustomNpcs.displayCurrencies)
							.setSize(38, 10)
							.setColor(color);
				}
			}
			else {
				addLabel(10, u + slot.xPos + 6, v + slot.yPos - 18, "")
						.setSize(162, 10)
						.setColor(color);
			}
			// clear items
			GuiButtonNop clearButton = new GuiButtonNop(this, 10, "", u + slot.xPos + 171, v + slot.yPos + 59,
					(button) -> {
						if (!menu.items.isEmpty()) {
							ConfirmScreen guiYesNo = new ConfirmScreen((bo) -> {
								if (bo) {
									Packets.sendServer(new SPacketBankClearCeil(menu.data.bank.id, menu.ceil, ceilPos, ceilsUpdate));
								}
								NoppesUtil.openGUI(player, this);
							},
									Component.translatable("bank.name", ": ")
											.append(Component.translatable(menu.data.bank.name).withStyle(TextFormatting.BOLD))
											.append("; ")
											.append(Component.translatable("gui.ceil", " #" + ((char) 167) + "l" +(menu.ceil + 1))),
									Component.translatable("message.bank.del.items"));
							setScreen(guiYesNo);
						}
					})
					.setSize(14, 14)
					.setTexture(INVENTORY_ITEMS)
					.setUV(190, 20, 24, 24)
					.setDefBack(false)
					.setIsVisible(isOwner && menu.items.getSizeInventory() > 0)
					.setIsEnabled(!menu.items.isEmpty())
					.setHoverTexts("bank.hover.clear.slots");
			add(clearButton);
		}
		// creative manager
		if (player.isCreative()) {
			int x = guiLeft + xSize + 7;
			int y = guiTop + 15;
			addButton(11, x, y, "bank.lock")
					.setSize(80, 18)
					.setIsEnabled(menu.items.getSizeInventory() > 0 && !cs.openStack.isEmpty())
					.setHoverTexts("bank.hover.lock");
			int size = Math.max(menu.data.bank.ceilSettings.get(menu.ceil).startCells, menu.items.getSizeInventory() - ceilsUpdate);
			addButton(12, x, y += 21, "bank.regrade")
					.setSize(80, 18)
					.setIsEnabled(menu.items.getSizeInventory() > cs.startCells)
					.setHoverTexts(Component.translatable("bank.hover.regrade", ((char) 167) + "6" + (menu.items.getSizeInventory() - size)));
			addButton(13, x, y + 21, "gui.reset")
					.setSize(80, 18)
					.setIsEnabled(!menu.items.isEmpty() || menu.items.getSizeInventory() != cs.startCells)
					.setHoverTexts("bank.hover.reset");
		}
		// ceil tabs
		if (ceilPos < 0) { ceilPos = 0; }
		GuiSafeButton tab;
		if (menu.data.bank.ceilSettings.size() > 1) {
			int ceil;
			int i;
			int x = guiLeft - 34;
			int y = guiTop + 16;
			for (i = 0; i < 5; i++) {
				ceil = i + ceilPos;
				if (ceil < menu.data.bank.ceilSettings.size()) {
					tab = new GuiSafeButton(this, 3 + i, (1 + i + ceilPos) + ">", x, y + 11 + i * 14, 30, 14, ceil);
					tab.setHoverTexts(Component.translatable("bank.hover.ceil." + (i + ceilPos * 5 == menu.ceil), "" + (ceil + 1)));
					add(tab);
					// stop if this ceil is not open
					NpcMiscInventory inv = menu.data.get(ceil);
					if (inv == null || inv.getSizeInventory() == 0 || ceil >= menu.data.bank.ceilSettings.size()) { break; }
				}
			}
			if (ceilPos > 0) {
				tab = new GuiSafeButton(this, 1, "" + ((char) 708), x, y, 30, 11, -1);
				tab.setHoverTexts("bank.hover.up").setSize(30, 14);
				add(tab);
			}
			if (i == 5 && ceilPos + i < menu.data.bank.ceilSettings.size()) {
				tab = new GuiSafeButton(this, 2, "" + ((char) 709), x, y + 81, 30, 11, -1);
				tab.setHoverTexts("bank.hover.down").setSize(30, 14);
				add(tab);
			}
		}
		// lock
		if (menu.data.bank.isPublic) {
			addButton(9, (width - xSize) / 2 - 29, (height - ySize) / 2 + 109, "")
					.setTexture(GuiBasic.WIDGETS) // lock
					.setUV(menu.data.bank.owner.isEmpty() ? 20 : 0, 146, 20, 20)
					.setSize(20, 20)
					.setIsVisible(isOwner)
					.setHoverTexts("bank.hover.settings");
		}
		resetRow(0);
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		if (isWait) { return; }
		if (button.id > 2 && button.id < 8) {
			if (menu.ceil != ((GuiSafeButton) button).ceil) {
				onClose();
				Packets.sendServer(new SPacketBankOpen(menu.data.bank.id, ((GuiSafeButton) button).ceil, ceilPos, 0, ceilsUpdate));
			}
			return;
		} // open ceil
		switch (button.id) {
			case 0: {
				if (menu.items.getSizeInventory() == menu.data.bank.ceilSettings.get(menu.ceil).maxCells) {
					ceilsUpdate = 1;
				}
				Packets.sendServer(new SPacketBankUpgrade(menu.data.bank.id, menu.ceil, ceilsUpdate, scrollY, ceilPos));
				isWait = true;
				break;
			} // open or upgrade
			case 1: {
				ceilPos--;
				if (ceilPos < 0) { ceilPos = 0; }
				initGui();
				break;
			} // up
			case 2: {
				ceilPos++;
				if (ceilPos > menu.data.bank.ceilSettings.size() - 5) { ceilPos = menu.data.bank.ceilSettings.size() - 5; }
				initGui();
				break;
			} // down
			case 9: {
				if (menu.data.bank.owner.isEmpty() && !player.isCreative()) { return; }
				setSubGui(new SubGuiEditBankAccess(menu.data.bank));
				break;
			} // settings
			case 11: {
				Packets.sendServer(new SPacketBankLock(menu.data.bank.id));
				isWait = true;
				break;
			} // lock
			case 12: {
				if (menu.items.getSizeInventory() > menu.data.bank.ceilSettings.get(menu.ceil).startCells) {
					Packets.sendServer(new SPacketBankRegrade(menu.data.bank.id, scrollY, ceilPos, ceilsUpdate));
					isWait = true;
				}
				break;
			} // regrade
			case 13: {
				Packets.sendServer(new SPacketBankResetCeil(menu.data.bank.id, ceilPos, ceilsUpdate));
				isWait = true;
				break;
			} // reset
			case 14: {
				try {
					ceilsUpdate = Integer.parseInt(button.getMessage().getString());
				}
				catch (Exception ignored) {
					ceilsUpdate = 0;
				}
				initGui();
				//Packets.sendServer(new SPacketBankUpgrade(menu.data.bank.id, menu.ceil, ceilsUpdate, scrollY, ceilPos));
				break;
			} // upgrade all
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		int u = (width - xSize) / 2 - 8;
		int v = (height - ySize) / 2;
		GlStateManager.enableBlend();
		// safe
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) u - 29.0f, (float) v + 14.0f, 0.0f);
		GlStateManager.scale(0.5f, 0.5f, 1.0f);
		mc.getTextureManager().bindTexture(SAFE);
		drawTexturedModalRect(0, 0, 0, 0, 70, 194);
		GlStateManager.popMatrix();
		// scroll bar
		if (menu.items.getSizeInventory() > 0) {
			if (!isMany) { GlStateManager.color(0.5f, 0.5f, 0.5f, 1.0F); }
			// place
			GlStateManager.pushMatrix();
			GlStateManager.translate(u + 181.5, v + 18, 0.0f);
			GlStateManager.scale(0.5f, 0.5f, 1.0f);
			mc.getTextureManager().bindTexture(INVENTORY_ITEMS);
			drawTexturedModalRect(0, 0, 236, 0, 20, 170); // up
			drawTexturedModalRect(0, 170, 236, 86, 20, 170); // down
			GlStateManager.popMatrix();
			// bar
			GlStateManager.pushMatrix();
			GlStateManager.translate(u + 181.5f, v + (isMany ? 14.5f + scrollBarY : 33.0f), 0.0f);
			GlStateManager.scale(0.5f, 0.5f, 1.0f);
			drawTexturedModalRect(0, -30, 215, 0, 20, 30); // up
			drawTexturedModalRect(0, 0, 215, 30, 20, 31); // down
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}
		// Slots
		hoverSlot = null;
		if (!hasSubGui() && menu.items.getSizeInventory() > 0) {
			int x = guiLeft + 7, xs;
			int y = guiTop + 20, ys;
			ScaledResolution sw = new ScaledResolution(minecraft);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			double d4 = sw.getScaledWidth() < mc.displayWidth
					? (int) Math.round((double) mc.displayWidth / (double) sw.getScaledWidth())
					: 1;
			GL11.glScissor((int) ((double) x * d4),
					(int) ((double) mc.displayHeight - (double) (y + 90) * d4),
					Math.max(0, (int) (162.0d * d4)),
					Math.max(0, (int) (90.0d * d4)));
			for (int s = 0; s < menu.items.getSizeInventory(); s++) {
				Slot slot = menu.inventorySlots.get(s);
				xs = x + (s % 9) * 18;
				ys = y - scrollY + (int) (Math.floor((double) s / 9.0d) * 18.0d);
				if (ys > y - 17 && ys < y + 90) {
					drawTexturedModalRect(xs, ys, 191, 1, 18, 18);
					xs += 1;
					ys += 1;
					if (slot.isEnabled()) { drawSlot(slot, xs, ys); }
					int yh = ys;
					int hy = 16;
					if (ys < y) { yh = y; hy = 16 - y + ys; }
					else if (ys > y + 72) { hy = Math.min(16, 90 - ys + y); }
					if (isMouseHover(mouseX, mouseY, xs, yh, 16, hy) && slot.isEnabled()) {
						hoverSlot = slot;
						GlStateManager.disableLighting();
						GlStateManager.disableDepth();
						int j1 = hoverSlot.xPos;
						int k1 = hoverSlot.yPos;
						GlStateManager.colorMask(true, true, true, false);
						this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, 0x80FFFFFF, 0x80FFFFFF);
						GlStateManager.colorMask(true, true, true, true);
						GlStateManager.enableLighting();
						GlStateManager.enableDepth();
						GlStateManager.enableBlend();
					}
				}
			}
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
		// upgrade / new tab
		if (!hasSubGui() && isOwner && (!stack.isEmpty() || money > 0 || donat > 0) &&
				menu.items.getSizeInventory() < menu.inventorySlots.size()) {
			Slot slot = menu.getSlot(menu.items.getSizeInventory());
			if (!stack.isEmpty()) {
				int xs = u + slot.xPos + 61;
				int ys = v + slot.yPos - 22;
				// background
				drawTexturedModalRect(xs - 1, ys - 1, 190, 0, 20, 20);
				xs += 1;
				ys += 1;
				// item
				minecraft.getRenderItem().renderItemAndEffectIntoGUI(stack, xs, ys);
				minecraft.getRenderItem().renderItemOverlayIntoGUI(font, stack, xs, ys, null);
				// info
				if (isMouseHover(mouseX, mouseY, xs, ys, 16, 16)) {
					List<Component> hover = new ArrayList<>();
					hover.add(Component.translatable("bank." + (canUpgrade ? "upg" : "tab") + ".cost.info"));
					hover.add(Component.literal("<br>"));
					for (String line : getItemToolTip(stack)) { hover.add(Component.literal(line)); }
					setHoverText(hover.toArray());
				}
			}
			if (money > 0 || donat > 0) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(u + slot.xPos + 4, v + slot.yPos - (money > 0 && donat > 0 ? 27 : 22), 0.0f);
				float s = 16.0f / 250.f;
				GlStateManager.scale(s, s, s);
				mc.getTextureManager().bindTexture(GuiBasic.MONEY);
				drawTexturedModalRect(0, 0, 0, 0, 256, 256);
				if (donat > 0) {
					if (money > 0) { GlStateManager.translate(0.0f, 192.0f, 0.0f); }
					mc.getTextureManager().bindTexture(GuiBasic.DONAT);
					drawTexturedModalRect(0, 0, 0, 0, 256, 256);
				}
				GlStateManager.popMatrix();
				if (money > 0 && isMouseHover(mouseX, mouseY, u + slot.xPos + 4, v + slot.yPos - (donat > 0 ? 25 : 20), 53, 12)) {
					setHoverText("bank.hover." + (canUpgrade ? "upgrade" : "open") + ".money");
				}
				if (donat > 0 && isMouseHover(mouseX, mouseY, u + slot.xPos + 4, v + slot.yPos - (money > 0 ? 13 : 20), 53, 12)) {
					setHoverText("bank.hover." + (canUpgrade ? "upgrade" : "open") + ".donat");
				}
			}
		}
		// creative manager
		if (player.isCreative()) {
			int x = guiLeft + xSize + 2;
			int y = guiTop + 10;
			mc.getTextureManager().bindTexture(bg);
			drawTexturedModalRect(x, y, 0, 0, 45, 71);
			drawTexturedModalRect(x + 45, y, 131, 0, 45, 71);
		}
		// info
		if (getLabel(10) != null) {
			int i = menu.items.getCountEmpty();
			float f0 = menu.items.getSizeInventory() == 0 ? 0.0f : (float) i / (float) menu.items.getSizeInventory();
			Component text;
			if (menu.items.getSizeInventory() == 0) { text = Component.translatable("bank.slots.empty"); }
			else { text = Component.translatable("bank.slots.info",
					(f0 < 0.2 ? ((char) 167) + "c": f0 < 0.85 ? ((char) 167) + "e": "") + i,
					"" + menu.items.getSizeInventory()); }
			getLabel(10).setMessage(text);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (startXMouse != 0 && startYMouse != 0) {
			Mouse.setCursorPosition(startXMouse, startYMouse);
			startXMouse = 0;
			startYMouse = 0;
		}
		if (isWait) { drawWait(); }
		// clear all items
		if (getButton(10) != null) { getButton(10).setIsEnabled(!menu.items.isEmpty()); }
		// background
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (getSlotUnderMouse() == null && hoverSlot != null) { ((IGuiContainerMixin) this).setHoveredSlot(hoverSlot); }
		// open or upgrade check
		GuiButtonNop button = getButton(0);
		if (button != null && button.isVisible()) {
			List<Component> hover = new ArrayList<>();
			hover.add(Component.translatable("bank.hover.update." + canUpgrade,
					((char)167) + "6" + ceilsUpdate, ((char)167) + "6" + menu.items.getSizeInventory(), ((char)167) + "6" + menu.data.bank.ceilSettings.get(menu.ceil).maxCells));
			boolean bo = true;
			if (!canUpgrade && menu.ceil > 0) {
				Bank.CeilSettings cs = menu.data.bank.ceilSettings.get(menu.ceil - 1);
				NpcMiscInventory invPre = menu.data.get(menu.ceil - 1);
				if (!cs.isFree && (invPre == null || invPre.getSizeInventory() != cs.maxCells)) {
					if (!player.isCreative()) { hover.add(Component.translatable("gui.allowed")); bo = false; }
					hover.add(Component.translatable("bank.hover.update.not.open", "" + menu.ceil));
				}
			}
			if (!stack.isEmpty()) {
				Map<ItemStack, Integer> items = new HashMap<>();
				items.put(stack, stack.getCount() * ceilsUpdate);
				if (!Util.instance.canRemoveItems(player.inventory.mainInventory, items, false, false)) {
					if (bo && !player.isCreative()) { hover.add(Component.translatable("gui.allowed")); bo = false; }
					hover.add(Component.translatable("hover.operation.not.items"));
					hover.add(Component.literal(stack.getDisplayName())
							.append(Component.literal(" x" + stack.getCount() * ceilsUpdate).withStyle(TextFormatting.DARK_RED)));
				}
			}
			PlayerData data = CustomNpcs.proxy.getPlayerData(player);
			if (money > 0 && data.game.getMoney() < money * (long) ceilsUpdate) {
				if (bo && !player.isCreative()) { hover.add(Component.translatable("gui.allowed")); bo = false; }
				hover.add(Component.translatable("hover.operation.not.money"));
				hover.add(Component.literal(money * ceilsUpdate + CustomNpcs.displayCurrencies).withStyle(TextFormatting.DARK_RED));
			}
			if (donat > 0 && data.game.getDonat() < donat * (long) ceilsUpdate) {
				if (bo && !player.isCreative()) { hover.add(Component.translatable("gui.allowed")); bo = false; }
				hover.add(Component.translatable("hover.operation.not.donat"));
				hover.add(Component.literal(donat * ceilsUpdate + CustomNpcs.displayDonation).withStyle(TextFormatting.DARK_RED));
			}
			button.setIsEnabled(bo || player.isCreative());
			if (button.isHoveredOrFocused()) { setHoverText(hover); }
		}
		if (!hoverText.isEmpty()) { drawHoverText(null); }
		else { renderHoveredToolTip(mouseX, mouseY); }
	}

	@Override
	public void subGuiClosed(GuiScreen subgui) {
		if (subgui instanceof SubGuiEditBankAccess) {
			SubGuiEditBankAccess gui = (SubGuiEditBankAccess) subgui;
			boolean isChanged = false;
			if (menu.data.bank.isChanging != gui.isChanging) {
				menu.data.bank.isChanging = gui.isChanging;
				isChanged = true;
			}
			if (!menu.data.bank.owner.equals(gui.owner)) {
				menu.data.bank.owner = gui.owner;
				isChanged = true;
			}
			if (gui.names.size() != menu.data.bank.access.size()) {
				menu.data.bank.access.clear();
				menu.data.bank.access.addAll(gui.names);
				isChanged = true;
			}
			else {
				for (String name : gui.names) {
					if (menu.data.bank.access.contains(name)) { continue; }
					menu.data.bank.access.clear();
					menu.data.bank.access.addAll(gui.names);
					isChanged = true;
					break;
				}
			}
			if (isChanged) {
				isWait = true;
				menu.data.setChanged();
			}
		}
	}

	@Override
	public boolean keyPressed(char typedChar, int keyCode) {
		if (isWait) { return false; }
		if (!hasSubGui() && isMany) {
			if (GuiBasic.isUpKey(keyCode)) {
				resetRow(-18);
				return true;
			}
			if (GuiBasic.isDownKey(keyCode)) {
				resetRow(+18);
				return true;
			}
		}
		return super.keyPressed(typedChar, keyCode);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (isWait) { return false; }
		isScrolling = false;
		if (!hasSubGui() && isMany) {
			double u = (double) guiLeft + (double) xSize - 17.0d;
			double v = ((double) height - (double) ySize) / 2.0d + 17.5d;
			if (isMouseHover(mouseX, mouseY, u, v, 10, 170.5d)) {
				isScrolling = true;
				mouseY -= 19.0d;
				if (mouseY <= 15.0d) { scrollY = 0; }
				else if (mouseY >= 155.0d) { scrollY = scrollMax; }
				else {
					mouseY -= 15.0d;
					scrollY = (int) (mouseY / 135.0d * (double) scrollMax);
				}
				resetRow(0);
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
		if (isWait) { return false; }
		if (!hasSubGui() && isScrolling && mouseButton == 0) {
			double y0 = ((double) height - (double) ySize) / 2.0d + 18.0d;
			double y1 = y0 + 170.0d;
			if (mouseY >= y0 && mouseY < y1) {
				mouseY -= 19.0d;
				if (mouseY <= 15.0d) { scrollY = 0; }
				else if (mouseY >= 155.0d) { scrollY = scrollMax; }
				else {
					mouseY -= 15.0d;
					scrollY = (int) (mouseY / 135.0d * (double) scrollMax);
				}
				resetRow(0);
				return true;
			}
		}
		return super.mouseDragged(mouseX, mouseY, mouseButton, dx, dy);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrolled) {
		if (isWait) { return false; }
		if (!hasSubGui() && scrolled != 0) {
			int u = (width - xSize) / 2 - 37;
			int v = (height - ySize) / 2 + 14;
			if (isMouseHover(mouseX, mouseY, u, v, 70, 194)) {
				if ((scrolled > 0 && getButton(1) != null && getButton(1).isVisible()) ||
						(scrolled < 0 && getButton(2) != null && getButton(2).isVisible())) {
					v = ValueUtil.correctInt(ceilPos - (int) scrolled, 0, menu.data.bank.ceilSettings.size() - 5);
					if (v < 0) { v = 0; }
					if (ceilPos != v) {
						ceilPos = v;
						initGui();
					}
				}
				return true;
			}
			if (isMany) {
				resetRow(scrolled > 0 ? -6 : 6);
				return true;
			}
		}
		return super.mouseScrolled(mouseX, mouseY, scrolled);
	}

	@Override
	public Slot findSlot(double mouseX, double mouseY, Slot foundSlot) {
		if (isWait) { return foundSlot; }
		if (menu.items.getSizeInventory() > 0) {
			int x = guiLeft + 7, xs;
			int y = guiTop + 20, ys;
			for (int s = 0; s < menu.items.getSizeInventory(); s++) {
				Slot slot = menu.inventorySlots.get(s);
				xs = x + (s % 9) * 18;
				ys = y - scrollY + (int) (Math.floor((double) s / 9.0d) * 18.0d);
				if (ys > y - 17 && ys < y + 90) {
					xs += 1;
					ys += 1;
					int yh = ys;
					int hy = 16;
					if (ys < y) { yh = y; hy = 16 - y + ys; }
					else if (ys > y + 72) { hy = Math.min(16, 90 - ys + y); }
					if (isMouseHover(mouseX, mouseY, xs, yh, 16, hy)) { return isWait ? null : slot; }
				}
			}
		}
		return foundSlot;
	}

	@Override
	public void onClose() {
		super.onClose();
		startXMouse = wrapper.mouseX;
		startYMouse = wrapper.mouseY;
		if (ContainerNPCBank.editPlayerBankData != null && !ContainerNPCBank.editPlayerBankData.isEmpty()) {
			GuiNpcManagePlayerData gui = new GuiNpcManagePlayerData(npc);
			gui.selection = EnumPlayerData.Bank;
			gui.selectedPlayer = Component.literal(ContainerNPCBank.editPlayerBankData);
			NoppesUtil.openGUI(player, gui);
			Packets.sendServer(new SPacketPlayerDataGet(EnumPlayerData.Bank, ContainerNPCBank.editPlayerBankData));
			ContainerNPCBank.editPlayerBankData = null;
		}
	}

	private void drawSlot(Slot slotIn, int xPos, int yPos) {
		Slot clickedSlot = ((IGuiContainerMixin) this).getClickedSlot();
		ItemStack draggedStack = ((IGuiContainerMixin) this).getDraggedStack();
		boolean isRightMouseClick = ((IGuiContainerMixin) this).getIsRightMouseClick();
		int dragSplittingLimit = ((IGuiContainerMixin) this).getDragSplittingLimit();
		ItemStack itemstack = slotIn.getStack();
		boolean hovering = false;
		boolean flag1 = slotIn == clickedSlot && !draggedStack.isEmpty() && !isRightMouseClick;
		ItemStack itemstack1 = mc.player.inventory.getItemStack();
		String s = null;
		if (slotIn == clickedSlot && !draggedStack.isEmpty() && isRightMouseClick && !itemstack.isEmpty()) {
			itemstack = itemstack.copy();
			itemstack.setCount(itemstack.getCount() / 2);
		}
		else if (dragSplitting && dragSplittingSlots.contains(slotIn) && !itemstack1.isEmpty()) {
			if (dragSplittingSlots.size() == 1) { return; }
			if (Container.canAddItemToSlot(slotIn, itemstack1, true) && inventorySlots.canDragIntoSlot(slotIn)) {
				itemstack = itemstack1.copy();
				hovering = true;
				Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemstack, slotIn.getStack().isEmpty() ? 0 : slotIn.getStack().getCount());
				int k = Math.min(itemstack.getMaxStackSize(), slotIn.getItemStackLimit(itemstack));
				if (itemstack.getCount() > k) {
					s = TextFormatting.YELLOW.toString() + k;
					itemstack.setCount(k);
				}
			}
			else
			{
				dragSplittingSlots.remove(slotIn);
				updateDragSplitting();
			}
		}
		zLevel = 100.0F;
		itemRender.zLevel = 100.0F;
		if (itemstack.isEmpty() && slotIn.isEnabled()) {
			TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();
			if (textureatlassprite != null) {
				GlStateManager.disableLighting();
				mc.getTextureManager().bindTexture(slotIn.getBackgroundLocation());
				drawTexturedModalRect(xPos, yPos, textureatlassprite, 16, 16);
				GlStateManager.enableLighting();
				flag1 = true;
			}
		}
		if (!flag1) {
			if (hovering) { drawRect(xPos, yPos, xPos + 16, yPos + 16, 0x80FFFFFF); }
			GlStateManager.enableDepth();
			itemRender.renderItemAndEffectIntoGUI(mc.player, itemstack, xPos, yPos);
			itemRender.renderItemOverlayIntoGUI(fontRenderer, itemstack, xPos, yPos, s);
		}
		itemRender.zLevel = 0.0F;
		zLevel = 0.0F;
	}

	private void updateDragSplitting() {
		ItemStack itemstack = mc.player.inventory.getItemStack();
		int dragSplittingLimit = ((IGuiContainerMixin) this).getDragSplittingLimit();
		if (!itemstack.isEmpty() && dragSplitting) {
			if (dragSplittingLimit == 2) {
				((IGuiContainerMixin) this).setDragSplittingRemnant(itemstack.getMaxStackSize());
			}
			else {
				((IGuiContainerMixin) this).setDragSplittingRemnant(itemstack.getCount());
				for (Slot slot : dragSplittingSlots) {
					ItemStack itemstack1 = itemstack.copy();
					ItemStack itemstack2 = slot.getStack();
					int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
					Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemstack1, i);
					int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));
					if (itemstack1.getCount() > j) { itemstack1.setCount(j); }
					int dragSplittingRemnant = ((IGuiContainerMixin) this).getDragSplittingRemnant();
					((IGuiContainerMixin) this).setDragSplittingRemnant(dragSplittingRemnant - itemstack1.getCount() - i);
				}
			}
		}
	}

	private void resetRow(int step) {
		if (!isMany) { return; }
		scrollY += step;
		if (scrollY < 0) { scrollY = 0; }
		else if (scrollY > scrollMax) { scrollY = scrollMax; }
		scrollBarY = (int) (((float) height - (float) ySize) / 2.0f + 17.0f + (float) scrollY / (float) scrollMax * 141.0f);
	}

	public static class GuiSafeButton extends GuiButtonNop {

		protected GuiNPCBankChest listener;
		protected final int ceil;
		protected final boolean isCeil;
		protected long ticks;
		protected long lastTick = System.currentTimeMillis();

		public GuiSafeButton(GuiNPCBankChest gui, int buttonId, Object label, int x, int y, int w, int h, int ceilIn) {
			super(gui, buttonId, label, x, y, null);
			width = w;
			height = h;
			listener = gui;

			isCeil = h == 14;
			ceil = ceilIn;
			txrX = 70;
			txrY = isCeil ? 0 : 28;
			txrW = w * 2;
			txrH = h * 2;
			ticks = listener.menu.ceil == ceil ? 500 : 0;
		}

		@Override
		public void renderWidget(int mouseX, int mouseY, float partialTicks) {
			if (!visible) { return; }
			Minecraft mc = Minecraft.getMinecraft();
			isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
			GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
			GlStateManager.enableBlend();
			// animation
			GlStateManager.pushMatrix();
			GlStateManager.translate(getX(), getY(), 0);
			GlStateManager.scale(0.5f, 0.5f, 1.0f);
			// door
			if (isCeil) { drawDoor(listener.menu.ceil == ceil || isHovered); }
			else {
				if (isHovered) {
					float f = Mouse.isButtonDown(0) ? 0.5f : 0.75f;
					GlStateManager.color(f, f, f, alpha);
				}
				mc.getTextureManager().bindTexture(SAFE);
				drawTexturedModalRect(0, 0, txrX, txrY, txrW, txrH);
				GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
			} // up or down
			GlStateManager.popMatrix();
			// text
			GlStateManager.pushMatrix();
			Component mes = getMessage();
			renderString(getMessage(), getX() + (isCeil ? - mc.fontRenderer.getStringWidth(mes.getString()) : 2), getY(),
					getX() + (isCeil ? 0 : getWidth() - 2), getY() + getHeight(),
					getFGColor() | (int) Math.ceil(alpha * 255.0F) << 24, showShadow, true, null);
			GlStateManager.popMatrix();
		}

		private void drawDoor(boolean isOpen) {
			// inside
			float f0 = 0.0f;
			NpcMiscInventory inv = listener.menu.data.get(ceil);
			if (inv != null) {
				f0 = inv.getSizeInventory() == 0 ? 1.0f : (float) inv.getCountEmpty() / (float) inv.getSizeInventory();
			}
			int y = f0 >= 0.95f ? 106 : f0 <= 0.2f ? 50 : 78;
			Minecraft.getMinecraft().getTextureManager().bindTexture(SAFE);
			drawTexturedModalRect(0, 0, txrX, y, txrW, txrH);
			double d0 = (double) ticks;
			if (d0 < 0) { ticks = 0; d0 = 0; }
			if (d0 == 0.0d) { drawTexturedModalRect(0, 0, txrX, txrY, txrW, txrH); }
			else if (d0 < 500.0d) {
				double d1 = Math.sin(Math.toRadians(90 * d0 / 500.0d));
				if (d1 < 0.5d) {
					f0 = (float) ValueUtil.correctDouble(-4.7d * d1 + 4.0d, 1.65d, 4.0d);
					double f1 = ValueUtil.correctDouble(85.714286d * Math.pow(d1, 3.0d) + 7.142857d * Math.pow(d1, 2.0d) + 17.0d * d1,
							0.0d, 21.0f);
					GlStateManager.scale(f0, 1.0f, 1.0f);
					GlStateManager.translate(f1, -4.0d, 1.0d);
					drawTexturedModalRect(0, 0, 136, 0, 20, 41);
					GlStateManager.pushMatrix();
					GlStateManager.scale(2.0f * (float) d1, 1.0f, 1.0f);
					drawTexturedModalRect(-6, 0, 130, 0, 6, 41);
					GlStateManager.popMatrix();
				}
				else {
					f0 = (float) ValueUtil.correctDouble(-1.7329d * d1 + 2.7329d, 1.0d, 1.86645d);
					double f1 = ValueUtil.correctDouble(38.50667d * Math.pow(d1, 3.0d) - 28.824 * Math.pow(d1, 2.0d) + 29.395333d * d1 - 1.078d,
							11.227d, 38.0d);
					GlStateManager.scale(f0, 1.0f, 1.0f);
					GlStateManager.translate(f1, -4.0f, 1.0f);
					drawTexturedModalRect(0, 0, 130, 0, 26, 41);
				}
			}
			else {
				GlStateManager.translate(38.0f, -4.0d, 1.0d);
				drawTexturedModalRect(0, 0, 130, 0, 26, 41);
			}
			long l = System.currentTimeMillis() - lastTick;
			ticks = ValueUtil.correctLong(ticks + (isOpen ? l : -l), 0L, 500L);
			lastTick = System.currentTimeMillis();
		}

	}

}
