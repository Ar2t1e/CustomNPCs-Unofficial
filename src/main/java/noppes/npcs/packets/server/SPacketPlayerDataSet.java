package noppes.npcs.packets.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumPlayerData;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.*;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketPlayerDataSet extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound data;
    private EnumPlayerData type;
    private String name;
    private int id;

    public SPacketPlayerDataSet() { }

    public SPacketPlayerDataSet(EnumPlayerData typeIn, String nameIn, int idIn, NBTTagCompound dataIn) {
        type = typeIn;
        name = nameIn;
        id = idIn;
        data = dataIn == null ? new NBTTagCompound() : dataIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_PLAYERDATA); }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeUtf(name);
        buf.writeInt(id);
        buf.writeNbt(data);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        type = buf.readEnum(EnumPlayerData.class);
        name = buf.readUtf();
        id = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (name != null && !name.isEmpty() && player.getServer() != null) {
            EntityPlayerMP pl = player.getServer().getPlayerList().getPlayerByUsername(name);
            PlayerData playerdata = PlayerDataController.instance.getDataFromUsername(player.getServer(), name);
            if (playerdata != null) {
                switch (type) {
                    case Quest: {
                        if (id < 0) {
                            int newId = -1 * id;
                            Quest q = QuestController.instance.quests.get(newId);
                            if (q != null) {
                                if (playerdata.questData.hasFinishedQuest(newId)) {
                                    playerdata.questData.removeFinishedQuest(newId);
                                    playerdata.questData.activeQuests.put(newId, new QuestData(q));
                                }
                                else {
                                    playerdata.questData.finish(q, player);
                                }
                            }
                        } else {
                            Quest q = QuestController.instance.quests.get(id);
                            if (q != null && !playerdata.questData.hasFinishedQuest(id)) { playerdata.questData.finish(q, player); }
                        }
                        break;
                    }
                    case Dialog: {
                        Dialog d = DialogController.instance.dialogs.get(id);
                        if (d != null && !playerdata.dialogData.has(id)) { playerdata.dialogData.read(id); }
                        break;
                    }
                    case Transport: {
                        TransportLocation l = TransportController.getInstance().getTransport(id);
                        if (l != null) { playerdata.transportData.transports.add(id); }
                        break;
                    }
                    case Bank: {
                        if (playerdata.bankData.lastBank != null && playerdata.bankData.lastBank.bank.id != id) {
                            playerdata.bankData.lastBank.save();
                        }
                        break;
                    }
                    case Factions: {
                        Faction f = FactionController.instance.factions.get(id);
                        if (f != null && !playerdata.factionData.factionData.containsKey(id)) {
                            playerdata.factionData.factionData.put(id, f.defaultPoints);
                        } // add
                        if (data.hasKey("value", 3)) {
                            playerdata.factionData.factionData.put(id, data.getInteger("value"));
                        } // set value
                        break;
                    }
                    case Game: {
                        playerdata.game.load(data);
                        break;
                    }
                    default: { return; }
                }
                playerdata.save(true);
                if (pl != null) { pl.sendMessage(Component.translatable("message.change.mod.data").getParent()); }
            }
            SPacketPlayerDataGet.sendPlayerData(type, player, name);
        }
        CustomNpcs.debugData.end("Packets");
    }
}
