package noppes.npcs.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.NBTTags;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataAI;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNpcPather
		extends GuiNPCInterface
		implements IGuiData, ICustomScrollListener, ITextfieldListener {

	protected final DataAI ai;
	public GuiCustomScrollNop scroll;

	public GuiNpcPather(EntityNPCInterface npc) {
		super();
		title = Component.literal("Npc Pather");
		setBackground("smallbg.png");
		drawDefaultBackground = false;
		imageWidth = 176;

		ai = npc.ais;
		Packets.sendServer(new SPacketMenuGet(EnumMenuType.MOVING_PATH));
	}

	@Override
	public void initGui() {
		int sel;
		if (scroll != null) { sel = scroll.getSelectedIndex(); }
		else {
			sel = 0;
			Vec3d vec3d = player.getPositionEyes(1.0f);
			Vec3d vec3d2 = player.getLook(1.0f);
			Vec3d vec3d3 = vec3d.addVector(vec3d2.x * 6.0d, vec3d2.y * 6.0d, vec3d2.z * 6.0d);
			RayTraceResult result = player.world.rayTraceBlocks(vec3d, vec3d3, false, false, true);
			if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
				int x = result.getBlockPos().getX();
				int y = result.getBlockPos().getY();
				int z = result.getBlockPos().getZ();
				int i = 0;
				for (int[] arr : ai.getMovingPath()) {
					if (arr[0] == x && y == arr[1] && z == arr[2]) {
						sel = i;
						break;
					}
					i++;
				}
			}
		}
		super.initGui();
		List<int[]> path = ai.getMovingPath();
		List<Component> list = new ArrayList<>();
		int i = 0;
		int[] pos = new int[] { 0, 0, 0 };
		for (int[] arr : path) {
			list.add(Component.empty()
					.append(Component.literal(i+": ").withStyle(TextFormatting.GRAY))
					.append(Component.literal("[" + arr[0] + ", " + arr[1] + ", " + arr[2] + "]").withStyle(TextFormatting.RESET)));
			if (scroll != null && scroll.getSelectedIndex() == i) { pos = arr; }
			i++;
		}
		if (scroll == null) { scroll = addScroll(0).setSize(166, 187).disabledSearch(); }
		int x0 = guiLeft + 5;
		add(scroll.setUnsortedList(list).setPos(x0, guiTop + 16).setSelected(sel));
		int y = guiTop + 19 + scroll.height;
		int wb = (scroll.width - 4) / 3;
		int x1 = x0 + 2 + wb;
		int x2 = x1 + 2 + wb;
		int lId = 0;
		addLabel(lId++, x0, y + 2, "X:")
				.setSize(12, 10);
		addTextField(0, x0 + 11, y, wb - 12, 14, pos[0]);
		addLabel(lId++, x1, y + 2, "Y:")
				.setSize(12, 10);
		addTextField(1, x1 + 11, y, wb - 12, 14, pos[1]);
		addLabel(lId, x2, y + 2, "Z:")
				.setSize(12, 10);
		addTextField(2, x2 + 11, y, wb - 12, 14, pos[2]);
		y += 17;
		addButton(0, x0, y, "gui.down")
				.setSize(wb, 16);
		addButton(1, x1, y, "gui.up")
				.setSize(wb, 16);
		addButton(2, x2, y, "selectServer.delete")
				.setSize(wb, 16);
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		if (scroll.hasSelected()) {
			switch (button.id) {
				case 0 : {
					List<int[]> list = ai.getMovingPath();
					int selected = scroll.getSelectedIndex();
					if (list.size() <= selected + 1) { return; }
					int[] a = list.get(selected);
					int[] b = list.get(selected + 1);
					list.set(selected, b);
					list.set(selected + 1, a);
					ai.setMovingPath(list);
					initGui();
					scroll.setSelectedIndex(selected + 1);
					break;
				} // down
				case 1 : {
					if (scroll.getSelectedIndex() - 1 < 0) { return; }
					List<int[]> list = ai.getMovingPath();
					int selected = scroll.getSelectedIndex();
					int[] a = list.get(selected);
					int[] b = list.get(selected - 1);
					list.set(selected, b);
					list.set(selected - 1, a);
					ai.setMovingPath(list);
					initGui();
					scroll.setSelectedIndex(selected - 1);
					break;
				} // up
				case 2 : {
					List<int[]> list = ai.getMovingPath();
					if (list.size() <= 1) { return; }
					list.remove(scroll.getSelectedIndex());
					scroll.setSelected(scroll.getSelectedIndex() - 1);
					ai.setMovingPath(list);
					initGui();
					break;
				} // remove
			}
		}
	}

	@Override
	public void save() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("MovingPathNew", NBTTags.nbtIntegerArraySet(ai.getMovingPath()));
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.MOVING_PATH, compound));
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		ai.load(compound);
		initGui();
	}

	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) { initGui(); }

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		int i = 0;
		for (int[] arr : ai.getMovingPath()) {
			if (scroll != null && scroll.getSelectedIndex() == i) {
				arr[textField.id] = textField.getInteger();
				break;
			}
			i++;
		}
	}

}
