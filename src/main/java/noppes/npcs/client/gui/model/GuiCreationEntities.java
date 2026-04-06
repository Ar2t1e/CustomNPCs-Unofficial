package noppes.npcs.client.gui.model;

import java.lang.reflect.Modifier;
import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.NPCRendererHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Slot;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.common.util.ComponentOrderComparator;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.containers.ContainerLayer;
import noppes.npcs.entity.EntityNPC64x32;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityNpcAlex;
import noppes.npcs.entity.EntityNpcClassicPlayer;

import javax.annotation.Nonnull;

public class GuiCreationEntities extends GuiCreationScreenInterface<ContainerLayer>
		implements ICustomScrollListener {

	public final HashMap<Component, Class<? extends EntityLivingBase>> data = new HashMap<>();
	protected final List<Component> list;
	protected GuiCustomScrollNop scroll;
	protected boolean resetToSelected = true;

	public GuiCreationEntities(EntityNPCInterface npc, ContainerLayer container) {
		super(npc, container);
		for (EntityEntry ent : ForgeRegistries.ENTITIES.getValuesCollection()) {
			Component name = Component.literal(ent.getName());
			Class<? extends Entity> c = ent.getEntityClass();
			try {
				if (!EntityLiving.class.isAssignableFrom(c) || Modifier.isAbstract(c.getModifiers()) || !(Minecraft.getMinecraft().getRenderManager()
                        .getEntityClassRenderObject(c) instanceof RenderLivingBase)) {
					continue;
				}
                if (name.getString().toLowerCase().contains("customnpc")) { continue; }
				data.put(name, c.asSubclass(EntityLivingBase.class));
			} catch (Exception e) { LogWriter.error(e); }
		}
		data.put(Component.literal("NPC 64x32"), EntityNPC64x32.class);
		data.put(Component.literal("NPC Alex Arms"), EntityNpcAlex.class);
		data.put(Component.literal("NPC Classic Player"), EntityNpcClassicPlayer.class);
		(list = new ArrayList<>(data.keySet())).add(Component.literal("NPC"));
		list.sort(new ComponentOrderComparator());
		active = 1;
		xOffset = 60;
	}

	@Override
	public void buttonEvent(@Nonnull GuiButtonNop button) {
		if (button.id == 10) {
			playerdata.setEntityClass(null);
			resetToSelected = true;
			npc.display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/humanmale/steve.png");
			npc.reset();
			npc.display.width = npc.baseWidth;
			npc.display.height = npc.baseHeight;
			initGui();
		}
		super.buttonEvent(button);
	}

	@Override
	public void initGui() {
		super.initGui();
		if (scroll == null) { scroll = addScroll(0).setUnsortedList(list); }
		scroll.setSize(121, ySize - 74);
		Component selected =  Component.literal("NPC");
		if (entity != null) {
			for (Map.Entry<Component, Class<? extends EntityLivingBase>> en : data.entrySet()) {
				if (en.getValue().toString().equals(entity.getClass().toString())) { selected = en.getKey(); }
			}
		}
		addButton(10, guiLeft, guiTop + 23, "Reset To NPC")
				.setSize(120, 20)
				.setIsVisible(!selected.getString().equals("NPC"));
		scroll.setSelected(selected);
		if (resetToSelected) {
			scroll.scrollTo(scroll.getSelected());
			resetToSelected = false;
		}
		add(scroll.setPos(guiLeft, guiTop + 46));
		for (Slot slot : inventorySlots.inventorySlots) {
			slot.xPos = -5000;
			slot.yPos = -5000;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		playerdata.setEntityClass(data.get(scroll.getNormalSelected()));
		EntityLivingBase entity = playerdata.getEntity(npc);
		if (entity != null) {
			@SuppressWarnings("rawtypes")
			RenderLivingBase render = (RenderLivingBase) mc.getRenderManager().getEntityClassRenderObject(entity.getClass());
			if (!NPCRendererHelper.getTexture(render, entity).equals(TextureMap.LOCATION_MISSING_TEXTURE.toString())) {
				npc.display.setSkinTexture(NPCRendererHelper.getTexture(render, entity));
			}
		}
		else { npc.display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/humanmale/steve.png"); }
		npc.reset();
		npc.display.width = npc.baseWidth;
		npc.display.height = npc.baseHeight;
		initGui();
	}

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }
	
}
