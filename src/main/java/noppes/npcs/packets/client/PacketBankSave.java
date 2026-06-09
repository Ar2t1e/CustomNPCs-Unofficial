package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.shared.common.PacketBasic;

public class PacketBankSave extends PacketBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public PacketBankSave() { }

    public PacketBankSave(NBTTagCompound dataIn) { data = dataIn; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

    @Override
    public void decode(FriendlyByteBuf buf) { data = buf.readAnySizeNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        int id = data.getInteger("BankID");
        if (id >= 0) {
            Bank bank = BankController.getInstance().getBank(id);
            if (bank == null) { bank = BankController.getInstance().addNewBank(); }
            bank.load(data);
            CustomNpcs.proxy.getPlayerData(player).bankData.lastBank = null;
        }
        CustomNpcs.debugData.end("Packets");
    }

}