package noppes.npcs.api;

import net.minecraft.resources.ResourceLocation;
import noppes.npcs.api.interfaces.ParamName;

import java.util.List;

public interface IPlayerSkin {

   int getType();

   boolean isLocation();

   boolean isActive();

   boolean isUrl();

   String getUrl();

   int getGenderType();

   IPlayerSkin setGender(@ParamName("type") int type);

   String getGender();

   int getBodyType();

   IPlayerSkin setBodyType(@ParamName("bodyType") int bodyType);

   int getBodyColor();

   IPlayerSkin setBodyColor(@ParamName("bodyColor") int bodyColor);

   int getHairType();

   IPlayerSkin setHairType(@ParamName("hairType") int hairType);

   int getHairColor();

   IPlayerSkin setHairColor(@ParamName("hairColor") int hairColor);

   int getFaceType();

   IPlayerSkin setFaceType(@ParamName("faceType") int faceType);

   int getEyesColor();

   IPlayerSkin setEyesColor(@ParamName("eyesColor") int eyesColor);

   int getPantsType();

   IPlayerSkin setPantsType(@ParamName("pantsType") int pantsType);

   int getJacketType();

   IPlayerSkin setJacketType(@ParamName("jacketType") int jacketType);

   int getShoesType();

   IPlayerSkin setShoesType(@ParamName("shoesType") int shoesType);

   List<Integer> getPeculiarities();

   IPlayerSkin setPeculiarities(@ParamName("peculiarities") List<Integer> peculiarities);

   ResourceLocation getLocation();

}
