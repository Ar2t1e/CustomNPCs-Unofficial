package noppes.npcs.api.entity.data;

import noppes.npcs.api.IContainer;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.handler.data.IQuest;

import java.util.List;

public interface IPlayerMail {

	IContainer getContainer();

	IQuest getQuest();

	String getSender();

	String getSubject();

	List<String> getText();

	void setQuest(@ParamName("id") int id);

	void setSender(@ParamName("sender") String sender);

	void setSubject(@ParamName("subject") String subject);

	void setText(@ParamName("text") String ... text);

	// New from Unofficial (BetaZavr)
	int getMoney();

	int getRansom();

	void setMoney(@ParamName("money") int money);

	void setRansom(@ParamName("money") int money);

}
