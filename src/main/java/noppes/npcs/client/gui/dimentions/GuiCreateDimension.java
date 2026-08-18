package noppes.npcs.client.gui.dimentions;

import java.io.IOException;
import java.util.Random;

import net.minecraft.network.chat.Component;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.mixin.world.storage.IWorldInfoMixin;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketDimensionSettings;
import noppes.npcs.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.dimensions.CustomWorldInfo;
import noppes.npcs.controllers.DimensionController;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class GuiCreateDimension extends GuiScreen {

	private static final String[] disallowedFilenames = new String[] { "CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9" };
	public static String getUncollidingSaveDirName(ISaveFormat format, String name) {
		name = name.replaceAll("[\\\\./\"]", "_");
		StringBuilder nameBuilder = new StringBuilder(name);
		for (String s1 : disallowedFilenames) {
			if (nameBuilder.toString().equalsIgnoreCase(s1)) {
				nameBuilder = new StringBuilder("_" + nameBuilder + "_");
			}
		}
		name = nameBuilder.toString();
		while (format.getWorldInfo(name) != null) { name = name + "-"; }
		return name;
	}
	private GuiTextField dimensionNameTextField;
	private GuiTextField seedTextField;
	private String saveDirName;
	private String gameType = "survival";
	private String savedGameMode;
	private boolean generateStructures = true;
	private boolean allowCheats;
	private boolean alreadyGenerated;
	private boolean userInMoreOptions;
	private GuiButton btnMoreOptions;
	private GuiButton btnStructures;
	private GuiButton btnDimensionType;
	private GuiButton btnCustomizeType;
	public String gameMode1;
	public String gameMode2;
	private String seedID;
	private String dimensionName;
	private int selectedIndex;
	public String chunkProviderSettingsJson = "";

	private final int dimensionId;

	public GuiCreateDimension(int dimensionIdIn) {
		seedID = "";
		dimensionName = "custom_dimension";
		dimensionId = dimensionIdIn;

		// If editing an existing dimension, load its data directly.
		if (dimensionId > 0) {
			CustomWorldInfo cwi = (CustomWorldInfo) DimensionController.getInstance().getMCWorldInfo(dimensionId);
			if (cwi != null) {
				recreateFromExistingWorld(cwi);
				dimensionName = cwi.getWorldName();
			}
		}
	}

	@Override
	protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
		if (!button.enabled) { return; }
		if (button.id == 1) { CustomNpcs.proxy.openGui(mc.player, EnumGuiType.NpcDimensions); }
		else if (button.id == 0) {
			CustomNpcs.proxy.openGui(mc.player, EnumGuiType.NpcDimensions);
			if (alreadyGenerated) { return; }
			alreadyGenerated = true;
			long i = (new Random()).nextLong();
			String s = seedTextField.getText();
			if (!StringUtils.isEmpty(s)) {
				try {
					long j = Long.parseLong(s);
					if (j != 0L) { i = j; }
				}
				catch (NumberFormatException numberformatexception) { i = s.hashCode(); }
			}
			WorldType.WORLD_TYPES[selectedIndex].onGUICreateWorldPress();
			final WorldInfo worldInfo = getWorldInfo(i);
			Packets.sendServer(new SPacketDimensionSettings(dimensionId, worldInfo));
		}
		else if (button.id == 3) { toggleMoreWorldOptions(); }
		else if (button.id == 4) {
			generateStructures = !generateStructures;
			updateDisplayState();
		}
		else if (button.id == 5) {
			do {
				++selectedIndex;
				if (selectedIndex >= WorldType.WORLD_TYPES.length) { selectedIndex = 0; }
			} while (!canSelectCurWorldType());
			chunkProviderSettingsJson = "";
			updateDisplayState();
			showMoreWorldOptions(userInMoreOptions);
		} else if (button.id == 8) {
			if (WorldType.WORLD_TYPES[selectedIndex] == WorldType.FLAT) { mc.displayGuiScreen(new GuiCreateFlatDimension(this, chunkProviderSettingsJson)); }
			else if (WorldType.WORLD_TYPES[selectedIndex] == WorldType.CUSTOMIZED) { mc.displayGuiScreen(new GuiCustomizeDimension(this, chunkProviderSettingsJson)); }
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, Component.translatable(dimensionId > 0 ? "dimensions.edit" : "dimensions.create").getFormattedText(), width / 2, 20, -1);
		if (userInMoreOptions) {
			drawString(fontRenderer, Component.translatable("selectWorld.enterSeed").getFormattedText(), width / 2 - 100, 47, 0xFFA0A0A0);
			drawString(fontRenderer, Component.translatable("selectWorld.seedInfo").getFormattedText(), width / 2 - 100, 85, 0xFFA0A0A0);
			if (btnStructures.visible) {
				drawString(fontRenderer,
						Component.translatable("selectWorld.mapFeatures.info").getFormattedText(),
						width / 2 - 150, 122, 0xFFA0A0A0);
			}
			seedTextField.drawTextBox();
			if (WorldType.WORLD_TYPES[selectedIndex].hasInfoNotice()) {
				fontRenderer.drawSplitString(
						Component.translatable(WorldType.WORLD_TYPES[selectedIndex].getInfoTranslationKey()).getFormattedText(),
						btnDimensionType.x + 2, btnDimensionType.y + 22,
						btnDimensionType.getButtonWidth(), 10526880);
			}
		} else {
			dimensionNameTextField.setVisible(dimensionId == 0);
			if (dimensionNameTextField.getVisible()) {
				drawString(fontRenderer, Component.translatable("dimensions.enter.name").getFormattedText(), width / 2 - 100, 47, 0xFFA0A0A0);
				drawString(fontRenderer,
						Component.translatable("selectWorld.resultFolder")
								.append(" \"")
								.append(CustomNpcs.MODID)
								.append(":")
								.append(saveDirName)
								.append("\"").getFormattedText(),
						width / 2 - 100, 85, 0xFFA0A0A0);
			}
			else {
				drawString(fontRenderer, Component.translatable("dimensions.enter.name")
						.append(": \"").append(CustomNpcs.MODID)
						.append(":")
						.append(saveDirName)
						.append("\"").getFormattedText(), width / 2 - 100, 47, 0xFFA0A0A0);
			}
			dimensionNameTextField.drawTextBox();
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 155, height - 28, 150, 20, Component.translatable(dimensionId > 0 ? "gui.save" : "dimensions.create").getFormattedText()));
		buttonList.add(new GuiButton(1, width / 2 + 5, height - 28, 150, 20, Component.translatable("gui.cancel").getFormattedText()));
		buttonList.add(btnMoreOptions = new GuiButton(3, width / 2 - 75, 187, 150, 20, Component.translatable("dimensions.more.dimension.options").getFormattedText()));
		buttonList.add(btnStructures = new GuiButton(4, width / 2 - 155, 100, 150, 20, Component.translatable("selectWorld.mapFeatures").getFormattedText()));
		btnStructures.visible = false;
		buttonList.add(btnDimensionType = new GuiButton(5, width / 2 + 5, 100, 150, 20, Component.translatable("selectWorld.mapType").getFormattedText()));
		btnDimensionType.visible = false;
		buttonList.add(btnCustomizeType = new GuiButton(8, width / 2 + 5, 120, 150, 20, Component.translatable("selectWorld.customizeType").getFormattedText()));
		btnCustomizeType.visible = false;
		dimensionNameTextField = new GuiTextField(9, fontRenderer, width / 2 - 100, 60, 200, 20);
		dimensionNameTextField.setFocused(true);
		dimensionNameTextField.setText(dimensionName);
		seedTextField = new GuiTextField(10, fontRenderer, width / 2 - 100, 60, 200, 20);
		seedTextField.setText(seedID);
		showMoreWorldOptions(userInMoreOptions);
		calcSaveDirName();
		updateDisplayState();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if (dimensionNameTextField.isFocused() && !userInMoreOptions) {
			dimensionNameTextField.textboxKeyTyped(typedChar, keyCode);
			dimensionName = NoppesUtilServer.validPath(Util.instance.deleteColor(dimensionNameTextField.getText().toLowerCase()));
			while (dimensionName.contains(" ")) { dimensionName = dimensionName.replace(" ", "_"); }
		} else if (seedTextField.isFocused() && userInMoreOptions) {
			seedTextField.textboxKeyTyped(typedChar, keyCode);
			seedID = seedTextField.getText();
		}
		if (keyCode == 28 || keyCode == 156) { actionPerformed(buttonList.get(0)); }
		buttonList.get(0).enabled = !dimensionNameTextField.getText().isEmpty();
		calcSaveDirName();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (userInMoreOptions) { seedTextField.mouseClicked(mouseX, mouseY, mouseButton); }
		else { dimensionNameTextField.mouseClicked(mouseX, mouseY, mouseButton); }
	}

	@Override
	public void onGuiClosed() { Keyboard.enableRepeatEvents(false); }

	@Override
	public void updateScreen() {
		dimensionNameTextField.updateCursorCounter();
		seedTextField.updateCursorCounter();
	}

	private WorldInfo getWorldInfo(long seedIn) {
		GameType gametype = GameType.getByName(gameType);
		boolean hardcore = false;
		WorldSettings worldsettings = new WorldSettings(seedIn, gametype, generateStructures, hardcore, WorldType.WORLD_TYPES[selectedIndex]);
		worldsettings.setGeneratorOptions(chunkProviderSettingsJson);
		if (allowCheats && !hardcore) { worldsettings.enableCommands(); }

		// Clean the name from color codes and validate for ResourceLocation
		String rawName = dimensionNameTextField.getText().trim();
		String cleanName = Util.instance.deleteColor(rawName);
		CustomWorldInfo worldInfo = new CustomWorldInfo(worldsettings, cleanName, dimensionId);
		if (dimensionId > 0) {
			// Editing existing dimension — update in-place
			CustomWorldInfo cwi = (CustomWorldInfo) DimensionController.getInstance().getMCWorldInfo(dimensionId);
			if (cwi instanceof IWorldInfoMixin) {
				((IWorldInfoMixin) cwi).setLevelName(cleanName);
				cwi.load(worldInfo.cloneNBTCompound(cwi.getPlayerNBTTagCompound()));
				return cwi;
			}
		}
		return worldInfo;
	}

	private void calcSaveDirName() {
		saveDirName = dimensionNameTextField.getText().toLowerCase().trim();
		while (saveDirName.contains(" ")) { saveDirName = saveDirName.replace(" ", "_"); }
		char[] aChar = ChatAllowedCharacters.ILLEGAL_FILE_CHARACTERS;
		for (char c0 : aChar) { saveDirName = saveDirName.replace(c0, '_'); }
		if (StringUtils.isEmpty(saveDirName)) { saveDirName = "World"; }
		saveDirName = getUncollidingSaveDirName(mc.getSaveLoader(), saveDirName);
	}

	private void toggleMoreWorldOptions() { showMoreWorldOptions(!userInMoreOptions); }

	/**
	 * This method is kept for potential future "Copy Dimension" feature.
	 * It is NOT used for normal editing.
	 */
	public void recreateFromExistingWorld(WorldInfo original) {
		dimensionName = NoppesUtilServer.validPath(Util.instance.deleteColor(Component.translatable("selectWorld.newWorld.copyOf", original.getWorldName()).getFormattedText()));
		seedID = original.getSeed() + "";
		selectedIndex = original.getTerrainType().getId();
		chunkProviderSettingsJson = original.getGeneratorOptions();
		generateStructures = original.isMapFeaturesEnabled();
		allowCheats = original.areCommandsAllowed();
		if (original.isHardcoreModeEnabled()) { gameType = "hardcore"; }
		else if (original.getGameType().isSurvivalOrAdventure()) { gameType = "survival"; }
		else if (original.getGameType().isCreative()) { gameType = "creative"; }
	}

	private boolean canSelectCurWorldType() {
		WorldType worldtype = WorldType.WORLD_TYPES[selectedIndex];
		return worldtype != null && worldtype.canBeCreated() && (worldtype != WorldType.DEBUG_ALL_BLOCK_STATES || isShiftKeyDown());
	}

	private void showMoreWorldOptions(boolean toggle) {
		userInMoreOptions = toggle;
		if (WorldType.WORLD_TYPES[selectedIndex] == WorldType.DEBUG_ALL_BLOCK_STATES) {
			if (savedGameMode == null) { savedGameMode = gameType; }
			gameType = "spectator";
			btnStructures.visible = false;
			btnDimensionType.visible = userInMoreOptions;
			btnCustomizeType.visible = false;
		} else {
			if (savedGameMode != null) {
				gameType = savedGameMode;
				savedGameMode = null;
			}
			btnStructures.visible = userInMoreOptions && WorldType.WORLD_TYPES[selectedIndex] != WorldType.CUSTOMIZED;
			btnDimensionType.visible = userInMoreOptions;
			btnCustomizeType.visible = userInMoreOptions && WorldType.WORLD_TYPES[selectedIndex].isCustomizable();
		}
		updateDisplayState();
		if (userInMoreOptions) { btnMoreOptions.displayString = Component.translatable("gui.done").getFormattedText(); }
		else { btnMoreOptions.displayString = Component.translatable("dimensions.more.dimension.options").getFormattedText(); }
	}

	private void updateDisplayState() {
		gameMode1 = Component.translatable("selectWorld.gameMode." + gameType + ".line1").getFormattedText();
		gameMode2 = Component.translatable("selectWorld.gameMode." + gameType + ".line2").getFormattedText();
		btnStructures.displayString = Component.translatable("selectWorld.mapFeatures").getFormattedText() + " ";
		if (generateStructures) {
			btnStructures.displayString = btnStructures.displayString + Component.translatable("options.on").getFormattedText();
		} else {
			btnStructures.displayString = btnStructures.displayString + Component.translatable("options.off").getFormattedText();
		}
		btnDimensionType.displayString = Component.translatable("selectWorld.mapType").getFormattedText() +
				" " + Component.translatable(WorldType.WORLD_TYPES[selectedIndex].getTranslationKey()).getFormattedText();
	}

}