package com.valverdeball.createtablegames;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class ChessTableMenu extends AbstractContainerMenu {

  private final BlockPos tablePos;

  public ChessTableMenu(int windowId, Inventory inv, BlockPos pos) {
    super(ModMenuTypes.CHESS_TABLE_MENU.get(), windowId);
    this.tablePos = pos;
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slot) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }
}