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
    if (this.minecraft == null || this.minecraft.level == null) {
      return;
    }

    net.minecraft.world.level.block.entity.BlockEntity be =
      this.minecraft.level.getBlockEntity(this.menu.getBlockPos());

    if (!(be instanceof ChessTableBlockEntity chessTable)) {
      return;
    }

    int squareSize = 16;
    int boardOriginX = this.leftPos + (this.imageWidth / 2) - (squareSize * 4);
    int boardOriginY = this.topPos + 10;

    for (int rank = 0; rank < 8; rank++) {
      for (int file = 0; file < 8; file++) {
        int x = boardOriginX + (file *squareSize);
        int y = boardOriginY + ((7 - rank) * squareSize);

        boolean isLightSquare = (file + rank) % 2 == 0;
        int color = isLightSquare ? 0xFFEEEED2 : 0xFF769656;

        guiGraphics.fill(x, y, x + squareSize, y + squareSize, color);

        byte square = chessTable.getSquare(file, rank);
        if (!ChessPiece.isEmpty(square)) {
          String letter = pieceLetter(square);
          guiGraphics.drawCenteredString(this.font, letter, x + (squareSize / 2), y + 4, 0xFF000000);
        }
      }
    }
  }

  private String pieceLetter(byte square) {
    String letter = switch (ChessPiece.type(square)) {
      case ChessPiece.PAWN -> "P";
      case ChessPiece.KNIGHT -> "N";
      case ChessPiece.BISHOP -> "B";
      case ChessPiece.ROOK -> "R";
      case ChessPiece.QUEEN -> "Q";
      case ChessPiece.KING -> "K";
      default -> "";
    };
    return ChessPiece.isWhite(square) ? letter : letter.toLowerCase();
  }
}