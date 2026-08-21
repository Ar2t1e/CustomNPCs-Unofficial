package noppes.npcs.client.gui.custom;

import java.util.*;

import net.minecraft.network.chat.Component;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiKeyPressed;
import noppes.npcs.packets.server.SPacketCustomGuiSubGuiClosed;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.GuiBasicContainer;
import noppes.npcs.shared.client.gui.components.custom.IComponentCustomGui;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.containers.ContainerCustomGui;

import javax.annotation.Nonnull;

public class GuiCustom extends GuiBasicContainer<ContainerCustomGui> implements IGuiData {

	protected GuiCustomComponents components = new GuiCustomComponents();
	public GuiCustomScrollingPanel scrollingPanel = new GuiCustomScrollingPanel();
	public noppes.npcs.shared.client.gui.components.custom.CustomGuiTexturedRect background;
	public CustomGuiWrapper guiWrapper;
	public GuiCustom subgui = null;
	public GuiCustom parent = null;
	public GuiCustom.InitCallback initCallback;

	public GuiCustom(ContainerCustomGui container) {
		super(container, Component.empty());
	}

	@Override
	public void initGui() {
		super.initGui();
		if (guiWrapper != null) {
			scrollingPanel.setComponents(this, guiWrapper.getScrollingPanel());
			components.setComponents(this, guiWrapper);
			closeOnEsc = guiWrapper.getClosesOnEsc();
		} else { closeOnEsc = true; }
		if (initCallback != null) { initCallback.init(); }
		if (subgui != null) { subgui.initGui(); }
	}

