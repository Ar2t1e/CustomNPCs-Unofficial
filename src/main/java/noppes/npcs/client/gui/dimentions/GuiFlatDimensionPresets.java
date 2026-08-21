package noppes.npcs.client.gui.dimentions;

import java.io.IOException;
import java.util.*;

import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class GuiFlatDimensionPresets extends GuiScreen {

	@SideOnly(Side.CLIENT)
	static class LayerItem {

		public Item icon;
		public int iconMetadata;
		public String name;
		public String generatorInfo;

		public LayerItem(Item iconIn, int iconMetadataIn, String nameIn, String generatorInfoIn) {
			icon = iconIn;
			iconMetadata = iconMetadataIn;
			name = nameIn;
			generatorInfo = generatorInfoIn;
		}

	}
	@SideOnly(Side.CLIENT)
	class ListSlot extends GuiSlot {

		public int selected = -1;

		public ListSlot() {
			super(GuiFlatDimensionPresets.this.mc, GuiFlatDimensionPresets.this.width,
					GuiFlatDimensionPresets.this.height, 80, GuiFlatDimensionPresets.this.height - 37, 24);
		}

		@Override
		protected void drawBackground() { }

		@Override
		protected void drawSlot(int entryID, int insideLeft, int yPos, int insideSlotHeight, int mouseXIn, int mouseYIn, float partialTicks) {
			GuiFlatDimensionPresets.LayerItem guiflatpresets$layeritem = GuiFlatDimensionPresets.FLAT_WORLD_PRESETS.get(entryID);
			renderIcon(insideLeft, yPos, guiflatpresets$layeritem.icon, guiflatpresets$layeritem.iconMetadata);
			fontRenderer.drawString(guiflatpresets$layeritem.name, insideLeft + 18 + 5, yPos + 6, 16777215);
		}

		@Override
		protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
			selected = slotIndex;
			updateButtonValidity();
			export.setText(GuiFlatDimensionPresets.FLAT_WORLD_PRESETS.get(list.selected).generatorInfo);
		}

		private void blitSlotIcon(int p_148171_1_, int p_148171_2_) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(Gui.STAT_ICONS);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(p_148171_1_, p_148171_2_ + 18.0F, zLevel).tex(0, 18.0F * 0.0078125F).endVertex();
			vertexbuffer.pos(p_148171_1_ + 18.0F, p_148171_2_ + 18.0F, zLevel).tex(0.140625F, 0.140625F).endVertex();
			vertexbuffer.pos(p_148171_1_ + 18.0F, p_148171_2_, zLevel).tex(0.140625F, 0.0F).endVertex();
			vertexbuffer.pos(p_148171_1_, p_148171_2_, zLevel).tex(0.0F, 0.0F).endVertex();
			tessellator.draw();
		}

		private void blitSlotBg(int p_148173_1_, int p_148173_2_) { blitSlotIcon(p_148173_1_, p_148173_2_); }

		@Override
		protected int getSize() {
			return GuiFlatDimensionPresets.FLAT_WORLD_PRESETS.size();
		}

		@Override
		protected boolean isSelected(int slotIndex) { return slotIndex == selected; }

		private void renderIcon(int p_178054_1_, int p_178054_2_, Item icon, int iconMetadata) {
			blitSlotBg(p_178054_1_ + 1, p_178054_2_ + 1);
			GlStateManager.enableRescaleNormal();
			RenderHelper.enableGUIStandardItemLighting();
			itemRender.renderItemIntoGUI(new ItemStack(icon, 1, iconMetadata), p_178054_1_ + 2, p_178054_2_ + 2);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableRescaleNormal();
		}

	}
	private static final List<GuiFlatDimensionPresets.LayerItem> FLAT_WORLD_PRESETS = new ArrayList<>();
	static {
		registerPreset("Classic Flat", Item.getItemFromBlock(Blocks.GRASS), Biomes.PLAINS,
                Collections.singletonList("village"),
                new FlatLayerInfo(1, Blocks.GRASS), new FlatLayerInfo(2, Blocks.DIRT),
                new FlatLayerInfo(1, Blocks.BEDROCK));
		registerPreset("Tunnelers' Dream", Item.getItemFromBlock(Blocks.STONE), Biomes.EXTREME_HILLS,
				Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
                new FlatLayerInfo(1, Blocks.GRASS), new FlatLayerInfo(5, Blocks.DIRT),
                new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
		registerPreset("Water World", Items.WATER_BUCKET, Biomes.DEEP_OCEAN,
				Arrays.asList("biome_1", "oceanmonument"),
                new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.SAND),
                new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE),
                new FlatLayerInfo(1, Blocks.BEDROCK));
		registerPreset("Overworld", Item.getItemFromBlock(Blocks.TALLGRASS), BlockTallGrass.EnumType.GRASS.getMeta(),
				Biomes.PLAINS,
				Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"),
                new FlatLayerInfo(1, Blocks.GRASS), new FlatLayerInfo(3, Blocks.DIRT),
                new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
		registerPreset("Snowy Kingdom", Item.getItemFromBlock(Blocks.SNOW_LAYER), Biomes.ICE_PLAINS,
				Arrays.asList("village", "biome_1"),
                new FlatLayerInfo(1, Blocks.SNOW_LAYER), new FlatLayerInfo(1, Blocks.GRASS),
                new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE),
                new FlatLayerInfo(1, Blocks.BEDROCK));
		registerPreset("Bottomless Pit", Items.FEATHER, Biomes.PLAINS,
				Arrays.asList("village", "biome_1"),
                new FlatLayerInfo(1, Blocks.GRASS), new FlatLayerInfo(3, Blocks.DIRT),
                new FlatLayerInfo(2, Blocks.COBBLESTONE));
		registerPreset("Desert", Item.getItemFromBlock(Blocks.SAND), Biomes.DESERT,
				Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
                new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE),
                new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
		registerPreset(
				new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE),
                new FlatLayerInfo(1, Blocks.BEDROCK));
		registerPreset("The Void", Item.getItemFromBlock(Blocks.BARRIER), Biomes.VOID,
                Collections.singletonList("decoration"),
                new FlatLayerInfo(1, Blocks.AIR));
	}
	private static void registerPreset(FlatLayerInfo... layers) {
		registerPreset("Redstone Ready", Items.REDSTONE, 0, Biomes.DESERT, null, layers);
	}
	private static void registerPreset(String name, Item icon, Biome biome, List<String> features, FlatLayerInfo... layers) {
		registerPreset(name, icon, 0, biome, features, layers);
	}
	private static void registerPreset(String name, Item icon, int iconMetadata, Biome biome, List<String> features, FlatLayerInfo... layers) {
		FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
		for (int i = layers.length - 1; i >= 0; --i) {
			flatgeneratorinfo.getFlatLayers().add(layers[i]);
		}
		flatgeneratorinfo.setBiome(Biome.getIdForBiome(biome));
		flatgeneratorinfo.updateLayers();
		if (features != null) {
			for (String s : features) {
				flatgeneratorinfo.getWorldFeatures().put(s, new HashMap<>());
			}
		}
		FLAT_WORLD_PRESETS.add(new GuiFlatDimensionPresets.LayerItem(icon, iconMetadata, name, flatgeneratorinfo.toString()));
	}
	private final GuiCreateFlatDimension parentScreen;

	private String presetsTitle;

	private String presetsShare;

	private String listText;

	private GuiFlatDimensionPresets.ListSlot list;

	private GuiButton btnSelect;

	private GuiTextField export;

	public GuiFlatDimensionPresets(GuiCreateFlatDimension parent) { parentScreen = parent; }

	@Override
	protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
		if (button.id == 0 && hasValidSelection()) {
			parentScreen.setPreset(export.getText());
			mc.displayGuiScreen(parentScreen);
		}
		else if (button.id == 1) { mc.displayGuiScreen(parentScreen); }
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		list.drawScreen(mouseX, mouseY, partialTicks);
		drawCenteredString(fontRenderer, presetsTitle, width / 2, 8, 0xFFFFFF);
		drawString(fontRenderer, presetsShare, 50, 30, 0xA0A0A0);
		drawString(fontRenderer, listText, 50, 70, 0xA0A0A0);
		export.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void updateButtonValidity() { btnSelect.enabled = hasValidSelection(); }

	private boolean hasValidSelection() {
		return list.selected > -1 && list.selected < FLAT_WORLD_PRESETS.size() || export.getText().length() > 1;
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		list.handleMouseInput();
	}

	@Override
	public void initGui() {
		buttonList.clear();
		Keyboard.enableRepeatEvents(true);
		presetsTitle = new TextComponentTranslation("createWorld.customize.presets.title").getFormattedText();
		presetsShare = new TextComponentTranslation("createWorld.customize.presets.share").getFormattedText();
		listText = new TextComponentTranslation("createWorld.customize.presets.list").getFormattedText();
		export = new GuiTextField(2, fontRenderer, 50, 40, width - 100, 20);
		list = new GuiFlatDimensionPresets.ListSlot();
		export.setMaxStringLength(1230);
		export.setText(parentScreen.getPreset());
		buttonList.add(btnSelect = new GuiButton(0, width / 2 - 155, height - 28, 150, 20,
				new TextComponentTranslation("createWorld.customize.presets.select").getFormattedText()));
		buttonList.add(new GuiButton(1, width / 2 + 5, height - 28, 150, 20,
				new TextComponentTranslation("gui.cancel").getFormattedText()));
		updateButtonValidity();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!export.textboxKeyTyped(typedChar, keyCode)) {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		export.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void updateScreen() {
		export.updateCursorCounter();
		super.updateScreen();
	}

}
