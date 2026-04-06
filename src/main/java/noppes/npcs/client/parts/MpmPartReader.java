package noppes.npcs.client.parts;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import noppes.npcs.constants.BodyPart;
import noppes.npcs.shared.client.util.AssetsFinder;
import noppes.npcs.shared.common.NoppesException;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.shared.common.util.NopVector2i;
import noppes.npcs.shared.common.util.NopVector3f;
import noppes.npcs.shared.common.util.NopVector3i;

public class MpmPartReader {

   public static Map<ResourceLocation, MpmPart> PARTS = new HashMap<>();
   public static Map<String, List<AnimationContainer>> ANIMATIONS = new HashMap<>();

   public static void reload() {
      Map<String, List<AnimationContainer>> mapA = new HashMap<>();
      List<ResourceLocation> list = AssetsFinder.find("animations", ".json");
      Iterator<ResourceLocation> var2 = list.iterator();
      while(true) {
         ResourceLocation loc;
         Resource r;
         do {
            if (!var2.hasNext()) {
               ANIMATIONS = mapA;
               Map<ResourceLocation, MpmPart> map = new HashMap<>();
               for (ResourceLocation l : AssetsFinder.find("parts", ".json")) {
                  MpmPart part = loadPart(l);
                  if (part != null) {
                     map.put(l, part);
                  }
               }

               PARTS = map;
               PARTS.put(ModelEyeData.RESOURCE, new MpmPartEyes(0, ModelEyeData.RESOURCE));
               PARTS.put(ModelEyeData.RESOURCE_RIGHT, new MpmPartEyes(1, ModelEyeData.RESOURCE_RIGHT));
               PARTS.put(ModelEyeData.RESOURCE_LEFT, new MpmPartEyes(2, ModelEyeData.RESOURCE_LEFT));
               for (Entry<ResourceLocation, MpmPart> entry : PARTS.entrySet()) {
                  if (entry.getValue().parentId != null && !PARTS.containsKey(entry.getValue().parentId)) {
                     LogWriter.error("Error in " + entry.getKey().toString() + " - Unable to find parent " + entry.getValue().parentId);
                     Notify(Component.literal("Error in " + entry.getKey().toString() + " - Unable to find parent " + entry.getValue().parentId));
                  }
               }
               return;
            }

            loc = var2.next();
            r = Minecraft.getInstance().getResourceManager().getResource(loc).orElse(null);
         } while(r == null);

         try {
            InputStream stream = r.open();
            try {
               JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
               mapA.put(loc.getPath().substring(11, loc.getPath().length() - 5), MpmPartAnimation.loadAnimations(root));
            } catch (Throwable var9) {
                try {
                    stream.close();
                } catch (Throwable var8) {
                    var9.addSuppressed(var8);
                }
                throw var9;
            }
            stream.close();
         } catch (Throwable ignored) { }
      }
   }

