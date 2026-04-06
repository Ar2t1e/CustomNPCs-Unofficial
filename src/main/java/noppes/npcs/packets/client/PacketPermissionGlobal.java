package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.mainmenu.GuiNpcGlobalMainMenu;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPermissionGlobal extends PacketBasic {

    protected static int channelId;
    private boolean banks;
    private boolean factions;
    private boolean dialogs;
    private boolean quests;
    private boolean transports;
    private boolean players_data;
    private boolean recipes;
    private boolean natural_spawns;
    private boolean linkeds;
    private boolean markets;
    private boolean auctions;
    private boolean mails;

    public PacketPermissionGlobal() {}

    public PacketPermissionGlobal(boolean banksIn, boolean factionsIn, boolean dialogsIn, boolean questsIn, boolean transportsIn,
                                  boolean players_dataIn, boolean recipesIn, boolean natural_spawnsIn, boolean linkedsIn, boolean marketsIn,
                                  boolean auctionsIn, boolean mailsIn) {
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
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
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
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
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
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (Minecraft.getMinecraft().currentScreen instanceof GuiNpcGlobalMainMenu) {
            ((GuiNpcGlobalMainMenu) Minecraft.getMinecraft().currentScreen).setMenuData(banks, factions, dialogs, quests, transports, players_data, recipes, natural_spawns, linkeds, markets, auctions, mails);
        }
        CustomNpcs.debugData.end("Packets");
    }

}