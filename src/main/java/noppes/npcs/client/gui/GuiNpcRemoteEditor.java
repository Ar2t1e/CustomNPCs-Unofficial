package noppes.npcs.client.gui;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.MerchantRecipeList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.*;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCheckBoxNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.util.CustomNPCsScheduler;

public class GuiNpcRemoteEditor
		extends GuiNPCInterface
		implements IGuiData, ICustomScrollListener {

	protected GuiCustomScrollNop scroll;

	// New from Unofficial (BetaZavr)
	protected static boolean all = false;
	protected final HashMap<Component, Integer> dataIDs = new HashMap<>();
	protected final DecimalFormat df = new DecimalFormat("#.#");
	public Entity selectEntity;

	public GuiNpcRemoteEditor() {
		super();
		setBackground("menubg.png");
		imageWidth = 256;

		Packets.sendServer(new SPacketRemoteNpcsGet(all));
	}

	@Override
	public void initGui() {
		super.initGui();
		if (scroll == null) { scroll = addScroll(0).setSize(165, 208); }
		add(scroll.setPos(guiLeft + 4, guiTop + 4));
		// title
		title = Component.translatable("remote.title");
		// edit
		int x = guiLeft + 170;
		int y = guiTop + 4;
		addButton(0, x, y, "selectServer.edit")
				.setSize(82, 18)
				.setIsEnabled(selectEntity != null && !(selectEntity instanceof EntityPlayer))
				.setHoverTexts("wand.hover.edit");
		// del
		addButton(1, x, y += 20, "selectServer.delete")
				.setSize(82, 18)
				.setIsEnabled(selectEntity != null)
				.setHoverTexts("wand.hover.del");
		// reset
		addButton(2, x, y += 20, "gui.reset")
				.setSize(82, 18)
				.setIsEnabled(selectEntity instanceof EntityNPCInterface)
				.setHoverTexts("wand.hover.reset");
		// tp
		addButton(4, x, y + 20, "remote.tp")
				.setSize(82, 18)
				.setIsEnabled(selectEntity != null)
				.setHoverTexts("wand.hover.tp");
		// reset all
		addButton(5, x, y = guiTop + 174, "remote.resetall")
				.setSize(82, 18)
				.setHoverTexts("wand.hover.resetall");
		// freeze
		addButton(3, x, y + 20, "remote.freeze")
				.setSize(82, 18)
				.setHoverTexts("wand.hover.freeze");
		// New from Unofficial (BetaZavr)
		// all entities
		addCheckBox(6, x, guiTop + 87, Component.empty(), null, GuiNpcRemoteEditor.all)
				.setSize(12, 12)
				.setHoverTexts("wand.hover.showall");
		// global
		addSideButton(7, guiLeft + imageWidth, guiTop + 8, "menu.global")
				.setIsRight(true)
				.setHoverTexts("display.hover.menu.global");
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 0: tryEditEntity(); break; // edit entity
			case 1: {
				if (!dataIDs.containsKey(scroll.getNormalSelected()) || minecraft == null || minecraft.world == null) { return; }
				ConfirmScreen guiYesNo = new ConfirmScreen((bo) -> {
					if (bo) { Packets.sendServer(new SPacketRemoteNpcDelete(dataIDs.get(scroll.getNormalSelected()), all)); }
					NoppesUtil.openGUI(player, this);
				},
						Component.empty().getParent(),
						Component.translatable("message.delete").getParent());
				setScreen(guiYesNo);
				break;
			} // remove entity
			case 2: {
				if (!dataIDs.containsKey(scroll.getNormalSelected()) || minecraft == null || minecraft.world == null) { return; }
				Packets.sendServer(new SPacketRemoteNpcReset(dataIDs.get(scroll.getNormalSelected())));
				Entity entity = player.world.getEntityByID(dataIDs.get(scroll.getNormalSelected()));
				if (entity instanceof EntityNPCInterface) { ((EntityNPCInterface)entity).reset(); }
				break;
			} // reset
			case 3: Packets.sendServer(new SPacketRemoteFreeze()); break; // freeze
			case 4: {
				if (!dataIDs.containsKey(scroll.getNormalSelected()) || minecraft == null || minecraft.world == null) { return; }
				Packets.sendServer(new SPacketRemoteNpcTp(dataIDs.get(scroll.getNormalSelected())));
				onClose();
				CustomNPCsScheduler.runTack(() -> Packets.sendServer(new SPacketRemoteNpcsGet(all)), 250);
				break;
			} // tp
			case 5: {
				for (int ids : dataIDs.values()) {
					Packets.sendServer(new SPacketRemoteNpcReset(ids));
					Entity entity = player.world.getEntityByID(ids);
					if (entity instanceof EntityNPCInterface) { ((EntityNPCInterface) entity).reset(); }
				}
				break;
			} // reset all
			case 6: {
				GuiNpcRemoteEditor.all = ((GuiCheckBoxNop) button).selected();
				Packets.sendServer(new SPacketRemoteNpcsGet(all));
				break;
			} // change all type
			case 7: {
				NoppesUtilServer.setEditingNpc(player, null);
				CustomNpcs.proxy.openGui(NoppesUtilServer.getEditingNpc(player), EnumGuiType.MainMenuGlobal, null);
				break;
			} // global tab
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (!hasSubGui()) {
			GlStateManager.pushMatrix();
			if (selectEntity != null) {
				int yaw = (int) (3 * player.world.getTotalWorldTime() % 360);
				int x = 221;
				int y = 162;
				if (selectEntity instanceof EntityItem) { y -= 18; }
				drawNpc(selectEntity, x, y, 1.0f, yaw, 0, 1);
			}
			GlStateManager.translate(0.0f, 0.0f, 1.0f);
			drawRect(guiLeft + 191, guiTop + 85, guiLeft + 252, guiTop + 171, new Color(0xFF808080).getRGB());
			drawRect(guiLeft + 192, guiTop + 86, guiLeft + 251, guiTop + 170, new Color(0xFF000000).getRGB());
			GlStateManager.popMatrix();
			if (GuiBasic.showHoverText && isMouseHover(mouseX, mouseY, guiLeft + 191, guiTop + 85, 61, 86)) {
				setHoverText("wand.hover.entity");
			}
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void tryEditEntity() {
		if (!dataIDs.containsKey(scroll.getNormalSelected()) || minecraft.world == null) { return; }
		Entity entity = minecraft.world.getEntityByID(dataIDs.get(scroll.getNormalSelected()));
		if (entity instanceof EntityNPCInterface) {
			Packets.sendServer(new SPacketRemoteMenuOpen(dataIDs.get(scroll.getNormalSelected())));
			return;
		}
		if (entity instanceof EntityVillager) {
			MerchantRecipeList merchantrecipelist = ((EntityVillager) entity).getRecipes(player);
			if (merchantrecipelist != null) {
				Packets.sendServer(new SPacketVillagerMenuOpen(dataIDs.get(scroll.getNormalSelected())));
				return;
			}
		}
		if (entity != null) {
			GuiNbtBook gui = new GuiNbtBook(entity.getPosition());
			NBTTagCompound data = new NBTTagCompound();
			entity.writeToNBTAtomically(data);
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("EntityId", entity.getEntityId());
			compound.setTag("Data", data);
			gui.setGuiData(compound);
			setScreen(gui);
		}
	}

	public void setSelected(String selected) { getButton(3).setDisplayText(selected); } // freeze

	// New from Unofficial (BetaZavr)
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
		scroll.setUnsortedList(list);
		scroll.setHoverTexts(hts);
		resetEntity();
	}

	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) { resetEntity(); }

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { tryEditEntity(); }

	private void resetEntity() {
		selectEntity = null;
		if (minecraft.world != null && dataIDs.containsKey(scroll.getNormalSelected())) {
			selectEntity = minecraft.world.getEntityByID(dataIDs.get(scroll.getNormalSelected()));
			if (selectEntity == null) {
				Packets.sendServer(new SPacketRemoteNpcsEntity(dataIDs.get(scroll.getNormalSelected())));
			}
		}
		if (selectEntity != null) { initGui(); }
	}

}
