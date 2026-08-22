package noppes.npcs.client.gui;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.client.gui.player.GuiMailmanWrite;
import noppes.npcs.client.gui.select.SubGuiQuestSelection;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMailSetup;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiMailmanSendSetup extends GuiBasic implements ITextfieldListener, GuiSelectionListener {

	protected final PlayerMail mail;

	public SubGuiMailmanSendSetup(PlayerMail mailIn) {
		super();
		setBackground("menubg.png");
		imageWidth = 256;

		mail = mailIn;
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 0: onClose(); break;
			case 1: {
				mail.questId = -1;
				mail.message = new NBTTagCompound();
				onClose();
				break;
			}
			case 2: {
				GuiMailmanWrite.parent = getParent();
				GuiMailmanWrite.mail = mail;
				Packets.sendServer(new SPacketMailSetup(mail.save()));
				break;
			}
			case 3: setSubGui(new SubGuiQuestSelection(mail.questId)); break;
			case 4: mail.questId = -1; initGui(); break;
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		int w = 60;
		int x0 = guiLeft + 5;
		int x1 = x0 + w + 2;
		int x2 = guiLeft + 26;
		int y = guiTop + 14;
		int lId = 0;
		// title
		addLabel(lId++, x0, y + 4, "mailbox.subject").setSize(w, 10);
		addTextField(1, x1 + 1, y, 180, 18, mail.title);
		// sender
		addLabel(lId++, x0, (y += 25) + 4, "mailbox.sender").setSize(w, 10);
		addTextField(0, x1 + 1, y, 180, 18, mail.sender);
		// texts
		addLabel(lId++, x0, (y += 55) + 5, "mailbox.setup").setSize(w, 10);
		addButton(2, x1, y, "mailbox.write").setSize(180, 20);
		// next quest
		addLabel(lId, x0, (y += 27) + 5, "quest.quest").setSize(w, 10);
		IQuest quest = mail.getQuest();
		addButton(3, x1, y, quest != null ? quest.getName() : "gui.select").setSize(158, 20);
		addButton(4, x1 + 160, y, "X").setSize(20, 20);
		addButton(0, x2, y = guiTop + 190, "gui.done").setSize(100, 20);
		addButton(1, x2 + 104, y, "gui.cancel").setSize(100, 20);
		if (player.openContainer instanceof ContainerMail) {
			mail.items.clear();
			for (int i =0; i < 4; i++) { mail.items.set(i, ((ContainerMail) player.openContainer).mail.items.get(i)); }
		}
	}

    @Override
	public void selected(int ob, String name) {
		mail.questId = ob;
		initGui();
	}

	@Override
	public void onClose() {
		super.onClose();
		if (player.openContainer instanceof ContainerMail) { player.openContainer = player.inventoryContainer; }
	}

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		switch (textField.id) {
			case 0: mail.sender = textField.getValue(); break;
			case 1: mail.title = textField.getValue(); break;
		}
	}

}
