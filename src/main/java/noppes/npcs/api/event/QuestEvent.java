package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.data.FactionOptions;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.Quest;

import java.util.List;

public class QuestEvent extends CustomNPCsEvent {

	@Cancelable
	@EventName(EnumScriptType.QUEST_CANCELED)
	public static class QuestCanceledEvent extends QuestEvent {
		public QuestCanceledEvent(IPlayer<?> player, IQuest quest) {
			super(player, quest);
		}
	}

	@EventName(EnumScriptType.QUEST_COMPLETED)
	public static class QuestCompletedEvent extends QuestEvent {
		public QuestCompletedEvent(IPlayer<?> player, IQuest quest) {
			super(player, quest);
		}
	}

	@EventName(EnumScriptType.QUEST_LOG_BUTTON)
	public static class QuestExtraButtonEvent extends QuestEvent {
		public QuestExtraButtonEvent(IPlayer<?> player, IQuest quest) {
			super(player, quest);
		}
	}

	@Cancelable
	@EventName(EnumScriptType.QUEST_START)
	public static class QuestStartEvent extends QuestEvent {
		public QuestStartEvent(IPlayer<?> player, IQuest quest) {
			super(player, quest);
		}
	}

	@EventName(EnumScriptType.QUEST_TURNING)
	public static class QuestTurnedInEvent extends QuestEvent {

		public int expReward;
		public int moneyReward;
		public List<IItemStack> itemRewards;
		public FactionOptions factionOptions;
		public PlayerMail mail;
		public int nextQuestId;
		public String command;

		public QuestTurnedInEvent(IPlayer<?> player, IQuest quest, List<IItemStack> itemRewardsIn) {
			super(player, quest);
			itemRewards = itemRewardsIn;
			factionOptions = ((Quest) quest).factionOptions.copy();
			mail = ((Quest) quest).mail.copy();
			nextQuestId = ((Quest) quest).nextQuestId;
			command = ((Quest) quest).command;
		}
	}

	public IPlayer<?> player;

	public IQuest quest;

	public QuestEvent(IPlayer<?> player, IQuest quest) {
		this.quest = quest;
		this.player = player;
	}

}
