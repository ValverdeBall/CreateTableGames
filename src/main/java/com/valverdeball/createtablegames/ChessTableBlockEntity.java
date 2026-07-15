package com.valverdeball.createtablegames;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import java.util.UUID;

public class ChessTableBlockEntity extends KineticBlockEntity implements MenuProvider {

  public ChessTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public Component getDisplayName() {
    return Component.literal("Chess Table");
  }

  @Override
  public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
    return new ChessTableMenu(windowId, inventory, this.getBlockPos());
  }

  private UUID whitePlayer = null;
  private UUID blackPlayer = null;

  private byte[] board = new byte[64];

  {
    setupInitialBoard();
  }

  public boolean assignPlayer(UUID playerUUID, PlayerFaction.Side side) {
    if (side == PlayerFaction.Side.WHITE) {
      if (this.whitePlayer != null && !this.whitePlayer.equals(playerUUID)) {
        return false;
      }
      this.whitePlayer = playerUUID;
    } else if (side == PlayerFaction.Side.BLACK) {
      if (this.blackPlayer != null && !this.blackPlayer.equals(playerUUID)) {
        return false;
      }
      this.blackPlayer = playerUUID;
    }
    this.notifyUpdate();
    return true;
  }

  private void setupInitialBoard() {
    byte[] backRank = {
      ChessPiece.ROOK, ChessPiece.KNIGHT, ChessPiece.BISHOP, ChessPiece.QUEEN,
      ChessPiece.KING, ChessPiece.BISHOP, ChessPiece.KNIGHT, ChessPiece.ROOK
    };

    for(int file = 0; file < 8; file++) {
      board[index(file, 0)] = ChessPiece.encode(backRank[file], PlayerFaction.Side.WHITE);
      board[index(file, 1)] = ChessPiece.encode(ChessPiece.PAWN, PlayerFaction.Side.WHITE);

      board[index(file, 6)] = ChessPiece.encode(ChessPiece.PAWN, PlayerFaction.Side.BLACK);
      board[index(file, 7)] = ChessPiece.encode(backRank[file], PlayerFaction.Side.BLACK);

      for (int rank = 2; rank < 6; rank++) {
        board[index(file, rank)] = ChessPiece.EMPTY;
      }
    }
  }

  private static int index(int file, int rank) {
    return rank * 8 + file;
  }

  public byte getSquare(int file, int rank) {
    return board[index(file, rank)];
  }

  public void setSquare(int file, int rank, byte piece) {
    board[index(file, rank)] = piece;
    this.notifyUpdate();
  }

  public byte[] getBoard() {
    return board;
  }

  @Override
  protected void write(CompoundTag tag, HolderLookup.Provider registrar, boolean clientPacket) {
    super.write(tag, registrar, clientPacket);

    if (this.whitePlayer != null) {
      tag.putUUID("WhitePlayerUUID", this.whitePlayer);
    }
    if (this.blackPlayer != null) {
      tag.putUUID("BlackPlayerUUID", this.blackPlayer);
    }

    tag.putByteArray("Board", this.board);
  }

  @Override
  protected void read(CompoundTag tag, HolderLookup.Provider registrar, boolean clientPacket) {
    super.read(tag, registrar, clientPacket);

    if(tag.hasUUID("WhitePlayerUUID")) {
      this.whitePlayer = tag.getUUID("WhitePlayerUUID");
    } else {
      this.whitePlayer = null;
    }

    if (tag.hasUUID("BlackPlayerUUID")) {
      this.blackPlayer = tag.getUUID("BlackPlayerUUID");
    } else {
      this.blackPlayer = null;
    }

    if (tag.contains("Board")) {
      byte[] loaded = tag.getByteArray("Board");
      if (loaded.length == 64) {
        this.board = loaded;
      } else {
        setupInitialBoard();
      }
    } else {
      setupInitialBoard();
    }
  }

  public UUID getWhitePlayer() { return whitePlayer; }
  public UUID getBlackPlayer() { return blackPlayer; }
}