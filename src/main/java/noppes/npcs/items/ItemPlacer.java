package noppes.npcs.items;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.api.item.ISpecBuilder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiOpen;
import noppes.npcs.packets.server.SPacketGetBuildData;
import noppes.npcs.util.BuilderData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemPlacer extends Item implements INPCToolItem, ISpecBuilder {

    public ItemPlacer() { super((new Item.Properties()).stacksTo(1)); }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> list, @Nonnull TooltipFlag flagIn) {
        BuilderData builder = ItemBuilder.getBuilder(stack, null);
        list.add(Component.translatable("info.item.builder.main.0"));
        list.add(Component.translatable("info.item.builder.main.1"));
        if (builder != null) {
            list.add(Component.translatable("info.item.placer"));
            for (int i = 4; i <= 5; i++) {
                list.add(Component.translatable("info.item.builder.main." + i));
            }
            list.add(Component.translatable("info.item.builder.range.1", "" + builder.region[0], "" + builder.region[1], "" + builder.region[2]));
        } else {
            list.add(Component.translatable("info.item.builder.main.2"));
            CompoundTag tags = stack.getTag();
            if (tags != null && tags.contains("ID", 3) && tags.contains("BuilderType", 3)) {
                Packets.sendServerDelayed(new SPacketGetBuildData(stack.getTag().getInt("ID"), stack.getTag().getInt("BuilderType")), stack, 2000);
            }
        }
    }

    @Override
    public void leftClick(ItemStack stack, ServerPlayer player, BlockPos pos) {
        if (pos == null) { return; }
        PlayerData data = PlayerData.get(player);
        BuilderData builder = ItemBuilder.getBuilder(stack, player);
        if (data == null || !stack.hasTag() || builder == null || builder.getID() == -1) {
            Packets.send(player, new PacketGuiOpen(getGUIType(), new BlockPos(-1, getType(), 0)));
            return;
        }
        if (data.overlay.isPressedCtrl()) { builder.undo(); }
    }

    @Override
    public void rightClick(ItemStack stack, ServerPlayer player, BlockPos pos) {
        if (pos == null) { return; }
        PlayerData data = PlayerData.get(player);
        BuilderData builder = ItemBuilder.getBuilder(stack, player);
        if (data == null || !stack.hasTag() || builder == null || builder.getID() == -1) {
            Packets.send(player, new PacketGuiOpen(getGUIType(), new BlockPos(-1, getType(), 0)));
            return;
        }
        if (data.overlay.isPressedCtrl()) {
            builder.redo();
            return;
        }
        builder.work(pos, player);
    }

    @Override
    public int getType() { return 3; }

    @Override
    public EnumGuiType getGUIType() { return EnumGuiType.PlacerTool; }

}
