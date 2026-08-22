package noppes.npcs.client.gui.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.controllers.data.Marcet;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMarcetsGet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

// Changed from Unofficial (BetaZavr)
public class SubGuiNpcSelectTrader extends GuiNPCInterface implements IGuiData, ICustomScrollListener {

	protected final Map<Component, Integer> data = new HashMap<>();
	protected GuiCustomScrollNop scrollMarkets;
	protected Component select = Component.empty();
	public int id;

	public SubGuiNpcSelectTrader(int idIn) {
		super(NoppesUtilServer.getEditingNpc(Minecraft.getMinecraft().player));
		setBackground("menubg.png");
		imageWidth = 190;
		imageHeight = 217;

		id = idIn;
		Packets.sendServer(new SPacketMarcetsGet(-1));
	}

	@Override
	public void initGui() {
		super.initGui();
		List<Component> list = new ArrayList<>();
		data.clear();
		for (Marcet m : MarcetController.getInstance().markets.values()) {
			if (!m.isValid()) { continue; }
			Component name = m.getSettingName();
			list.add(name);
			data.put(name, m.getId());
			if (id == m.getId()) { select = name; }
		}
		if (scrollMarkets == null) { scrollMarkets = addScroll(0).setSize(170, 157); }
		int x = guiLeft + 12, y = guiTop + 14;
		scrollMarkets.setNormalList(list);
		if (data.containsValue(id) && !select.getFormattedText().isEmpty()) { scrollMarkets.setSelected(select); }
		add(scrollMarkets.setPos(x, y));
		addLabel(0, x + 2, y - 10, "market.select")
				.setSize(170, 12)
				.setHoverTexts("market.hover.role.list");
		addButton(66, guiLeft + 50, guiTop + 190, "gui.done")
				.setSize(90, 20)
				.setHoverTexts("hover.back");
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (background != null) { // add right
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			GlStateManager.pushMatrix();
			GlStateManager.translate(guiLeft, guiTop, 0.0f);
			GlStateManager.scale(bgScale, bgScale, bgScale);
			mc.getTextureManager().bindTexture(background);
			drawTexturedModalRect(imageWidth, 0, 252, 0, 4, imageHeight);
			GlStateManager.popMatrix();
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		if (button.id == 66) { onClose(); }
	}

	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		if (scroll.getSelected().equals(select.getFormattedText()) || !data.containsKey(scroll.getNormalSelected())) { return; }
		select = scroll.getNormalSelected();
		id = data.get(scroll.getNormalSelected());
		initGui();
	}

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { onClose(); }

	@Override
	public void setGuiData(NBTTagCompound compound) { initGui(); }

}
