package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.controllers.Preset;
import noppes.npcs.client.controllers.PresetController;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.model.part.ModelData;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

import javax.annotation.Nonnull;

public class SubGuiPresetSave extends GuiNPCInterface {

	protected final ModelData data;
	protected final GuiScreen parent;

	public SubGuiPresetSave(GuiScreen parentIn, ModelData dataIn) {
		super();
		drawDefaultBackground = true;
		imageWidth = 200;

		parent = parentIn;
		data = dataIn;
	}

	@Override
	public void buttonEvent(@Nonnull GuiButtonNop button) {
		if (button.id == 0) {
			String name = getTextField(0).getValue().trim();
			if (name.isEmpty()) { return; }
			Preset preset = new Preset();
			preset.name = name;
			preset.data = data.copy();
			PresetController.instance.addPreset(preset);
		}
		onClose();
	}

	@Override
	public void initGui() {
		super.initGui();
		addTextField(0, guiLeft, guiTop + 70, 200, 20, "")
				.setHoverTexts("display.hover.part.name");
		addButton(0, guiLeft, guiTop + 100, "Save")
				.setSize(98, 20)
				.setHoverTexts("hover.save");
		addButton(1, guiLeft + 100, guiTop + 100, "Cancel")
				.setSize(98, 20)
				.setHoverTexts("hover.back");
	}

	public void onClose() {
		super.onClose();
		NoppesUtil.openGUI(player, parent);
	}

}
