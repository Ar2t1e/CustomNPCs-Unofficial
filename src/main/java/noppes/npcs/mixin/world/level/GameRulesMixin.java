package noppes.npcs.mixin.world.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.GameRules;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.custom.CustomBlockLiquid;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ModData;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(value = GameRules.class, priority = 498)
public class GameRulesMixin {

    @Inject(method = {"register"}, at = {@At("TAIL")})
    private static <T extends GameRules.Value<T>> void registerAddFluids(String key, GameRules.Category category, GameRules.Type<T> rules, CallbackInfoReturnable<GameRules.Key<T>> cir) {
        if (key.equals("waterSourceConversion") && category == GameRules.Category.UPDATES) {
            File blocksFile = new File(CustomNpcs.Dir, "custom_blocks.js");
            CompoundTag nbtBlocks = new CompoundTag();
            try { if (blocksFile.exists()) { nbtBlocks = NBTJsonUtil.LoadFile(blocksFile); } }
            catch (Exception e) { LogWriter.error("Try Load custom_blocks.js: ", e); }
            boolean hEL = false;
            if (nbtBlocks.contains("Blocks", 9)) {
                for (int i = 0; i < nbtBlocks.getList("Blocks", 10).size(); i++) {
                    String name = nbtBlocks.getList("Blocks", 10).getCompound(i).getString("RegistryName");
                    if (name.equals("liquidexample")) {
                        hEL = true;
                        break;
                    }
                }
            }
            if (!blocksFile.exists() || !nbtBlocks.contains("Blocks", 9) || !hEL) {
                if (!nbtBlocks.contains("Blocks", 9)) { nbtBlocks.put("Blocks", new ListTag()); }
                if (!hEL) {
                    CompoundTag nbt = ModData.getExampleBlocks();
                    for (int i = 0; i < nbt.getList("Blocks", 10).size(); i++) {
                        String name = nbt.getList("Blocks", 10).getCompound(i).getString("RegistryName");
                        if (name.equals("liquidexample")) {
                            nbtBlocks.getList("Blocks", 10).add(nbt.getList("Blocks", 10).getCompound(i));
                        }
                    }
                }
                try {
                    Util.instance.saveFile(blocksFile, nbtBlocks);
                } catch (Exception e) { LogWriter.error(e); }
            }
            for (int i = 0; i < nbtBlocks.getList("Blocks", 10).size(); i++) {
                CompoundTag nbtBlock = nbtBlocks.getList("Blocks", 10).getCompound(i);
                // Simple
                if (nbtBlock.getByte("BlockType") == (byte) 1 &&
                        nbtBlock.contains("HasInGameRules", 1) && nbtBlock.getBoolean("HasInGameRules")) {
                    String name = "custom_fluid_" + nbtBlock.getString("RegistryName") + "SourceConversion";
                    CustomBlockLiquid.gameRules.put(nbtBlock.getString("RegistryName"),
                            GameRules.register(name, GameRules.Category.UPDATES, GameRules.BooleanValue.create(true)));
                    LogWriter.info("Add Fluid GameRules \"" + name + "\"");
                }
            }
        }

    }

}
