package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.containers.ContainerMerchantAdd;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketMerchantSetSlot extends PacketServerBasic {

    protected static int channelId;
    private final int shopItem;

    public SPacketMerchantSetSlot(int shopItemIn) { shopItem = shopItemIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.EDIT_VILLAGER); }

    public static void encode(SPacketMerchantSetSlot msg, FriendlyByteBuf buf) { buf.writeInt(msg.shopItem); }

    public static SPacketMerchantSetSlot decode(FriendlyByteBuf buf) { return new SPacketMerchantSetSlot(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.containerMenu instanceof ContainerMerchantAdd container) { container.setShopItem(shopItem); }
        CustomNpcs.debugData.end("Packets");
    }

}