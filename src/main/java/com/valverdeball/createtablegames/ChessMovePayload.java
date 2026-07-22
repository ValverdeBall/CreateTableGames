package com.valverdeball.createtablegames;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;
import java.util.List;

public record ChessMovePayload(BlockPos pos, int fromFile, int fromRank, int toFile, int toRank) implements CustomPacketPayload {

  public static final Type<ChessMovePayload> TYPE = 
    new Type<>(ResourceLocation.fromNamespaceAndPath("createtablegames", "chess_move"));

  public static final StreamCodec<FriendlyByteBuf, ChessMovePayload> CODEC = StreamCodec.of(
    (buf, value) -> {
      BlockPos.STREAM_CODEC.encode(buf, value.pos());
      buf.writeByte(value.fromFile());
      buf.writeByte(value.fromRank());
      buf.writeByte(value.toFile());
      buf.writeByte(value.toRank());
    },
    buf -> new ChessMovePayload(
      BlockPos.STREAM_CODEC.decode(buf),
      buf.readByte(),
      buf.readByte(),
      buf.readByte(),
      buf.readByte()
    )
  );

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void handle(final ChessMovePayload payload, final IPayloadContext context) {
    context.enqueueWork(() -> {
      if (context.player() instanceof ServerPlayer serverPlayer) {
        var level = serverPlayer.level();

        if (level.getBlockEntity(payload.pos()) instanceof ChessTableBlockEntity chessTable) {
          UUID playerUUID = serverPlayer.getUUID();
          PlayerFaction.Side side;

          if (playerUUID.equals(chessTable.getWhitePlayer())) {
            side = PlayerFaction.Side.WHITE;
          } else if (playerUUID.equals(chessTable.getBlackPlayer())) {
            side = PlayerFaction.Side.BLACK;
          } else {
            return;
          }

          if (chessTable.getPendingPromotionFile() != -1) {
            return;
          }

          byte piece = chessTable.getSquare(payload.fromFile(), payload.fromRank());
          if (ChessPiece.isEmpty(piece) || ChessPiece.sideOf(piece) != side) {
            return;
          }

          List<int[]> legalMoves = ChessMoves.legalMovesFor(
            chessTable.getBoard(), payload.fromFile(), payload.fromRank(),
            chessTable.canCastleKingside(side), chessTable.canCastleQueenside(side), chessTable.getEnPassantFile()
          );
          boolean isLegal = false;
          for (int[] move : legalMoves) {
            if (move[0] == payload.toFile() && move[1] == payload.toRank()) {
              isLegal = true;
              break;
            }
          }

          if (!isLegal) {
            return;
          }

          byte type = ChessPiece.type(piece);
          int fileDelta = payload.toFile() - payload.fromFile();

          if (type == ChessPiece.KING && Math.abs(fileDelta) == 2) {
            int rookFromFile = fileDelta > 0 ? 7 : 0;
            int rookToFile = fileDelta > 0 ? payload.fromFile() + 1 : payload.fromFile() - 1;
            byte rook = chessTable.getSquare(rookFromFile, payload.fromRank());
            chessTable.setSquare(rookToFile, payload.fromRank(), rook);
            chessTable.setSquare(rookFromFile, payload.fromRank(), ChessPiece.EMPTY);
          }

          if (type == ChessPiece.PAWN && payload.toFile() != payload.fromFile()
             && ChessPiece.isEmpty(chessTable.getSquare(payload.toFile(), payload.toRank()))) {
            chessTable.setSquare(payload.toFile(), payload.fromRank(), ChessPiece.EMPTY);
             }
          
          chessTable.setSquare(payload.toFile(), payload.toRank(), piece);
          chessTable.setSquare(payload.fromFile(), payload.fromRank(), ChessPiece.EMPTY);

          int promotionRank = (side == PlayerFaction.Side.WHITE) ? 7 : 0;
          if (type == ChessPiece.PAWN && payload.toRank() == promotionRank) {
            chessTable.setPendingPromotion(payload.toFile(), payload.toRank());
          }

          if (type == ChessPiece.KING) {
            chessTable.revokeCastleKingside(side);
            chessTable.revokeCastleQueenside(side);
          } else if (type == ChessPiece.ROOK) {
            if (payload.fromFile() == 0) chessTable.revokeCastleQueenside(side);
            if (payload.fromFile() == 7) chessTable.revokeCastleKingside(side);
          }

          PlayerFaction.Side enemySide = (side == PlayerFaction.Side.WHITE) ? PlayerFaction.Side.BLACK : PlayerFaction.Side.WHITE;
          int enemyBackRank = (side == PlayerFaction.Side.WHITE) ? 7 : 0;
          if (payload.toRank() == enemyBackRank) {
            if (payload.toFile() == 0) chessTable.revokeCastleQueenside(enemySide);
            if (payload.toFile() == 7) chessTable.revokeCastleKingside(enemySide);
          }

          if (type == ChessPiece.PAWN && Math.abs(payload.toRank() - payload.fromRank()) == 2) {
        chessTable.setEnPassantFile(payload.fromFile());
      } else {
        chessTable.setEnPassantFile(-1);
          }

          chessTable.notifyUpdate();
        }
      }
    });
  }
}