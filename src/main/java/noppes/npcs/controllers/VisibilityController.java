package noppes.npcs.controllers;

import java.util.*;

import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;

public class VisibilityController {

	public static VisibilityController instance = new VisibilityController();
	private final Map<Integer, EntityNPCInterface> trackedEntityHashTable = new TreeMap<>();

	public static void checkIsVisible(EntityNPCInterface npc, EntityPlayerMP playerMP) {
		if (CustomNpcs.EnableInvisibleNpcs && CustomNpcs.InvisibilityAlgorithm == 0) {
			boolean bo = playerMP.getHeldItemMainhand().getItem() != CustomItems.wand && playerMP.getHeldItemOffhand().getItem() != CustomItems.wand;
			if (!npc.display.isVisibleTo(playerMP) && !playerMP.isSpectator() && bo) { npc.setInvisible(playerMP); }
			else { npc.setVisible(playerMP); }
		}
	}

	public void trackNpc(EntityNPCInterface npc) {
		if (!npc.world.isRemote) {
			boolean hasOptions = npc.display.getAvailability().hasOptions();
			if ((hasOptions || npc.display.getVisible() != 0) && !trackedEntityHashTable.containsKey(npc.getEntityId())) { trackedEntityHashTable.put(npc.getEntityId(), npc); }
			if (!hasOptions && npc.display.getVisible() == 0) { trackedEntityHashTable.remove(npc.getEntityId()); }
		}
	}

	public void remove(EntityNPCInterface npc) {
		if (!npc.world.isRemote) { trackedEntityHashTable.remove(npc.getEntityId()); }
	}

	public void onUpdate(EntityPlayerMP player) {
		if (CustomNpcs.EnableInvisibleNpcs) {
			for (Map.Entry<Integer, EntityNPCInterface> entry : trackedEntityHashTable.entrySet()) { checkIsVisible(entry.getValue(), player); }
		}
	}

}
