package noppes.npcs.client.parts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.mixin.client.model.geom.IModelPartMixin;
import noppes.npcs.shared.common.util.NopVector2i;
import noppes.npcs.shared.common.util.NopVector3f;

public class MpmPartBedrock extends MpmPartAbstractClient {

   private ModelPart model;
   public NopVector2i textureSize;

   public MpmPartBedrock() {
      this.textureSize = NopVector2i.ZERO;
   }

   public void render(MpmPartData data, PoseStack mStack, VertexConsumer c, int lightMapUV, LivingEntity player) {
      mStack.pushPose();
      if (model != null) {
         Map<String, ModelPart> children = ((IModelPartMixin) ((Object) model)).getChildren();
         model.translateAndRotate(mStack);
         float f = 0.0625F;
         mStack.translate(-this.rotatePoint.x * f, -this.rotatePoint.y * f, -this.rotatePoint.z * f);
         mStack.scale(this.scale.x, this.scale.y, this.scale.z);
         for (ModelPart modelpart : children.values()) {
            modelpart.render(mStack, c, lightMapUV, OverlayTexture.NO_OVERLAY, data.color.x, data.color.y, data.color.z, 1.0F);
         }
      }

      mStack.popPose();
   }

   public void load(JsonObject renderData) {
      if (renderData != null && !renderData.keySet().isEmpty()) {
         MeshDefinition meshdefinition = new MeshDefinition();
         PartDefinition root = meshdefinition.getRoot();
         JsonObject ob = renderData.get("minecraft:geometry").getAsJsonArray().get(0).getAsJsonObject();
         JsonObject description = ob.get("description").getAsJsonObject();
         this.textureSize = new NopVector2i(description.get("texture_width").getAsInt(), description.get("texture_height").getAsInt());
         JsonArray bones = ob.get("bones").getAsJsonArray();
         Map<String, PartDefinition> namedParts = new HashMap<>();
         Map<String, NopVector3f> parentPivots = new HashMap<>();
         Map<String, ModelPartWrapper> defaultPose = new HashMap<>();

         for(int i = 0; i < bones.size(); ++i) {
            JsonObject bone = bones.get(i).getAsJsonObject();
            String name = bone.get("name").getAsString();
            String pName = bone.has("parent") ? bone.get("parent").getAsString() : null;
            PartDefinition parent = pName != null && namedParts.containsKey(pName) ? namedParts.get(pName) : root;
            NopVector3f pPivot = parentPivots.getOrDefault(pName, NopVector3f.ZERO);
            NopVector3f pivot = MpmPartReader.jsonVector3f(bone.get("pivot"));
            parentPivots.put(name, pivot);
            NopVector3f rotation = MpmPartReader.jsonVector3f(bone.get("rotation")).mul(0.017453292F);
            PartPose pose = PartPose.offsetAndRotation(pivot.x - pPivot.x, pPivot.y - pivot.y, pivot.z - pPivot.z, rotation.x, rotation.y, rotation.z);
            PartDefinition partDef = parent.addOrReplaceChild(name, CubeListBuilder.create(), pose);
            defaultPose.put(name, new ModelPartWrapper((ModelPart)null, new NopVector3f(pose.x, pose.y, pose.z), rotation));
            if (bone.has("cubes")) {
               JsonArray cubes = bone.get("cubes").getAsJsonArray();

               for(int j = 0; j < cubes.size(); ++j) {
                  CubeListBuilder builder = CubeListBuilder.create();
                  JsonObject cube = cubes.get(j).getAsJsonObject();
                  NopVector2i uv = MpmPartReader.jsonVector2i(cube.get("uv"));
                  boolean mirror = cube.has("mirror") && cube.get("mirror").getAsBoolean();
                  NopVector3f cPivot = MpmPartReader.jsonVector3f(cube.get("pivot"));
                  rotation = MpmPartReader.jsonVector3f(cube.get("rotation")).mul(0.017453292F);
                  NopVector3f origin = MpmPartReader.jsonVector3f(cube.get("origin"));
                  NopVector3f size = MpmPartReader.jsonVector3f(cube.get("size"));
                  CubeDeformation deform = cube.has("inflate") ? new CubeDeformation(cube.get("inflate").getAsFloat()) : CubeDeformation.NONE;
                  builder.texOffs(uv.x, uv.y).mirror(mirror).addBox(origin.x - cPivot.x, cPivot.y - size.y - origin.y, origin.z - cPivot.z, size.x, size.y, size.z, deform);
                  partDef.addOrReplaceChild("cube_" + name + j, builder, PartPose.offsetAndRotation(cPivot.x - pivot.x, pivot.y - cPivot.y, cPivot.z - pivot.z, rotation.x, rotation.y, rotation.z));
               }
            }

            namedParts.put(name, partDef);
         }

         this.model = LayerDefinition.create(meshdefinition, description.get("texture_width").getAsInt(), description.get("texture_height").getAsInt()).bakeRoot();
         this.model.setPos(this.translate.x, this.translate.y, this.translate.z);

         Entry<String, ModelPartWrapper> entry;
         for(Iterator<Entry<String, ModelPartWrapper>> var30 = defaultPose.entrySet().iterator(); var30.hasNext(); entry.getValue().mcPart = this.getChild(this.model, entry.getKey())) {
            entry = var30.next();
         }
         defaultPose.put(null, new ModelPartWrapper(this.model, this.translate, this.rotate));
         this.defaultPose = defaultPose;
         NopVector3f rotation = this.rotate.mul(0.017453292F);
         this.model.setRotation(rotation.x, rotation.y, rotation.z);
      }

   }

   private ModelPart getChild(ModelPart root, String name) {
      Map<String, ModelPart> children = ((IModelPartMixin) ((Object) root)).getChildren();
      if (children.containsKey(name)) {
         return children.get(name);
      } else {
         Iterator<ModelPart> var4 = children.values().iterator();
         ModelPart p;
         do {
            if (!var4.hasNext()) {
               return null;
            }

            ModelPart child = var4.next();
            p = this.getChild(child, name);
         } while(p == null);
         return p;
      }
   }

}
