package noppes.npcs.client.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.blocks.tiles.TileBorder;
import noppes.npcs.client.gui.availability.SubGuiNpcAvailability;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import java.awt.*;

public class GuiBorderBlock extends GuiNPCInterface
		implements IGuiData, ITextfieldListener {

	private final TileBorder tile;

	public GuiBorderBlock(BlockPos pos) {
		super();

		tile = (TileBorder) player.world.getTileEntity(pos);
		Packets.sendServer(new SPacketTileEntityGet(pos));
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = guiLeft + 60;
		int xl = guiLeft + 1;
		int y = guiTop + 40;
		// availability
		addButton(4, x - 20, y, "availability.available")
				.setSize(120, 20)
				.setHoverTexts("border.hover.availability");
		int color = new Color(0xFFFFFF).getRGB();
		// height
		addLabel(0, xl, (y += 25) + 5, "schematic.height").setColor(color);
		addTextField(0, x, y, 40, 20, tile.height + "")
				.setMinMaxDefault(0, 500, 6);
		// message
		addLabel(1, xl, (y += 24) + 5, "gui.message").setColor(color);
		Component hover = Component.translatable("border.hover.message");
		Component mes = Component.translatable(tile.message);
		if (!tile.message.equals(mes.getString())) {
			hover.append("<br>");
			hover.append(Component.translatable("gui.translation", mes.getString()));
		}
		addTextField(1, x, y, 200, 20, tile.message)
				.setHoverTexts(hover);
		// gm type
		addLabel(2, xl, (y += 24) + 5, "gui.creative").setColor(color);
		addYesNo(5, x - 1, y, tile.creative)
				.setSize(60, 20)
				.setHoverTexts("border.hover.creative");
		// exit
		addButton(66, x - 20, guiTop + 188, "gui.done")
				.setSize(120, 20)
				.setHoverTexts("hover.exit");
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 4: save(); setSubGui(new SubGuiNpcAvailability(tile.availability, this)); break;
			case 5: tile.creative = ((GuiButtonYesNo) button).getBoolean(); break;
			case 66: onClose(); break;
		}
	}

	@Override
	public void save() {
		if (tile != null) { Packets.sendServer(new SPacketTileEntitySave(tile.writeToNBT(new NBTTagCompound()))); }
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		if (tile != null) { tile.readFromNBT(compound); }
		initGui();
	}

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		if (tile != null) {
			switch (textField.id) {
				case 0: tile.height = textField.getInteger(); break;
				case 1: tile.message = textField.getValue(); break;
			}
		}
	}

}
