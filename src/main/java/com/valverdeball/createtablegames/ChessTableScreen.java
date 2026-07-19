package com.valverdeball.createtablegames;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;
import java.util.List;

public class ChessTableScreen extends AbstractContainerScreen<ChessTableMenu> {

  private Button whiteButton;
    private Button blackButton;

  private int selectedFile = -1;
    private int selectedRank = -1;

  public ChessTableScreen(ChessTableMenu menu, Inventory inventory, Component title) {
    super (menu, inventory, title);
    this.imageWidth=176;
    this.imageHeight=190;
  }

  @Override
  protected void init() {
    super.init();

    int buttonWidth = 50;
    int buttonHeight = 20;

    int xPos = this.leftPos + (this.imageWidth / 2) - (buttonWidth + 5);
    int yPos = this.topPos + 148;

    this.whiteButton = this.addRenderableWidget(Button.builder(
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

    this.blackButton = this.addRenderableWidget(Button.builder(
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

    this.whiteButton.visible = chessTable.getWhitePlayer() == null;
    this.whiteButton.active = chessTable.getWhitePlayer() == null;
    this.blackButton.visible = chessTable.getBlackPlayer() == null;
    this.blackButton.active = chessTable.getBlackPlayer() == null;

    int squareSize = 16;
    int boardOriginX = this.leftPos + (this.imageWidth / 2) - (squareSize * 4);
    int boardOriginY = this.topPos + 10;

    List<int[]> legalMoves = (this.selectedFile != -1)
      ? ChessMoves.legalMovesFor(chessTable.getBoard(), this.selectedFile, this.selectedRank)
      : new java.util.ArrayList<>();

    for (int rank = 0; rank < 8; rank++) {
      for (int file = 0; file < 8; file++) {
        int x = boardOriginX + (file *squareSize);
        int y = boardOriginY + ((7 - rank) * squareSize);

        boolean isLightSquare = (file + rank) % 2 == 0;
        int color = isLightSquare ? 0xFFEEEED2 : 0xFF769656;

        guiGraphics.fill(x, y, x + squareSize, y + squareSize, color);

        if (file == this.selectedFile && rank == this.selectedRank) {
          guiGraphics.fill(x, y, x + squareSize, y + squareSize, 0x80FFFF00);
        } else {
          for (int[] move : legalMoves) {
            if (move[0] == file && move[1] == rank) {
              guiGraphics.fill(x, y, x + squareSize, y + squareSize, 0x8000FF00);
            }
          }
        }

        byte square = chessTable.getSquare(file, rank);
        if (!ChessPiece.isEmpty(square)) {
          String letter = pieceLetter(square);
          int textColor = ChessPiece.isWhite(square) ? 0xFFFFFFFF : 0xFF000000;
          guiGraphics.drawCenteredString(this.font, letter, x + (squareSize / 2), y + 4, textColor);
        }
      }
    }
  }

  @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && this.minecraft != null && this.minecraft.level != null) {
        net.minecraft.world.level.block.entity.BlockEntity be = 
          this.minecraft.level.getBlockEntity(this.menu.getBlockPos());

        if (be instanceof ChessTableBlockEntity chessTable) {
          int squareSize = 16;
          int boardOriginX = this.leftPos + (this.imageWidth / 2) - (squareSize * 4);
          int boardOriginY = this.topPos + 10;

          int clickedFile = (int) ((mouseX - boardOriginX) / squareSize);
          int clickedRow = (int) ((mouseY - boardOriginY) / squareSize);

          if (clickedFile >= 0 && clickedFile < 8 && clickedRow >= 0 && clickedRow < 8) {
            int clickedRank = 7 - clickedRow;
            handleSquareClick(chessTable, clickedFile, clickedRank);
            return true;
          }
        }
      }
      return super.mouseClicked(mouseX, mouseY, button);
    }

  private void handleSquareClick(ChessTableBlockEntity chessTable, int file, int rank) {
    if (this.minecraft == null || this.minecraft.player == null) return;

    UUID localUUID = this.minecraft.player.getUUID();
    PlayerFaction.Side localSide;

    if (localUUID.equals(chessTable.getWhitePlayer())) {
      localSide = PlayerFaction.Side.WHITE;
    } else if (localUUID.equals(chessTable.getBlackPlayer())) {
      localSide = PlayerFaction.Side.BLACK;
    } else {
      localSide = PlayerFaction.Side.NONE;
    }

    if (localSide == PlayerFaction.Side.NONE) {
      return;
    }

    if (this.selectedFile == -1) {
      byte square = chessTable.getSquare(file, rank);
      if (!ChessPiece.isEmpty(square) && ChessPiece.sideOf(square) == localSide) {
        this.selectedFile = file;
        this.selectedRank = rank;
      }
      return;
    }
      List<int[]> legalMoves = ChessMoves.legalMovesFor(chessTable.getBoard(), this.selectedFile, this.selectedRank);
      boolean isLegal = false;
      for (int[] move : legalMoves) {
        if (move[0] == file && move[1] == rank) {
          isLegal = true;
          break;
        }
      }

      if (isLegal) {
        PacketDistributor.sendToServer(new ChessMovePayload(this.menu.getBlockPos(), this.selectedFile, this.selectedRank, file, rank));
      }

      this.selectedFile = -1;
      this.selectedRank = -1;
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