   private static MpmPart loadPart(ResourceLocation location) {
      Resource r = Minecraft.getInstance().getResourceManager().getResource(location).orElse(null);
      if (r != null) {
         try {
            InputStream stream = r.open();

            MpmPart var10;
            try {
               JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
               PartRenderType renderType = PartRenderType.valueOf(getRequiredString(root, "render_type").toUpperCase());
               MpmPart part = new MpmPart();
               if (renderType == PartRenderType.BEDROCK) {
                  part = new MpmPartBedrock();
               }

               if (renderType == PartRenderType.SIMPLE) {
                  part = new MpmPartSimple();
               }

               part.isEnabled = !root.has("enabled") || root.get("enabled").getAsBoolean();
               part.id = location;
               part.name = getRequiredString(root, "name");
               part.texture = root.has("texture") ? ResourceLocation.tryParse(root.get("texture").getAsString()) : null;
               part.menu = getRequiredString(root, "menu");
               part.author = getRequiredString(root, "author");
               part.translate = jsonVector3f(root.get("translate"));
               part.scale = jsonVector3fOrOne(root.get("scale"));
               part.rotatePoint = jsonVector3f(root.get("rotate_offset"));
               part.rotate = jsonVector3f(root.get("rotate"));
               part.previewRotation = root.get("preview_rotation").getAsInt();
               part.hiddenParts = jsonEnumList(BodyPart.class, root.get("hidden_parts"));
               part.disableCustomTextures = root.has("disable_custom_textures") && root.get("disable_custom_textures").getAsBoolean();
               part.defaultUsePlayerSkins = root.has("default_use_player_skins") && root.get("default_use_player_skins").getAsBoolean();
               part.renderType = renderType;
               part.bodyPart = BodyPart.valueOf(getRequiredString(root, "body_part").toUpperCase());
               part.load(root.has("render_data") ? root.get("render_data").getAsJsonObject() : null);
               if (root.has("parent")) {
                  part.parentId = ResourceLocation.tryParse(root.get("parent").getAsString());
               }

               part.animationType = root.has("animation_type") ? PartBehaviorType.valueOf(root.get("animation_type").getAsString().toUpperCase()) : PartBehaviorType.NONE;
               if (root.has("animation_inherit")) {
                  String inPart = root.get("animation_inherit").getAsString().toLowerCase();
                  if (!ANIMATIONS.containsKey(inPart)) {
                     throw new NoppesException("Unknown animation inherit: " + inPart);
                  }
                  part.animationData.load(ANIMATIONS.get(inPart), part);
               }

               if (root.has("animation_data")) {
                  part.animationData.load(MpmPartAnimation.loadAnimations(root.get("animation_data").getAsJsonObject()), part);
               }

               var10 = part;
            } catch (Throwable var8) {
                try {
                    stream.close();
                } catch (Throwable var7) {
                    var8.addSuppressed(var7);
                }
                throw var8;
            }
            stream.close();
            return var10;
         } catch (Throwable var9) {
            LogWriter.error("Error in " + location, var9);
            Notify(Component.literal("Error in " + location + " - " + var9.getMessage()));
         }
      }

      return null;
   }

   public static String getRequiredString(JsonObject root, String part) {
      if (!root.has(part)) {
         throw new NoppesException("Can't fine " + part);
      } else {
         return root.get(part).getAsString();
      }
   }

   @SuppressWarnings("unchecked")
   public static <T extends Enum<BodyPart>> List<T> jsonEnumList(Class<BodyPart> type, JsonElement el) {
      List<T> list = new ArrayList<>();
      if (el != null && el.isJsonArray()) {
         JsonArray arr = el.getAsJsonArray();
         for(int i = 0; i < arr.size(); ++i) {
            list.add((T) Enum.valueOf(type, arr.get(i).getAsString().toUpperCase()));
         }
      }
      return list;
   }

   public static NopVector2i jsonVector2i(JsonElement el) {
      if (el != null && el.isJsonArray()) {
         JsonArray arr = el.getAsJsonArray();
         int[] r = new int[arr.size()];

         for(int i = 0; i < arr.size(); ++i) {
            r[i] = arr.get(i).getAsInt();
         }

         return new NopVector2i(r);
      } else {
         return NopVector2i.ZERO;
      }
   }

   @SuppressWarnings("all")
   public static NopVector3i jsonVector3i(JsonElement el) {
      if (el != null && el.isJsonArray()) {
         JsonArray arr = el.getAsJsonArray();
         int[] r = new int[arr.size()];

         for(int i = 0; i < arr.size(); ++i) {
            r[i] = arr.get(i).getAsInt();
         }
         return new NopVector3i(r);
      } else {
         return NopVector3i.ZERO;
      }
   }

   public static NopVector3f jsonVector3f(JsonElement el) {
      if (el == null) {
         return NopVector3f.ZERO;
      } else {
         JsonArray arr = el.getAsJsonArray();
         float[] r = new float[arr.size()];

         for(int i = 0; i < arr.size(); ++i) {
            r[i] = arr.get(i).getAsFloat();
         }

         return new NopVector3f(r);
      }
   }

   public static NopVector3f jsonVector3fOrOne(JsonElement el) {
      if (el == null) {
         return NopVector3f.ONE;
      } else {
         JsonArray arr = el.getAsJsonArray();
         float[] r = new float[arr.size()];

         for(int i = 0; i < arr.size(); ++i) {
            r[i] = arr.get(i).getAsFloat();
         }

         return new NopVector3f(r);
      }
   }

   public static void Notify(Component message) {
      Component componentTranslation = ((MutableComponent) message).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
      if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.shouldInformAdmins()) {
         Minecraft.getInstance().player.sendSystemMessage(componentTranslation);
      }
   }

}
