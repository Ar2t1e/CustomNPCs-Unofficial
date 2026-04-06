package noppes.npcs.client.controllers;

import java.io.File;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.controllers.ServerCloneController;

public class ClientCloneController extends ServerCloneController {
   public static ClientCloneController Instance = new ClientCloneController();

   public File getDir() {
      File dir = new File(CustomNpcs.Dir, "clones");
      if (!dir.exists()) { dir.mkdir(); }
      return dir;
   }

   public IEntity<?> spawn(double x, double y, double z, int tab, String name, IWorld level) {
      return null;
   }

}
