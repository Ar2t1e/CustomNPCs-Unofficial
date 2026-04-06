package noppes.npcs.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNpcPather
		extends GuiNPCInterface
		implements IGuiData {

	protected GuiCustomScrollNop scroll;
	protected final DataAI ai;

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
		List<Component> list = new ArrayList<>();
		for (int[] arr : ai.getMovingPath()) { list.add(Component.literal("x:" + arr[0] + " y:" + arr[1] + " z:" + arr[2])); }
		if (scroll == null) { scroll = addScroll(0).setSize(160, 177); }
		add(scroll.setUnsortedList(list).setPos(guiLeft + 7, guiTop + 16).setSelect(sel));
		int y = guiTop + 40 + scroll.height;
		addButton(0, guiLeft + 7, y, "gui.down")
				.setSize(52, 20);
		addButton(1, guiLeft + 61, y, "gui.up")
				.setSize(52, 20);
		addButton(2, guiLeft + 115, y, "selectServer.delete")
				.setSize(52, 20);
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
					scroll.setSelect(scroll.getSelectedIndex() - 1);
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

}
