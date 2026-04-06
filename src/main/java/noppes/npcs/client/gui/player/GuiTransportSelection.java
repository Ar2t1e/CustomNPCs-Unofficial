package noppes.npcs.client.gui.player;

import java.awt.*;
import java.util.*;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketPlayerTransport;
import noppes.npcs.roles.RoleTransporter;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.util.Util;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;

public class GuiTransportSelection extends GuiNPCInterface
		implements IScrollData, ICustomScrollListener {

	protected final ResourceLocation resource = getResource("smallbg.png");
	protected GuiCustomScrollNop scroll;

	// New from Unofficial (BetaZavr)
	protected final Map<Component, Integer> data = new LinkedHashMap<>();
	protected Map<ItemStack, Boolean> barterItems;
	protected boolean canTransport = true;
	protected int bxSize = 0;
	protected int bySize = 0;

	public GuiTransportSelection(EntityNPCInterface npc) {
		super(npc);
		drawDefaultBackground = false;
		imageWidth = 176;
		title = Component.empty();
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft = (width - imageWidth) / 2;
		guiTop = (height - 222) / 2;
		Component title = Component.empty();
		TransportController tData = TransportController.getInstance();
		if (npc != null && npc.role instanceof RoleTransporter) {
			TransportLocation loc = ((RoleTransporter) npc.role).getLocation();
			if (loc != null) {
				title = Component.translatable(loc.category.title).append(": ").append(Component.translatable(loc.name));
			}
		}
		addLabel(0, guiLeft + (imageWidth - fontRenderer.getStringWidth(title.getString())) / 2, guiTop + 10, title);
		addButton(0, guiLeft + 10, guiTop + 192, "transporter.travel")
				.setSize(156, 20);
		if (scroll == null) { scroll = addScroll(0).setSize(156, 165); }
		List<Component> list = new ArrayList<>(data.keySet());
		add(scroll.setPos(guiLeft + 10, guiTop + 20)
				.setNormalList(list));
		if (!data.isEmpty()) {
			List<Component> suffixes = new ArrayList<>();
			for (Component name : list) {
				Component sfx = Component.empty();
				TransportLocation loc = tData.getTransport(data.get(name));
				if (loc != null) {
					TextFormatting color = TextFormatting.GREEN;
					if (loc.money > 0 || !loc.inventory.isEmpty()) {
						if (loc.money > 0) {
							if (loc.money > CustomNpcs.proxy.getPlayerData(player).game.getMoney()) { color = TextFormatting.RED; }
							sfx = Component.empty()
									.append(Component.literal(Util.instance.getTextReducedNumber(loc.money, true, true, false)
													+ CustomNpcs.displayCurrencies)
											.withStyle(color));
						}
					}
					if (!loc.inventory.isEmpty()) {
						color = TextFormatting.GOLD;
						Map<ItemStack, Boolean> items = Util.instance.getInventoryItemCount(player, loc.inventory);
						for (ItemStack s : items.keySet()) {
							if (!items.get(s)) { color = TextFormatting.RED; break; }
						}
						sfx.append(Component.literal(" [").withStyle(TextFormatting.GRAY))
								.append(Component.literal("I").withStyle(color))
								.append(Component.literal("]").withStyle(TextFormatting.GRAY));
					}
				}
				suffixes.add(sfx);
			}
			scroll.setSuffixes(suffixes);
		}
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		if (button.id == 0 && data.containsKey(scroll.getNormalSelected())) {
			Packets.sendServer(new SPacketPlayerTransport(data.get(scroll.getNormalSelected())));
			onClose();
		}
	}

	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(resource);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, 176, 222);
		barterItems = null;
		if (data.containsKey(scroll.getNormalSelected())) {
			TransportLocation select = TransportController.getInstance().getTransport(data.get(scroll.getNormalSelected()));
			if (select != null) {
				// barter & money
				if (bxSize > 0) {
					int w = bxSize + 13;
					int h = bySize + 18;
					int x = guiLeft + 176;
					int y = guiTop + 14;
					mc.getTextureManager().bindTexture(resource);
					drawTexturedModalRect(x, y, 176 - w, 0, w, h);
					drawTexturedModalRect(x, y + h, 176 - w, 218, w, 4);
					x += 5;
					y += 4;
					if (!select.inventory.isEmpty()) {
						fontRenderer.drawString(Component.translatable("market.barter").getFormattedText(), x, y, CustomNpcs.LableColor.getRGB(), false);
					}
					if (select.money > 0L) {
						fontRenderer.drawString(Util.instance.getTextReducedNumber(select.money, true, true, false)
								+ CustomNpcs.displayCurrencies, x, y + 32, CustomNpcs.LableColor.getRGB(), false);
					}
				}
				// items
				bxSize = 0;
				bySize = 0;
				if (!select.inventory.isEmpty()) {
					GlStateManager.pushMatrix();
					mc.getTextureManager().bindTexture(RESOURCE_SLOT);
					barterItems = Util.instance.getInventoryItemCount(player, select.inventory);
					int slot = 0;
					canTransport = true;
					for (ItemStack stack : barterItems.keySet()) {
						int u = guiLeft + imageWidth + 5 + (slot % 3) * 18;
						int v = guiTop + 30 + (slot / 3) * 18;
						GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
						drawTexturedModalRect(u, v, 0, 0, 18, 18);
						if (canTransport) { canTransport = barterItems.get(stack); }
						if (getButton(0) != null && getButton(0).isHoveredOrFocused()) {
							Gui.drawRect(u + 1, v + 1, u + 17, v + 17, barterItems.get(stack) ? 0x8000FF00 : player.isCreative() ? 0x80FF6E00 : 0x80FF0000);
						}
						slot++;
					}
					float a = (float) slot / 3.0f;
					bxSize = (a >= 1.0f ? 3 : a >= 2.0f / 3.0f ? 2 : 1) * 18;
					bySize = (int) (Math.ceil(a) * 18.0d);
					GlStateManager.popMatrix();
				}
				if (select.money > 0) { bySize += 14; }
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		if (data.containsKey(scroll.getNormalSelected())) {
			TransportLocation select = TransportController.getInstance().getTransport(data.get(scroll.getNormalSelected()));
			if (select != null && !select.inventory.isEmpty()) {
				int slot = 0;
				for (ItemStack stack : barterItems.keySet()) {
					int u = guiLeft + imageWidth + 5 + (slot % 3) * 18;
					int v = guiTop + 31 + (slot / 3) * 18;
					GlStateManager.pushMatrix();
					GlStateManager.translate(u, v, 50.0f);
					minecraft.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
					minecraft.getRenderItem().renderItemOverlays(fontRenderer, stack, 0, 0);
					GlStateManager.popMatrix();
					if (isMouseHover(mouseX, mouseY, u, v, 18, 18)) {
						setHoverText(stack.getTooltip(player, minecraft.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL));
					}
					slot++;
				}
			}
			GuiButtonNop button = getButton(0);
			if (button != null) {
				button.setIsEnabled(canTransport && select != null);
				if (!button.isEnabled() && button.isHoveredOrFocused()) {
					if (select == null) { setHoverText(Component.translatable("transporter.hover.not.select")); }
					else if (select.money > CustomNpcs.proxy.getPlayerData(player).game.getMoney()) { setHoverText(Component.translatable("transporter.hover.not.money")); }
					else { setHoverText(Component.translatable("transporter.hover.not.item")); }
				}
			}
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void setData(Vector<String> dataList, Map<String, Integer> dataMap) {
		data.clear();
		for (String key : dataMap.keySet()) { data.put(Component.translatable(key), dataMap.get(key)); }
		initGui();
	}

	@Override
	public void setSelected(String selected) { }

	// New from Unofficial (BetaZavr)
	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		if (data.containsKey(scroll.getNormalSelected())) { initGui();}
	}

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) {
		GuiButtonNop button = getButton(0);
		if (data.containsKey(scroll.getNormalSelected()) && button != null && button.isEnabled()) {
			onClose();
			Packets.sendServer(new SPacketPlayerTransport(data.get(scroll.getNormalSelected())));
		}
	}

}
