package noppes.npcs.client.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.TileRedstoneBlock;
import noppes.npcs.client.gui.availability.SubGuiNpcAvailability;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNpcRedstoneBlock extends GuiNPCInterface implements IGuiData, ITextfieldListener {

	protected static final int minRange = 0;
	protected static final int maxRange = 50;
	protected final TileRedstoneBlock tile;

	public GuiNpcRedstoneBlock(BlockPos pos) {
		super();
		tile = (TileRedstoneBlock) player.world.getTileEntity(pos);
		Packets.sendServer(new SPacketTileEntityGet(pos));
	}

	@Override
	public void initGui() {
		super.initGui();
		// options
		addButton(4, guiLeft + 40, guiTop + 20, "availability.options")
				.setSize(120, 20);
		addLabel(11, guiLeft + 40, guiTop + 47, "gui.detailed")
				.setColor(CustomNpcs.MainColor.getRGB());
		addYesNo(1, guiLeft + 110, guiTop + 42, tile.isDetailed)
				.setSize(50, 20);
		if (tile.isDetailed) {
			// x on
			addLabel(0, guiLeft + 1, guiTop + 76, Component.translatable("bard.ondistance").append(" X:"))
					.setColor(CustomNpcs.MainColor.getRGB());
			addTextField(0, guiLeft + 80, guiTop + 71, 30, 20, tile.onRangeX)
					.setMinMaxDefault(minRange, maxRange, 6);
			// y on
			addLabel(1, guiLeft + 113, guiTop + 76, "Y:")
					.setColor(CustomNpcs.MainColor.getRGB());
			addTextField(1, guiLeft + 122, guiTop + 71, 30, 20, tile.onRangeY)
					.setMinMaxDefault(minRange, maxRange, 6);
			// z on
			addLabel(2, guiLeft + 155, guiTop + 76, "Z:")
					.setColor(CustomNpcs.MainColor.getRGB());
			addTextField(2, guiLeft + 164, guiTop + 71, 30, 20, tile.onRangeZ)
					.setMinMaxDefault(minRange, maxRange, 6);
			// x off
			addLabel(3, guiLeft - 3, guiTop + 99, Component.translatable("bard.offdistance").append(" X:"))
					.setColor(CustomNpcs.MainColor.getRGB());
			addTextField(3, guiLeft + 80, guiTop + 94, 30, 20, tile.offRangeX)
					.setMinMaxDefault(minRange, maxRange, 10);
			// z off
			addLabel(4, guiLeft + 113, guiTop + 99, "Y:")
					.setColor(CustomNpcs.MainColor.getRGB());
			addTextField(4, guiLeft + 122, guiTop + 94, 30, 20, tile.offRangeY)
					.setMinMaxDefault(minRange, maxRange, 10);
			// z off
			addLabel(5, guiLeft + 155, guiTop + 99, "Z:")
					.setColor(CustomNpcs.MainColor.getRGB());
			addTextField(5, guiLeft + 164, guiTop + 94, 30, 20, tile.offRangeZ)
					.setMinMaxDefault(minRange, maxRange, 10);
		}
		else {
			// range on
			addLabel(0, guiLeft + 1, guiTop + 76, "bard.ondistance")
					.setColor(CustomNpcs.MainColor.getRGB());
			addTextField(0, guiLeft + 80, guiTop + 71, 30, 20, tile.onRange)
					.setMinMaxDefault(minRange, maxRange, 6);
			// range off
			addLabel(3, guiLeft - 3, guiTop + 99, "bard.offdistance")
					.setColor(CustomNpcs.MainColor.getRGB());
			addTextField(3, guiLeft + 80, guiTop + 94, 30, 20, tile.offRange)
					.setMinMaxDefault(minRange, maxRange, 10);
		}
		addButton(66, guiLeft + 40, guiTop + 190, "gui.done")
				.setSize(120, 20)
				.setHoverTexts("hover.exit");
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 1: tile.isDetailed = button.getValue() == 1; initGui(); break;
			case 4: save(); setSubGui(new SubGuiNpcAvailability(tile.availability, this)); break;
			case 66: onClose(); break;
		}
	}

	@Override
	public void save() {
		if (tile != null) {
			NBTTagCompound compound = new NBTTagCompound();
			tile.writeToNBT(compound);
			compound.removeTag("BlockActivated");
			Packets.sendServer(new SPacketTileEntitySave(compound));
		}
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		tile.readFromNBT(compound);
		initGui();
	}

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		if (tile.isDetailed) {
			switch (textField.id) {
				case 0: tile.onRangeX = textField.getInteger(); break;
				case 1: tile.onRangeY = textField.getInteger(); break;
				case 2: tile.onRangeZ = textField.getInteger(); break;
				case 3: tile.offRangeX = textField.getInteger(); break;
				case 4: tile.offRangeY = textField.getInteger(); break;
				case 5: tile.offRangeZ = textField.getInteger(); break;
			}
			if (tile.onRangeX > tile.offRangeX) { tile.offRangeX = tile.onRangeX; }
			if (tile.onRangeY > tile.offRangeY) { tile.offRangeY = tile.onRangeY; }
			if (tile.onRangeZ > tile.offRangeZ) { tile.offRangeZ = tile.onRangeZ; }
		}
		else {
			switch (textField.id) {
				case 0: tile.onRange = textField.getInteger(); break;
				case 3: tile.offRange = textField.getInteger(); break;
			}
			if (tile.onRange > tile.offRange) { tile.offRange = tile.onRange; }
		}
	}

}
