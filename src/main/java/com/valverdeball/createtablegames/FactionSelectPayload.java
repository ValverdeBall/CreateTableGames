package com.valverdeball.createtablegames;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FactionSelectPayload(PlayerFaction.Side chosenSide) implements CustomPacketPayload {

  public static final Type<FactionSelectPayload> TYPE = 
    new Type<>(ResourceLocation.fromNamespaceAndPath("createtablegames", "faction_select"));

  public static final StreamCodec<FriendlyByteBuf, FactionSelectPayload> CODEC = StreamCodec.of(
    (buf, value) -> buf.writeEnum(value.chosenSide()),
    buf -> new FactionSelectPayload(buf.readEnum(PlayerFaction.Side.class))
  );

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void handle(final FactionSelectPayload payload, final IPayloadContext context) {
    context.enqueueWork(() -> {
      if (context.player() instanceof ServerPlayer serverPlayer) {
        PlayerFaction faction = serverPlayer.getData(CreateTableGames.PLAYER_FACTION);
        faction.setSide(payload.chosenSide());
      }
    });
  }
}