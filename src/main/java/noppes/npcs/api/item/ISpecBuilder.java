package noppes.npcs.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.util.BuilderData;

public interface ISpecBuilder {

    void leftClick(@ParamName("stack") ItemStack stack, @ParamName("player") ServerPlayer player, @ParamName("pos") BlockPos pos);

    void rightClick(@ParamName("stack") ItemStack stack, @ParamName("player") ServerPlayer player, @ParamName("pos") BlockPos pos);

    int getType();

    EnumGuiType getGUIType();

}
