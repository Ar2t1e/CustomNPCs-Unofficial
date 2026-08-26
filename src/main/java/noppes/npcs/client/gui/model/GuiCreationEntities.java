package noppes.npcs.client.gui.model;

import java.util.*;

import net.minecraft.client.renderer.entity.NPCRendererHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.old.*;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiCreationEntities extends GuiCreationScreenInterface
		implements ICustomScrollListener {

	protected final List<EntityEntry> types;
	protected GuiCustomScrollNop scroll;
	protected boolean resetToSelected = true;

	public GuiCreationEntities(EntityNPCInterface npc) {
		super(npc);
		types = getAllEntities();
		types.sort(Comparator.comparing((t) -> {
			if (t.getRegistryName() != null) { return t.getRegistryName().toString(); }
			return t.getName().toLowerCase();
		}));
		active = 1;
		xOffset = 60;
	}

	@Override
	public void initGui() {
		super.initGui();
		add(new GuiButtonNop(this, 10, "Reset To NPC", guiLeft, guiTop + 46,
				button -> {
					playerdata.setEntity(null);
					npc.display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/humanmale/steve.png");
					resetToSelected = true;
					npc.reset();
					npc.display.width = npc.baseWidth;
					npc.display.height = npc.baseHeight;
					initGui();
				}).setSize(120, 20));
		if (scroll == null) {
			List<Component> list = new ArrayList<>();
			LinkedHashMap<Integer, List<Component>> hts = new LinkedHashMap<>();
			for (EntityEntry entry : types) {
				ResourceLocation loc = entry.getRegistryName();
				if (loc == null) { continue; }
				Component name;
				List<Component> hover = new ArrayList<>();
				if (loc.getResourceDomain().equals(CustomNpcs.MODID)) {
					name = Component.translatable("entity.customnpcs." + entry.getName());
					hover.add(Component.translatable("entity.hover.customnpcs." + entry.getName()));
				}
				else if (loc.getResourceDomain().equals("minecraft")) {
					name = Component.translatable("entity." + entry.getName() + ".name");
					hover.add(Component.translatable("entity.hover.minecraft"));
				}
				else {
					name = Component.translatable("entity." + entry.getName() + ".name");
					hover.add(Component.translatable("entity.hover.in.mod"));
					hover.add(Component.literal(loc.getResourceDomain()));
				}
				list.add(name);
				hts.put(hts.size(), hover);
			}
			scroll = addScroll(0)
					.setUnsortedList(list)
					.setHoverTexts(hts);
		}

		int index = -1;
		for(int i = 0; i < types.size(); ++i) {
			EntityEntry entry = types.get(i);
			if ((entity == null && entry.getEntityClass() == EntityCustomNpc.class) || (entity != null && entry.getEntityClass() == entity.getClass())) {
				index = i;
				break;
			}
		}
		if (index >= 0) { scroll.setSelected(index); }
		else { scroll.setSelected("entity." + CustomNpcs.MODID + ".customnpc"); }

		if (resetToSelected) {
			scroll.scrollTo(scroll.getSelected());
			resetToSelected = false;
		}
		add(scroll.setPos(guiLeft, guiTop + 68)
				.setSize(120, imageHeight - 96));
		addLabel(110, guiLeft + 124, guiTop + 5, "gui.simpleRenderer")
				.setColor(CustomNpcs.MainColor.getRGB());
		add(new GuiButtonYesNo(this, 110, guiLeft + 260, guiTop, playerdata.simpleRender,
				(b) -> playerdata.simpleRender = ((GuiButtonYesNo)b).getBoolean()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		if (!scroll.hasSelected()) { playerdata.setEntity(null); }
		else { playerdata.setEntity((Class<? extends EntityLivingBase>) types.get(scroll.getSelectedIndex()).getEntityClass()); }

		EntityLivingBase entity = playerdata.getEntity(npc);
		if (entity != null) {
			Render<Entity> mcRender = mc.getRenderManager().getEntityClassRenderObject(entity.getClass());
			if (mcRender instanceof RenderLivingBase<?>) {
				@SuppressWarnings("rawtypes")
				RenderLivingBase<EntityLivingBase> render = (RenderLivingBase) mcRender;
				if (!NPCRendererHelper.getTexture(render, entity).equals(TextureMap.LOCATION_MISSING_TEXTURE.toString())) {
					npc.display.setSkinTexture(NPCRendererHelper.getTexture(render, entity));
				}
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

	private static List<EntityEntry> getAllEntities() {
		List<EntityEntry> data = new ArrayList<>();
        for (EntityEntry ent : ForgeRegistries.ENTITIES.getValuesCollection()) {
			try {
				Class<? extends Entity> cl = ent.getEntityClass();
				if (EntityLivingBase.class.isAssignableFrom(cl) &&
						!EntityDragon.class.isAssignableFrom(cl)) {
					// old entities
					if (EntityNPCHumanMale.class.isAssignableFrom(cl) ||
							EntityNPCVillager.class.isAssignableFrom(cl) ||
							EntityNPCHumanFemale.class.isAssignableFrom(cl) ||
							EntityNPCDwarfMale.class.isAssignableFrom(cl) ||
							EntityNPCFurryMale.class.isAssignableFrom(cl) ||
							EntityNpcMonsterMale.class.isAssignableFrom(cl) ||
							EntityNpcMonsterFemale.class.isAssignableFrom(cl) ||
							EntityNpcSkeleton.class.isAssignableFrom(cl) ||
							EntityNPCDwarfFemale.class.isAssignableFrom(cl) ||
							EntityNPCFurryFemale.class.isAssignableFrom(cl) ||
							EntityNPCOrcMale.class.isAssignableFrom(cl) ||
							EntityNPCOrcFemale.class.isAssignableFrom(cl) ||
							EntityNPCElfMale.class.isAssignableFrom(cl) ||
							EntityNPCElfFemale.class.isAssignableFrom(cl) ||
							EntityNpcEnderchibi.class.isAssignableFrom(cl) ||
							EntityNpcNagaMale.class.isAssignableFrom(cl) ||
							EntityNpcNagaFemale.class.isAssignableFrom(cl) ||
							EntityNPCEnderman.class.isAssignableFrom(cl)
					) { continue; }
					data.add(ent);
				}
			} catch (Exception ignored) {}
		}
		return data;
    }
	
}
