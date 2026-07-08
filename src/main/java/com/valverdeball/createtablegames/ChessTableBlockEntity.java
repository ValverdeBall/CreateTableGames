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

  public void assignPlayer(UUID playerUUID, PlayerFaction.Side side) {
    if (side == PlayerFaction.Side.WHITE) {
      this.whitePlayer = playerUUID;
    } else if (side == PlayerFaction.Side.BLACK) {
      this.blackPlayer = playerUUID;
    }
    this.setChanged();
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
  }

  public UUID getWhitePlayer() { return whitePlayer; }
  public UUID getBlackPlayer() { return blackPlayer; }
}