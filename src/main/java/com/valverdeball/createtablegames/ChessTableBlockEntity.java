package com.valverdeball.createtablegames;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;

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
}