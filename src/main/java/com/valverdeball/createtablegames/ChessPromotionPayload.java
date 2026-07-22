package com.valverdeball.createtablegames;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ChessPromotionPayload(BlockPos pos, byte pieceType) implements CustomPacketPayload {

  public static final Type<ChessPromotionPayload> TYPE = 
    new Type<>(ResourceLocation.fromNamespaceAndPath("createtablegames", "chess_promotion"));

  public static final StreamCodec<FriendlyByteBuf, ChessPromotionPayload> CODEC = StreamCodec.of(
    (buf, value) -> {
      BlockPos.STREAM_CODEC.encode(buf, value.pos());
      buf.writeByte(value.pieceType());
    },
    buf -> new ChessPromotionPayload(
      BlockPos.STREAM_CODEC.decode(buf),
      buf.readByte()
    )
  );

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void handle(final ChessPromotionPayload payload, final IPayloadContext context) {
    context.enqueueWork(() ->  {
      if (context.player() instanceof ServerPlayer serverPlayer) {
        var level = serverPlayer.level();

        if (level.getBlockEntity(payload.pos()) instanceof ChessTableBlockEntity chessTable) {
          int file = chessTable.getPendingPromotionFile();
          int rank = chessTable.getPendingPromotionRank();

          if (file == -1) {
            return;
          }

          byte pawn = chessTable.getSquare(file, rank);
          UUID playerUUID = serverPlayer.getUUID();
          PlayerFaction.Side side = ChessPiece.sideOf(pawn);

          boolean isOwner = (side == PlayerFaction.Side.WHITE && playerUUID.equals(chessTable.getWhitePlayer()))
            || (side == PlayerFaction.Side.BLACK && playerUUID.equals(chessTable.getBlackPlayer()));
          if (!isOwner) {
            return;
          }

          byte chosen = payload.pieceType();
          if (chosen != ChessPiece.QUEEN && chosen != ChessPiece.ROOK && chosen != ChessPiece.BISHOP && chosen != ChessPiece.KNIGHT) {
            return;
          }

          chessTable.setSquare(file, rank, ChessPiece.encode(chosen, side));
          chessTable.clearPendingPromotion();
        }
      }
    });
  }
}