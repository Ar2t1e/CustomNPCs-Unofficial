package noppes.npcs.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.util.GuiTooltipUtils;
import noppes.npcs.containers.ContainerMerchantAdd;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMerchantSetSlot;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.GuiBasicContainer;
import noppes.npcs.shared.client.gui.components.GuiWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class GuiMerchantAdd extends GuiBasicContainer<ContainerMerchantAdd> {

   // New fields from Unofficial (BetaZavr)
   private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
   private static final Component TRADES_LABEL = Component.translatable("merchant.trades");
   private int shopItem;
   private final TradeOfferButton[] tradeOfferButtons = new TradeOfferButton[7];
   int scrollOff;

   private GuiGraphics graphics;
   private int mouseX;
   private int mouseY;

   protected final List<Component> hoverText = new ArrayList<>();

   public GuiMerchantAdd(ContainerMerchantAdd container, Inventory inv, Component titleIn) {
      super(container, inv, titleIn);
      imageWidth = 276;
      inventoryLabelX = 107;
   }

   @Override
   public boolean hasSubGui() { return false; }

   @Override
   public Screen getSubGui() { return null; }

   @Override
   public Screen getParent() { return null; }

   @Override
   public GuiWrapper getWrapper() {
      return null;
   }

   @Override
   public void init() {
      super.init();
      int i = (width - imageWidth) / 2;
      int j = (height - imageHeight) / 2;
      int k = j + 16 + 2;
      for(int l = 0; l < 7; ++l) {
         tradeOfferButtons[l] = addRenderableWidget(new TradeOfferButton(i + 5, k, l, (button) -> {
             if (button instanceof TradeOfferButton) {
                 shopItem = ((TradeOfferButton) button).getIndex() + scrollOff;
                 postButtonClick();
             }
         }));
         k += 20;
      }
   }

   // New fields from Unofficial (BetaZavr)
   @Override
   public void drawWait(GuiGraphics graphics) {
      if (minecraft == null) { minecraft = Minecraft.getInstance(); }
      if (minecraft.level == null) { return; }
      int x = minecraft.getWindow().getGuiScaledWidth() / 2;
      int y = minecraft.getWindow().getGuiScaledHeight() / 2 - 30;
      PoseStack matrixStack = graphics.pose();
      graphics.drawCenteredString(minecraft.font, Component.translatable("gui.wait"), width / 2, height / 2, CustomNpcs.MainColor.getRGB());
      int pos_0 = (int) Math.floor((double) (minecraft.level.getGameTime() % 16) / 2.0d);
      matrixStack.pushPose();
      RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
      graphics.blit(GuiBasic.INFO, x + GuiBasic.getPosX.apply(pos_0) - 1, y + GuiBasic.getPosY.apply(pos_0) - 1, 0, 12, 6, 6);
      int pos_1 = pos_0 - 1;
      if (pos_1 < 0) { pos_1 += 8; }
      graphics.blit(GuiBasic.INFO, x + GuiBasic.getPosX.apply(pos_1), y + GuiBasic.getPosY.apply(pos_1), 6, 12, 5, 5);
      int pos_2 = pos_0 - 2;
      if (pos_2 < 0) { pos_2 += 8; }
      graphics.blit(GuiBasic.INFO, x + GuiBasic.getPosX.apply(pos_2) + 1, y + GuiBasic.getPosY.apply(pos_2) + 1, 11, 12, 4, 4);
      matrixStack.popPose();
   }

   @Override
   protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
      /*int i = menu.getTraderLevel();
      if (i > 0 && i <= 5 && menu.showProgressBar()) {
         Component component = this.title.copy().append(LEVEL_SEPARATOR).append(Component.translatable("merchant.level." + i));
         int j = this.font.width(component);
         int k = 49 + this.imageWidth / 2 - j / 2;
         graphics.drawString(this.font, component, k, 6, 4210752, false);
      } else {*/
      graphics.drawString(this.font, this.title, 49 + this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 4210752, false);
      //}

      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
      int l = this.font.width(TRADES_LABEL);
      graphics.drawString(this.font, TRADES_LABEL, 5 - l / 2 + 48, 6, 4210752, false);
   }

   @Override
   protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      graphics.blit(VILLAGER_LOCATION, i, j, 0, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
      /*MerchantOffers merchantoffers = menu.getOffers();
      if (!merchantoffers.isEmpty()) {
         int k = this.shopItem;
         if (k < 0 || k >= merchantoffers.size()) {
            return;
         }
         MerchantOffer merchantoffer = (MerchantOffer)merchantoffers.get(k);
         if (merchantoffer.isOutOfStock()) {
            p_283072_.blit(VILLAGER_LOCATION, this.leftPos + 83 + 99, this.topPos + 35, 0, 311.0F, 0.0F, 28, 21, 512, 256);
         }
      }*/
   }

   private void renderProgressBar(@NotNull GuiGraphics graphics, int x, int y, MerchantOffer merchantOffer) {
      /*int i = menu.getTraderLevel();
      int j = menu.getTraderXp();
      if (i < 5) {
         graphics.blit(VILLAGER_LOCATION, x + 136, y + 16, 0, 0.0F, 186.0F, 102, 5, 512, 256);
         int k = VillagerData.getMinXpPerLevel(i);
         if (j >= k && VillagerData.canLevelUp(i)) {
            float f = 100.0F / (float)(VillagerData.getMaxXpPerLevel(i) - k);
            int i1 = Math.min(Mth.floor(f * (float)(j - k)), 100);
            graphics.blit(VILLAGER_LOCATION, x + 136, y + 16, 0, 0.0F, 191.0F, i1 + 1, 5, 512, 256);
            int j1 = menu.getFutureTraderXp();
            if (j1 > 0) {
               int k1 = Math.min(Mth.floor((float)j1 * f), 100 - i1);
               graphics.blit(VILLAGER_LOCATION, x + 136 + i1 + 1, y + 16 + 1, 0, 2.0F, 182.0F, k1, 3, 512, 256);
            }
         }
      }*/
   }

   private void renderScroller(@NotNull GuiGraphics graphics, int p_283154_, int p_281664_, MerchantOffers p_282877_) {
      int i = p_282877_.size() + 1 - 7;
      if (i > 1) {
         int j = 139 - (27 + (i - 1) * 139 / i);
         int k = 1 + j / i + 139 / i;
         int i1 = Math.min(113, this.scrollOff * k);
         if (this.scrollOff == i - 1) { i1 = 113; }
         graphics.blit(VILLAGER_LOCATION, p_283154_ + 94, p_281664_ + 18 + i1, 0, 0.0F, 199.0F, 6, 27, 512, 256);
      } else {
         graphics.blit(VILLAGER_LOCATION, p_283154_ + 94, p_281664_ + 18, 0, 6.0F, 199.0F, 6, 27, 512, 256);
      }
   }

   private void postButtonClick() {
      menu.tradeContainer.setSelectionHint(shopItem);
      Packets.sendServer(new SPacketMerchantSetSlot(shopItem));
   }

   private void renderButtonArrows(@NotNull GuiGraphics graphics, MerchantOffer merchantOffer, int x, int y) {
      RenderSystem.enableBlend();
      if (merchantOffer.isOutOfStock()) {
         graphics.blit(VILLAGER_LOCATION, x + 5 + 35 + 20, y + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
      } else {
         graphics.blit(VILLAGER_LOCATION, x + 5 + 35 + 20, y + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
      }
   }

   private void renderAndDecorateCostA(@NotNull GuiGraphics graphics, ItemStack stack_0, ItemStack stack_1, int x, int y) {
      graphics.renderFakeItem(stack_0, x, y);
      if (stack_1.getCount() == stack_0.getCount()) {
         graphics.renderItemDecorations(this.font, stack_0, x, y);
      } else {
         graphics.renderItemDecorations(this.font, stack_1, x, y, stack_1.getCount() == 1 ? "1" : null);
         graphics.pose().pushPose();
         graphics.pose().translate(0.0F, 0.0F, 200.0F);
         String count = stack_0.getCount() == 1 ? "1" : String.valueOf(stack_0.getCount());
         this.font.drawInBatch(count, (float)(x + 14) + 19.0F - 2.0F - (float)this.font.width(count), (float)y + 6.0F + 3.0F, 16777215, true, graphics.pose().last().pose(), graphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880, false);
         graphics.pose().popPose();
         graphics.pose().pushPose();
         graphics.pose().translate(0.0F, 0.0F, 300.0F);
         graphics.blit(VILLAGER_LOCATION, x + 7, y + 12, 0, 0.0F, 176.0F, 9, 2, 512, 256);
         graphics.pose().popPose();
      }
   }

   private boolean canScroll(int p_99141_) { return p_99141_ > 7; }

   @Override
   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (menu.trader == null) {
         onClose();
         return;
      }
      super.render(graphics, mouseX, mouseY, partialTicks);
      this.graphics = graphics;
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      MerchantOffers merchantoffers = menu.getOffers();
      if (!merchantoffers.isEmpty()) {
         int i = (width - imageWidth) / 2;
         int j = (height - imageHeight) / 2;
         int k = j + 16 + 1;
         int l = i + 5 + 5;
         renderScroller(graphics, i, j, merchantoffers);
         int i1 = 0;
         Iterator<MerchantOffer> var11 = merchantoffers.iterator();

         while(true) {
            MerchantOffer merchantoffer;
            while(var11.hasNext()) {
               merchantoffer = var11.next();
               if (canScroll(merchantoffers.size()) && (i1 < scrollOff || i1 >= 7 + scrollOff)) {
                  ++i1;
               } else {
                  ItemStack itemstack = merchantoffer.getBaseCostA();
                  ItemStack itemstack1 = merchantoffer.getCostA();
                  ItemStack itemstack2 = merchantoffer.getCostB();
                  ItemStack itemstack3 = merchantoffer.getResult();
                  graphics.pose().pushPose();
                  graphics.pose().translate(0.0F, 0.0F, 100.0F);
                  int j1 = k + 2;
                  renderAndDecorateCostA(graphics, itemstack1, itemstack, l, j1);
                  if (!itemstack2.isEmpty()) {
                     graphics.renderFakeItem(itemstack2, i + 5 + 35, j1);
                     graphics.renderItemDecorations(font, itemstack2, i + 5 + 35, j1);
                  }
                  renderButtonArrows(graphics, merchantoffer, i, j1);
                  graphics.renderFakeItem(itemstack3, i + 5 + 68, j1);
                  graphics.renderItemDecorations(font, itemstack3, i + 5 + 68, j1);
                  graphics.pose().popPose();
                  k += 20;
                  ++i1;
               }
            }

            int k1 = shopItem;
            /*merchantoffer = merchantoffers.get(k1);
            if (menu.showProgressBar()) { renderProgressBar(graphics, i, j, merchantoffer); }

            if (merchantoffer.isOutOfStock() && isHovering(186, 35, 22, 21, (double)mouseX, (double)mouseY) && menu.canRestock()) {
               graphics.renderTooltip(font, DEPRECATED_TOOLTIP, mouseX, mouseY);
            }*/

            TradeOfferButton[] var19 = tradeOfferButtons;
            int var20 = var19.length;

            for (TradeOfferButton merchantscreen$tradeofferbutton : var19) {
               if (merchantscreen$tradeofferbutton.isHoveredOrFocused()) {
                  merchantscreen$tradeofferbutton.renderToolTip(graphics, mouseX, mouseY);
               }
               merchantscreen$tradeofferbutton.visible = merchantscreen$tradeofferbutton.index < menu.getOffers().size();
            }

            RenderSystem.enableDepthTest();
            break;
         }
      }
      renderTooltip(graphics, mouseX, mouseY);
   }

   @Override
   public boolean mouseScrolled(double p_99127_, double p_99128_, double p_99129_) {
      /*int i = menu.getOffers().size();
      if (canScroll(i)) {
         int j = i - 7;
         scrollOff = Mth.clamp((int)((double)this.scrollOff - p_99129_), 0, j);
      }*/
      return true;
   }

   @Override
   public boolean mouseDragged(double p_99135_, double p_99136_, int p_99137_, double p_99138_, double p_99139_) {
      /*int i = menu.getOffers().size();
      if (isDragging) {
         int j = this.topPos + 18;
         int k = j + 139;
         int l = i - 7;
         float f = ((float)p_99136_ - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
         f = f * (float)l + 0.5F;
         this.scrollOff = Mth.clamp((int)f, 0, l);
         return true;
      } else {*/
         return super.mouseDragged(p_99135_, p_99136_, p_99137_, p_99138_, p_99139_);
      //}
   }

   @Override
   public boolean mouseClicked(double p_99131_, double p_99132_, int p_99133_) {
      /*this.isDragging = false;
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      if (this.canScroll(menu.getOffers().size()) && p_99131_ > (double)(i + 94) && p_99131_ < (double)(i + 94 + 6) && p_99132_ > (double)(j + 18) && p_99132_ <= (double)(j + 18 + 139 + 1)) {
         this.isDragging = true;
      }*/
      return super.mouseClicked(p_99131_, p_99132_, p_99133_);
   }

   @Override
   public List<Component> getHoverText() { return hoverText; }

   @Override
   public void setHoverText(@Nullable List<Component> newHoverText) {
      hoverText.clear();
      if (newHoverText != null && !newHoverText.isEmpty()) { hoverText.addAll(newHoverText); }
   }

   @Override
   public void drawHoverText(String text, Object... args) {
      if (!CustomNpcs.ShowDescriptions) { return; }
      if (text == null) {
         if (!hoverText.isEmpty()) {
            GuiTooltipUtils.renderTooltip(graphics, font, hoverText, Optional.empty(), mouseX - leftPos, mouseY - topPos);
         }
         hoverText.clear();
         return;
      }
      setHoverText(text, args);
      if (!hoverText.isEmpty()) {
         GuiTooltipUtils.renderTooltip(graphics, font, hoverText, Optional.empty(), mouseX - leftPos, mouseY - topPos);
         hoverText.clear();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class TradeOfferButton extends Button {
      final int index;

      public TradeOfferButton(int x, int y, int slot, Button.OnPress p_99208_) {
         super(x, y, 88, 20, CommonComponents.EMPTY, p_99208_, DEFAULT_NARRATION);
         index = slot;
         visible = false;
      }

      public int getIndex() { return index; }

      public void renderToolTip(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
         if (isHovered && menu.getOffers().size() > index + scrollOff) {
            ItemStack itemstack1;
            if (mouseX < getX() + 20) {
               itemstack1 = menu.getOffers().get(index + scrollOff).getCostA();
               graphics.renderTooltip(font, itemstack1, mouseX, mouseY);
            } else if (mouseX < getX() + 50 && mouseX > getX() + 30) {
               itemstack1 = menu.getOffers().get(index + scrollOff).getCostB();
               if (!itemstack1.isEmpty()) {
                  graphics.renderTooltip(font, itemstack1, mouseX, mouseY);
               }
            } else if (mouseX > getX() + 65) {
               itemstack1 = menu.getOffers().get(index + scrollOff).getResult();
               graphics.renderTooltip(font, itemstack1, mouseX, mouseY);
            }
         }
      }
   }

}
