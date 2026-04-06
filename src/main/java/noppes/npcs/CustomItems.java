package noppes.npcs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.fluids.CustomFluid;
import noppes.npcs.items.*;
import noppes.npcs.items.custom.*;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ModData;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

@EventBusSubscriber(bus = Bus.MOD, modid = CustomNpcs.MODID)
public class CustomItems {

   public static ItemNpcWand wand;
   public static ItemNpcCloner cloner;
   public static ItemNpcScripter scripter;
   public static ItemNpcMovingPath moving;
   public static ItemMounter mount;
   public static ItemTeleporter teleporter;
   public static ItemScripted scripted_item;
   public static ItemNbtBook nbt_book;
   public static ItemSoulstoneEmpty soulstoneEmpty;
   public static ItemSoulstoneFilled soulstoneFull;
   // New from Unofficial (BetaZavr)
   public static ItemBoundary npcboundary;
   public static ItemBuilder npcbuilder;
   public static ItemRemover npcremover;
   public static ItemPlacer npcplacer;
   public static ItemReplacer npcreplacer;
   public static ItemSaver npcsaver;
   // custom
   public static List<ICustomElement> customitems = new ArrayList<>();
   public static CompoundTag registryNbt;

   @SubscribeEvent
   public static void registerItems(RegisterEvent event) {
      if (event.getRegistryKey() == Keys.ITEMS && event.getForgeRegistry() != null) {
         CustomNpcs.debugData.start("Mod");
         // mod block items
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcredstoneblock", CustomBlocks.redstone_item = createItem(CustomBlocks.redstone));
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcmailbox", CustomBlocks.mailbox_item = createItem(CustomBlocks.mailbox));
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcmailbox2", CustomBlocks.mailbox2_item = createItem(CustomBlocks.mailbox2));
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcmailbox3", CustomBlocks.mailbox3_item = createItem(CustomBlocks.mailbox3));
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcwaypoint", CustomBlocks.waypoint_item = createItem(CustomBlocks.waypoint));
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcborder", CustomBlocks.border_item = createItem(CustomBlocks.border));
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcscripted", CustomBlocks.scripted_item = createItem(CustomBlocks.scripted));
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcscripteddoortool", CustomBlocks.scripted_door_item = new ItemScriptedDoor(CustomBlocks.scripted_door));
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcbuilderblock", CustomBlocks.builder_item = createItem(CustomBlocks.builder));
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npccopyblock", CustomBlocks.copy_item = createItem(CustomBlocks.copy));
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npccarpentybench", CustomBlocks.carpentry_item = createItem(CustomBlocks.carpenty));
         // mod items
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcwand", wand = new ItemNpcWand());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcmobcloner", cloner = new ItemNpcCloner());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcscripter", scripter = new ItemNpcScripter());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcmovingpath", moving = new ItemNpcMovingPath());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcmounter", mount = new ItemMounter());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcteleporter", teleporter = new ItemTeleporter());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcsoulstoneempty", soulstoneEmpty = new ItemSoulstoneEmpty());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcsoulstonefilled", soulstoneFull = new ItemSoulstoneFilled());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":scripted_item", scripted_item = new ItemScripted((new Properties()).stacksTo(1)));
         event.getForgeRegistry().register(CustomNpcs.MODID + ":nbt_book", nbt_book = new ItemNbtBook());
         // New from Unofficial (BetaZavr)
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcboundary", npcboundary = new ItemBoundary());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcbuilder", npcbuilder = new ItemBuilder());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcremover", npcremover = new ItemRemover());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcplacer", npcplacer = new ItemPlacer());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcreplacer", npcreplacer = new ItemReplacer());
         event.getForgeRegistry().register(CustomNpcs.MODID + ":npcsaver", npcsaver = new ItemSaver());

         List<String> names = new ArrayList<>();
         // mod block items
         names.add(CustomNpcs.MODID + ":npcredstoneblock");
         names.add(CustomNpcs.MODID + ":npcmailbox");
         names.add(CustomNpcs.MODID + ":npcmailbox2");
         names.add(CustomNpcs.MODID + ":npcmailbox3");
         names.add(CustomNpcs.MODID + ":npcwaypoint");
         names.add(CustomNpcs.MODID + ":npcborder");
         names.add(CustomNpcs.MODID + ":npcscripted");
         names.add(CustomNpcs.MODID + ":npcscripteddoortool");
         names.add(CustomNpcs.MODID + ":npcbuilderblock");
         names.add(CustomNpcs.MODID + ":npccopyblock");
         names.add(CustomNpcs.MODID + ":npccarpentybench");
         // mod items
         names.add(CustomNpcs.MODID + ":npcwand");
         names.add(CustomNpcs.MODID + ":npcmobcloner");
         names.add(CustomNpcs.MODID + ":npcscripter");
         names.add(CustomNpcs.MODID + ":npcmovingpath");
         names.add(CustomNpcs.MODID + ":npcmounter");
         names.add(CustomNpcs.MODID + ":npcteleporter");
         names.add(CustomNpcs.MODID + ":npcsoulstoneempty");
         names.add(CustomNpcs.MODID + ":npcsoulstonefilled");
         names.add(CustomNpcs.MODID + ":scripted_item");
         names.add(CustomNpcs.MODID + ":nbt_book");
         names.add(CustomNpcs.MODID + ":npcboundary");
         names.add(CustomNpcs.MODID + ":npcbuilder");
         names.add(CustomNpcs.MODID + ":npcremover");
         names.add(CustomNpcs.MODID + ":npcplacer");
         names.add(CustomNpcs.MODID + ":npcreplacer");
         names.add(CustomNpcs.MODID + ":npcsaver");

         // Custom Items
         File itemsFile = new File(CustomNpcs.Dir, "custom_items.js");
         CompoundTag nbtItems = getItemsNbt(itemsFile);
         boolean resave = nbtItems.getBoolean("resave");
         nbtItems.remove("resave");

         for (int i = 0; i < nbtItems.getList("Items", 10).size(); i++) {
            CompoundTag nbtItem = nbtItems.getList("Items", 10).getCompound(i);
            if (!nbtItem.contains("RegistryName", 8) || !nbtItem.contains("ItemType", 1)
                    || nbtItem.getString("RegistryName").isEmpty() || nbtItem.getByte("ItemType") < (byte) 0
                    || nbtItem.getByte("ItemType") > (byte) 8) {
               LogWriter.error("Attempt to load item pos: " + i + "; name: \"" + nbtItem.getString("RegistryName") + "\" - failed");
               continue;
            }
            String preName = nbtItem.getString("RegistryName");
            String name = NoppesUtilServer.validPath(preName);
            if (!preName.equals(name)) {
               nbtItem.putString("RegistryName", name);
               resave = true;
            }
            if (nbtItem.contains("CollectionBlocks", 9)) {
               ListTag list = nbtItem.getList("CollectionBlocks", 10);
               for (int j = 0; j < list.size(); j++) {
                  CompoundTag nbt = list.getCompound(j);
                  if (nbt.contains("Name", 8)) {
                     String n = nbt.getString("Name");
                     String v = NoppesUtilServer.validPath(n);
                     if (!n.equals(v)) {
                        nbt.putString("Name", v);
                        resave = true;
                     }
                  }
               }
            }
            if (nbtItem.contains("CollectionBlockTags", 9)) {
               ListTag list = nbtItem.getList("CollectionBlockTags", 10);
               for (int j = 0; j < list.size(); j++) {
                  CompoundTag nbt = list.getCompound(j);
                  if (nbt.contains("Name", 8)) {
                     String n = nbt.getString("Name");
                     String v = NoppesUtilServer.validPath(n);
                     if (!n.equals(v)) {
                        nbt.putString("Name", v);
                        resave = true;
                     }
                  }
               }
            }
            String location = CustomNpcs.MODID + ":custom_" + name;
            registryNbt = nbtItem;
            switch (nbtItem.getByte("ItemType")) {
               case (byte) 1: {
                  registryItem(location, new CustomWeapon(getTier(nbtItem), getProperty(nbtItem), nbtItem), names,
                          nbtItem.getBoolean("CreateDefaultFiles"), event.getForgeRegistry());
                  break;
               } // Weapon
               case (byte) 2: {
                  Tier tier = getTier(nbtItem);
                  Properties property = getProperty(nbtItem);
                  Item item = switch (nbtItem.getString("ToolClass").toLowerCase()) {
                     case "axe" -> new CustomAxe(tier, property, nbtItem);
                     case "hoe" -> new CustomHoe(tier, property, nbtItem);
                     case "shovel" -> new CustomShovel(tier, property, nbtItem);
                     default -> new CustomPickaxe(tier, property, nbtItem);
                  };
                  registryItem(location, item, names, nbtItem.getBoolean("CreateDefaultFiles"), event.getForgeRegistry());
                  break;
               } // Tool
               case (byte) 3: {
                  //Items.IRON_BOOTS
                  /*ArmorMaterial mat = CustomArmor.getMaterialArmor(nbtItem.getString("Material"));
                  for (int a = 0; a < nbtItem.getList("EquipmentSlots", 10).size(); a++) {
                     CompoundTag part = nbtItem.getList("EquipmentSlots", 8).getCompound(a);
                     ArmorItem.Type itemType = CustomArmor.getSlotEquipment(part.getString("Slot"));
                     registryItem(location + "_" + itemType.getName().toLowerCase(),
                             new CustomArmor(mat, itemType, getProperty(nbtItem),
                                     ValueUtil.correctInt(part.getInt("MaxStackDamage"), 0, Integer.MAX_VALUE),
                                     ValueUtil.correctInt(part.getInt("Defense"), 0, Integer.MAX_VALUE),
                                     ValueUtil.correctInt(part.getInt("Enchantability"), 0, Integer.MAX_VALUE),
                                     ValueUtil.correctFloat(part.getFloat("Toughness"), 0.0f, Float.MAX_VALUE),
                                     ValueUtil.correctFloat(part.getFloat("KnockbackResistance"), 0.0f, Float.MAX_VALUE), nbtItem),
                             names, nbtItem.getBoolean("CreateDefaultFiles"), event.getForgeRegistry());
                  }*/
                  break;
               } // Armor
               case (byte) 4: {
                  registryItem(location, new CustomShield(getProperty(nbtItem), nbtItem), names,
                          nbtItem.getBoolean("CreateDefaultFiles"), event.getForgeRegistry());
                  break;
               } // Shield
               case (byte) 5: {
                  registryItem(location, new CustomBow(getProperty(nbtItem), nbtItem), names,
                          nbtItem.getBoolean("CreateDefaultFiles"), event.getForgeRegistry());
                  break;
               } // Bow
               case (byte) 6: {
                  if (!nbtItem.contains("FoodData", 10)) {
                     nbtItem.put("FoodData", new CompoundTag());
                     resave = true;
                  }
                  registryItem(location, new CustomFood(getProperty(nbtItem), nbtItem), names,
                          nbtItem.getBoolean("CreateDefaultFiles"), event.getForgeRegistry());
                  break;
               } // Food
               case (byte) 7: continue; // Potion
               case (byte) 8: {
                  registryItem(location, new CustomFishingRod(getProperty(nbtItem), nbtItem), names,
                          nbtItem.getBoolean("CreateDefaultFiles"), event.getForgeRegistry());
                  break;
               } // Fishing Rod
               default: {
                  if (nbtItem.getByte("ItemType") == 0) {
                     if (nbtItem.contains("FoodData", 10)) {
                        nbtItem.remove("FoodData");
                        resave = true;
                     }
                     registryItem(location, new CustomItem(getProperty(nbtItem), nbtItem), names,
                             nbtItem.getBoolean("CreateDefaultFiles"), event.getForgeRegistry());
                  }
                  break;
               } // 0: Simple
            }
            if (nbtItem.getBoolean("CreateDefaultFiles")) {
               nbtItem.remove("CreateDefaultFiles");
               resave = true;
            }
         }
         registryNbt = null;
         if (resave) { Util.instance.saveFile(itemsFile, nbtItems); }
         // Custom Blocks
         for (Map.Entry<ICustomElement, Item> entry : CustomBlocks.customblocks.entrySet()) {
            if (entry.getValue() != null) {
               String location = CustomNpcs.MODID + ":custom_" + entry.getKey().getCustomName();
               if (!names.contains(location)) {
                  names.add(location);
                  event.getForgeRegistry().register(location, entry.getValue());
               }
            }
         }
         // custom fluids
         for (Map.Entry<String, ICustomElement> entry : CustomBlocks.customfluid.entrySet()) {
            if (entry.getValue() instanceof CustomFluid fluid && fluid.getBucket() instanceof BucketItem bucket) {
               event.getForgeRegistry().register(entry.getKey().replace("fluid_", "") + "_bucket", bucket);
            }
         }
         // Sorting:
         customitems.sort(Comparator.comparing(ICustomElement::getElementType)
                 .thenComparing(ICustomElement::getCustomName));
         CustomNpcs.debugData.end("Mod");
      }
   }

   public static CompoundTag getItemsNbt(File file) {
      CompoundTag nbtInFile = new CompoundTag();
      CompoundTag compound = ModData.getExampleItems().copy();
      try {
         if (file.exists()) { nbtInFile = NBTJsonUtil.LoadFile(file); }
      }
      catch (Exception e) { LogWriter.error("Try Load " + file.getName() + ": ", e); }

      List<String> names = new ArrayList<>();
      ListTag listInFile = nbtInFile.getList("Items", 10);
      ListTag listItems = compound.getList("Items", 10);
      ListTag exampleItems = listItems.copy();
      boolean resave = false;
      for (int i = 0; i < listInFile.size(); i++) {
         CompoundTag nbtItem = listInFile.getCompound(i);
         String name = nbtItem.getString("RegistryName");
         boolean isExample = false;
         for (int j = 0; j < exampleItems.size(); j++) {
            if (name.equals(exampleItems.getCompound(j).getString("RegistryName"))) {
               isExample = true;
               break;
            }
         }
         if (names.contains(name)) {
            if (isExample) {
               name = name.replace("example", "custom");
               isExample = false;
            }
            while (names.contains(name)) { name += "_"; }
            nbtItem.putString("RegistryName", name);
            resave = true;
         }
         names.add(name);
         if (!isExample) { listItems.add(nbtItem); }
      }
      compound.putBoolean("resave", resave);
      return compound;
   }

   private static Properties getProperty(CompoundTag nbtItem) {
      Properties properties = new Properties();
      CompoundTag nbtProperties = nbtItem.getCompound("Properties");
      if (nbtProperties.contains("MaxStackDamage", 3)) { properties.defaultDurability(nbtProperties.getInt("MaxStackDamage")); }
      else { properties.stacksTo(nbtProperties.contains("MaxStackSize", 3) ? nbtProperties.getInt("MaxStackSize") : 64); }
      if (nbtProperties.contains("Rarity", 8)) {
         properties.rarity(switch (nbtProperties.getString("Rarity").toLowerCase()) {
            case "uncommon" -> Rarity.UNCOMMON;
            case "rare" -> Rarity.RARE;
            case "epic" -> Rarity.EPIC;
            default -> Rarity.COMMON;
         });
      }
      if (nbtProperties.contains("FireResistant", 1) && nbtProperties.getBoolean("FireResistant")) { properties.fireResistant(); }
      if (nbtProperties.contains("CanRepair", 1) && !nbtProperties.getBoolean("CanRepair")) { properties.setNoRepair(); }
      if (nbtProperties.contains("RepairItem", 8)) {
         Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbtProperties.getString("RepairItem")));
         if (item != null) { properties.craftRemainder(item); }
      }

      if (nbtProperties.contains("requiredFeatures", 9)) {
         ListTag list = nbtProperties.getList("requiredFeatures", 10);
         List<FeatureFlag> featureFlags = new ArrayList<>();
         for (int i = 0; i < list.size(); i++) {
            CompoundTag nbt = list.getCompound(i);
            if (nbt.contains("UniverseId", 8) && nbt.contains("Name", 8)) {
               FeatureFlagRegistry.Builder b = new FeatureFlagRegistry.Builder(nbt.getString("UniverseId"));
               featureFlags.add(b.create(new ResourceLocation(CustomNpcs.MODID, nbt.getString("Name"))));
            }
         }
         if (!featureFlags.isEmpty()) { properties.requiredFeatures(featureFlags.toArray(new FeatureFlag[0])); }
      }

      if (nbtProperties.contains("FoodData", 10)) {
         CompoundTag food = nbtProperties.getCompound("FoodData");
         FoodProperties.Builder builder = new FoodProperties.Builder();
         builder.nutrition(food.contains("Nutrition", 3) ? food.getInt("Nutrition") : 3);
         builder.saturationMod(food.contains("Saturation", 5) ? food.getFloat("Saturation") : 0.3f);
         if (food.contains("IsMeat", 1) && food.getBoolean("IsMeat")) { builder.meat(); }
         if (food.contains("AlwaysEdible", 1) && food.getBoolean("AlwaysEdible")) { builder.alwaysEat(); }
         if (food.contains("IsFastFood", 1) && food.getBoolean("IsFastFood")) { builder.fast(); }
         if (food.contains("Effects", 9)) {
            ListTag list = food.getList("Effects", 10);
            for (int i = 0; i < list.size(); i++) {
               CompoundTag nbt = list.getCompound(i);
               MobEffect effect = null;
               if (nbt.contains("Name", 8)) {
                  effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(nbt.getString("Name")));
               }
               if (effect == null && nbt.contains("Id", 3)) {
                  effect = switch (nbt.getInt("Id")) {
                     case 1 -> MobEffects.MOVEMENT_SPEED;
                     case 2 -> MobEffects.MOVEMENT_SLOWDOWN;
                     case 3 -> MobEffects.DIG_SPEED;
                     case 4 -> MobEffects.DIG_SLOWDOWN;
                     case 5 -> MobEffects.DAMAGE_BOOST;
                     case 6 -> MobEffects.HEAL;
                     case 7 -> MobEffects.HARM;
                     case 8 -> MobEffects.JUMP;
                     case 9 -> MobEffects.CONFUSION;
                     case 10 -> MobEffects.REGENERATION;
                     case 11 -> MobEffects.DAMAGE_RESISTANCE;
                     case 12 -> MobEffects.FIRE_RESISTANCE;
                     case 13 -> MobEffects.WATER_BREATHING;
                     case 14 -> MobEffects.INVISIBILITY;
                     case 15 -> MobEffects.BLINDNESS;
                     case 16 -> MobEffects.NIGHT_VISION;
                     case 17 -> MobEffects.HUNGER;
                     case 18 -> MobEffects.WEAKNESS;
                     case 19 -> MobEffects.POISON;
                     case 20 -> MobEffects.WITHER;
                     case 21 -> MobEffects.HEALTH_BOOST;
                     case 22 -> MobEffects.ABSORPTION;
                     case 23 -> MobEffects.SATURATION;
                     case 24 -> MobEffects.GLOWING;
                     case 25 -> MobEffects.LEVITATION;
                     case 26 -> MobEffects.LUCK;
                     case 27 -> MobEffects.UNLUCK;
                     case 28 -> MobEffects.SLOW_FALLING;
                     case 29 -> MobEffects.CONDUIT_POWER;
                     case 30 -> MobEffects.DOLPHINS_GRACE;
                     case 31 -> MobEffects.BAD_OMEN;
                     case 32 -> MobEffects.HERO_OF_THE_VILLAGE;
                     case 33 -> MobEffects.DARKNESS;
                     default -> null;
                  };
               }
               if (effect != null) {
                  MobEffect finalEffect = effect;
                  builder.effect(() -> new MobEffectInstance(finalEffect,
                          nbt.contains("DurationTicks", 3) ? ValueUtil.correctInt(nbt.getInt("DurationTicks"), 0, Integer.MAX_VALUE) : 100,
                          nbt.contains("Amplifier", 3) ? ValueUtil.correctInt(nbt.getInt("Amplifier"), 0, Integer.MAX_VALUE) : 0,
                          nbt.contains("Ambient", 1) && nbt.getBoolean("Ambient"),
                          !nbt.contains("ShowParticles", 1) || nbt.getBoolean("ShowParticles"),
                          !nbt.contains("ShowIcon", 1) || nbt.getBoolean("ShowIcon")),
                          ValueUtil.correctFloat(nbt.getFloat("Probability"), 0.0f, 1.0f));
               }
            }
         }
         properties.food(builder.build());
      }
      return properties;
   }

   private static Tier getTier(CompoundTag nbtItem) {
      CompoundTag tier = nbtItem.getCompound("Tier");
      int uses = tier.contains("MaxStackDamage", 3) ? tier.getInt("MaxStackDamage") : 250;
      int level = tier.contains("HarvestLevel", 3) ? tier.getInt("HarvestLevel") : 0;
      int enchantability = tier.contains("Enchantability", 3) ? tier.getInt("Enchantability") : 15;
      float speed = tier.contains("Efficiency", 5) ? tier.getFloat("Efficiency") : 2.0f;
      float damage = tier.contains("EntityDamage", 5) ? tier.getFloat("EntityDamage") : 0.0f;
      Ingredient ingredient;
      if (tier.contains("RepairItem", 10)) { ingredient = Ingredient.of(ItemStack.of(tier.getCompound("RepairItem"))); }
      else if (tier.contains("RepairItemTag", 8)) { ingredient = Ingredient.of(TagKey.create(Registries.ITEM, new ResourceLocation(tier.getString("RepairItemTag")))); }
      else { ingredient = Ingredient.of(ItemTags.PLANKS); }

      return new Tier() {
         @Override
         public int getUses() { return uses; }
         @Override
         public float getSpeed() { return speed; }
         @Override
         public float getAttackDamageBonus() { return damage; }
         @Override
         public int getLevel() { return level; }
         @Override
         public int getEnchantmentValue() { return enchantability; }
         @Override
         public @Nonnull Ingredient getRepairIngredient() { return ingredient; }
      };
   }

   public static void registerDispenser() {
      if (soulstoneFull == null) { return; }
      DispenserBlock.registerBehavior(soulstoneFull, new DefaultDispenseItemBehavior() {
         public @NotNull ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack item) {
            Direction enumFacing = source.getBlockState().getValue(DispenserBlock.FACING);
            double x = source.x() + (double) enumFacing.getStepX();
            double z = source.z() + (double) enumFacing.getStepZ();
            ItemSoulstoneFilled.Spawn(null, item, source.getLevel(), new BlockPos((int)x, (int)source.y(), (int)z));
            item.split(1);
            return item;
         }
      });
   }

   private static Item createItem(Block block) { return new ItemNpcBlock(block, new Properties()); }

   private static void registryItem(String location, Item item, List<String> names, boolean defFiles, IForgeRegistry<Item> registry) {
      if (names.contains(location) || Item.getId(item) > 0 || !(item instanceof ICustomElement element)) {
         LogWriter.error("Attempt to load a registered item \"" + location + "\"");
         return;
      }
      boolean isExample = location.endsWith(":custom_itemexample") || location.endsWith(":custom_weaponexample") ||
              location.endsWith(":custom_armorexample") || location.endsWith(":custom_armorobjexample") ||
              location.endsWith(":custom_shieldexample") || location.endsWith(":custom_bowexample") || location.endsWith(":custom_toolexample") ||
              location.endsWith(":custom_axeexample") || location.endsWith(":custom_foodexample") || location.endsWith(":custom_fishingrodexample");
      if (isExample || defFiles) { CustomNpcs.proxy.createAllFiles((ICustomElement) item); }
      LogWriter.info("Load Custom Item \"" + location + "\"");
      customitems.add(element);
      names.add(location);
      registry.register(location, item);
   }

}
