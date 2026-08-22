package noppes.npcs.client.gui.elements.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.shared.common.util.LogWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class GameElementData {

    protected static final Gson GSON = new Gson();
    public final ICustomElement element;
    public final Object displayElement;
    public final JsonObject blockStates;
    public final JsonObject[] models;
    public final String[][] modelsOBJ;

    //@SuppressWarnings("deprecation")
    public GameElementData(ICustomElement elementIn) {
        element = elementIn;
        if (element instanceof Block) {
            File assets = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID);
            File file = new File(assets, "blockstates/custom_" + elementIn.getCustomName() + ".json");
            blockStates = readJson(file);
            //LogWriter.info("[DEBUG] blockStates "+blockStates);
            /*
            try {
                ResourceLocation blockStatesLoc = new ResourceLocation(CustomNpcs.MODID, "blockstates/custom_" + element.getCustomName() + ".json");
                Set<ICustomModelLoader> loaders = IModelLoaderRegistryMixin.getLoaders();
                ResourceLocation actual = ModelLoaderRegistry.getActualLocation(blockStatesLoc);
                LogWriter.info("[DEBUG] actual "+actual.getClass());

                ICustomModelLoader accepted;
                if (actual.getResourcePath().endsWith(".obj")) { accepted = OBJLoader.INSTANCE; }
                else if (actual.getResourcePath().endsWith(".b3d")) { accepted = B3DLoader.INSTANCE; }
                if (actual instanceof ModelResourceLocation) { accepted = ModelLoader.VariantLoader.INSTANCE; }
                else { accepted = ModelLoader.VanillaLoader.INSTANCE; }
                LogWriter.info("[DEBUG] accepted "+accepted);

                ModelBlockDefinition definition = ModelBlockDefinition.parseFromReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
                LogWriter.info("[DEBUG] definition "+definition);
            }
            catch (Exception e) { LogWriter.error(e); }
            /**/
            models = new JsonObject[0];
            modelsOBJ = new String[0][2];
            displayElement = null;
        }
        else if (element instanceof Item) {
            blockStates = null;
            models = new JsonObject[0];
            modelsOBJ = new String[0][2];
            displayElement = null;

        } else {
            blockStates = null;
            models = new JsonObject[0];
            modelsOBJ = new String[0][2];
            displayElement = null;
        }
    }

    private JsonObject readJson(File file) {
        try { return GSON.fromJson(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8), JsonObject.class); }
        catch (IOException e) { LogWriter.error(e); }
        return null;
    }

}
