package noppes.npcs.mixin.minecraftforge.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.registries.IRegistryDelegate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ItemModelMesherForge.class, priority = 502, remap = false)
public interface IItemModelMesherForgeMixin {

    @Accessor Map<IRegistryDelegate<Item>, Int2ObjectMap<IBakedModel>> getModels();

}
