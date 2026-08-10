package noppes.npcs.client.model.custom;

import net.minecraft.client.model.*;
import noppes.npcs.mixin.client.model.IModelBoxMixin;

public class CustomCube {

    public static ModelRenderer createBannerFlag(ModelBanner bannerModel) {
        ModelRenderer model = new ModelRenderer(bannerModel, 0, 0);
        model.addBox(-10.0F, -32.0F, -2.0F, 20, 40, 1, 0.0F);
        ModelBox list = model.cubeList.get(0);

        PositionTextureVertex[] vp = ((IModelBoxMixin) list).getVertexPositions();
        if (vp != null) {
            TexturedQuad[] quadList = new TexturedQuad[6];
            quadList[0] = new TexturedQuad(new PositionTextureVertex[] { vp[5], vp[1], vp[2], vp[6] }, 11, 1, 12, 17, 64, 32); // right
            quadList[1] = new TexturedQuad(new PositionTextureVertex[] { vp[0], vp[4], vp[7], vp[3] }, 0, 1, 1, 17, 64, 32); // left
            quadList[2] = new TexturedQuad(new PositionTextureVertex[] { vp[5], vp[4], vp[0], vp[1] }, 1, 0, 11, 0, 64, 32); // top
            quadList[3] = new TexturedQuad(new PositionTextureVertex[] { vp[2], vp[3], vp[7], vp[6] }, 11, 0, 21, 1, 64, 32); // bottom
            quadList[4] = new TexturedQuad(new PositionTextureVertex[] { vp[1], vp[0], vp[3], vp[2] }, 1, 1, 11, 17, 64, 32); // front
            quadList[5] = new TexturedQuad(new PositionTextureVertex[] { vp[4], vp[5], vp[6], vp[7] }, 12, 1, 22, 17, 64, 32); // back
            ((IModelBoxMixin) list).setQuadList(quadList);
        }
        return model;
    }

    public static ModelRenderer createShieldFlag(ModelShield modelShield) {
        ModelRenderer model = new ModelRenderer(modelShield, 0, 0);
        model.addBox(-10.0F, -32.0F, -2.0F, 20, 40, 1, 0.0F);
        ModelBox list = model.cubeList.get(0);
        PositionTextureVertex[] vp = ((IModelBoxMixin) list).getVertexPositions();
        if (vp != null) {
            TexturedQuad[] quadList = new TexturedQuad[6];
            quadList[0] = new TexturedQuad(new PositionTextureVertex[] { vp[5], vp[1], vp[2], vp[6] }, 11, 1, 12, 17, 64, 32); // right
            quadList[1] = new TexturedQuad(new PositionTextureVertex[] { vp[0], vp[4], vp[7], vp[3] }, 0, 1, 1, 17, 64, 32); // left
            quadList[2] = new TexturedQuad(new PositionTextureVertex[] { vp[5], vp[4], vp[0], vp[1] }, 1, 0, 11, 0, 64, 32); // top
            quadList[3] = new TexturedQuad(new PositionTextureVertex[] { vp[2], vp[3], vp[7], vp[6] }, 11, 0, 21, 1, 64, 32); // bottom
            quadList[4] = new TexturedQuad(new PositionTextureVertex[] { vp[1], vp[0], vp[3], vp[2] }, 1, 1, 11, 17, 64, 32); // front
            quadList[5] = new TexturedQuad(new PositionTextureVertex[] { vp[4], vp[5], vp[6], vp[7] }, 12, 1, 22, 17, 64, 32); // back
            ((IModelBoxMixin) list).setQuadList(quadList);
        }
        return model;
    }

}
