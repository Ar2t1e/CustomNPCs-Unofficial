package noppes.npcs.client.gui.player.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import noppes.npcs.client.util.TabRegistry;

import javax.annotation.Nonnull;
import java.awt.*;

public abstract class AbstractTab extends GuiButton {

	protected ResourceLocation texture = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	protected RenderItem itemRender;
	public int potionOffsetLast;
	ItemStack renderStack;

	// New from Unofficial (GoodBird)
	protected boolean active;
	protected boolean isHovered;

	public AbstractTab(int id, int posX, int posY, @Nonnull ItemStack renderStackIn, @Nonnull Component hoverText) {
		super(id, posX, posY, 28, 32, hoverText.getFormattedText());
		renderStack = renderStackIn;
		itemRender = FMLClientHandler.instance().getClient().getRenderItem();
	}

	@Override
	public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		int newPotionOffset = TabRegistry.getPotionOffsetNEI();
		if (mc.currentScreen instanceof GuiInventory) {
			ScaledResolution scaleW = new ScaledResolution(mc);
			int left = (scaleW.getScaledWidth() - ((GuiContainer) mc.currentScreen).getXSize()) / 2;
			newPotionOffset = ((GuiContainer) mc.currentScreen).getGuiLeft() - left;
		}
		if (newPotionOffset != potionOffsetLast) {
			x += newPotionOffset - potionOffsetLast;
			potionOffsetLast = newPotionOffset;
		}
		isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		active = false;
		if (visible) {
			if (mc.currentScreen instanceof GuiInventory) { active = id == 100; } else { active = id != 100; }
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			int yTexPos = isFocused() ? 32 : 2;
			int ySize = isFocused() ? 32 : 29;
			int xOffset = active ? 0 : 28;
			mc.getTextureManager().bindTexture(texture);
			drawTexturedModalRect(x, y, xOffset, yTexPos, 28, ySize);

			zLevel = 100.0f;
			itemRender.zLevel = 100.0f;
			GlStateManager.enableLighting();
			GlStateManager.enableRescaleNormal();
			RenderHelper.enableGUIStandardItemLighting();
			itemRender.renderItemAndEffectIntoGUI(renderStack, x + 6, y + 8);
			itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, renderStack, x + 6, y + 8, null);
			GlStateManager.disableLighting();
			itemRender.zLevel = 0.0f;
			zLevel = 0.0f;
			if (isHovered && displayString != null && !displayString.isEmpty()) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0f, 0.0f, 300.0f);
				drawHoveringText(mc, mouseX, mouseY);
				GlStateManager.popMatrix();
			}
			RenderHelper.disableStandardItemLighting();
		}
	}

	@Override
	public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
		boolean inWindow = visible && isHovered;
		if (inWindow) { onTabClicked(); }
		return inWindow;
	}

	protected void drawHoveringText(@Nonnull Minecraft mc, int x, int y) {
		y -= 12;
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		int k = mc.fontRenderer.getStringWidth(displayString);
		int i1 = 8;

		int color = new Color(0xF0100010).getRGB();
		drawGradientRect(x - 3, y - 4, x + k + 3, y - 3, color, color);
		drawGradientRect(x - 3, y + i1 + 3, x + k + 3, y + i1 + 4, color, color);
		drawGradientRect(x - 3, y - 3, x + k + 3, y + i1 + 3, color, color);
		drawGradientRect(x - 4, y - 3, x - 3, y + i1 + 3, color, color);
		drawGradientRect(x + k + 3, y - 3, x + k + 4, y + i1 + 3, color, color);

		color = new Color(0x505000FF).getRGB();
		int nextColor = (color & new Color(0xFEFEFE).getRGB()) >> 1 | (color & new Color(0xFF000000).getRGB());
		drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + i1 + 3 - 1, color, nextColor);
		drawGradientRect(x + k + 2, y - 3 + 1, x + k + 3, y + i1 + 3 - 1, color, nextColor);
		drawGradientRect(x - 3, y - 3, x + k + 3, y - 3 + 1, color, color);
		drawGradientRect(x - 3, y + i1 + 2, x + k + 3, y + i1 + 3, nextColor, nextColor);

		mc.fontRenderer.drawStringWithShadow(displayString, x, y, -1);

		GlStateManager.enableLighting();
		GlStateManager.enableRescaleNormal();
	}

	public boolean isFocused() { return active || isHovered; }

	public abstract void onTabClicked();

	public abstract boolean shouldAddToList();

}
