package noppes.npcs.client.gui.select;

import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
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
		Packets.sendServer(new SPacketRemoteNpcsGet(false));
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
		List<Component> list = new ArrayList<>();
		dataIDs.clear();
		Component mainKey = Component.empty()
				.append(Component.literal("ID:-1 ").withStyle(TextFormatting.GREEN))
				.append(Component.literal(main.getName() + " ").withStyle(TextFormatting.RESET))
				.append(Component.literal(df.format(-1.0f)).withStyle(TextFormatting.GRAY));
		dataIDs.put(mainKey, -1);
		LinkedHashMap<Integer, List<Component>> hts = new LinkedHashMap<>();
		for (int i = 0; i < nbtList.tagCount(); ++i) {
			NBTTagCompound nbt = nbtList.getCompoundTagAt(i);
			int id = nbt.getInteger("K");
			TextFormatting type;
			switch (nbt.getInteger("C")) {
				case 1: type = TextFormatting.GREEN; break;
				case 2: type = TextFormatting.RED; break;
				case 3: type = TextFormatting.YELLOW; break;
				case 4: type = TextFormatting.AQUA; break;
				default: type = TextFormatting.GRAY; break;
			}
			String distance = df.format(nbt.getFloat("V"));
			Component key = Component.empty()
					.append(Component.literal("ID:" + id).withStyle(type))
					.append(Component.literal(" " + nbt.getString("N")).withStyle(TextFormatting.RESET))
					.append(Component.literal(" (" + distance + ")").withStyle(TextFormatting.GRAY));
			list.add(key);
			dataIDs.put(key, nbt.getInteger("K"));

			List<Component> hoverList = new ArrayList<>();
			hoverList.add(Component.literal("Name: ").withStyle(TextFormatting.GRAY)
					.append(Component.literal(nbt.getString("N")).withStyle(TextFormatting.WHITE)));
			hoverList.add(Component.literal("Entity ID: ").withStyle(TextFormatting.GRAY)
					.append(Component.literal("" + id).withStyle(type)));
			hoverList.add(Component.literal("Distance to: ").withStyle(TextFormatting.GRAY)
					.append(Component.literal(distance).withStyle(TextFormatting.GOLD))
					.append(Component.literal(" blocks").withStyle(TextFormatting.GRAY)));
			hoverList.add(Component.literal("Class Type: ").withStyle(TextFormatting.GRAY)
					.append(Component.literal(nbt.getString("S")).withStyle(TextFormatting.WHITE)));
			hts.put(i, hoverList);
		}
		scroll.setUnsortedList(list);
		scroll.setHoverTexts(hts);
		resetEntity();
	}

	private void resetEntity() {
		selectEntity = null;
		if (dataIDs.containsKey(scroll.getNormalSelected())) {
			Entity entity = mc.world.getEntityByID(dataIDs.get(scroll.getNormalSelected()));
			if (!(entity instanceof EntityNPCInterface)) { return; }
			selectEntity = (EntityNPCInterface) entity;
		}
	}

}
