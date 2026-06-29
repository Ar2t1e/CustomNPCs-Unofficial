package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.*;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.role.IRoleTransporter;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketChatBubble;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.Util;

import javax.annotation.Nullable;

public class RoleTransporter extends RoleInterface implements IRoleTransporter {

	protected int ticks = 10;
	public int transportId = -1;
	public String name = "";

	public RoleTransporter(EntityNPCInterface npc) {
		super(npc);
		type = RoleType.TRANSPORTER;
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		compound.setInteger("TransporterId", transportId);
		return compound;
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = RoleType.TRANSPORTER;
		transportId = compound.getInteger("TransporterId");
		TransportLocation loc = getLocation();
		if (loc != null) { name = loc.name; } else { name = ""; }
	}

	@Override
	public boolean aiShouldExecute() {
		--ticks;
		if (ticks <= 0) {
			ticks = 10;
			if (hasTransport()) {
				TransportLocation loc = getLocation();
				if (loc != null && loc.type == 0) {
					List<EntityPlayer> inRange = new ArrayList<>();
					try { inRange = npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(6.0, 6.0, 6.0)); }
					catch (Exception ignored) { }
					for (EntityPlayer player : inRange) {
						if (npc.canSee(player)) { unlock(player, loc); }
					}
				}
			}
		}
		return false;
	}

	@Override
	public void interact(EntityPlayer player) {
		if (hasTransport() && player instanceof EntityPlayerMP) {
			TransportLocation loc = getLocation();
			if (loc != null) {
				if (loc.type != 1) { unlock(player, loc); }
				NoppesUtilServer.sendOpenGui((EntityPlayerMP) player, EnumGuiType.PlayerTransporter, npc);
			}
		}
	}

	public void transport(EntityPlayerMP player, int location) {
		TransportLocation loc = TransportController.getInstance().getTransport(location);
		PlayerData playerdata = PlayerData.get(player);
		if (loc != null && (loc.isDefault() || playerdata.transportData.transports.contains(loc.id))) {
			RoleEvent.TransporterUseEvent event = new RoleEvent.TransporterUseEvent(player, npc.wrappedNPC, loc.copy());
			if (!EventHooks.onNPCRole(npc, event) && event.location != null) {
				loc = (TransportLocation) event.location;
				if (!player.isCreative()) {
					if (loc.money > 0) {
						if (loc.money > playerdata.game.getMoney()) {
							player.sendMessage(Component.translatable("transporter.hover.not.money").getParent());
							return;
						}
						playerdata.game.addMoney(-1L * loc.money);
					}
					if (!loc.inventory.isEmpty()) {
						Map<ItemStack, Boolean> barterItems = Util.instance.getInventoryItemCount(player, loc.inventory);
						for (ItemStack stack : barterItems.keySet()) {
							if (!barterItems.get(stack)) {
								player.sendMessage(Component.translatable("transporter.hover.not.money").getParent());
								return;
							}
						}
						for (ItemStack stack : barterItems.keySet()) {
							int amount = stack.getCount();
							for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
								ItemStack is = player.inventory.getStackInSlot(i);
								if (isItemEqual(stack, is)) {
									if (amount < is.getCount()) {
										is.splitStack(amount);
										break;
									}
									player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
									amount -= is.getCount();
								}
							}
						}
						player.inventoryContainer.detectAndSendChanges();
						CustomNPCsScheduler.runTack(() -> {
							for (QuestData data : playerdata.questData.activeQuests.values()) {
								for (IQuestObjective obj : data.quest
										.getObjectives((IPlayer<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(player))) {
									if (obj.getType() == 0) { playerdata.questData.checkQuestCompletion(player, data); }
								}
							}
						});
					}
				}
				npc.say(player, new Line(Component.translatable("transporter.go.way").getFormattedText()));
				SPacketDimensionTeleport.teleportPlayer(player, loc.dimension, loc.pos.getX(), loc.pos.getY(), loc.pos.getZ(),
						loc.yaw, loc.pitch);
			}
		}
	}

	private void unlock(EntityPlayer player, TransportLocation loc) {
		PlayerTransportData data = CustomNpcs.proxy.getPlayerData(player).transportData;
		if (!data.transports.contains(transportId) && player instanceof EntityPlayerMP) {
			RoleEvent.TransporterUnlockedEvent event = new RoleEvent.TransporterUnlockedEvent(player, npc.wrappedNPC);
			if (!EventHooks.onNPCRole(npc, event)) {
				data.transports.add(transportId);
				Packets.send((EntityPlayerMP) player, new PacketChatBubble(npc.getEntityId(), Component.translatable("transporter.unlock",
						Component.translatable(loc.name).getFormattedText(),
						Component.translatable(loc.category.title).getFormattedText()), true));
			}
		}

		if (data.transports.contains(transportId)) { return; }
		RoleEvent.TransporterUnlockedEvent event = new RoleEvent.TransporterUnlockedEvent(player, npc.wrappedNPC);
		if (EventHooks.onNPCRole(npc, event)) { return; }
		data.transports.add(transportId);
		player.sendMessage(Component.translatable("transporter.unlock",
				Component.translatable(loc.name).getFormattedText(),
				Component.translatable(loc.category.title).getFormattedText())
				.getParent());
	}

	@Override
	public @Nullable TransportLocation getLocation() {
		return TransportController.getInstance().getTransport(transportId);
	}

	public boolean hasTransport() {
		TransportLocation loc = getLocation();
		return loc != null && loc.id == transportId;
	}

	public void setTransport(TransportLocation location) {
		transportId = location.id;
		name = location.name;
		location.npc = npc.getUniqueID();
	}

	// New from Unofficial (BetaZavr)
	private boolean isItemEqual(ItemStack stack, ItemStack other) {
		return !other.isEmpty() && stack.getItem() == other.getItem()
				&& (stack.getItemDamage() < 0 || stack.getItemDamage() == other.getItemDamage());
	}

}
