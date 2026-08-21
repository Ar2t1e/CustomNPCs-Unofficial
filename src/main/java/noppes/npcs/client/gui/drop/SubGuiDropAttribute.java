package noppes.npcs.client.gui.drop;

import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.data.AttributeSet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import javax.annotation.Nonnull;

public class SubGuiDropAttribute extends GuiNPCInterface implements ITextfieldListener {

	protected double[] values;
	public AttributeSet attribute;

	public SubGuiDropAttribute(AttributeSet attributeIn) {
		super();
		setBackground("companion_empty.png");
		closeOnEsc = true;
		imageWidth = 172;
		imageHeight = 167;

		attribute = attributeIn;
	}

	@Override
	public void buttonEvent(@Nonnull GuiButtonNop button) {
		switch (button.id) {
			case 70: attribute.setSlot(button.getValue() - 1); initGui(); break;
			case 66: onClose(); break;
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		int lId = 80;
		// name
		addTextField(72, guiLeft + 4, guiTop + 5, 163, 20, attribute.getAttribute())
				.setHoverTexts("drop.hover.attribute.name", Component.translatable("attribute.name." + attribute.getAttribute()).getFormattedText());
		// values
		values = new double[] { attribute.getMinValue(), attribute.getMaxValue() };
		addLabel(lId++, guiLeft + 56, guiTop + 36, "type.value");
		String tied = Component.translatable("drop.tied.random").getFormattedText();
		if (attribute.parent.tiedToLevel) { tied = Component.translatable("drop.tied.level").getFormattedText(); }
		// min
		addTextField(73, guiLeft + 4, guiTop + 27, 50, 14, "" + values[0])
				.setMinMaxDefault(-4096.0d, 4096.0d, attribute.getMinValue())
				.setHoverTexts("drop.hover.attribute.values", tied);
		// max
		addTextField(74, guiLeft + 4, guiTop + 41, 50, 14, "" + values[1])
				.setMinMaxDefault(-4096.0d, 4096.0d, attribute.getMaxValue())
				.setHoverTexts("drop.hover.attribute.values", tied);
		// slot
		Object[] slots = new String[7];
		for (int i = 0; i < 7; i++) { slots[i] = "attribute.slot." + i; }
		addButton(70, guiLeft + 4, guiTop + 57, false, attribute.slot + 1, slots)
				.setSize(87, 20)
				.setHoverTexts("drop.hover.attribute.slot");
		// chance
		addLabel(lId, guiLeft + 56, guiTop + 84, "drop.chance");
		addTextField(75, guiLeft + 4, guiTop + 79, 50, 20, String.valueOf(attribute.getChance()))
				.setMinMaxDefault(0.0001d, 100.0d, attribute.getChance())
				.setHoverTexts("drop.hover.attribute.chance");
		// done
		addButton(66, guiLeft + 4, guiTop + 142, "gui.done")
				.setSize(80, 20)
				.setIsEnabled(check())
				.setHoverTexts("hover.back");
	}

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		switch (textField.id) {
			case 72: attribute.setAttribute(textField.getValue()); initGui(); break; // name
			case 73: values[0] = textField.getDouble(); attribute.setValues(values[0], values[1]); break; // value min
			case 74: values[1] = textField.getDouble(); attribute.setValues(values[0], values[1]); break; // value max
			case 75: attribute.setChance(textField.getDouble()); initGui(); break; // chance
		}
	}

	private boolean check() {
		return getTextField(72) != null && !getTextField(72).getValue().isEmpty();
	}

}
