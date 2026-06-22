package noppes.npcs;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.registries.DeferredRegister;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.fluids.CustomFluid;
import noppes.npcs.potions.CustomPotion;

import java.util.Map;

public class CustomTabs {

    public static final CreativeModeTab TOOLS = CreativeModeTab.builder()
            .title(Component.translatable("tab.cnpcs.tools"))
            .icon(() -> CustomItems.wand.getDefaultInstance())
            .displayItems((params, output) -> {
                output.accept(CustomItems.wand.getDefaultInstance());
                output.accept(CustomItems.cloner.getDefaultInstance());
                output.accept(CustomItems.scripter.getDefaultInstance());
                output.accept(CustomItems.moving.getDefaultInstance());
                output.accept(CustomItems.mount.getDefaultInstance());
                output.accept(CustomItems.teleporter.getDefaultInstance());
                output.accept(CustomItems.scripted_item.getDefaultInstance());
                output.accept(CustomItems.nbt_book.getDefaultInstance());
                // New from Unofficial (BetaZavr)
                output.accept(CustomItems.npcboundary.getDefaultInstance());
                output.accept(CustomItems.npcbuilder.getDefaultInstance());
                output.accept(CustomItems.npcremover.getDefaultInstance());
                output.accept(CustomItems.npcplacer.getDefaultInstance());
                output.accept(CustomItems.npcreplacer.getDefaultInstance());
                output.accept(CustomItems.npcsaver.getDefaultInstance());
                // next normal
                output.accept(CustomItems.soulstoneEmpty.getDefaultInstance());
                output.accept(CustomBlocks.redstone_item.getDefaultInstance());
                output.accept(CustomBlocks.waypoint_item.getDefaultInstance());
                output.accept(CustomBlocks.border_item.getDefaultInstance());
                output.accept(CustomBlocks.scripted_item.getDefaultInstance());
                output.accept(CustomBlocks.scripted_door_item.getDefaultInstance());
                output.accept(CustomBlocks.builder_item.getDefaultInstance());
                output.accept(CustomBlocks.copy_item.getDefaultInstance());
                output.accept(CustomBlocks.carpenty_item.getDefaultInstance());
                output.accept(CustomBlocks.mailbox_item.getDefaultInstance());
                output.accept(CustomBlocks.mailbox2_item.getDefaultInstance());
                output.accept(CustomBlocks.mailbox3_item.getDefaultInstance());
            })
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .build();
    public static final CreativeModeTab BLOCKS = CreativeModeTab.builder()
            .title(Component.translatable("tab.cnpcs.blocks"))
            .icon(() -> {
                for (Map.Entry<ICustomElement, Item> entry : CustomBlocks.customblocks.entrySet()) {
                    if (entry.getKey().getCustomName().equals("blockexample")) { return entry.getValue().getDefaultInstance(); }
                }
                return CustomBlocks.scripted_item.getDefaultInstance();
            })
            .displayItems((params, output) -> {
                for (Map.Entry<ICustomElement, Item> entry : CustomBlocks.customblocks.entrySet()) {
                    if (entry.getKey().showInCreative()) { output.accept(entry.getValue().getDefaultInstance()); }
                }
                for (ICustomElement element : CustomBlocks.customfluid.values()) {
                    if (element.showInCreative() && element instanceof CustomFluid fluid && fluid.getBucket() instanceof BucketItem bucket) {
                        output.accept(bucket);
                    }
                }
            })
            .withTabsBefore(new ResourceLocation(CustomNpcs.MODID, "cnpcs_tools"))
            .build();
    public static final CreativeModeTab ITEMS = CreativeModeTab.builder()
            .title(Component.translatable("tab.cnpcs.items"))
            .icon(() -> {
                for (ICustomElement element : CustomItems.customitems) {
                    if (element.getCustomName().equals("itemexample") && element instanceof Item item) { return item.getDefaultInstance(); }
                }
                return CustomItems.scripted_item.getDefaultInstance();
            })
            .displayItems((params, output) -> {
                for (ICustomElement element : CustomItems.customitems) {
                    if (element instanceof Item item && element.showInCreative()) { output.accept(item.getDefaultInstance()); }
                }
                params.holders().lookup(Registries.POTION).ifPresent((holderLookup) -> {
                    addPotion(output, holderLookup, Items.TIPPED_ARROW);
                    addPotion(output, holderLookup, Items.POTION);
                    addPotion(output, holderLookup, Items.SPLASH_POTION);
                    addPotion(output, holderLookup, Items.LINGERING_POTION);
                });
            })
            .withTabsBefore(new ResourceLocation(CustomNpcs.MODID, "cnpcs_blocks"))
            .build();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CustomNpcs.MODID);

    static {
        CREATIVE_TABS.register("cnpcs_tools", () -> TOOLS);
        CREATIVE_TABS.register("cnpcs_blocks", () -> BLOCKS);
        CREATIVE_TABS.register("cnpcs_items", () -> ITEMS);
    }

    private static void addPotion(CreativeModeTab.Output output, HolderLookup<Potion> holderLookup, Item item) {
        holderLookup.listElements().filter(holder -> {
            if (!holder.is(Potions.EMPTY_ID)) {
                return holder.value() instanceof CustomPotion && CustomPotions.CUSTOMS.containsKey(holder.value().getName(""));
            }
            return false;
        })
                .map((holder) -> PotionUtils.setPotion(new ItemStack(item), holder.value()))
                .forEach((stack) -> output.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
    }

}
