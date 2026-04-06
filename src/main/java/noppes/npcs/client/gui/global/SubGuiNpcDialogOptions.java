package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.ConfirmScreen;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

import java.util.*;

public class SubGuiNpcDialogOptions
		extends GuiNPCInterface
		implements ICustomScrollListener {

	private final Dialog dialog;
	private final Map<Component, Integer> data = new LinkedHashMap<>(); // {scrollTitle, dialogID}
	private GuiCustomScrollNop scroll;

	// New from Unofficial (BetaZavr)
	public final GuiScreen parent;

	public SubGuiNpcDialogOptions(EntityNPCInterface npcIn, Dialog dialogIn, GuiScreen gui) {
		super(npcIn);
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
		closeOnEsc = true;

		dialog = dialogIn;
		parent = gui;
	}

	public void initGui() {
		super.initGui();
		addLabel(66, guiLeft, guiTop + 4, "dialog.options");
		getLabel(66).setCenter(imageWidth);
		data.clear();
		List<Component> list = new ArrayList<>();
		fix();
		DialogController dData = DialogController.instance;
		LinkedHashMap<Integer, List<Component>> hts = new LinkedHashMap<>();
		for (int id : dialog.options.keySet()) {
			DialogOption option = dialog.options.get(id);
			Component key = Component.empty();
			key.append("ID:" + id + " ").withStyle(TextFormatting.GRAY);
			if (option == null) { continue; }
			switch (option.optionType) {
				case COMMAND_BLOCK: {
					key.append(Component.literal("C").withStyle(TextFormatting.YELLOW));
					List<Component> hovers = new ArrayList<>();
					hovers.add(Component.empty()
							.append(Component.translatable("gui.type"))
							.append(Component.literal(": " + option.optionType.get() + " - "))
							.append(Component.literal(option.optionType.name()).withStyle(TextFormatting.YELLOW)));
					hovers.add(Component.empty()
							.append(Component.translatable("quest.has." + !option.command.isEmpty()))
							.append(Component.literal(": " + option.optionType.get() + " - "))
							.append(Component.literal(!option.command.isEmpty() ? " - \"" + option.command + "\"" : "")));
					hts.put(id, hovers);
					break;
				}
				case DIALOG_OPTION: {
					key.append(Component.literal("D").withStyle(TextFormatting.DARK_AQUA));
					List<Component> hovers = new ArrayList<>();
					hovers.add(Component.empty()
							.append(Component.translatable("gui.type"))
							.append(Component.literal(": " + option.optionType.get() + " - "))
							.append(Component.literal(option.optionType.name()).withStyle(TextFormatting.DARK_AQUA)));
					if (option.hasDialogs()) {
						hovers.add(Component.translatable("availability.selectdialog").append(Component.literal(":")));
						for (DialogOption.OptionDialogID od : option.dialogs) {
							Component hd = Component.literal("ID: " + od.dialogId + " -");
							if (dData.hasDialog(od.dialogId)) {
								hd.append(Component.literal(" \""));
								hd.append(Component.translatable(dData.get(od.dialogId).title));
								hd.append(Component.literal("\" "));
								hd.append(Component.translatable("quest.task.item.0"));
							}
							else {
								hd.append(Component.translatable("quest.task.item.1"));
							}
							hovers.add(hd);
						}
					}
					else { hovers.add(Component.translatable("quest.has.false")); }
					hts.put(id, hovers);
					break;
				}
				case QUIT_OPTION: {
					key.append(Component.literal("E").withStyle(TextFormatting.LIGHT_PURPLE));
					hts.put(id, Collections.singletonList(Component.empty()
							.append(Component.translatable("gui.type"))
							.append(Component.literal(": " + option.optionType.get() + " - "))
							.append(Component.literal(option.optionType.name()).withStyle(TextFormatting.LIGHT_PURPLE))));
					break;
				}
				case ROLE_OPTION: {
					key.append(Component.literal("R").withStyle(TextFormatting.GREEN));
					List<Component> hovers = new ArrayList<>();
					hovers.add(Component.empty()
							.append(Component.translatable("gui.type"))
							.append(Component.literal(": " + option.optionType.get() + " - "))
							.append(Component.literal(option.optionType.name()).withStyle(TextFormatting.GREEN)));

					hovers.add(Component.empty()
							.append(Component.translatable("role.name"))
							.append(Component.literal(" -"))
							.append(Component.translatable("quest.task.item."
									+ (npc != null && npc.role.getEnumType() != RoleType.NONE ? "0" : "1")))
					);
					hts.put(id, hovers);
					break;
				}
				case DISABLED: {
					key.append(Component.literal("N").withStyle(TextFormatting.DARK_RED));
					hts.put(id, Collections.singletonList(Component.empty()
							.append(Component.translatable("gui.type"))
							.append(Component.literal(": " + option.optionType.get() + " - "))
							.append(Component.literal(option.optionType.name()).withStyle(TextFormatting.DARK_RED))));
					break;
				}
			}
			key.append(Component.literal(" - \"").withStyle(TextFormatting.GRAY))
					.append(Component.literal(option.title).withStyle(TextFormatting.RESET))
					.append(Component.literal("\"").withStyle(TextFormatting.GRAY))
					.withColor(option.optionColor);
			data.put(key, id);
			list.add(key);
		}
		if (scroll == null) { scroll = addScroll(0).setSize(248, 154); }
		add(scroll.setPos(guiLeft + 4, guiTop + 14)
				.setUnsortedList(list)
				.setHoverTexts(hts));
		addButton(0, guiLeft + 4, guiTop + 170, "gui.add")
				.setSize(48, 20);
		addButton(1, guiLeft + 54, guiTop + 170, "gui.remove")
				.setSize(48, 20)
				.setIsEnabled(scroll.hasSelected());
		addButton(2, guiLeft + 104, guiTop + 170, "selectServer.edit")
				.setSize(48, 20)
				.setIsEnabled(scroll.hasSelected());
		addButton(3, guiLeft + 154, guiTop + 170, "type.up")
				.setSize(48, 20)
				.setIsEnabled(scroll.hasSelected() && scroll.getSelectedIndex() != 0);
		addButton(4, guiLeft + 204, guiTop + 170, "type.down")
				.setSize(48, 20);
		addButton(66, guiLeft + 82, guiTop + 192, "gui.done")
				.setSize(98, 20);
	}

	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 0: {
				DialogOption option = new DialogOption();
				option.slot = dialog.options.size();
				dialog.options.put(option.slot, option);
				option.optionColor = SubGuiNpcDialogOption.LastColor;
				scroll.setSelect(option.slot);
				setSubGui(new SubGuiNpcDialogOption(option, parent));
				break;
			} // add new
			case 1: {
				if (!data.containsKey(scroll.getNormalSelected())) { return; }
				DialogOption option = dialog.options.get(data.get(scroll.getNormalSelected()));
				ConfirmScreen guiYesNo = new ConfirmScreen((bo) -> {
					if (bo) {
						dialog.options.remove(data.get(scroll.getNormalSelected()));
						initGui();
					}
					if (parent instanceof SubGuiDialogEdit && ((SubGuiDialogEdit) parent).parent != null) { NoppesUtil.openGUI(player, ((SubGuiDialogEdit) parent).parent); }
					else { NoppesUtil.openGUI(player, this); }
				},
						Component.literal("ID:" + option.slot + " - " + option.title),
						Component.translatable("message.delete"));
				setScreen(guiYesNo);
				break;
			} // remove
			case 2: {
				if (!data.containsKey(scroll.getNormalSelected())) { return; }
				DialogOption option = dialog.options.get(data.get(scroll.getNormalSelected()));
				if (option != null) { setSubGui(new SubGuiNpcDialogOption(option, parent)); }
				break;
			} // edit
			case 3: {
				if (!data.containsKey(scroll.getNormalSelected())) { return; }
				dialog.upPos(data.get(scroll.getNormalSelected()));
				scroll.setSelect(scroll.getSelectedIndex() - 1);
				initGui();
				break;
			} // up dialog
			case 4: { // down dialog
				if (!data.containsKey(scroll.getNormalSelected())) { return; }
				dialog.downPos(data.get(scroll.getNormalSelected()));
				scroll.setSelect(scroll.getSelectedIndex() + 1);
				initGui();
				break;
			}
			case 66: {
				onClose();
				break;
			} // back
		}
	}

	// New from Unofficial (BetaZavr)
	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		if (!data.containsKey(scroll.getNormalSelected())) {
			scroll.setSelect(-1);
			return;
		}
		initGui();
	}

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) {
		if (!data.containsKey(scroll.getNormalSelected())) { return; }
		DialogOption option = dialog.options.get(data.get(scroll.getNormalSelected()));
		if (option == null) { return; }
		setSubGui(new SubGuiNpcDialogOption(option, parent));
	}

	@Override
	public void subGuiClosed(GuiScreen subgui) {
		if (parent instanceof SubGuiDialogEdit && ((SubGuiDialogEdit) parent).parent != null) { NoppesUtil.openGUI(player, ((SubGuiDialogEdit) parent).parent); }
		initGui();
	}

	private void fix() {
		Map<Integer, DialogOption> map = new TreeMap<>();
		int i = 0;
		boolean bo = false;
		for (int id : dialog.options.keySet()) {
			if (id != i) { bo = true; }
			DialogOption dlOp = dialog.options.get(id).copy();
			dlOp.slot = i;
			map.put(i, dlOp);
			i++;
		}
		if (bo) {
			dialog.options.clear();
			dialog.options.putAll(map);
		}
	}

}
