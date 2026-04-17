package noppes.npcs.client.gui.global;

import java.awt.*;
import java.util.*;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.crafting.IShapedRecipe;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.handler.data.INpcRecipe;
import noppes.npcs.api.wrapper.WrapperRecipe;
import noppes.npcs.client.gui.SubGuiEditIngredients;
import noppes.npcs.client.gui.SubGuiEditText;
import noppes.npcs.client.gui.availability.SubGuiNpcAvailability;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerManageRecipes;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketDetectHeldItem;
import noppes.npcs.packets.server.SPacketRecipeRemove;
import noppes.npcs.packets.server.SPacketRecipeRemoveGroup;
import noppes.npcs.packets.server.SPacketRecipeSave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCheckBoxNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.Util;

public class GuiNpcManageRecipes
		extends GuiContainerNPCInterface2<ContainerManageRecipes>
		implements IScrollData, IGuiData, ICustomScrollListener, ITextfieldListener {

	protected static boolean onlyMod = true;
	protected static final WrapperRecipe recipe = new WrapperRecipe();
	protected static final int green = new Color(0xFF70F070).getRGB();
	protected static final int red = new Color(0xFFF07070).getRGB();

	protected final ContainerManageRecipes container;
	protected final Map<Boolean, Map<Component, List<WrapperRecipe>>> data = new TreeMap<>(); // <isGlobal, <Group, recipe data>>
	protected GuiCustomScrollNop groups;
	protected GuiCustomScrollNop recipes;
	protected boolean wait = false;


	public GuiNpcManageRecipes(EntityNPCInterface npc, ContainerManageRecipes containerIn) {
		super(npc, containerIn, Component.empty());
		setBackground("inventorymenu.png");
        drawDefaultBackground = false;
		ySize = 200;

		backGui = EnumGuiType.MainMenuGlobal;
		container = containerIn;
		recipe.domen = CustomNpcs.MODID;
	}

	@Override
	public void initGui() {
		super.initGui();
		wait = false;
		data.clear();
		if (onlyMod && !recipe.domen.equals(CustomNpcs.MODID)) { recipe.clear(); }
		for (ResourceLocation loc : CraftingManager.REGISTRY.getKeys()) {
			IRecipe r = CraftingManager.REGISTRY.getObject(loc);
			if (r instanceof INpcRecipe || r instanceof IShapedRecipe || r instanceof ShapelessRecipes) {
				if (onlyMod && !(r instanceof INpcRecipe)) { continue; }
				WrapperRecipe wrapper = new WrapperRecipe();
				wrapper.copyFrom(r, CraftingManager.REGISTRY.getNameForObject(r));
				if (!data.containsKey(wrapper.global)) { data.put(wrapper.global, new LinkedHashMap<>()); }
				if (!data.get(wrapper.global).containsKey(wrapper.group)) { data.get(wrapper.global).put(wrapper.group, new ArrayList<>()); }
				data.get(wrapper.global).get(wrapper.group).add(wrapper);
			}
		}
		data.forEach((k0, v0) -> v0.forEach((k1, v1) -> v1.sort(Comparator.comparing(WrapperRecipe::getName))));
		if (recipe.group.isEmpty() && !data.get(recipe.global).isEmpty()) {
			recipe.clear();
			recipe.group = data.get(recipe.global).values().iterator().next().get(0).group;
		}
		if (!recipe.name.isEmpty()) {
			boolean found = false;
			if (data.get(recipe.global).containsKey(recipe.group) && !data.get(recipe.global).get(recipe.group).isEmpty()) {
				for (WrapperRecipe wr : data.get(recipe.global).get(recipe.group)) {
					if (wr.name.equals(recipe.name)) {
						found = true;
						recipe.copyFrom(wr);
						break;
					}
				}
			}
			if (!found) { recipe.name = ""; }
		}
		if (recipe.name.isEmpty() && data.get(recipe.global).containsKey(recipe.group) &&
				!data.get(recipe.global).get(recipe.group).isEmpty()) { recipe.copyFrom(data.get(recipe.global).get(recipe.group).get(0)); }
		addLabel(0, guiLeft + 172, guiTop + 8, "gui.recipe.groups")
				.setHoverTexts("recipe.hover.info.groups");
		addLabel(1, guiLeft + 294, guiTop + 8, "gui.recipe.crafts")
				.setHoverTexts("recipe.hover.info.crafts");
		if (groups == null) { groups = addScroll(0); }
		if (recipes == null) { recipes = addScroll(1); }
		List<Component> recipesList = new ArrayList<>();
		List<Component> groupsList = new ArrayList<>(data.get(recipe.global).keySet());
		LinkedHashMap<Integer, List<Component>> htsG = new LinkedHashMap<>();
		int i = 0;
		for (Component groupName : groupsList) {
			Component domen = Component.literal(CustomNpcs.MODID);
			Component name = Component.literal("Empty");
			if (!data.get(recipe.global).get(groupName).isEmpty()) {
				domen = data.get(recipe.global).get(groupName).get(0).domen;
				ItemStack stack = data.get(recipe.global).get(groupName).get(0).product;
				ResourceLocation regName = stack.getItem().getRegistryName();
				name = Component.empty()
						.append(Component.literal(regName == null ? "n/a" : regName.toString()))
						.append(Component.literal("; count: ").withStyle(TextFormatting.GRAY))
						.append(Component.literal("" + stack.getCount()).withStyle(TextFormatting.GOLD))
						.append(Component.literal("; meta: ").withStyle(TextFormatting.GRAY))
						.append(Component.literal("" + stack.getItemDamage()).withStyle(TextFormatting.YELLOW));
				if (stack.hasTagCompound()) {
					name.append(Component.literal("; (").withStyle(TextFormatting.GRAY))
							.append(Component.literal("has NBT").withStyle(TextFormatting.LIGHT_PURPLE))
							.append(Component.literal(")").withStyle(TextFormatting.GRAY));
				}
			}
			List<Component> ht = new ArrayList<>();
			ht.add(Component.empty()
					.append(Component.literal("Group: ").withStyle(TextFormatting.GRAY))
					.append(groupName.copy().withStyle(TextFormatting.WHITE)));
			ht.add(Component.empty()
					.append(Component.literal("Item: ").withStyle(TextFormatting.GRAY))
					.append(name.withStyle(TextFormatting.WHITE)));
			ht.add(Component.empty()
					.append(Component.literal("Mod: ").withStyle(TextFormatting.GRAY))
					.append(domen.withStyle(TextFormatting.AQUA)));
			ht.add(Component.empty()
					.append(Component.literal("Is global group: ").withStyle(TextFormatting.GRAY))
					.append(Component.literal(recipe.global ? "true" : "false").withStyle(recipe.global ? TextFormatting.GREEN : TextFormatting.RED)));
			htsG.put(i++, ht);
		}
		LinkedHashMap<Integer, List<Component>> htsR = new LinkedHashMap<>();
		if (data.get(recipe.global).containsKey(recipe.group)) {
			for (WrapperRecipe wrapper : data.get(recipe.global).get(recipe.group)) {
				recipesList.add(wrapper.name);
				List<Component> ht = new ArrayList<>();
				ht.add(Component.empty()
						.append(Component.literal("Group: ").withStyle(TextFormatting.GRAY))
						.append(wrapper.group.copy().withStyle(TextFormatting.WHITE)));
				ht.add(Component.empty()
						.append(Component.literal("Name: ").withStyle(TextFormatting.GRAY))
						.append(wrapper.name.copy().withStyle(TextFormatting.WHITE)));
				ht.add(Component.empty()
						.append(Component.literal("ID: ").withStyle(TextFormatting.GRAY))
						.append(Component.literal("" + wrapper.id).withStyle(TextFormatting.GOLD)));
				ht.add(Component.empty()
						.append(Component.literal("Mod: ").withStyle(TextFormatting.GRAY))
						.append(wrapper.domen.copy().withStyle(TextFormatting.WHITE)));
				ht.add(Component.empty()
						.append(Component.literal("Is main product: ").withStyle(TextFormatting.GRAY))
						.append(Component.literal(wrapper.main ? "true" : "false").withStyle(wrapper.main ? TextFormatting.GREEN : TextFormatting.RED)));
				ht.add(Component.empty()
						.append(Component.literal("Is global group: ").withStyle(TextFormatting.GRAY))
						.append(Component.literal(wrapper.global ? "true" : "false").withStyle(wrapper.global ? TextFormatting.GREEN : TextFormatting.RED)));
				ht.add(Component.empty()
						.append(Component.literal("Is shaped: ").withStyle(TextFormatting.GRAY))
						.append(Component.literal(wrapper.isShaped ? "true" : "false").withStyle(wrapper.isShaped ? TextFormatting.GREEN : TextFormatting.RED)));
				ht.add(Component.empty()
						.append(Component.literal("Always known: ").withStyle(TextFormatting.GRAY))
						.append(Component.literal(wrapper.known ? "true" : "false").withStyle(wrapper.known ? TextFormatting.GREEN : TextFormatting.RED)));
				htsR.put(i++, ht);
			}
		}
		add(groups.setPos(guiLeft + 172, guiTop + 20)
				.setUnsortedList(groupsList)
				.setHoverTexts(htsG)
				.setSize(120, 168));
		if (!recipe.group.getString().isEmpty()) { groups.setSelected(recipe.group); }
		add(recipes.setPos(guiLeft + 294, guiTop + 20)
				.setUnsortedList(recipesList)
				.setHoverTexts(htsR)
				.setSize(120, 168));
		if (!recipe.name.getString().isEmpty()) { recipes.setSelected(recipe.name); }
		int x = guiLeft + 118;
		int y = guiTop + 191;
		boolean isCNPCsMod = recipe.domen.getString().equals(CustomNpcs.MODID);
		boolean hasItem = recipe.isValid() && isCNPCsMod;
		// Global type
		addButton(0, guiLeft + 6, y, true, recipe.global ? 0 : 1, "menu.global", "tile.npccarpentybench.name")
				.setSize(163, 20)
				.setColor(recipe.global ? new Color(0x4000FF00).getRGB() : new Color(0x400000FF).getRGB())
				.setHoverTexts("recipe.hover.type");
		// Only mod list
		if (recipe.global) {
			addCheckBox(30, guiLeft + 7, guiTop + 97, "gui.recipe.type.true", "gui.recipe.type.false", onlyMod)
					.setSize(95, 12);
		}
		// Groups
		addButton(1, guiLeft + 172, y, "gui.add")
				.setSize(59, 20)
				.setHoverTexts("recipe.hover.add.group");
		addButton(2, guiLeft + 234, y, "gui.remove")
				.setSize(59, 20)
				.setIsEnabled(groups.hasSelected() && recipe.domen.getString().equals(CustomNpcs.MODID))
				.setHoverTexts("recipe.hover.del.group");
		// Recipes
		addButton(3, guiLeft + 294, y, "gui.copy")
				.setSize(59, 20)
				.setIsEnabled(!isCNPCsMod || recipes.getList().size() < 16)
				.setHoverTexts("recipe.hover.add.recipe");
		addButton(4, guiLeft + 356, y, "gui.remove")
				.setSize(59, 20)
				.setIsEnabled(recipes.hasSelected() && recipe.domen.getString().equals(CustomNpcs.MODID))
				.setHoverTexts("recipe.hover.del.recipe");
		// Recipe settings
		if (isCNPCsMod) {
			y = guiTop + 4;
			addLabel(2, guiLeft + 6, y + 5, "availability.options");
			addButton(8, x, y, "selectServer.edit")
					.setSize(50, 20)
					.setIsEnabled(hasItem)
					.setHoverTexts("availability.hover");
			addButton(9, x, y += 21, false, recipe.isShaped ? 1 : 0, "gui.shaped.0", "gui.shaped.1")
					.setSize(50, 20)
					.setIsEnabled(hasItem)
					.setColor(hasItem ? recipe.isShaped ? green : new Color(0xFF7070FF).getRGB() : new Color(0x0).getRGB())
					.setHoverTexts("recipe.hover.shared");
			addButton(7, x, y += 21, false, recipe.known ? 1 : 0, "gui.known.0", "gui.known.1")
					.setSize(50, 20)
					.setIsEnabled(hasItem)
					.setColor(hasItem ? recipe.known ? green : red : 0)
					.setHoverTexts("recipe.hover.known");
			addButton(5, x, y += 21, false, recipe.ignoreDamage ? 0 : 1, "gui.ignoreDamage.0", "gui.ignoreDamage.1")
					.setSize(50, 20)
					.setColor(hasItem ? recipe.ignoreDamage ? green : red : 0)
					.setHoverTexts("recipe.hover.damage");
			addButton(6, x, y + 21, false, recipe.ignoreNBT ? 0 : 1, "gui.ignoreNBT.0", "gui.ignoreNBT.1")
					.setSize(50, 20)
					.setColor(hasItem ? recipe.ignoreNBT ? green : red : 0)
					.setHoverTexts("recipe.hover.nbt");
		}
		// Product
		int craftOffset = recipe.global ? 9 : 0;
		Component hover = Component.translatable("recipe.hover.product");
		if (recipe.domen.getString().equals(CustomNpcs.MODID)) {
			if (!recipe.main) { hover.append(Component.translatable("recipe.hover.ingredient.4")); }
			hover.append(Component.translatable("recipe.hover.ingredient.1"));
			hover.append(Component.translatable("recipe.hover.ingredient.2"));
		}
		hover.append(Component.translatable("recipe.hover.ingredient.3"));
		if (recipe.product != null) {
			hover.append("<br>");
			List<String> list = recipe.product.getTooltip(player,
					mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
			for (String str : list) { hover.append("<br>").append(str); }
		}
		addButton(10, guiLeft + 7 + craftOffset + (recipe.global ? 61 : 76), guiTop + 14 + craftOffset + (int) ((recipe.global ? 1.0 : 1.5) * 19.0), "")
				.setSize(30, 30)
				.setTexture(GuiNPCInterface.ANIMATION_BUTTONS)
				.setDefBack(false)
				.setIsAnim(true)
				.setUV(220, 96, 36, 36)
				.setColor(recipe.product.isEmpty() ? red : !recipe.main ? new Color(0xFFA0A0A0).getRGB() : 0)
				.setIsEnabled(isCNPCsMod && recipe.isValid())
				.setStacks(recipe.product)
				.setHoverTexts(hover.getFormattedText());
		// Craft grid
		// set buttons
		int s = recipe.global ? 3 : 4;
		for (int h = 0; h < s; ++h) {
			for (int w = 0; w < s; ++w) {
				int id = 11 + w + h * s;
				addButton(id, guiLeft + craftOffset + w * 19 + 7, guiTop + craftOffset + h * 19 + 20, "")
						.setSize(18, 18)
						.setTexture(GuiNPCInterface.ANIMATION_BUTTONS)
						.setDefBack(false)
						.setIsAnim(true)
						.setUV(220, 96, 36, 36)
						.setIsEnabled(isCNPCsMod && recipe.isValid());
				if (recipe.domen.getString().equals(CustomNpcs.MODID)) { getButton(id).setColor(recipe.isValid() ? green : red); }
			}
		}
		// set recipe
		for (int w = 0; w < recipe.width; ++w) {
			for (int h = 0; h < recipe.height; ++h) {
				int id = 11 + h * recipe.height + w;
				int slotID = h * recipe.width + w;
				ItemStack[] stacks = recipe.recipeItems.get(slotID);
				getButton(id).setStacks(stacks);
				if (recipe.domen.getString().equals(CustomNpcs.MODID)) { getButton(id).setColor(recipe.isValid() ? (stacks != null && stacks.length > 0) ? 0 : green : red); }
			}
		}
		// Clear
		addButton(28, guiLeft + 92, guiTop + 77, "")
				.setSize(18, 18)
				.setTexture(GuiNPCInterface.ANIMATION_BUTTONS)
				.setDefBack(false)
				.setIsAnim(true)
				.setUV(120, 0, 24, 24);
	}

	@Override
	public void mouseButtonEvent(GuiButtonNop button, int mouseButton) {
		int id = button.id;
		boolean isCNPCsMod = recipe.domen.getString().equals(CustomNpcs.MODID);
		if (mouseButton == 0) {
			ItemStack heldStack = player.inventory.getItemStack();
			if (id >= 10 && id < 27 && isCNPCsMod) {
				// show list of ingredients
				if (id != 10 && isShiftKeyDown()) {
					if (recipe.recipeItems.get(id - 11).length > 0) { setSubGui(new SubGuiEditIngredients(id - 11, recipe.recipeItems.get(id - 11)));}
					return;
				}
				// product
				if (id == 10) {
					if (isAltKeyDown()) { recipe.product.setCount(1); }
					else if (recipe.product.isEmpty()) {
						ItemStack stack = null;
						if (recipe.main && !heldStack.isEmpty()) { stack = heldStack.copy(); }
						else {
							for (WrapperRecipe wr: data.get(recipe.global).get(recipe.group)) {
								if (wr.main) {
									stack = wr.product.copy();
									stack.setCount(heldStack.getCount());
									break;
								}
							}
						}
						if (stack != null) { recipe.product = stack; }
					}
					else {
						if (heldStack.isEmpty()) { recipe.product.setCount(Math.max(1, recipe.product.getCount() - 1)); } // -1
						else if (!recipe.main || NoppesUtilPlayer.compareItems(recipe.product, heldStack, false, false)) { // +N
							recipe.product.setCount(Math.min(recipe.product.getMaxStackSize(), recipe.product.getCount() + heldStack.getCount()));
						}
						else if (recipe.main) { recipe.product = heldStack.copy(); } // replace
						button.setStacks(recipe.product);
					}
					if (recipe.product.isEmpty()) { button.setColor(red); }
				}
				// ingredient
				else {
					int pos = id - 11;
					ItemStack[] array = recipe.recipeItems.get(pos);
					if (isCtrlKeyDown()) {
						if (heldStack.isEmpty() || array.length >= 16) { return; }
						if (array.length == 0) {
							array = new ItemStack[] { heldStack.copy() };
							array[0].setCount(1);
							button.setStacks(array);
							recipe.recipeItems.put(pos, array);
						}
						else {
							boolean found = false;
							for (ItemStack stack : array) {
								if (!stack.isEmpty() && NoppesUtilPlayer.compareItems(stack, heldStack, false, false)) {
									found = true;
									break;
								}
							}
							if (!found) {
								array = Arrays.copyOf(array, array.length + 1);
								array[array.length - 1] = heldStack.copy();
								button.setStacks(array);
								recipe.recipeItems.put(pos, array);
							}
						}
					} // try to add new
					else if (isAltKeyDown()) {
						if (button.getCurrentStackID() < array.length) {
							array[button.getCurrentStackID()].setCount(1);
							button.setStacks(array);
							recipe.recipeItems.put(pos, array);
						} else if (array.length == 0 && !heldStack.isEmpty()) {
							array = new ItemStack[] { heldStack.copy() };
							array[0].setCount(1);
							button.setStacks(array);
							recipe.recipeItems.put(pos, array);
						}
					} // set count == 1
					else if (array == null || array.length == 0) {
						if (!heldStack.isEmpty()) {
							array = new ItemStack[]{ heldStack.copy() };
							button.setStacks(array);
							recipe.recipeItems.put(pos, array);
						}
					} // install at least something
					else {
						if (heldStack.isEmpty()) {
							int p = button.getCurrentStackID();
							int count = Math.max(0, array[p].getCount() - 1);
							if (count > 0) { array[p].setCount(count); }
							else {
								List<ItemStack> list = new ArrayList<>();
								for (int i = 0; i < array.length; i++) {
									if (i == p) { continue; }
									list.add(array[i]);
								}
								array = list.toArray(new ItemStack[0]);
							}
							button.setStacks(array);
							button.setCurrentStackPos(p);
							recipe.recipeItems.put(pos, array);
						} // -1
						else {
							boolean found = false;
							for (int i = 0; i < array.length; i++) {
								if (!array[i].isEmpty() && NoppesUtilPlayer.compareItems(array[i], heldStack, false, false)) {
									// +N
									found = true;
									array[i].setCount(Math.min(array[i].getMaxStackSize(), array[i].getCount() + heldStack.getCount()));
									button.setStacks(array);
									button.setCurrentStackPos(i);
									break;
								}
							}
							if (!found) {
								array[button.getCurrentStackID()] = heldStack.copy();
								button.setStacks(array);
								button.setCurrentStackPos(button.getCurrentStackID());
							}
						}
						recipe.recipeItems.put(pos, array);
					} // +/- count? and set display found stack
                    button.setColor(recipe.isValid() ? array != null && array.length > 0 ? 0 : green : red);
                }
				if (id == 10) {
					if (heldStack.isEmpty()) { recipe.product.setCount(Math.max(1, recipe.product.getCount() - 1)); }
					else if (recipe.product.isEmpty()) {
						ItemStack stack = null;
						if (recipe.main) { stack = heldStack.copy(); }
						else {
							for (WrapperRecipe wr: data.get(recipe.global).get(recipe.group)) {
								if (wr.main) {
									stack = wr.product.copy();
									break;
								}
							}
						}
						if (stack != null) {
							stack.setCount(1);
							recipe.product = stack;
						}
					}
					else if (!recipe.main || NoppesUtilPlayer.compareItems(recipe.product, heldStack, false, false)) { // +N
						recipe.product.setCount(Math.min(recipe.product.getMaxStackSize(), recipe.product.getCount() + 1));
					}
					if (recipe.product.isEmpty()) { button.setColor(red); }
				}
				else {
					int pos = id - 11;
					ItemStack[] array = recipe.recipeItems.get(pos);
					if (heldStack.isEmpty() && array != null && array.length > 0) {
						int p = button.getCurrentStackID();
						int count = Math.max(0, array[p].getCount() - 1);
						if (count > 0) { array[p].setCount(count); }
						else {
							List<ItemStack> list = new ArrayList<>();
							for (int i = 0; i < array.length; i++) {
								if (i == p) { continue; }
								list.add(array[i]);
							}
							array = list.toArray(new ItemStack[0]);
						}
						button.setStacks(array);
						button.setCurrentStackPos(p);
						recipe.recipeItems.put(pos, array);
					}
					else if ((array == null || array.length == 0) && !heldStack.isEmpty()) {
						ItemStack stack = heldStack.copy();
						stack.setCount(1);
						array = new ItemStack[] { stack };
						button.setStacks(array);
						recipe.recipeItems.put(pos, array);
					}
					else if (array != null) {
						for (int i = 0; i < array.length; i++) {
							if (!array[i].isEmpty() && NoppesUtilPlayer.compareItems(array[i], heldStack, false, false)) {
								array[i].setCount(Math.min(array[i].getMaxStackSize(), array[i].getCount() + 1));
								button.setStacks(array);
								button.setCurrentStackPos(i);
								recipe.recipeItems.put(pos, array);
								break;
							}
						}
					}
                    button.setColor(recipe.isValid() ? array != null && array.length > 0 ? 0 : green : red);
                }
				return;
			}
			switch (id) {
				case 0: {
					save();
					recipe.clear();
					recipe.global = button.getValue() == 0;
					initGui();
					break;
				} // global type
				case 1: {
					SubGuiEditText subGui = new SubGuiEditText(0, new String[] { recipe.group.toString() });
					subGui.latinAlphabetOnly = true;
					subGui.allowUppercase = false;
					setSubGui(subGui);
					break;
				} // Add Group
				case 2: {
					Packets.sendServer(new SPacketRecipeRemoveGroup(container.width, recipe.group.getString()));
					recipe.clear();
					wait = true;
					break;
				} // Del Group
				case 3: {
					int i;
					String[] text;
					Component[] hovers;
					Component label;
					if (isCNPCsMod) {
						i = 1;
						text = new String[] { recipe.name.getString() };
						label = Component.translatable("gui.name").append(":");
						hovers = new Component[] { Component.translatable("recipe.hover.recipe.named")
										.append(". ")
										.append(Component.translatable("hover.latin.alphabet.only")) };
					}
					else {
						i = 4;
						text = new String[] { recipe.group.getString(), recipe.name.getString() };
						label = Component.translatable("gui.group").append(" / ")
								.append(Component.translatable("gui.name")).append(":");
						hovers = new Component[] { Component.translatable("recipe.hover.group.named")
										.append(". ")
										.append(Component.translatable("hover.latin.alphabet.only")),
								Component.translatable("recipe.hover.recipe.named")
										.append(". ")
										.append(Component.translatable("hover.latin.alphabet.only")) };
					}
					SubGuiEditText subGui = new SubGuiEditText(i, text);
					subGui.label = label.getString();
					subGui.setHoverTexts(hovers);
					subGui.latinAlphabetOnly = true;
					subGui.allowUppercase = false;
					setSubGui(subGui);
					break;
				} // Add Recipe
				case 4: {
					Packets.sendServer(new SPacketRecipeRemove(container.width, recipe.group.getString(), recipe.name.getString()));
					recipe.name = Component.empty();
					wait = true;
					break;
				} // Del Recipe
				case 5: {
					recipe.ignoreDamage = !recipe.ignoreDamage;
					save();
					initGui();
					break;
				} // ignore Meta
				case 6: {
					recipe.ignoreNBT = !recipe.ignoreNBT;
					save();
					initGui();
					break;
				} // ignore NBT
				case 7: {
					recipe.known = !recipe.known;
					save();
					initGui();
					break;
				} // know
				case 8: {
					setSubGui(new SubGuiNpcAvailability(recipe.availability, this));
					break;
				} // availability
				case 9: {
					recipe.isShaped = !recipe.isShaped;
					save();
					break;
				} // replace shaped <-> shapeless
				case 28: {
					if (!heldStack.isEmpty()) {
						Packets.sendServer(new SPacketDetectHeldItem(ItemStack.EMPTY));
					}
					break;
				} // clear held stack
				case 30: {
					onlyMod = ((GuiCheckBoxNop) button).selected();
					initGui();
					break;
				} // only mod
			}
		}
		else if (mouseButton == 2) {
			ItemStack heldStack = player.inventory.getItemStack();
			if (heldStack.isEmpty()) {
				ItemStack stack = button.getCurrentStack().copy();
				stack.setCount(stack.getMaxStackSize());
				Packets.sendServer(new SPacketDetectHeldItem(stack));
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (wait) { drawWait(); return; }
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (!hasSubGui() && !CustomNpcs.ShowDescriptions) {
			for (int i = 11; i < 27; i++) {
				if (getButton(i) != null && getButton(i).visible && getButton(i).isHoveredOrFocused()) {
					if (getButton(i).getCurrentStack().isEmpty()) { continue; }
					Component hover = Component.translatable("recipe.hover.ingredients", "" + (i - 11));
					if (recipe.domen.getString().equals(CustomNpcs.MODID)) {
						hover.append(Component.translatable("recipe.hover.ingredient.0"));
						hover.append(Component.translatable("recipe.hover.ingredient.1"));
						hover.append(Component.translatable("recipe.hover.ingredient.2"));
					}
					hover.append(Component.translatable("recipe.hover.ingredient.3"));
					if (getButton(i).getCurrentStack() != null) {
						hover.append("<br>");
						List<String> list = getButton(i).getCurrentStack().getTooltip(player, player.capabilities.isCreativeMode ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
						for (String str : list) { hover.append("<br>").append(str); }
					}
					drawHoverText(hover.getFormattedText());
					break;
				}
			}
		}
	}

	@Override
	public void save() {
		GuiTextFieldNop.unfocus();
		if (recipe.isValid() && recipe.parent instanceof INpcRecipe && recipe.domen.getString().equals(CustomNpcs.MODID)) {
			container.saveRecipe();
			Packets.sendServer(new SPacketRecipeSave(container.recipe.writeNBT()));
			wait = true;
		}
	}

	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		if (scroll.id == 0) {
			if (!recipe.group.getString().equals(groups.getSelected()) && data.get(recipe.global).containsKey(groups.getNormalSelected())) {
				save();
				recipe.clear();
				recipe.group = groups.getNormalSelected();
			}
        } // Group
		else if (scroll.id == 1) {
			if (!recipe.name.getString().equals(recipes.getSelected()) && data.get(recipe.global).containsKey(recipe.group)) {
				for (WrapperRecipe wrapper : data.get(recipe.global).get(recipe.group)) {
					if (wrapper.name.getString().equals(recipes.getSelected())) {
						save();
						recipe.copyFrom(wrapper);
						break;
					}
				}
			}
        } // Recipe
        initGui();
    }

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) {
		switch (scroll.id) {
			case 0: setSubGui(new SubGuiEditText(2, scroll.getSelected())); break; // rename Group
			case 1: setSubGui(new SubGuiEditText(3, scroll.getSelected())); break; // rename Recipe
		}
	}

	@Override
	public void subGuiClosed(GuiScreen subgui) {
		if (subgui instanceof SubGuiNpcAvailability) { save(); }
		else if (subgui instanceof SubGuiEditIngredients) {
			ItemStack[] stacks = new ItemStack[0];
			if (((SubGuiEditIngredients) subgui).stacks != null) {
				List<ItemStack> list = new ArrayList<>();
				for (ItemStack stack : ((SubGuiEditIngredients) subgui).stacks) {
					if (stack.isEmpty()) { continue; }
					list.add(stack);
				}
				if (!list.isEmpty()) { stacks = list.toArray(stacks); }
			}
			if (getButton(11 + ((SubGuiEditIngredients) subgui).id) != null) {
				GuiButtonNop button = getButton(11 + ((SubGuiEditIngredients) subgui).id)
						.setStacks(stacks)
						.setCurrentStackPos(0);
				if (stacks.length == 0) {
					button.setColor(recipe.isValid() ? new Color(0xFF70F070).getRGB() :  new Color(0xFFF07070).getRGB());
				}
			}
			recipe.recipeItems.put(((SubGuiEditIngredients) subgui).id, stacks);
		}
		else if (subgui instanceof SubGuiEditText) {
			if (((SubGuiEditText) subgui).cancelled) { return; }
			if (((SubGuiEditText) subgui).id == 0) {
				save();
				recipe.clear();
				recipe.group = Component.literal(NoppesUtilServer.validLocation(((SubGuiEditText) subgui).text[0]));
				recipe.name = Component.literal("default");
				Client.sendData(EnumPacketServer.RecipesAddGroup, recipe.global, recipe.group);
			} // Add new Group
			else if (((SubGuiEditText) subgui).id == 1) {
				save();
				String name = ((SubGuiEditText) subgui).text[0];
				while (true) {
					boolean found = false;
					for (WrapperRecipe wr : data.get(recipe.global).get(recipe.group)) {
						if (wr.name.equals(name)) {
							name = name+ "_";
							found = true;
							break;
						}
					}
					if (!found) { break; }
				}
				recipe.name = name;
				Client.sendData(EnumPacketServer.RecipeAdd, recipe.getNbt());
			} // Add new Recipe
			else if (((SubGuiEditText) subgui).getId() == 2) {
				String old = recipe.group;
				recipe.group = Util.instance.getResourceName(((SubGuiEditText) subgui).text[0]);
				Client.sendData(EnumPacketServer.RecipesRenameGroup, recipe.global, old, recipe.group);
			} // Rename Group
			else if (((SubGuiEditText) subgui).getId() == 3) {
				String old = recipe.name;
				recipe.name = Util.instance.getResourceName(((SubGuiEditText) subgui).text[0]);
				Client.sendData(EnumPacketServer.RecipesRename, recipe.global, old, recipe.group, recipe.name);
			} // Rename Recipe
			else if (((SubGuiEditText) subgui).getId() == 4) {
				String group = ((SubGuiEditText) subgui).text[0];
				if (data.get(recipe.global).containsKey(group) && data.get(recipe.global).get(group).size() >= 16) { return; }
				recipe.group = Util.instance.getResourceName(group);
				String name = Util.instance.getResourceName(((SubGuiEditText) subgui).text[1]);
				while (data.get(recipe.global).containsKey(recipe.group)) {
					boolean found = false;
					for (WrapperRecipe wr : data.get(recipe.global).get(recipe.group)) {
						if (wr.name.equals(name)) {
							name = name+ "_";
							found = true;
							break;
						}
					}
					if (!found) { break; }
				}
				recipe.name = name;
				Client.sendData(EnumPacketServer.RecipeAdd, recipe.getNbt());
			} // Copy vanilla Recipe
			else { return; }
			wait = true;
		}
	}

}
