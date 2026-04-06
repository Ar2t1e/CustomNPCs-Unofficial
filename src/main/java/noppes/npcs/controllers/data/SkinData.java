package noppes.npcs.controllers.data;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.IPlayerSkin;
import noppes.npcs.command.arguments.URLArgument;
import noppes.npcs.controllers.PlayerSkinController;
import noppes.npcs.shared.SharedReferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkinData implements IPlayerSkin {

    private Type type;
    private String url = null;
    private ResourceLocation location = null;
    private ResourceLocation defaultSkin = null;

    private int gender = 0;
    private int body = 0;
    private int bodyColor = 0;
    private int hair = 0;
    private int hairColor = 0;
    private int face = 0;
    private int eyesColor = 0;
    private int legs = 0;
    private int jacket = 0;
    public int shoes = 0;
    private final List<Integer> peculiarities = new ArrayList<>();
    private ResourceLocation cacheResLoc = null;

    public SkinData copy() {
        SkinData skinData = new SkinData();
        skinData.load(save());
        return skinData;
    }

    public ResourceLocation getDefault() { return defaultSkin ; }

    public void setIsDefault() { defaultSkin = location; }

    public static SkinData create(Type typeIn, ResourceLocation locationIn) {
        SkinData sd = new SkinData();
        sd.type = typeIn;
        sd.location = locationIn;
        return sd;
    }

    public void load(CompoundTag nbtSkin) {
        url = null;
        location = null;
        cacheResLoc = null;
        type = switch (nbtSkin.getString("Type").toLowerCase()) {
            case "cape" -> Type.CAPE;
            case "elytra" -> Type.ELYTRA;
            default -> Type.SKIN;
        };
        CompoundTag composite = nbtSkin.getCompound("Composite");
        gender = composite.getInt("gender");
        body = composite.getInt("body");
        bodyColor = composite.getInt("bodyColor");
        hair = composite.getInt("hair");
        hairColor = composite.getInt("hairColor");
        face = composite.getInt("face");
        eyesColor = composite.getInt("eyesColor");
        legs = composite.getInt("leg");
        jacket = composite.getInt("jacket");
        shoes = composite.getInt("shoes");
        peculiarities.clear();
        peculiarities.addAll(Arrays.stream(composite.getIntArray("peculiarities")).boxed().toList());
        if (nbtSkin.contains("CacheResLoc", 8)) { cacheResLoc = new ResourceLocation(nbtSkin.getString("CacheResLoc")); }
        if (nbtSkin.contains("Location", 8)) {
            url = null;
            location = new ResourceLocation(nbtSkin.getString("Location"));
            cacheResLoc = null;
        }
        if (nbtSkin.contains("URL", 8)) {
            url = nbtSkin.getString("URL");
            location = null;
            cacheResLoc = null;
        }
        if (nbtSkin.contains("Default", 8)) { defaultSkin = new ResourceLocation(nbtSkin.getString("Default")); }
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putString("Type", switch (type) {
            case CAPE -> "cape";
            case ELYTRA -> "elytra";
            default -> "skin";
        });
        if (url != null && !url.isEmpty()) { compound.putString("URL", url); }
        if (location != null) { compound.putString("Location", location.toString()); }
        if (defaultSkin != null) { compound.putString("Default", defaultSkin.toString()); }
        CompoundTag composite = new CompoundTag();
        composite.putInt("gender", gender);
        composite.putInt("body", body);
        composite.putInt("bodyColor", bodyColor);
        composite.putInt("hair", hair);
        composite.putInt("hairColor", hairColor);
        composite.putInt("face", face);
        composite.putInt("eyesColor", eyesColor);
        composite.putInt("leg", legs);
        composite.putInt("jacket", jacket);
        composite.putInt("shoes", shoes);
        composite.putIntArray("peculiarities", peculiarities);
        if (cacheResLoc != null) { composite.putString("CacheResLoc", cacheResLoc.toString()); }
        compound.put("Composite", composite);

        return compound;
    }

    public Type type() { return type; }

    @Override
    public int getType() {
        return switch (type) {
            case CAPE -> 1;
            case ELYTRA -> 2;
            default -> 0;
        };
    }

    @Override
    public boolean isLocation() { return location != null && !location.equals(defaultSkin); }

    @Override
    public boolean isUrl() { return url != null && !url.isEmpty() && url.startsWith(URLArgument.HTTPS_START) && location == null && cacheResLoc == null; }

    @Override
    public String getUrl() { return url; }

    public void setUrl(String newUrl) {
        url = newUrl;
        location = null;
        cacheResLoc = null;
        PlayerSkinController.getInstance().update(this);
    }

    public void setLocation(String newLocation) {
        url = null;
        location = new ResourceLocation(newLocation);
        cacheResLoc = null;
        PlayerSkinController.getInstance().update(this);
    }

    @Override
    public int getGenderType() { return gender; }

    @Override
    public IPlayerSkin setGender(int type) {
        if (type != gender) {
            gender = type % 3;
            markChanged();
        }
        return this;
    }

    @Override
    public String getGender() { return gender == 0 ? "male" : gender == 1 ? "female" : "hermaphrodite"; }

    @Override
    public int getBodyType() { return body; }

    @Override
    public IPlayerSkin setBodyType(int bodyIn) {
        if (body != bodyIn) {
            body = bodyIn;
            markChanged();
        }
        return this;
    }

    @Override
    public int getBodyColor() { return bodyColor; }

    @Override
    public IPlayerSkin setBodyColor(int bodyColorIn) {
        if (bodyColor != bodyColorIn) {
            bodyColor = bodyColorIn;
            markChanged();
        }
        return this;
    }

    public int getHairType() { return hair; }

    @Override
    public IPlayerSkin setHairType(int hairIn) {
        if (hair != hairIn) {
            hair = hairIn;
            markChanged();
        }
        return this;
    }

    @Override
    public int getHairColor() { return hairColor; }

    @Override
    public IPlayerSkin setHairColor(int hairColorIn) {
        if (hairColor != hairColorIn) {
            hairColor = hairColorIn;
            markChanged();
        }
        return this;
    }

    @Override
    public int getFaceType() { return face; }

    @Override
    public IPlayerSkin setFaceType(int faceIn) {
        if (face != faceIn) {
            face = faceIn;
            markChanged();
        }
        return this;
    }

    @Override
    public int getEyesColor() { return eyesColor; }

    @Override
    public IPlayerSkin setEyesColor(int eyesColorIn) {
        if (eyesColor != eyesColorIn) {
            eyesColor = eyesColorIn;
            markChanged();
        }
        return this;
    }

    @Override
    public int getPantsType() { return legs; }

    @Override
    public IPlayerSkin setPantsType(int legsIn) {
        if (legs != legsIn) {
            legs = legsIn;
            markChanged();
        }
        return this;
    }

    @Override
    public int getJacketType() { return jacket; }

    @Override
    public IPlayerSkin setJacketType(int jacketIn) {
        if (jacket != jacketIn) {
            jacket = jacketIn;
            markChanged();
        }
        return this;
    }

    @Override
    public int getShoesType() { return shoes; }

    @Override
    public IPlayerSkin setShoesType(int shoesIn) {
        if (shoes != shoesIn) {
            shoes = shoesIn;
            markChanged();
        }
        return this;
    }

    @Override
    public List<Integer> getPeculiarities() { return peculiarities; }

    @Override
    public IPlayerSkin setPeculiarities(List<Integer> peculiaritiesIn) {
        peculiarities.clear();
        peculiarities.addAll(peculiaritiesIn);
        markChanged();
        return this;
    }

    public void markChanged() {
        calculateResLoc();
        PlayerSkinController.getInstance().update(this);
    }
    @Override
    public boolean isActive() { return cacheResLoc != null && url == null && location == null; }

    public void calculateResLoc() {
        StringBuilder path = new StringBuilder("textures/entity/custom/");
        path.append(getGender()).append("_");
        path.append(getBodyType()).append("_");
        path.append(getBodyColor()).append("_");
        path.append(getHairType()).append("_");
        path.append(getHairColor()).append("_");
        path.append(getFaceType()).append("_");
        path.append(getEyesColor()).append("_");
        path.append(getPantsType()).append("_");
        path.append(getJacketType()).append("_");
        path.append(getShoesType());
        for (int id : getPeculiarities()) { path.append("_").append(id); }
        path.append(".png");
        url = null;
        location = null;
        cacheResLoc = new ResourceLocation(CustomNpcs.MODID, path.toString());
    }

    public boolean isValid() {
        return isUrl() || (isLocation()) || cacheResLoc != null || defaultSkin != null;
    }

    @Override
    public ResourceLocation getLocation() {
        if (isLocation()) { return location; }
        if (isUrl()) { return new ResourceLocation(SharedReferences.modid(), "textures/entity/custom/url/" + (url + true).hashCode() + ".png"); }
        return cacheResLoc != null ? cacheResLoc : defaultSkin;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getPartResLocByNumber(ResourceManager textureManager, String name, int partNum) {
        ResourceLocation loc = new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/" + getGender() + "/" + name + "/" + partNum + ".png");
        if (textureManager.getResource(loc).isEmpty()) {
            loc = new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/" + getGender() + "/" + name + "/0.png");
        }
        return textureManager.getResource(loc).isPresent() ? loc : null;
    }

    public void reset(String newData) {
        if (newData == null) { return; }
        url = null;
        location = null;
        if (newData.startsWith(URLArgument.HTTPS_START)) {
            url = newData;
            return;
        }
        String key = CustomNpcs.MODID + ":textures/entity/custom/";
        if (newData.startsWith(key)) {
            String[] data = newData.substring(key.length()).split("_");
            if (data.length > 8) {
                gender = data[0].equals("male") ? 0 : data[0].equals("female") ? 1 : 2;
                try { body = Integer.parseInt(data[1]); } catch (NumberFormatException e) { body = 0; }
                try { bodyColor = Integer.parseInt(data[2]); } catch (NumberFormatException e) { bodyColor = 0; }
                try { hair = Integer.parseInt(data[3]); } catch (NumberFormatException e) { hair = 0; }
                try { hairColor = Integer.parseInt(data[4]); } catch (NumberFormatException e) { hairColor = 0; }
                try { face = Integer.parseInt(data[5]); } catch (NumberFormatException e) { face = 0; }
                try { eyesColor = Integer.parseInt(data[6]); } catch (NumberFormatException e) { eyesColor = 0; }
                try { legs = Integer.parseInt(data[7]); } catch (NumberFormatException e) { legs = 0; }
                try { jacket = Integer.parseInt(data[8]); } catch (NumberFormatException e) { jacket = 0; }
                try { shoes = Integer.parseInt(data[9]); } catch (NumberFormatException e) { shoes = 0; }
                peculiarities.clear();
                for (int i = 10; i < data.length; i++) {
                    try { peculiarities.add(Integer.parseInt(data[i])); } catch (NumberFormatException ignored) { }
                }
                calculateResLoc();
                return;
            }
        }
        if (!newData.contains(":")) { return; }
        location = new ResourceLocation(newData);
    }

    public String toString() {
        String clazz = "SkinData: {type: " + type + "; ";
        boolean bo = false;
        if (isUrl()) { clazz += "url: \"" + url + "\""; bo = true; }
        if (location != null) {
            if (bo) {clazz += "; "; }
            clazz += "location: \"" + location + "\"";
            bo = true;
        }
        if (cacheResLoc != null) {
            if (bo) {clazz += "; "; }
            clazz += "composite: \"" + cacheResLoc + "\"";
            bo = true;
        }
        if (defaultSkin != null) {
            if (bo) {clazz += "; "; }
            clazz += "default: \"" + defaultSkin + "\"";
        }
        return clazz + "}";
    }

    public boolean remove() {
        if (defaultSkin != null) {
            url = null;
            cacheResLoc = null;
            location = defaultSkin;
            return true;
        }
        return false;
    }

}
