package noppes.npcs.client.gui;

import java.util.*;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.DimensionManager;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.IDimensionGetter;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.controllers.data.DimensionData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.*;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

public class GuiNpcDimension extends GuiNPCInterface
		implements IDimensionGetter, ICustomScrollListener {

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
		int sw = 184;
		int x0 = guiLeft + 5;
		int x1 = x0 + sw + 2;
		int y = guiTop + 4;
		if (scroll == null) { scroll = addScroll(0).setSize(sw, 199); }
		if (!scroll.hasSelected()) {
			for (Component key : data.keySet()) {
				if (data.get(key) == (minecraft.player != null ?
						minecraft.player.world.provider.getDimension() : 0)) {
					scroll.setSelected(key);
					break;
				}
			}
		}
		DimensionController dData = DimensionController.getInstance();
		// title
		addLabel(0, x0, y, "gui.dimensions")
				.setSize(imageWidth - 10, 10)
				.setCenter(imageWidth - 10);
		// scroll
		add(scroll.setPos(x0, y += 10));
		int id = data.getOrDefault(scroll.getNormalSelected(), 0);
		// tp to
		addButton(4, x1, y, "TP")
				.setSize(60, 20)
				.setIsEnabled(scroll.hasSelected() && !dData.isDelete(id))
				.setHoverTexts("dimensions.hover.tp");
		// settings
		addButton(1, x1, y += 22, "gui.settings")
				.setSize(60, 20)
				.setIsEnabled(scroll.hasSelected() &&
						dData.getMCWorldInfo(id) != null &&
						!dData.isDelete(id))
				.setHoverTexts("dimensions.hover.settings");
		// add
		addButton(2, x1, y += 44, "gui.add")
				.setSize(60, 20)
				.setHoverTexts("dimensions.hover.add");
		// reset
		addButton(5, x1, y += 22, "gui.reset")
				.setSize(60, 20)
				.setIsEnabled(scroll.hasSelected() && DimensionManager.isDimensionRegistered(id) && !dData.isDelete(id))
				.setHoverTexts("dimensions.hover.recreate");
		// del
		addButton(3, x1, y + 22, dData.isDelete(id) ? "gui.restore" : "gui.remove")
				.setSize(60, 20)
				.setIsEnabled(scroll.hasSelected() && id > 100 && dData.hasData(id))
				.setHoverTexts("dimensions.hover.del");
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		DimensionController dData = DimensionController.getInstance();
		switch (button.id) {
			case 1: {
				if (data.containsKey(scroll.getNormalSelected())) {
					int id = data.get(scroll.getNormalSelected());
					if (dData.getMCWorldInfo(id) != null) {
						FriendlyByteBuf buffer = new FriendlyByteBuf();
						buffer.writeInt(id);
						CustomNpcs.proxy.openGui(null, EnumGuiType.DimensionSetting, buffer);
					}
				}
				break;
			} // settings
			case 2: {
				FriendlyByteBuf buffer = new FriendlyByteBuf();
				buffer.writeInt(0);
				CustomNpcs.proxy.openGui(null, EnumGuiType.DimensionSetting, buffer);
				break;
			} // add
			case 3: {
				if (data.containsKey(scroll.getNormalSelected())) {
					int id = data.get(scroll.getNormalSelected());
					if (dData.hasData(id) && !dData.isDelete(id)) {
						ConfirmScreen guiYesNo = new ConfirmScreen((agree) -> {
							if (agree) {
								Packets.sendServer(new SPacketDimensionDelete(id));
							}
							NoppesUtil.openGUI(player, this);
						},
								Component.literal("ID: " + id).getParent(),
								Component.translatable("message.delete").getParent());
						setScreen(guiYesNo);
					}
				}
				break;
			} // remove
			case 4: tp(); break;
			case 5: {
				if (data.containsKey(scroll.getNormalSelected())) {
					int id = data.get(scroll.getNormalSelected());
					if (dData.hasData(id) && !dData.isDelete(id)) {
						ConfirmScreen guiYesNo = new ConfirmScreen((agree) -> {
							if (agree) { Packets.sendServer(new SPacketDimensionRecreate(id)); }
							NoppesUtil.openGUI(player, this);
						},
								Component.literal("ID: " + id).getParent(),
								Component.translatable("message.recreate").getParent());
						setScreen(guiYesNo);
					}
				}
				break;
			} // reset
		}
	}

	// New from Unofficial (BetaZavr)
	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) { initGui(); }

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { tp(); }

	@Override
	public void resetDimension() {
		data.clear();
		List<Component> list = new ArrayList<>();
		List<Component> suffixes = new ArrayList<>();
		LinkedHashMap<Integer, List<Component>> htx = new LinkedHashMap<>();
		for (DimensionData dd : DimensionController.getInstance().getDatas()) {
			TextFormatting color = dd.isRemoved ? TextFormatting.DARK_GRAY : TextFormatting.GRAY;
			Component key = Component.empty()
					.append(Component.literal("ID:").withStyle(color))
					.append(Component.literal("" + dd.dimensionId).withStyle(dd.isRemoved ? TextFormatting.GRAY : TextFormatting.GOLD))
					.append(Component.literal(" \"").withStyle(color))
					.append(Component.translatable(dd.name).withStyle(dd.isRemoved ? TextFormatting.GRAY : TextFormatting.RESET))
					.append(Component.literal("\"").withStyle(color));
			list.add(key);
			data.put(key, dd.dimensionId);
			boolean isMC = dd.dimensionId == 0 || dd.dimensionId == 1 || dd.dimensionId == -1;
			Component sfx = Component.empty()
					.append(Component.literal(isMC ? "MC" : "Mod").withStyle(isMC ? TextFormatting.GOLD : TextFormatting.AQUA))
					.append(Component.literal(".").withStyle(TextFormatting.GRAY))
					.append(Component.literal(dd.isRemoved ? "D" : dd.isLoad ? "L" : "U")
							.withStyle(dd.isRemoved ? TextFormatting.DARK_RED : dd.isLoad ? TextFormatting.GREEN : TextFormatting.GRAY));
			suffixes.add(sfx);
			List<Component> hover = new ArrayList<>();
			hover.add(Component.empty()
					.append(Component.literal(isMC ? "Minecraft" : "Mod").withStyle(isMC ? TextFormatting.GOLD : TextFormatting.AQUA))
					.append(Component.literal(" dimension").withStyle(TextFormatting.GRAY)));
			hover.add(Component.empty()
					.append(Component.literal("ID: ").withStyle(TextFormatting.GRAY))
					.append(Component.literal("" + dd.dimensionId).withStyle(TextFormatting.GOLD)));
			hover.add(Component.empty()
					.append(Component.literal("Now is ").withStyle(TextFormatting.GRAY))
					.append(Component.literal(dd.isRemoved ? "Delete" : dd.isLoad ? "loaded" : "unloaded")
							.withStyle(dd.isRemoved ? TextFormatting.DARK_RED : dd.isLoad ? TextFormatting.GREEN : TextFormatting.RED)));
			if (!dd.worldName.isEmpty()) {
				hover.add(Component.empty()
						.append(Component.literal("Game name: \"").withStyle(TextFormatting.GRAY))
						.append(Component.literal(dd.worldName).withStyle(TextFormatting.RESET))
						.append(Component.literal("\"").withStyle(TextFormatting.GRAY)));
			}
			if (!dd.suffix.isEmpty()) {
				hover.add(Component.empty()
						.append(Component.literal("Suffix: \"").withStyle(TextFormatting.GRAY))
						.append(Component.literal(dd.suffix).withStyle(TextFormatting.RESET))
						.append(Component.literal("\"").withStyle(TextFormatting.GRAY)));
			}
			hover.add(Component.empty()
					.append(Component.literal("Spawn pos X:").withStyle(TextFormatting.GRAY))
					.append(Component.literal(""+dd.spawnPos.getX()).withStyle(TextFormatting.GOLD))
					.append(Component.literal(", Y:").withStyle(TextFormatting.GRAY))
					.append(Component.literal(""+dd.spawnPos.getY()).withStyle(TextFormatting.GOLD))
					.append(Component.literal(", Z:").withStyle(TextFormatting.GRAY))
					.append(Component.literal(""+dd.spawnPos.getZ()).withStyle(TextFormatting.GOLD)));
			hover.add(Component.empty()
							.append(Component.literal("Spawn angle: ").withStyle(TextFormatting.GRAY))
							.append(Component.literal(""+dd.spawnAngle).withStyle(TextFormatting.GOLD)));
			htx.put(htx.size(), hover);
		}
		if (scroll != null) {
			scroll.setUnsortedList(list)
					.setSuffixes(suffixes)
					.setHoverTexts(htx);
		}
		initGui();
	}

	private void tp() {
		if (data.containsKey(scroll.getNormalSelected())) {
			Packets.sendServer(new SPacketDimensionTeleport(data.get(scroll.getNormalSelected())));
			onClose();
		}
	}

}
