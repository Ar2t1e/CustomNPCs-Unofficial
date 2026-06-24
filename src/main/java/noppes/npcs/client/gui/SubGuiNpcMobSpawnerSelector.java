package noppes.npcs.client.gui;

import java.awt.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.entity.data.role.IJobSpawner;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCloneList;
import noppes.npcs.packets.server.SPacketGetServerCloneEntity;
import noppes.npcs.roles.data.JobSpawnerCloneData;
import noppes.npcs.roles.data.JobSpawnerNbtData;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.client.controllers.ClientCloneController;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.Util;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;

public class SubGuiNpcMobSpawnerSelector extends GuiBasic
		implements IGuiData, ICustomScrollListener, ITextfieldListener {

	protected GuiCustomScrollNop scroll;
	public int activeTab = 1;

	// New from Unofficial (BetaZavr)
	private final EntityNPCInterface npc;
	public IJobSpawner.IJobSpawnerData spawnData;
	public boolean isDead;
	public int showingClones = 2;
	public EntityLiving select;

	public SubGuiNpcMobSpawnerSelector(IJobSpawner.IJobSpawnerData spawnDataIn) {
		super();
		setBackground("menubg.png");
		imageWidth = 256;

		npc = NoppesUtilServer.getEditingNpc(player);
		spawnData = spawnDataIn;
	}

	@Override
	public void initGui() {
		super.initGui();
		guiTop += 10;
		if (scroll == null) { scroll = addScroll(0).setSize(165, 188); }
		else { scroll.clear(); }
		add(scroll.setPos(guiLeft + 4, guiTop + 26));
		GuiMenuTopButton tab = addTopButton(3, guiLeft + 4, guiTop - 17, "spawner.clones")
				.setIsEnabled(showingClones == 0);
		tab = addTopButton(4, tab.getX() + tab.getWidth(), tab.getY(), "spawner.entities")
				.setIsEnabled(showingClones == 1);
		addTopButton(5, tab.getX() + tab.getWidth(), tab.getY(), "gui.server")
				.setIsEnabled(showingClones == 2);
		if (showingClones == 0 || showingClones == 2) {
			for (int id = 1; id < 10; id++) {
				addSideButton(21 + id, guiLeft, guiTop + 4 + (id - 1) * 21, Component.translatable("gui.tab").append(" " + id))
						.setIsEnabled(id == activeTab);
			}
			showClones();
		}
		else { showEntities(); }
		addButton(0, guiLeft + 171, guiTop + 170, "gui.done")
				.setSize(80, 20)
				.setHoverTexts("hover.exit");
		addButton(1, guiLeft + 171, guiTop + 192, "gui.cancel")
				.setSize(80, 20);
		if (spawnData == null) { return; }
		addLabel(5, guiLeft + 170, guiTop + 153, Component.translatable("type.count").append(":"));
		addTextField(2, guiLeft + 216, guiTop + 148, 35, 20, "" + spawnData.getCount())
				.setMinMaxDefault(0, 7, spawnData.getCount());
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		if (button.id > 20) {
			activeTab = button.id - 20;
			initGui();
			return;
		}
		switch (button.id) {
			case 0: onClose(); break;
			case 1: scroll.clear(); onClose(); break;
			case 3: {
				select = null;
				showingClones = 0;
				initGui();
				break;
			}
			case 4: {
				select = null;
				showingClones = 1;
				initGui();
				break;
			}
			case 5: {
				select = null;
				showingClones = 2;
				initGui();
				break;
			}
		}
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		if (compound.hasKey("NPCData", 10)) {
			Entity entity = EntityList.createEntityFromNBT(compound.getCompoundTag("NPCData"), player.world);
			select = entity instanceof EntityLiving ? (EntityLiving) entity : null;
			return;
		}
		NBTTagList nbtList = compound.getTagList("List", 8);
		List<String> list = new ArrayList<>();
		for (int i = 0; i < nbtList.tagCount(); ++i) { list.add(nbtList.getStringTagAt(i)); }
		scroll.setList(list);
		if (spawnData != null) {
			scroll.setSelected(Util.instance.deleteColor(spawnData.getTitle().getFormattedText()));
			resetEntity();
		}
	}

	public @Nonnull String getSelected() { return scroll.getSelected(); }

	private void showClones() {
		if (showingClones == 2) { Packets.sendServer(new SPacketCloneList(activeTab)); }
		else { scroll.setList(new ArrayList<>(ClientCloneController.Instance.getClones(activeTab))); }
	}

	// New from Unofficial (BetaZavr)
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (select != null) { drawNpc(select, 210, 80, 1.0f, (int) (3 * player.world.getTotalWorldTime() % 360), 0, 0); }
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0f, 0.0f, 1.0f);
		Gui.drawRect(guiLeft + 181, guiTop + 4, guiLeft + 242, guiTop + 90, new Color(0xFF808080).getRGB());
		Gui.drawRect(guiLeft + 182, guiTop + 5, guiLeft + 241, guiTop + 89, new Color(0xFF000000).getRGB());
		GlStateManager.popMatrix();
	}

	@Override
	public boolean keyPressed(char typedChar, int keyCode) {
		boolean bo = super.keyPressed(typedChar, keyCode);
		if (!hasSubGui()) {
			if (keyCode == Keyboard.KEY_UP ||
					keyCode == Keyboard.KEY_DOWN ||
					keyCode == mc.gameSettings.keyBindForward.getKeyCode() ||
					keyCode == mc.gameSettings.keyBindBack.getKeyCode()) {
				resetEntity();
			}
		}
		return bo;
	}

    @Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		if (!scroll.getSelected().isEmpty()) { resetEntity(); }
	}

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { onClose(); }

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		if (spawnData == null || textField.id != 2) { return; }
		spawnData.setCount(textField.getInteger());
	}

	public NBTTagCompound getCompound() {
		String sel = scroll.getSelected();
		if (sel.isEmpty()) { return null; }
		NBTTagCompound nbtEntity = null;
		if (showingClones == 0) {
			nbtEntity = ClientCloneController.Instance.getCloneData(player, sel, activeTab);
			if (nbtEntity != null) { nbtEntity.setBoolean("ClientClone", true); }
		}
		else if (showingClones == 1) {
			for (Map.Entry<EntityEntry, Class<? extends Entity>> entry : EntityUtil.getAllEntitiesClasses(player.world).entrySet()) {
				if (entry.getKey().getName().equals(sel)) {
					Entity entity = EntityList.createEntityByIDFromName(Objects.requireNonNull(entry.getKey().getRegistryName()), player.world);
					if (entity instanceof EntityLiving) { entity.writeToNBT(nbtEntity = new NBTTagCompound()); }
				}
			}
		}
		return nbtEntity;
	}

	private void resetEntity() {
		String sel = scroll.getSelected();
		ITextComponent content = scroll.getNormalSelected().getContents();
		if (content instanceof TextComponentTranslation) { sel = ((TextComponentTranslation) content).getKey(); }
		if (showingClones == 0) {
			NBTTagCompound npcNbt = ClientCloneController.Instance.getCloneData(player, sel, activeTab);
			if (npcNbt == null) { return; }
			Entity entity = EntityList.createEntityFromNBT(npcNbt, player.world);
			select = null;
			if (entity instanceof EntityLiving) {
				npcNbt.setBoolean("ClientClone", true);
				if (spawnData instanceof JobSpawnerNbtData) { ((JobSpawnerNbtData) spawnData).load(npcNbt); }
				else {
					spawnData = new JobSpawnerNbtData(npc);
					((JobSpawnerNbtData) spawnData).load(npcNbt);
				}
				select = (EntityLiving) entity;
			}
		} // client
		else if (showingClones == 1) {
			for (Map.Entry<EntityEntry, Class<? extends Entity>> entry : EntityUtil.getAllEntitiesClasses(player.world).entrySet()) {
				if (entry.getKey().getName().equals(sel)) {
					Entity entity = EntityList.createEntityByIDFromName(Objects.requireNonNull(entry.getKey().getRegistryName()), player.world);
					select = null;
					if (entity instanceof EntityLiving) {
						select = (EntityLiving) entity;
						NBTTagCompound npcNbt = new NBTTagCompound();
						entity.writeToNBTOptional(npcNbt);
						npcNbt.removeTag("ClientClone");
						if (spawnData instanceof JobSpawnerNbtData) { ((JobSpawnerNbtData) spawnData).load(npcNbt); }
						else {
							spawnData = new JobSpawnerNbtData(npc);
							((JobSpawnerNbtData) spawnData).load(npcNbt);
						}
					}
					return;
				}
			}
		} // mob
		else { // server
			if (!(spawnData instanceof JobSpawnerCloneData)) { spawnData = new JobSpawnerCloneData(npc); }
			((JobSpawnerCloneData) spawnData).setName(sel);
			((JobSpawnerCloneData) spawnData).setTab(activeTab);
			Packets.sendServer(new SPacketGetServerCloneEntity(false, isDead, activeTab, sel));
		}
	}

	private void showEntities() {
		ArrayList<String> list = new ArrayList<>();
		List<Class<? extends Entity>> classes = new ArrayList<>();
		for (EntityEntry ent : ForgeRegistries.ENTITIES.getValuesCollection()) {
			if (Objects.requireNonNull(ent.getRegistryName()).getResourceDomain().equals(CustomNpcs.MODID)) { continue; }
			Class<? extends Entity> c = ent.getEntityClass();
			String name = ent.getName();
			try {
				if (classes.contains(c) || !EntityLiving.class.isAssignableFrom(c) || Modifier.isAbstract(c.getModifiers())) { continue; }
				Entity entity = EntityList.createEntityByIDFromName(ent.getRegistryName(), player.world);
				if (!(entity instanceof EntityMob)) { continue; }
				list.add(name);
				classes.add(c);
			} catch (Exception e) { LogWriter.error(e); }
		}
		scroll.setList(list);
	}

}