	@Override
	public void updateScreen() {
		if (subgui != null) { subgui.updateScreen(); }
		else {
			components.containerTick();
			scrollingPanel.containerTick();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		hoverText.clear();
		drawDefaultBackground();
		GlStateManager.pushMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) getGuiLeft(), (float) getGuiTop(), 0.0F);
			if (background != null) { background.render(mouseX, mouseY, partialTicks); }
			components.render(mouseX - getGuiLeft(), mouseY - getGuiTop(), partialTicks);
			scrollingPanel.render(mouseX - getGuiLeft(), mouseY - getGuiTop(), partialTicks);
			if (!hoverText.isEmpty() && subgui == null) { drawHoveringText(toHoverText(), mouseX, mouseY); }
			GlStateManager.popMatrix();
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (subgui == null) { renderHoveredToolTip(mouseX, mouseY); }
		GlStateManager.popMatrix();
		if (subgui != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, 80.0F);
			subgui.drawScreen(mouseX, mouseY, partialTicks);
			GlStateManager.popMatrix();
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) { }

	@Override
	public boolean keyPressed(char typedChar, int keyCode) {
		if (subgui != null) { return subgui.keyPressed(typedChar, keyCode); }
		Packets.sendServer(new SPacketCustomGuiKeyPressed(keyCode));
		if (components.keyPressed(typedChar, keyCode)) { return true; }
		if (scrollingPanel.keyPressed(typedChar, keyCode)) { return true; }
		return GuiBasic.isInventoryKey(keyCode) || super.keyPressed(typedChar, keyCode);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (subgui != null) { return subgui.mouseClicked(mouseX, mouseY, mouseButton); }
		boolean clicked = false;
		clicked |= components.mouseClicked(mouseX - (double)getGuiLeft(), mouseY - (double)getGuiTop(), mouseButton);
		clicked |= scrollingPanel.mouseClicked(mouseX - (double)getGuiLeft(), mouseY - (double)getGuiTop(), mouseButton);
		return clicked | super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrolled) {
		if (subgui != null) { return subgui.mouseScrolled(mouseX, mouseY, scrolled); }
		boolean isScrolled = false;
		isScrolled |= components.mouseScrolled(mouseX - (double)getGuiLeft(), mouseY - (double)getGuiTop(), scrolled);
		isScrolled |= scrollingPanel.mouseScrolled(mouseX - (double)getGuiLeft(), mouseY - (double)getGuiTop(), scrolled);
		return isScrolled | super.mouseScrolled(mouseX, mouseY, scrolled);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
		if (subgui != null) { return subgui.mouseDragged(mouseX, mouseY, mouseButton, dx, dy); }
		boolean clicked = false;
		clicked |= components.mouseDragged(mouseX - (double)getGuiLeft(), mouseY - (double)getGuiTop(), mouseButton, dx, dy);
		clicked |= scrollingPanel.mouseDragged(mouseX - (double)getGuiLeft(), mouseY - (double)getGuiTop(), mouseButton, dx, dy);
		return clicked | super.mouseDragged(mouseX, mouseY, mouseButton, dx, dy);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (subgui != null) { return subgui.mouseReleased(mouseX, mouseY, mouseButton); }
		boolean released = false;
		released |= components.mouseReleased(mouseX - (double)getGuiLeft(), mouseY - (double)getGuiTop(), mouseButton);
		released |= scrollingPanel.mouseReleased(mouseX - (double)getGuiLeft(), mouseY - (double)getGuiTop(), mouseButton);
		return released | super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean doesGuiPauseGame() { return guiWrapper == null || guiWrapper.getDoesPauseGame(); }

	@Override
	public void onClose() {
		if (subgui == null) {
			if (parent == null) { super.onClose(); }
			else {
				Packets.sendServer(new SPacketCustomGuiSubGuiClosed());
				parent.subgui = null;
			}
		}
		else { subgui.onClose(); }
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		setGuiWrapper((CustomGuiWrapper)(new CustomGuiWrapper((IPlayer<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(mc.player))).of(compound));
		initGui();
	}

	@Override
	public void onResize(@Nonnull Minecraft minecraft, int width, int height) {
		super.onResize(minecraft, width, height);
		if (subgui != null) { subgui.onResize(minecraft, width, height); }
	}

	public void setGuiWrapper(CustomGuiWrapper guiWrapperIn) {
		guiWrapper = guiWrapperIn;
		xSize = guiWrapperIn.getWidth();
		ySize = guiWrapperIn.getHeight();
		background = new noppes.npcs.shared.client.gui.components.custom.CustomGuiTexturedRect(this, (CustomGuiTexturedRectWrapper) guiWrapperIn.getBackgroundRect());
		if (guiWrapperIn.getSubGuiWrapper() != null) {
			if (subgui == null &&minecraft.player != null) {
				subgui = new GuiCustom((ContainerCustomGui) inventorySlots);
				subgui.setWorldAndResolution(minecraft, width, height);
			}
			if (subgui != null) {
				subgui.parent = this;
				subgui.setGuiWrapper(guiWrapperIn.getSubGuiWrapper());
			}
		} else {
			((ContainerCustomGui) inventorySlots).setGui(guiWrapperIn, mc.player);
			subgui = null;
			if (parent == null) { initGui(); }
		}
	}

	@SuppressWarnings("unused")
	public int getTotalGuiLeft() { return parent != null ? parent.getTotalGuiLeft() + getGuiLeft() : getGuiLeft(); }

	@SuppressWarnings("unused")
	public int getTotalGuiTop() { return parent != null ? parent.getTotalGuiTop() + getGuiTop() : getGuiTop(); }

	@SuppressWarnings("unused")
	public void addPanel(IComponentGui component) { scrollingPanel.components.put(component.getId(), component); }

	public IComponentGui getComponent(UUID id) {
		Optional<IComponentGui> c = components.components.values()
				.stream()
				.filter((t) -> t instanceof IComponentCustomGui &&
						((IComponentCustomGui) t).component() != null &&
						((IComponentCustomGui) t).component().getUniqueID().equals(id))
				.findFirst();
		if (c.isPresent()) { return c.get(); }
		c = scrollingPanel.components.values().stream().filter((t) -> t instanceof IComponentCustomGui &&
				((IComponentCustomGui) t).component() != null &&
				((IComponentCustomGui) t).component().getUniqueID().equals(id)).findFirst();
		return c.orElseGet(() -> subgui != null ? subgui.getComponent(id) : null);
	}

	public interface InitCallback {  void init(); }

}
