package noppes.npcs.client.gui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketGiveStack;
import noppes.npcs.packets.server.SPacketNbtBookBlockSave;
import noppes.npcs.packets.server.SPacketNbtBookEntitySave;
import noppes.npcs.packets.server.SPacketNbtBookStackSave;
import noppes.npcs.shared.client.gui.GuiTextAreaScreen;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.util.NBTJsonUtil;

public class GuiNbtBook extends GuiNPCInterface implements IGuiData {


	protected final BlockPos pos;
	protected TileEntity tile;
	protected IBlockState state;
	protected ItemStack blockStack;
	protected String faultyText = null;
	protected String errorMessage = null;

	// New from Unofficial (BetaZavr)
	protected ItemStack stack;
	protected GuiCustomScrollNop scroll;
	public NBTTagCompound originalCompound;
	public NBTTagCompound compound;
	public Entity entity;
	public int entityId;

	public GuiNbtBook(BlockPos posIn) {
		super();
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 217;

		pos = posIn;
	}

	@Override
	public void initGui() {
		super.initGui();
		boolean onlyClient = stack == null && state == null && entity == null;
		int h = 120;
		if (scroll == null) { scroll = addScroll(0).setSize(188, h); }
		add(scroll.setPos(guiLeft + 60, guiTop + 45));
		if (stack != null) {
			h = 118;
			scroll.setSize(188, h - 20);
			addLabel(11, guiLeft + 60, guiTop + 6, "id: \"" + stack.getItem().getRegistryName() + "\"");
			addButton(1, guiLeft + 38, guiTop + 144, "gui.copy").setSize(180, 20);
			setObjectToScroll(stack);
		}
		else if (state != null) {
			addLabel(11, guiLeft + 60, guiTop + 6, "x: " + pos.getX() + ", y: " + pos.getY() + ", z: " + pos.getZ());
			addLabel(12, guiLeft + 60, guiTop + 16, "id: " + Block.REGISTRY.getNameForObject(state.getBlock()));
			addLabel(13, guiLeft + 60, guiTop + 26, "meta: " + state.getBlock().getMetaFromState(state));
			setObjectToScroll(state);
		}
		else if (entity != null) {
			h = 140;
			scroll.setSize(188, h - 20);
			String name = "Not registered name!";
			if (EntityRegistry.getEntry(entity.getClass()) == null) { onlyClient = true; }
			else {
				EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
				if (entry != null) {
					ResourceLocation reg = entry.getRegistryName();
					if (reg != null) { name = "id: " + reg; }
				}
			}
			addLabel(12, guiLeft + 60, guiTop + 6, name);
			setObjectToScroll(entity);
		}
		addLabel(2, guiLeft + 4, guiTop + 172, "nbt.edit");
		addButton(0, guiLeft + 128, guiTop + 166, "nbt.edit")
				.setSize(59, 20)
				.setIsEnabled(compound != null && !compound.hasNoTags());
		addButton(2, guiLeft + 189, guiTop + 166, "gui.fast")
				.setSize(59, 20)
				.setIsEnabled(compound != null && !compound.hasNoTags());
		addLabel(0, guiLeft + 4, guiTop + 167, "")
				.setSize(58, 12)
				.setCentered(false)
				.setColor(0xFFA00000);
		addLabel(1, guiLeft + 4, guiTop + 177, "")
				.setSize(imageWidth - 8, 12)
				.setCentered(false)
				.setColor(0xFFA00000);
		addButton(66, guiLeft + 128, guiTop + 190, "gui.close")
				.setSize(120, 20);
		GuiButtonNop button = addButton(67, guiLeft + 4, guiTop + 190, "gui.save")
				.setSize(120, 20)
				.setIsEnabled(!onlyClient);
		if (!onlyClient) {
			if (errorMessage != null) {
				button.setIsEnabled(false);
				int i = errorMessage.indexOf(" at: ");
				if (i > 0) {
					getLabel(0).setSize(58, 12)
							.setMessage(errorMessage.substring(0, i));
					getLabel(1).setSize(imageWidth - 8, 12)
							.setMessage(errorMessage.substring(i));
				}
				else {
					getLabel(0).setSize(imageWidth - 8, 12)
						.setMessage(errorMessage);
				}
			}
			else if (originalCompound != null) { button.setIsEnabled(!originalCompound.equals(compound)); }
		}
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 0: {
				if (compound != null) {
					String text = NBTJsonUtil.Convert(compound);
					if (text.length() > 30000) { text = compound.toString(); }
					if (text.length() <= 100000) {
						if (faultyText != null) { setSubGui((new GuiTextAreaScreen(0, text, faultyText)).enableHighlighting()); }
						else { setSubGui((new GuiTextAreaScreen(0, text)).enableHighlighting()); }
					} else {
						errorMessage = "NBT data is too long! Length: " + text.length();
						NoppesStringUtils.setClipboardContents(text);
						initGui();
					}
				}
				break;
			} // edit
			case 1: {
				if (stack != null && !stack.isEmpty()) { Packets.sendServer(new SPacketGiveStack(stack.writeToNBT(new NBTTagCompound()))); }
				break;
			} // copy
			case 2: {
				if (compound != null) {
					String text = compound.toString();
					if (text.length() <= 100000) {
						if (faultyText != null) { setSubGui((new GuiTextAreaScreen(0, text, faultyText)).enableHighlighting()); }
						else { setSubGui((new GuiTextAreaScreen(0, text)).enableHighlighting()); }
					} else {
						errorMessage = "NBT data is too long! Length: " + text.length();
						NoppesStringUtils.setClipboardContents(text);
						initGui();
					}
				}
				break;
			} // edit fast
			case 66: onClose(); break;
			case 67: {
				if (!compound.equals(originalCompound)) {
					if (stack != null) { Packets.sendServer(new SPacketNbtBookStackSave(compound)); }
					else if (tile == null) { Packets.sendServer(new SPacketNbtBookEntitySave(entityId, compound)); }
					else { Packets.sendServer(new SPacketNbtBookBlockSave(pos, compound)); }
					originalCompound = compound.copy();
					button.active = false;
					errorMessage = "Saved";
					initGui();
				}
				break;
			} // save
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (hasSubGui()) { return; }
		if (stack != null || state != null) {
			// background
			GlStateManager.pushMatrix();
			Gui.drawRect(guiLeft + 3, guiTop + 3, guiLeft + 55, guiTop + 55, 0xFF808080);
			Gui.drawRect(guiLeft + 4, guiTop + 4, guiLeft + 54, guiTop + 54, 0xFF000000);
			GlStateManager.popMatrix();
			// object
			GlStateManager.pushMatrix();
			GlStateManager.translate(guiLeft + 5.0f, guiTop + 5.0f, 0.0f);
			GlStateManager.scale(3.0f, 3.0f, 3.0f);
			ItemStack item = stack != null ? stack : blockStack;
			itemRender.renderItemAndEffectIntoGUI(item, 0, 0);
			itemRender.renderItemOverlays(fontRenderer, item, 0, 0);
			GlStateManager.popMatrix();
		}
		if (entity != null) {
			GlStateManager.pushMatrix();
			int x = 30;
			int y = 80;
			float s = 1.0F;
			if (entity instanceof EntityItemFrame) {
				x = 10;
				y = 54;
				s = 1.4f;
			}
			drawNpc(entity, x, y, s, 0, 0, 1);
			GlStateManager.translate(0.0f, 0.0f, 1.0f);
			int color = 0xFF808080;
			if (EntityRegistry.getEntry(entity.getClass()) == null) { color = 0xFFFF4040; }
			Gui.drawRect(guiLeft + 5, guiTop + 11, guiLeft + 55, guiTop + 97, color);
			Gui.drawRect(guiLeft + 6, guiTop + 12, guiLeft + 54, guiTop + 96, 0xFF000000);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void subGuiClosed(GuiScreen gui) {
		if (gui instanceof GuiTextAreaScreen) {
			try {
				compound = JsonToNBT.getTagFromJson(((GuiTextAreaScreen) gui).text);
				errorMessage = faultyText = null;
			}
			catch (NBTException e) {
				errorMessage = e.getLocalizedMessage();
				faultyText = ((GuiTextAreaScreen) gui).text;
			}
			initGui();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setGuiData(NBTTagCompound nbt) {
		if (nbt.hasKey("Item") && nbt.getBoolean("Item")) { stack = new ItemStack(nbt.getCompoundTag("Data")); }
		else if (nbt.hasKey("EntityId")) {
			entityId = nbt.getInteger("EntityId");
			entity = player.world.getEntityByID(entityId);
		} else {
			tile = player.world.getTileEntity(pos);
			state = player.world.getBlockState(pos);
			blockStack = state.getBlock().getItem(player.world, pos, state);
		}
		originalCompound = nbt.getCompoundTag("Data");
		compound = originalCompound.copy();
		initGui();
	}

	private void setObjectToScroll(Object obj) {
		addLabel(15, guiLeft + 60, guiTop + (state != null ? 36 : 16), "(?) Class \"" + obj.getClass().getSimpleName() + "\":")
				.setHoverTexts(obj.getClass().getName());
		// get data
		Map<String, Field> fs = new TreeMap<>();
		Map<String, Method> ms = new TreeMap<>();
		Map<String, Class<?>> cs = new TreeMap<>();
		for (Field f : obj.getClass().getDeclaredFields()) { fs.put(f.getName(), f); }
		for (Field f : obj.getClass().getFields()) { if (!fs.containsKey(f.getName())) { fs.put(f.getName(), f); } }
		for (Method m : obj.getClass().getDeclaredMethods()) { ms.put(m.getName(), m); }
		for (Method m : obj.getClass().getMethods()) { if (!ms.containsKey(m.getName())) { ms.put(m.getName(), m); } }
		for (Class<?> c : obj.getClass().getDeclaredClasses()) { cs.put(c.getName(), c); }
		for (Class<?> c : obj.getClass().getClasses()) { if (!cs.containsKey(c.getName())) { cs.put(c.getName(), c); } }
		// create list
		List<Component> list = new ArrayList<>();
		LinkedHashMap<Integer, List<Component>> hts = new LinkedHashMap<>();
		int i = 0;
		for (String key : fs.keySet()) {
			try {
				Field f = fs.get(key);
				int mdf = f.getModifiers();
				list.add(Component.empty()
						.append(Component.literal("F: ").withStyle(TextFormatting.GOLD))
						.append(Component.literal(key).withStyle(Modifier.isPublic(mdf) ? TextFormatting.GREEN : TextFormatting.RED)));
				hts.put(i++, getFieldTypes(obj, mdf, f));
			}
			catch (Exception e) { LogWriter.error("Error:", e); }
		}
		for (String key : ms.keySet()) {
			try {
				Method m = ms.get(key);
				int mdf = m.getModifiers();
				list.add(Component.empty()
						.append(Component.literal("M: ").withStyle(TextFormatting.DARK_AQUA))
						.append(Component.literal(key).withStyle(Modifier.isPublic(mdf) ? TextFormatting.GREEN : TextFormatting.RED)));
				hts.put(i++, getMethodTypes(mdf, m));
			}
			catch (Exception e) { LogWriter.error("Error:", e); }
		}
		for (String key : cs.keySet()) {
			Class<?> c = cs.get(key);
			int mdf = c.getModifiers();
			Component mf = Component.empty();
			if (Modifier.isPublic(mdf)) { mf.append(Component.literal("public ").withStyle(TextFormatting.GREEN)); }
			else if (Modifier.isProtected(mdf)) { mf.append(Component.literal("protected ").withStyle(TextFormatting.RED)); }
			else { mf.append(Component.literal("private ").withStyle(TextFormatting.DARK_RED)); }
			if (Modifier.isStatic(mdf)) { mf.append(Component.literal("static ").withStyle(TextFormatting.YELLOW)); }
			if (Modifier.isFinal(mdf)) { mf.append(Component.literal("final ").withStyle(TextFormatting.AQUA)); }
			mf.append(Component.literal("subclass:").withStyle(TextFormatting.BLUE));
			List<Component> l = new ArrayList<>();
			l.add(mf);
			l.add(Component.literal(c.getSimpleName()));

			list.add(Component.empty()
					.append(Component.literal("C: ").withStyle(TextFormatting.DARK_BLUE))
					.append(Component.literal(key).withStyle(Modifier.isPublic(mdf) ? TextFormatting.GREEN : TextFormatting.RED)));
			hts.put(i++, l);
		}
		scroll.setUnsortedList(list)
				.setHoverTexts(hts);
	}

	private static List<Component> getFieldTypes(Object obj, int mdf, Field f) {
		Component mf = Component.empty()
				.append(Component.literal("field: ").withStyle(TextFormatting.GOLD));
		if (Modifier.isPublic(mdf)) { mf.append(Component.literal("public ").withStyle(TextFormatting.GREEN)); }
		else if (Modifier.isProtected(mdf)) { mf.append(Component.literal("protected ").withStyle(TextFormatting.RED)); }
		else { mf.append(Component.literal("private ").withStyle(TextFormatting.DARK_RED)); }
		if (Modifier.isStatic(mdf)) { mf.append(Component.literal("static ").withStyle(TextFormatting.YELLOW)); }
		if (Modifier.isFinal(mdf)) { mf.append(Component.literal("final ").withStyle(TextFormatting.AQUA)); }
		Object v = null;
		try {
			boolean bo = !f.isAccessible();
			if (bo) { f.setAccessible(true); }
			v = f.get(obj);
			if (bo) { f.setAccessible(false); }
		} catch (Exception ignored) { }
		List<Component> hoverText = new ArrayList<>();
		hoverText.add(mf);
		hoverText.add(Component.empty()
				.append(Component.literal("value type: ").withStyle(TextFormatting.GRAY))
				.append(Component.literal(f.getType().getName()).withStyle(TextFormatting.RESET)));
		hoverText.add(Component.empty()
				.append(Component.literal("value: ").withStyle(TextFormatting.GRAY))
				.append(Component.literal(v != null ? v.toString() : "null").withStyle(TextFormatting.RESET)));
		return hoverText;
	}

	private static List<Component> getMethodTypes(int mdf, Method m) {
		Component mf = Component.empty()
				.append(Component.literal("method: ").withStyle(TextFormatting.DARK_AQUA));
		if (Modifier.isPublic(mdf)) { mf.append(Component.literal("public ").withStyle(TextFormatting.GREEN)); }
		else if (Modifier.isProtected(mdf)) { mf.append(Component.literal("protected ").withStyle(TextFormatting.RED)); }
		else { mf.append(Component.literal("private ").withStyle(TextFormatting.DARK_RED)); }
		if (Modifier.isStatic(mdf)) { mf.append(Component.literal("static ").withStyle(TextFormatting.YELLOW)); }
		if (Modifier.isFinal(mdf)) { mf.append(Component.literal("final ").withStyle(TextFormatting.AQUA)); }

		List<Component> hoverText = new ArrayList<>();
		hoverText.add(mf);
		if (m.getParameters() != null && m.getParameters().length > 0) {
			hoverText.add(Component.literal("parameters: (").withStyle(TextFormatting.GRAY));
			Parameter[] prms = m.getParameters();
			for (int j = 0; j < prms.length; j++) {
				String pName = prms[j].getType().getName();
				String aName = prms[j].getType().getSimpleName();
				Component ps = Component.literal(" ").append(Component.literal(pName.replace(aName, "")).withStyle(TextFormatting.DARK_GRAY))
						.append(Component.literal(aName).withStyle(TextFormatting.YELLOW));
				if (j < prms.length - 1) { ps.append(Component.literal(",").withStyle(TextFormatting.GRAY)); }
				hoverText.add(ps);
			}
			hoverText.add(Component.literal(")").withStyle(TextFormatting.GRAY));
		} else {
			hoverText.add(Component.literal("parameters: ()").withStyle(TextFormatting.GRAY));
		}
		hoverText.add(Component.empty()
				.append(Component.literal("return type: ").withStyle(TextFormatting.GRAY))
				.append(Component.literal(m.getReturnType().getName()).withStyle(TextFormatting.RESET)));
		return hoverText;
	}

}
