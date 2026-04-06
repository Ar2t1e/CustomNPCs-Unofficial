package noppes.npcs.client.gui.roles;

import java.util.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcTransportGet;
import noppes.npcs.packets.server.SPacketTransportCategoriesGet;
import noppes.npcs.packets.server.SPacketTransportSave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

import javax.annotation.Nonnull;

public class GuiNpcTransporter extends GuiNPCInterface2
		implements IGuiData, ICustomScrollListener, ITextfieldListener {

	protected final Map<Component, TransportCategory> dataCat = new HashMap<>();
	protected @Nonnull TransportLocation location = new TransportLocation();
	protected GuiCustomScrollNop scroll;

	public GuiNpcTransporter(EntityNPCInterface npc) {
		super(npc);

		backGui = EnumGuiType.MainMenuAdvanced;
		Packets.sendServer(new SPacketTransportCategoriesGet());
		Packets.sendServer(new SPacketNpcTransportGet());
	}

	@Override
	public void initGui() {
		super.initGui();
		if (scroll == null) { scroll = addScroll(0).setSize(143, 196); }
		int x = guiLeft + 6;
		int y = guiTop + 16;
		List<Component> list = new ArrayList<>();
		LinkedHashMap<Integer, List<Component>> hts = new LinkedHashMap<>();
		int i = 0;
		Component select = Component.empty();
		for (Component line : dataCat.keySet()) {
			list.add(line);
			if (dataCat.get(line).locations.containsKey(location.id)) { select = line; }
			List<Component> hover = new ArrayList<>();
			TransportCategory cat = dataCat.get(line);
			if (cat != null && !cat.locations.isEmpty()) {
				hover.add(Component.translatable("gui.location", ":").withStyle(TextFormatting.GRAY));
				Component p = Component.translatable("gui.position").append(": ").withStyle(TextFormatting.GRAY);
				int j = 0;
				for (int id : cat.locations.keySet()) {
					if (j >= 5) {
						hover.add(Component.literal("...").withStyle(TextFormatting.GRAY));
						break;
					}
					else {
						TransportLocation loc = cat.locations.get(id);
						hover.add(Component.empty()
								.append(Component.literal(" ID: ").withStyle(TextFormatting.GRAY))
								.append(Component.literal("" + id).withStyle(TextFormatting.YELLOW))
								.append(Component.literal(" \"").withStyle(TextFormatting.GRAY))
								.append(Component.translatable(loc.name).withStyle(TextFormatting.RESET))
								.append(Component.literal("\"; ").withStyle(TextFormatting.GRAY))
								.append(p)
								.append(Component.literal("X: ").withStyle(TextFormatting.GRAY))
								.append(Component.literal("" + loc.pos.getX()).withStyle(TextFormatting.GOLD))
								.append(Component.literal("; Y: ").withStyle(TextFormatting.GRAY))
								.append(Component.literal("" + loc.pos.getY()).withStyle(TextFormatting.GOLD))
								.append(Component.literal("; Z: ").withStyle(TextFormatting.GRAY))
								.append(Component.literal("" + loc.pos.getZ()).withStyle(TextFormatting.GOLD))
								.append(Component.literal("; Dimension ID: ").withStyle(TextFormatting.GRAY))
								.append(Component.literal("" + loc.dimension).withStyle(TextFormatting.BLUE)));
						j++;
					}
				}
			}
			hts.put(i++, hover);
		}
		add(scroll.setPos(x, y)
				.setUnsortedList(list)
				.setHoverTexts(hts)
				.setSelected(select));
		addLabel(0, x + 2, y - 11, Component.translatable("gui.categories").append(":"));
		x += 147;
		addLabel(1, x, y - 11, Component.translatable("gui.name").append(":"))
				.setSize(200, 20)
				.setIsVisible(scroll.hasSelected());
		int w = font.getStringWidth("ID:") + 5;
		addLabel(2, x + 200 - w, y - 11, "ID:" + location.id)
				.setSize(w + 2, 20)
				.setIsVisible(scroll.hasSelected());
		addTextField(0, x, y, 200, 20, location.name)
				.setIsVisible(scroll.hasSelected())
				.setHoverTexts("manager.hover.transport.loc.name");
		addButton(0, x, y + 24, false, location.type, "transporter.discovered", "transporter.start", "transporter.interaction")
				.setSize(200, 20)
				.setIsVisible(scroll.hasSelected())
				.setHoverTexts(Component.translatable("manager.hover.transport.type")
						.append(Component.translatable("manager.hover.transport.addinfo")));
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		if (button.id == 0) { location.type = button.getValue(); }
	}

	@Override
	public void save() {
		if (dataCat.containsKey(scroll.getNormalSelected())) {
			location.pos = player.getPosition();
			location.dimension = player.world.provider.getDimension();
			Packets.sendServer(new SPacketTransportSave(dataCat.get(scroll.getNormalSelected()).id, location.save()));
		}
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		if (compound.hasNoTags()) {
			dataCat.clear();
			for (TransportCategory category : TransportController.getInstance().getCategories()) {
				Component catKey = Component.empty()
						.append(Component.literal("ID: " + category.id + " \"").withStyle(TextFormatting.GRAY))
						.append(Component.translatable(category.title).withStyle(TextFormatting.RESET))
						.append(Component.literal("\"").withStyle(TextFormatting.GRAY));
				dataCat.put(catKey, category);
			}
		}
		else {
			location = new TransportLocation();
			location.load(compound);
		}
		initGui();
	}

	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) { initGui(); }

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		String name = textField.getValue();
		if (!name.isEmpty()) { location.name = name; }
	}

}
