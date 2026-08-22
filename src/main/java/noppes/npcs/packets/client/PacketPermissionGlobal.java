package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPermissionGlobal extends PacketBasic {

    protected static int channelId;
    public boolean admin;
    public boolean banks;
    public boolean factions;
    public boolean dialogs;
    public boolean quests;
    public boolean transports;
    public boolean players_data;
    public boolean recipes;
    public boolean natural_spawns;
    public boolean linkeds;
    public boolean markets;
    public boolean auctions;
    public boolean mails;
    public boolean elements;
    public boolean dungeons;
    public boolean permissions;

    public PacketPermissionGlobal() {}

    public PacketPermissionGlobal(boolean adminIn, boolean banksIn, boolean factionsIn, boolean dialogsIn, boolean questsIn, boolean transportsIn,
                                  boolean players_dataIn, boolean recipesIn, boolean natural_spawnsIn, boolean linkedsIn, boolean marketsIn,
                                  boolean auctionsIn, boolean mailsIn, boolean elementsIn, boolean dungeonsIn, boolean permissionsIn) {
        admin = adminIn;
        banks = banksIn;
        factions = factionsIn;
        dialogs = dialogsIn;
        quests = questsIn;
        transports = transportsIn;
        players_data = players_dataIn;
        recipes = recipesIn;
        natural_spawns = natural_spawnsIn;
        linkeds = linkedsIn;
        markets = marketsIn;
        auctions = auctionsIn;
        mails = mailsIn;
        elements = elementsIn;
        dungeons = dungeonsIn;
        permissions = permissionsIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        admin = buf.readBoolean();
        banks = buf.readBoolean();
        factions = buf.readBoolean();
        dialogs = buf.readBoolean();
        quests = buf.readBoolean();
        transports = buf.readBoolean();
        players_data = buf.readBoolean();
        recipes = buf.readBoolean();
        natural_spawns = buf.readBoolean();
        linkeds = buf.readBoolean();
        markets = buf.readBoolean();
        auctions = buf.readBoolean();
        mails = buf.readBoolean();
        elements = buf.readBoolean();
        dungeons = buf.readBoolean();
        permissions = buf.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(admin);
        buf.writeBoolean(banks);
        buf.writeBoolean(factions);
        buf.writeBoolean(dialogs);
        buf.writeBoolean(quests);
        buf.writeBoolean(transports);
        buf.writeBoolean(players_data);
        buf.writeBoolean(recipes);
        buf.writeBoolean(natural_spawns);
        buf.writeBoolean(linkeds);
        buf.writeBoolean(markets);
        buf.writeBoolean(auctions);
        buf.writeBoolean(mails);
        buf.writeBoolean(elements);
        buf.writeBoolean(dungeons);
        buf.writeBoolean(permissions);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}