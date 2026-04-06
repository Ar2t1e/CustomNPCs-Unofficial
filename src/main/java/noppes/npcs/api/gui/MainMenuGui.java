package noppes.npcs.api.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiParts;

import java.util.Objects;

public abstract class MainMenuGui {

   protected CustomGuiWrapper gui;

   public MainMenuGui(int active, EntityCustomNpc npc, IPlayer<?> player) {
      this(active, npc, player, true);
   }

   public MainMenuGui(int active, EntityCustomNpc npc, IPlayer<?> player, boolean renderHeader) {
      this.gui = new CustomGuiWrapper(player);
      this.gui.setBackgroundTexture(CustomNpcs.MODID + ":textures/gui/components.png");
      this.gui.setSize(420, 220);
      this.gui.getBackgroundRect().setPos(0, 20);
      this.gui.getBackgroundRect().setSize(420, 200);
      this.gui.getBackgroundRect().setTextureOffset(0, 0);
      this.gui.getBackgroundRect().setRepeatingTexture(64, 64, 4);
      this.gui.npc = npc;
      if (renderHeader) {
         NpcAPI api = NpcAPI.Instance();
         if (api == null) { return; }
         IButton button = (new CustomGuiButtonWrapper()).setSize(22, 20);
         ITexturedRect rect = button.getTextureRect().setTexture(CustomNpcs.MODID + ":textures/gui/components.png").setRepeatingTexture(64, 20, 3).setTextureOffset(0, 64);
         ICustomGuiComponent var10000 = button.setTextureHoverOffset(22).setPos(4, 0);
         int buttonId = 1;
         var10000.setId(buttonId);
         button.setDisplayItem(api.getIItemStack(new ItemStack(Items.ENDER_EYE)));
         button.setHoverText("menu.display");
         button.setOnPress((gui, bx) -> player.showCustomGui((new DisplayMenu(npc, player)).gui));
         this.gui.addComponent(button);
         button = (new CustomGuiButtonWrapper()).setSize(22, 20);
         button.setTextureRect(rect);
         button.setTextureHoverOffset(22).setPos(buttonId * 22 + 4, 0).setId(buttonId++);
         button.setDisplayItem(api.getIItemStack(new ItemStack(Items.PLAYER_HEAD)));
         button.setHoverText("menu.model");
         button.setOnPress((gui, bx) -> {
            CustomGuiWrapper wrapper = (new ModelMenu(npc, player)).gui;
            ((ContainerCustomGui) player.getMCEntity().containerMenu).setGui(wrapper, player.getMCEntity());
            if (player.getMCEntity() instanceof ServerPlayer) {
               Packets.send((ServerPlayer) player.getMCEntity(), new PacketGuiParts(npc.getId(), wrapper.toNBT()));
            }
         });
         this.gui.addComponent(button);
         button = (new CustomGuiButtonWrapper()).setSize(22, 20);
         button.setTextureRect(rect);
         button.setTextureHoverOffset(22).setPos(buttonId * 22 + 4, 0).setId(buttonId++);
         button.setDisplayItem(api.getIItemStack(new ItemStack(Items.CHEST)));
         button.setHoverText("menu.inventory");
         button.setOnPress((gui, bx) -> player.showCustomGui((new InventoryMenu(npc, player)).gui));
         this.gui.addComponent(button);
         button = (new CustomGuiButtonWrapper()).setSize(22, 20);
         button.setTextureRect(rect);
         button.setTextureHoverOffset(22).setPos(buttonId * 22 + 4, 0).setId(buttonId++);
         button.setDisplayItem(api.getIItemStack(new ItemStack(Items.REDSTONE)));
         button.setHoverText("menu.logic");
         button.setOnPress((gui, bx) -> player.showCustomGui((new LogicMenu(npc, player)).gui));
         this.gui.addComponent(button);
         button = (new CustomGuiButtonWrapper()).setSize(22, 20);
         button.setTextureRect(rect);
         button.setTextureHoverOffset(22).setPos(buttonId * 22 + 4, 0).setId(buttonId++);
         button.setDisplayItem(api.getIItemStack(new ItemStack(Items.DIAMOND_CHESTPLATE)));
         button.setHoverText("menu.health");
         button.setOnPress((gui, bx) -> player.showCustomGui((new HealthMenu(npc, player)).gui));
         this.gui.addComponent(button);
         button = (new CustomGuiButtonWrapper()).setSize(22, 20);
         button.setTextureRect(rect);
         button.setTextureHoverOffset(22).setPos(buttonId * 22 + 4, 0).setId(buttonId++);
         button.setDisplayItem(api.getIItemStack(new ItemStack(Items.TOTEM_OF_UNDYING)));
         button.setHoverText("menu.death");
         button.setOnPress((gui, bx) -> player.showCustomGui((new DeathMenu(npc, player)).gui));
         this.gui.addComponent(button);
         button = (new CustomGuiButtonWrapper()).setSize(22, 20);
         button.setTextureRect(rect);
         button.setTextureHoverOffset(22).setPos(buttonId * 22 + 4, 0).setId(buttonId++);
         button.setDisplayItem(api.getIItemStack(new ItemStack(Items.IRON_BOOTS)));
         button.setHoverText("menu.movement");
         button.setOnPress((gui, bx) -> player.showCustomGui((new MovementMenu(npc, player)).gui));
         this.gui.addComponent(button);
         button = (new CustomGuiButtonWrapper()).setSize(22, 20);
         button.setTextureRect(rect);
         button.setTextureHoverOffset(22).setPos(buttonId * 22 + 4, 0).setId(buttonId++);
         button.setDisplayItem(api.getIItemStack(new ItemStack(Items.DIAMOND_SWORD)));
         button.setHoverText("stats.meleeproperties");
         button.setOnPress((gui, bx) -> player.showCustomGui((new MeleeMenu(npc, player)).gui));
         this.gui.addComponent(button);
         button = (new CustomGuiButtonWrapper()).setSize(22, 20);
         button.setTextureRect(rect);
         button.setTextureHoverOffset(22).setPos(buttonId * 22 + 4, 0).setId(buttonId++);
         button.setDisplayItem(api.getIItemStack(new ItemStack(Items.ARROW)));
         button.setOnPress((gui, bx) -> player.showCustomGui((new DisplayMenu(npc, player)).gui));
         this.gui.addComponent(button);
         button = (new CustomGuiButtonWrapper()).setSize(22, 20);
         button.setTextureRect(rect);
         button.setTextureHoverOffset(22).setPos(buttonId * 22 + 4, 0).setId(buttonId);
         button.setDisplayItem(api.getIItemStack(new ItemStack(Items.REDSTONE)));
         button.setOnPress((gui, bx) -> player.showCustomGui((new DisplayMenu(npc, player)).gui));
         this.gui.addComponent(button);
         IButton b = (IButton)this.gui.getComponent(active);
         b.setEnabled(false);
         b.setPos(b.getPosX(), 3);
      }
   }

   public static void open(Player player, EntityCustomNpc npc) {
      IPlayer<?> p = (IPlayer<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(player);
      DisplayMenu menu = new DisplayMenu(npc, p);
      p.showCustomGui(menu.gui);
   }

}
