package noppes.npcs.mixin.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.PlayMessages.OpenContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = OpenContainer.class, priority = 502)
public interface IMixinOpenContainer {

    @Invoker(
            value = "<init>",
            remap = false
    )
    static OpenContainer OpenContainer(MenuType<?> id, int windowId, Component name, FriendlyByteBuf additionalData) { return null; }

}
