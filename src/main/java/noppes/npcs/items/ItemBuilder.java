package noppes.npcs.items;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.api.item.ISpecBuilder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.packets.server.SPacketGetBuildData;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.BuilderData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemBuilder extends Item implements INPCToolItem, ISpecBuilder {

    public ItemBuilder() { super((new Item.Properties()).stacksTo(1)); }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> list, @Nonnull TooltipFlag flagIn) {
        BuilderData builder = ItemBuilder.getBuilder(stack, null);
        list.add(Component.translatable("info.item.builder.main.0"));
        list.add(Component.translatable("info.item.builder.main.1"));
        if (builder != null) {
            list.add(Component.translatable("info.item.builder"));
            for (int i = 3; i <= 5; i++) {
                list.add(Component.translatable("info.item.builder.main." + i));
            }
            list.add(Component.translatable("info.item.builder.range.0", "" + builder.region[0], "" + builder.region[1], "" + builder.region[2]));
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
            NoppesUtilServer.openContainerGui(player, getGUIType(), (buffer) -> {
                buffer.writeInt(-1);
                buffer.writeBlockPos(new BlockPos(-1, getType(), 0));
            });
            return;
        }
        if (data.overlay.isPressedCtrl()) {
            builder.undo();
            return;
        }
        BlockState state = player.level().getBlockState(pos);
        ItemStack st = new ItemStack(state.getBlock(), 1);
        if (builder.inv.getCountEmpty() == 0 || st.isEmpty()) { return; }
        BlockEntity tile = player.level().getBlockEntity(pos);
        if (tile != null) { st.setTag(tile.saveWithFullMetadata()); }
        String name = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(st.getItem())) +
                (st.getDamageValue() != 0 ? " [" + st.getDamageValue() + "]" : "");
        int emp = 0;
        for (int i = 1; i < 10; i++) {
            ItemStack stInv = builder.inv.getItem(i);
            if (emp == 0 && stInv.isEmpty()) {
                emp = i;
            }
            if (!stInv.isEmpty() && ItemStack.isSameItem(stInv, st)) {
                player.sendSystemMessage(Component.translatable("builder.err.add.block.0"));
                return;
            }
        }
        if (emp == 0) {
            player.sendSystemMessage(Component.translatable("builder.err.add.block.1"));
            return;
        }
        builder.inv.setItem(emp, st);
        builder.chances.put(emp, 100);
        player.sendSystemMessage(Component.translatable("builder.add.block", name));
        CompoundTag nbtStack = builder.getNbt();
        stack.setTag(nbtStack);
        player.containerMenu.broadcastChanges();
        Packets.send(player, new PacketSyncUpdate(builder.getID(), 7, nbtStack));
    }

    @Override
    public void rightClick(ItemStack stack, ServerPlayer player, BlockPos pos) {
        if (pos == null) { return; }
        PlayerData data = PlayerData.get(player);
        BuilderData builder = ItemBuilder.getBuilder(stack, player);
        if (data == null || !stack.hasTag() || builder == null || builder.getID() == -1) {
            NoppesUtilServer.openContainerGui(player, getGUIType(), (buffer) -> {
                buffer.writeInt(-1);
                buffer.writeBlockPos(new BlockPos(-1, getType(), 0));
            });
            return;
        }
        if (data.overlay.isPressedCtrl()) {
            builder.redo();
            return;
        }
        builder.work(pos, player);
    }

    @Override
    public int getType() { return 1; }

    public static BuilderData getBuilder(ItemStack stack, Player player) {
        if (stack.getItem() instanceof ISpecBuilder && stack.hasTag() && stack.getTag() != null && stack.getTag().contains("ID", 3)) {
            int id = stack.getTag().getInt("ID");
            BuilderData builder = SyncController.dataBuilder.get(id);
            if (builder == null) {
                builder = new BuilderData(id, ((ISpecBuilder) stack.getItem()).getType());
                builder.read(stack.getTag());
                SyncController.dataBuilder.put(id, builder);
                if (player instanceof ServerPlayer sPlayer) {
                    Packets.send(sPlayer, new PacketSyncUpdate(id , 7, builder.getNbt()));
                }
            }
            return builder;
        }
        return null;
    }

    public static boolean isBuilder(ItemStack stack, BuilderData bd) {
        if (stack.getItem() instanceof ISpecBuilder && stack.hasTag() && stack.getTag() != null && stack.getTag().contains("ID", 3)) {
            BuilderData baseBD = SyncController.dataBuilder.get(stack.getTag().getInt("ID"));
            return baseBD != null && baseBD.getID() == bd.getID() && baseBD.getType() == bd.getType();
        }
        return false;
    }

    @Override
    public EnumGuiType getGUIType() { return EnumGuiType.BuilderTool; }

}
