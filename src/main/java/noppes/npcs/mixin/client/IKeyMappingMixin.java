package noppes.npcs.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyMappingLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(value = KeyMapping.class, priority = 502)
public interface IKeyMappingMixin {

    @Accessor String getName();

    @Accessor InputConstants.Key getKey();

    @Accessor void setKey(InputConstants.Key newKey);

    @Accessor("ALL") Map<String, KeyMapping> getAll();

    @Accessor("MAP") KeyMappingLookup getMap();

    @Accessor("CATEGORIES") Set<String> getCategories();

    @Accessor void setCategory(String newCategory);

    @Accessor void setName(String newName);

    @Accessor void setDefaultKey(InputConstants.Key newKey);

}
