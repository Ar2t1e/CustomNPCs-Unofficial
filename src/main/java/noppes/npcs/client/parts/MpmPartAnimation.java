package noppes.npcs.client.parts;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.shared.common.util.NopVector3f;

public class MpmPartAnimation {

   private final Map<Integer, ModelPartWrapper[]> animations = new HashMap<>();

   public void load(List<AnimationContainer> animationsList, MpmPart part) {
      if (animationsList != null && !animationsList.isEmpty()) {
         animationsList.forEach((container) -> {
            ModelPartWrapper[] list = this.animations.computeIfAbsent(container.animation, (k) -> new ModelPartWrapper[0]);
            ModelPartWrapper model = part.getPart(container.part);
            if (model != null) {
               if (container.additional) {
                  container = container.copy();

                  for(int i = 0; i < container.actualLength; ++i) {
                     if (i < container.length) {
                        if (container.hasTranslate) {
                           container.translates[i] = container.translates[i].add(model.oriPos);
                        }

                        if (container.hasRotation) {
                           container.rotations[i] = container.rotations[i].add(model.oriRot);
                        }
                     } else {
                        int pos = container.length - i % container.length - 2;
                        if (container.hasTranslate) {
                           container.translates[i] = container.translates[pos];
                        }
                        if (container.hasRotation) {
                           container.rotations[i] = container.rotations[pos];
                        }
                     }
                  }
               }

               model.animations.put(container.animation, container);
               list = Arrays.copyOf(list, list.length + 1);
               list[list.length - 1] = model;
               this.animations.put(container.animation, list);
            }

         });
      }
   }

   public static List<AnimationContainer> loadAnimations(JsonObject json) {
      List<AnimationContainer> list = new ArrayList<>();
      if (json != null && !json.keySet().isEmpty()) {
         Iterator<Entry<String, JsonElement>> var2 = json.entrySet().iterator();
         label115:
         while(var2.hasNext()) {
            Entry<String, JsonElement> entry = var2.next();
            try {
               int animation = AnimationType.get(entry.getKey());
               JsonObject animationData = entry.getValue().getAsJsonObject();
               int length = animationData.get("animation_length").getAsInt();
               float speed = animationData.get("animation_speed").getAsFloat();
               boolean loop = animationData.has("loop") && animationData.get("loop").getAsBoolean();
               boolean additional = animationData.has("additional") && animationData.get("additional").getAsBoolean();
               Iterator<Entry<String, JsonElement>> var10 = animationData.get("bones").getAsJsonObject().entrySet().iterator();

               while(true) {
                  while(true) {
                     if (!var10.hasNext()) {
                        continue label115;
                     }
                     Entry<String, JsonElement> bone = var10.next();
                     list.removeIf((c) -> c.animation == animation && c.part.equals(bone.getKey()));
                     AnimationContainer con = new AnimationContainer(animation, bone.getKey(), length, speed, additional, loop);
                     list.add(con);
                     JsonObject boneAnimation = bone.getValue().getAsJsonObject();
                     JsonArray tranArray;
                     int i;
                     if (boneAnimation.has("rotation")) {
                        con.hasRotation = true;
                        tranArray = boneAnimation.get("rotation").getAsJsonArray();

                        for(i = 0; i < con.actualLength; ++i) {
                           if (i < length) {
                              con.rotations[i] = MpmPartReader.jsonVector3f(tranArray.get(i)).mul(0.017453292F);
                           } else {
                              con.rotations[i] = con.rotations[length - i % length - 2];
                           }
                        }
                     }
                     if (boneAnimation.has("translate")) {
                        con.hasTranslate = true;
                        tranArray = boneAnimation.get("translate").getAsJsonArray();

                        for(i = 0; i < con.actualLength; ++i) {
                           if (i < length) {
                              con.translates[i] = MpmPartReader.jsonVector3f(tranArray.get(i));
                           } else {
                              con.translates[i] = con.translates[length - i % length - 2];
                           }
                        }
                     } else {
                        for(i = 0; i < con.actualLength; ++i) {
                           con.translates[i] = NopVector3f.ZERO;
                        }
                     }

                     if (boneAnimation.has("scale")) {
                        con.hasScale = true;
                        tranArray = boneAnimation.get("scale").getAsJsonArray();

                        for(i = 0; i < con.actualLength; ++i) {
                           if (i < length) {
                              con.scale[i] = MpmPartReader.jsonVector3f(tranArray.get(i));
                           } else {
                              con.scale[i] = con.scale[length - i % length - 2];
                           }
                        }
                     } else {
                        for(i = 0; i < con.actualLength; ++i) {
                           con.scale[i] = NopVector3f.ZERO;
                        }
                     }
                  }
               }
            } catch (Exception var16) {
               throw new CustomNPCsException(var16, "Error in animation: " + entry.getKey());
            }
         }
      }
      return list;
   }

   public void start(int animation) {
      ModelPartWrapper[] models = this.animations.get(animation);
      if (models != null) {
         for (ModelPartWrapper model : models) {
            model.animations.get(animation).start();
         }
      }
   }

   public boolean animation(int animation, int step, float partialTick) {
      ModelPartWrapper[] models = this.animations.get(animation);
      if (models == null) {
         return false;
      } else {
         for (ModelPartWrapper m : models) {
            m.animations.get(animation).animation(m, step, partialTick);
         }
         return true;
      }
   }

   public boolean animation(int animation, float step) {
      ModelPartWrapper[] models = this.animations.get(animation);
      if (models == null) {
         return false;
      } else {
         for (ModelPartWrapper m : models) {
            m.animations.get(animation).animation(m, step);
         }
         return true;
      }
   }
}
