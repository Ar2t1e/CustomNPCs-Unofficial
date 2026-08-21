package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.data.IPlayerData;

public class PlayerMailData implements IPlayerData {

	protected static final String dataName = "MailData";

	public final List<PlayerMail> playerMails = new ArrayList<>();

	@Override
	public void load(NBTTagCompound compound) {
		NBTTagList list = compound.getTagList(dataName, 10);
		playerMails.clear();
		for (int i = 0; i < list.tagCount(); ++i) {
			PlayerMail mail = new PlayerMail();
			mail.load(list.getCompoundTagAt(i));
			playerMails.add(mail);
		}
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (PlayerMail mail : playerMails) { list.appendTag(mail.save()); }
		compound.setTag(dataName, list);
		return compound;
	}

	public boolean hasMail() {
		for (PlayerMail mail : playerMails) {
			if (!mail.beenRead) { return true; }
		}
		return false;
	}

	// New from Unofficial (BetaZavr)
	public void clear() { playerMails.clear(); }

	public void addMail(PlayerMail mail) {
		mail = mail.copy();
		mail.timeWhenReceived = System.currentTimeMillis();
		if (mail.timeWhenReceived <= 0L) {
			mail.timeWhenReceived = 100000L;
		}
		mail.timeWillCome = mail.timeWhenReceived + 1000L * ((long) CustomNpcs.MailTimeWhenLettersWillBeReceived[0]
				+ (long) (Math.random() * (double) (CustomNpcs.MailTimeWhenLettersWillBeReceived[1]
				- CustomNpcs.MailTimeWhenLettersWillBeReceived[0])));
		boolean found = true;
		while (found) {
			found = false;
			for (PlayerMail m : playerMails) {
				if (m.timeWhenReceived == mail.timeWhenReceived) {
					mail.timeWhenReceived--;
					found = true;
					break;
				}
			}
		}
		playerMails.add(mail);
	}

	public PlayerMail get(long id) {
		for (PlayerMail mail : playerMails) {
			if (mail.timeWhenReceived == id) {
				return mail;
			}
		}
		return null;
	}

	public PlayerMail get(PlayerMail selected) {
		for (PlayerMail mail : playerMails) {
			if (mail.timeWhenReceived == selected.timeWhenReceived && mail.getSubject().equals(selected.getSubject())) {
				return mail;
			}
		}
		return null;
	}

}
