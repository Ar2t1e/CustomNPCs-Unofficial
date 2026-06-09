package noppes.npcs.client.gui;

import java.util.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.api.gui.IDimensionGetter;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.packets.server.SPacketDimensionsGet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNpcDimension extends GuiNPCInterface
		implements IDimensionGetter, IGuiData, ICustomScrollListener {

	protected final HashMap<Component, Integer> data = new HashMap<>();
	protected GuiCustomScrollNop scroll;

	public GuiNpcDimension() {
		super();
		setBackground("menubg.png");
		imageWidth = 256;

		Packets.sendServer(new SPacketDimensionsGet());
	}

	@Override
	public void initGui() {
		super.initGui();
		if (scroll == null) { scroll = addScroll(0).setSize(186, 199); }
		if (!scroll.hasSelected()) {
			for (Component key : data.keySet()) {
				if (data.get(key) == (minecraft.player != null ?
						minecraft.player.world.provider.getDimension() : 0)) {
					scroll.setSelected(key);
					break;
				}
			}
		}
		add(scroll.setPos(guiLeft + 4, guiTop + 4));
		int id = data.getOrDefault(scroll.getNormalSelected(), 0);
		// title
		addLabel(0, guiLeft, guiTop + 4, "gui.dimensions")
				.setCenter(imageWidth);
		// settings
		addButton(1, guiLeft + 192, guiTop + 36, "gui.settings")
				.setSize(60, 20)
				.setIsEnabled(scroll.hasSelected() && DimensionController.has(id))
				.setHoverTexts("dimensions.hover.settings");
		// add
		addButton(2, guiLeft + 192, guiTop + 80, "gui.add")
				.setSize(60, 20)
				.setHoverTexts("dimensions.hover.add");
		// del
		addButton(3, guiLeft + 192, guiTop + 102, "gui.remove")
				.setSize(60, 20)
				.setIsEnabled(scroll.hasSelected() && DimensionController.has(id))
				.setHoverTexts("dimensions.hover.del");
		// tp to
		addButton(4, guiLeft + 192, guiTop + 14, "TP")
				.setSize(60, 20)
				.setIsEnabled(scroll.hasSelected())
				.setHoverTexts("dimensions.hover.tp");
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 1: {
				player.sendMessage(new TextComponentTranslation("gui.wip"));
				if (data.containsKey(scroll.getNormalSelected())) {
					int id = data.get(scroll.getNormalSelected());
					if (DimensionController.has(id)) {
						//CustomNpcs.proxy.openGui(null, EnumGuiType.DimensionSetting, id, 0, 0);
					}
				}
				break;
			} // settings
			case 2: {
				player.sendMessage(new TextComponentTranslation("gui.wip"));
				//CustomNpcs.proxy.openGui(null, EnumGuiType.DimensionSetting, 0, 0, 0);
				break;
			} // add
			case 3: {
				if (data.containsKey(scroll.getNormalSelected())) {
					player.sendMessage(new TextComponentTranslation("gui.wip"));
					int id = data.get(scroll.getNormalSelected());
					if (DimensionController.has(id)) {
						/*ConfirmScreen guiYesNo = new ConfirmScreen((agree) -> {
							if (agree) {
								Client.sendData(EnumPacketServer.DimensionDelete, id);
							}
							NoppesUtil.openGUI(player, this);
						},
								Component.literal("ID: " + id),
								Component.translatable("message.delete"));
						setScreen(guiYesNo);*/
					}
				}
				break;
			} // remove
			case 4: tp(); break;
		}
	}

	@Override
	public void resetDimension() { initGui(); }

	// New from Unofficial (BetaZavr)
	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) { initGui(); }

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { tp(); }

	@Override
	public void setGuiData(NBTTagCompound compound) {
		data.clear();
		NBTTagList dimsData = compound.getTagList("Data", 10);
		List<Component> list = new ArrayList<>();
		List<Component> suffixes = new ArrayList<>();
		for (int i = 0; i < dimsData.tagCount(); i++) {
			NBTTagCompound nbt = dimsData.getCompoundTagAt(i);
			boolean isDel = nbt.getBoolean("deleted");
			TextFormatting color = isDel ? TextFormatting.DARK_GRAY : TextFormatting.GRAY;
			int id = nbt.getInteger("id");
			Component key = Component.empty()
					.append(Component.literal("ID:").withStyle(color))
					.append(Component.literal("" + id).withStyle(isDel ? TextFormatting.GRAY : TextFormatting.GOLD))
					.append(Component.literal(" - \"").withStyle(color))
					.append(Component.translatable(nbt.getString("name")).withStyle(isDel ? TextFormatting.GRAY : TextFormatting.RESET))
					.append(Component.literal("\"").withStyle(color));
			list.add(key);
			data.put(key, id);
			boolean isMC = id == 0 || id == 1 || id == -1;
			Component sfx = Component.empty()
					.append(Component.literal(isMC ? "MC" : "Mod").withStyle(isMC ? TextFormatting.GOLD : TextFormatting.AQUA))
					.append(Component.literal(".").withStyle(TextFormatting.GRAY))
					.append(Component.literal(isDel ? "delete" : nbt.getBoolean("loaded") ? "loaded" : "unloaded")
							.withStyle(isDel ? TextFormatting.GRAY : nbt.getBoolean("loaded") ? TextFormatting.GREEN : TextFormatting.RED));
			suffixes.add(sfx);
		}
		scroll.setUnsortedList(list)
				.setSuffixes(suffixes);
		initGui();
	}

	private void tp() {
		if (data.containsKey(scroll.getNormalSelected())) {
			Packets.sendServer(new SPacketDimensionTeleport(data.get(scroll.getNormalSelected())));
			onClose();
		}
	}

}
