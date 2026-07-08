package com.valverdeball.createtablegames;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class ChessTableScreen extends AbstractContainerScreen<ChessTableMenu> {

  public ChessTableScreen(ChessTableMenu menu, Inventory inventory, Component title) {
    super (menu, inventory, title);
    this.imageWidth=176;
    this.imageHeight=166;
  }

  @Override
  protected void init() {
    super.init();

    int buttonWidth = 50;
    int buttonHeight = 20;

    int xPos = this.leftPos + (this.imageWidth / 2) - (buttonWidth + 5);
    int yPos = this.topPos + 40;

    this.addRenderableWidget(Button.builder(
      Component.translatable("gui.createtablegames.chess_table.white"), 
      button -> {
        PacketDistributor.sendToServer(new FactionSelectPayload(PlayerFaction.Side.WHITE, this.menu.getBlockPos()));
        if (this.minecraft.player != null) {
          this.minecraft.player.closeContainer();
        }
      })
      .bounds(xPos, yPos, buttonWidth, buttonHeight)
      .build()
    );

    this.addRenderableWidget(Button.builder(
      Component.translatable("gui.createtablegames.chess_table.black"),
      button -> {
        PacketDistributor.sendToServer(new FactionSelectPayload(PlayerFaction.Side.BLACK, this.menu.getBlockPos()));
        if (this.minecraft.player != null) {
          this.minecraft.player.closeContainer();
        }
      })
      .bounds(xPos + buttonWidth + 10, yPos, buttonWidth, buttonHeight)
      .build()
    );
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    super.render(guiGraphics, mouseX, mouseY, partialTick);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    
  }
}