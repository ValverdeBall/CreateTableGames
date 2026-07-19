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

          byte piece = chessTable.getSquare(payload.fromFile(), payload.fromRank());
          if (ChessPiece.isEmpty(piece) || ChessPiece.sideOf(piece) != side) {
            return;
          }

          List<int[]> legalMoves = ChessMoves.legalMovesFor(chessTable.getBoard(), payload.fromFile(), payload.fromRank());
          boolean isLegal = false;
          for (int[] move : legalMoves) {
            if (move[0] == payload.toFile() && move[1] == payload.toRank()) {
              isLegal = true;
              break;
            }
          }

          if (isLegal) {
            chessTable.setSquare(payload.toFile(), payload.toRank(), piece);
            chessTable.setSquare(payload.fromFile(), payload.fromRank(), ChessPiece.EMPTY);
          }
        }
      }
    });
  }
}