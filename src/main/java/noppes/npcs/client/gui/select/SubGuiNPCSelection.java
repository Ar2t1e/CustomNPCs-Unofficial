package noppes.npcs.client.gui.select;

import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketRemoteNpcsEntity;
import noppes.npcs.packets.server.SPacketRemoteNpcsGet;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class SubGuiNPCSelection
		extends GuiNPCInterface
		implements IGuiData, ICustomScrollListener {

	protected final HashMap<Component, Integer> dataIDs = new HashMap<>();
	protected final DecimalFormat df = new DecimalFormat("#.#");
	protected GuiCustomScrollNop scroll;
	public EntityNPCInterface selectEntity;
	public EntityNPCInterface main;

	public SubGuiNPCSelection(EntityNPCInterface completer) {
		super();
		imageWidth = 256;
		setBackground("menubg.png");

		selectEntity = completer;
		main = completer;

		Packets.sendServer(new SPacketRemoteNpcsGet(false));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (!hasSubGui()) {
			int u = guiLeft + 182;
			int v = guiTop + 40;
			GlStateManager.pushMatrix();
			if (selectEntity != null) {
				drawNpc(selectEntity, u - guiLeft + 30, v - guiTop + 70, 1.0f, (int) (3 * player.world.getTotalWorldTime() % 360), 0, 1);
			}
			GlStateManager.translate(0.0f, 0.0f, 1.0f);
			drawRect(u - 1, v - 1, u + 60, v + 85, 0xFF808080);
			drawRect(u, v, u + 59, v + 84, 0xFF000000);
			GlStateManager.popMatrix();
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void initGui() {
		super.initGui();
		if (scroll == null) { scroll = addScroll(0).setSize(165, 209); }
		add(scroll.setPos(guiLeft + 4, guiTop + 4));
	}

	@Override
	public boolean keyPressed(char typedChar, int keyCode) {
		if (isEscKey(keyCode) || isInventoryKey(keyCode)) { onClose(); }
		boolean bo = super.keyPressed(typedChar, keyCode);
		if (isUpKey(keyCode) || isDownKey(keyCode)) { resetEntity(); }
		return bo;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		boolean bo = super.mouseClicked(mouseX, mouseY, mouseButton);
		scroll.mouseClicked(mouseX, mouseY, mouseButton);
		return bo;
	}

	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		resetEntity();
		initGui();
	}

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { onClose(); }

	@Override
	public void setGuiData(NBTTagCompound compound) {
		NBTTagList nbtList = compound.getTagList("Data", 10);
		dataIDs.clear();
		if (minecraft.world == null) { return; }
		List<Component> list = new ArrayList<>();
		LinkedHashMap<Integer, List<Component>> hts = new LinkedHashMap<>();
		for (int i = 0; i < nbtList.tagCount(); ++i) {
			NBTTagCompound nbt = nbtList.getCompoundTagAt(i);
			int id = nbt.getInteger("Id");
			ITextComponent name = Component.jsonToComponent(nbt.getString("Name")).getParent();
			TextFormatting type;
			switch (nbt.getInteger("Type")) {
				case 1: type = TextFormatting.GREEN; break;
				case 2: type = TextFormatting.RED; break;
				case 3: type = TextFormatting.YELLOW; break;
				case 4: type = TextFormatting.AQUA; break;
				default: type = TextFormatting.GRAY; break;
			}
			Component distance = Component.literal(df.format(nbt.getFloat("Distance"))).withStyle(TextFormatting.GOLD);
			ITextComponent tempName = name.createCopy();
			tempName.getStyle().setColor(TextFormatting.RESET);
			Component key = Component.empty()
					.append(Component.literal("ID:" + id).withStyle(type))
					.append(" ")
					.append(tempName)
					.append(Component.literal(" (").withStyle(TextFormatting.GRAY))
					.append(distance)
					.append(Component.literal(")").withStyle(TextFormatting.GRAY));
			list.add(key);
			dataIDs.put(key, id);
			List<Component> hoverList = new ArrayList<>();
			tempName = name.createCopy();
			tempName.getStyle().setColor(TextFormatting.WHITE);
			hoverList.add(Component.literal("Name: ").withStyle(TextFormatting.GRAY)
					.append(tempName));
			hoverList.add(Component.literal("Entity ID: ").withStyle(TextFormatting.GRAY)
					.append(Component.literal("" + id).withStyle(type)));
			hoverList.add(Component.literal("Distance to: ").withStyle(TextFormatting.GRAY)
					.append(distance)
					.append(Component.literal(" blocks").withStyle(TextFormatting.GRAY)));
			hoverList.add(Component.literal("Class Type: ").withStyle(TextFormatting.GRAY)
					.append(Component.literal(nbt.getString("Class")).withStyle(TextFormatting.WHITE)));
			hts.put(i, hoverList);
		}
		scroll.setUnsortedList(list)
				.setHoverTexts(hts);
		resetEntity();
	}

	private void resetEntity() {
		selectEntity = null;
		if (minecraft.world != null && dataIDs.containsKey(scroll.getNormalSelected())) {
			selectEntity = (EntityNPCInterface) minecraft.world.getEntityByID(dataIDs.get(scroll.getNormalSelected()));
			if (selectEntity == null) {
				Packets.sendServer(new SPacketRemoteNpcsEntity(dataIDs.get(scroll.getNormalSelected())));
			}
		}
		if (selectEntity != null) { initGui(); }
	}

}
