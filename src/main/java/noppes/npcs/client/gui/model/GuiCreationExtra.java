package noppes.npcs.client.gui.model;

import java.lang.reflect.Method;
import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.controllers.CobblemonHelper;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityFakeLiving;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiCreationExtra extends GuiCreationScreenInterface implements ICustomScrollListener, ITextfieldListener {

   protected final String[] ignoredTags = new String[]{"CanBreakDoors", "Bred", "PlayerCreated", "HasReproduced"};
   protected final String[] grimmsTags = new String[]{"DataSkin", "DataHair", "DataFace", "DataUniform", "DataGemstone", "DataVisor", "DataGloves", "DataCape"};
   protected GuiCustomScrollNop scroll;
   protected Map<String, GuiType> data = new HashMap<>();
   protected GuiType selected;
   public int nextAvailableFieldId = 0;

   public GuiCreationExtra(EntityNPCInterface npc) {
      super(npc);
      active = 2;
   }

   @Override
   public void init() {
      super.init();
      if (entity == null) { return; }
      data = getData(entity);
      if (scroll == null) {
         List<String> list = new ArrayList<>(data.keySet());
         scroll = addScroll(0).setList(list);
         if (!list.isEmpty()) { scroll.setSelected(list.get(0)); }
      }
      selected = data.get(scroll.getSelected());
      if (selected != null) {
         add(scroll.setPos(guiLeft, guiTop + 46)
                 .setSize(100, imageHeight - 74));
         selected.init();
      }
   }

   public Map<String, GuiType> getData(LivingEntity entity) {
      Map<String, GuiType> data = new HashMap<>();
      CompoundTag compound = getExtras(entity);
      Set<String> keys = compound.getAllKeys();
      Iterator<String> var5 = keys.iterator();
      while(true) {
         String name;
         do {
            if (!var5.hasNext()) {
               if (PixelmonHelper.isPixelmon(entity)) {
                  data.put("Model", new GuiCreationExtra.GuiTypePixelmon("Model"));
               }
               if (CobblemonHelper.isPokemon(entity)) {
                  data.put("CobblemonModel", new GuiCreationExtra.GuiTypeCobblemon("CobblemonModel"));
               }

               if (Objects.equals(entity.getEncodeId(), "tgvstyle.Dog")) {
                  data.put("Breed", new GuiCreationExtra.GuiTypeDoggyStyle("Breed"));
               }
               return data;
            }
            name = var5.next();
         } while(isIgnored(name));

         Tag base = compound.get(name);
         if (name.equals("Age")) {
            data.put("Child", new GuiCreationExtra.GuiTypeBoolean("Child", entity.isBaby()));
         } else if (name.equals("Color") && Objects.requireNonNull(base).getId() == 1) {
            data.put("Color", new GuiCreationExtra.GuiTypeByte("Color", compound.getByte("Color")));
         } else if (base != null && base.getId() == 3) {
            data.put(name, new GuiCreationExtra.GuiTypeInt(name, compound.getInt(name)));
         } else if (base != null && base.getId() == 1) {
            byte b = ((ByteTag)base).getAsByte();
            if (b == 0 || b == 1) {
               if (playerdata.extra.contains(name)) {
                  b = playerdata.extra.getByte(name);
               }
               data.put(name, new GuiCreationExtra.GuiTypeBoolean(name, b == 1));
            }
         }
      }
   }

   private boolean isIgnored(String tag) {
      for (String s : ignoredTags) {
         if (s.equals(tag)) {
            return true;
         }
      }
      return false;
   }

   private boolean isGrimms(String tag) {
      for (String s : grimmsTags) {
         if (s.equals(tag)) {
            return true;
         }
      }
      return false;
   }

   @SuppressWarnings("unchecked")
   private void updateTexture() {
      LivingEntity entity = playerdata.getEntity(npc);
      if (minecraft == null) { minecraft = Minecraft.getInstance(); }
      EntityRenderer<LivingEntity> render = (EntityRenderer<LivingEntity>) minecraft.getEntityRenderDispatcher().getRenderer(entity);
      npc.display.setSkinTexture(render.getTextureLocation(entity).toString());
   }

   private CompoundTag getExtras(LivingEntity entity) {
      CompoundTag fake = new CompoundTag();
      (new EntityFakeLiving(entity.level())).addAdditionalSaveData(fake);
      CompoundTag compound = new CompoundTag();
      try { entity.addAdditionalSaveData(compound); } catch (Throwable ignored) {}
      Set<String> keys = fake.getAllKeys();
      for (String name : keys) { compound.remove(name); }
      return compound;
   }

   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) {
      if (scroll.id == 0) { init(); }
      else if (selected != null) { selected.scrollClicked(scroll); }
   }

   @Override
   public void buttonEvent(GuiButtonNop guiButton) {
      if (selected != null) { selected.buttonEvent(guiButton);}
   }

   @Override
   public void unFocused(GuiTextFieldNop textfield) {
      if (selected != null) { selected.unFocused(textfield); }
   }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

   class GuiTypeBoolean extends GuiType {
      private boolean bo;

      public GuiTypeBoolean(String name, boolean boIn) {
         super(name);
         bo = boIn;
      }

      @Override
      public void init() {
         addYesNo(11, guiLeft + 120, guiTop + 50, bo)
                 .setSize(60, 20);
      }

      @Override
      public void buttonEvent(GuiButtonNop button) {
         if (button.id == 11) {
            bo = ((GuiButtonYesNo)button).getBoolean();
            if (name.equals("Child")) {
               playerdata.extra.putInt("Age", bo ? -24000 : 0);
               playerdata.clearEntity();
            } else {
               playerdata.extra.putBoolean(name, bo);
               playerdata.clearEntity();
               updateTexture();
            }
         }
      }
   }

   class GuiTypeByte extends GuiType {

      protected final byte b;

      public GuiTypeByte(String name, byte bo) {
         super(name);
         b = bo;
      }

      @Override
      public void init() {
         Object[] numbs = new Object[16];
         for (int i = 0; i < 16; i++) { numbs[i] = i; }
         addButton(11, guiLeft + 120, guiTop + 45, true, b, numbs)
                 .setSize(50, 20);
      }

      @Override
      public void buttonEvent(GuiButtonNop button) {
         if (button.id == 11) {
            playerdata.extra.putByte(name, (byte)button.getValue());
            playerdata.clearEntity();
            updateTexture();
         }
      }
   }

   class GuiTypeInt extends GuiType {

      protected final int initVal;
      protected int fieldId;

      public GuiTypeInt(String name, int b) {
         super(name);
         initVal = b;
         fieldId = nextAvailableFieldId++;
      }

      @Override
      public void init() {
         addTextField(11, guiLeft + 120, guiTop + 45, 50, 20, initVal)
                 .setNumbersOnly();
      }

      @Override
      public void unFocused(GuiTextFieldNop textfield) {
         if (textfield.id == 11) {
            playerdata.extra.putInt(name, textfield.getInteger());
            playerdata.clearEntity();
            updateTexture();
         }
      }
   }

   class GuiTypePixelmon extends GuiType {

      public GuiTypePixelmon(String name) {
         super(name);
      }

      @Override
      public void init() {
         addScroll(1)
                 .setPos(guiLeft + 120, guiTop + 20)
                 .setSize(120, 200)
                 .setList(PixelmonHelper.getPixelmonList())
                 .setSelected(PixelmonHelper.getName(entity));
      }

      @Override
      public void scrollClicked(GuiCustomScrollNop scroll) {
         String name = scroll.getSelected();
         playerdata.setExtra(entity, "name", name);
         updateTexture();
      }
   }

   class GuiTypeCobblemon extends GuiType {
      public GuiTypeCobblemon(String name) {
         super(name);
      }

      @Override
      public void init() {
         GuiCustomScrollNop scrollIn = addScroll(1)
                 .setPos(guiLeft + 120, guiTop + 20)
                 .setSize(120, 200)
                 .setList(CobblemonHelper.getTypes());
         ResourceLocation rt = CobblemonHelper.getType(entity);
         if (rt != null) { scrollIn.setSelected(rt.toString()); }
      }

      @Override
      public void scrollClicked(GuiCustomScrollNop scroll) {
         String name = scroll.getSelected();
         playerdata.setExtra(entity, "CobblemonModel", name);
         updateTexture();
      }
   }

   class GuiTypeDoggyStyle extends GuiType {
      public GuiTypeDoggyStyle(String name) {
         super(name);
      }

      @Override
      public void init() {
         Enum<?> breed = null;
         try {
            Method method = entity.getClass().getMethod("getBreedID");
            breed = (Enum<?>) method.invoke(entity);
         } catch (Exception ignored) {}
         if (breed !=null) {
            Object[] numbs = new Object[16];
            for (int i = 0; i < 27; i++) { numbs[i] = i; }
            addButton(11, guiLeft + 120, guiTop + 45, true, breed.ordinal(), numbs)
                    .setSize(50, 20);
         }
      }

      @Override
      public void buttonEvent(GuiButtonNop button) {
         if (button.id == 11) {
            LivingEntity entity = playerdata.getEntity(npc);
            playerdata.setExtra(entity, "breed", button.getMessage().toString());
            updateTexture();
         }
      }
   }

}
