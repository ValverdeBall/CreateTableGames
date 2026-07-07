package com.valverdeball.createtablegames;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
  public static final DeferredRegister<Item> ITEMS =
  DeferredRegister.create(Registries.ITEM, "createtablegames");

  public static final DeferredHolder<Item, Item> CHESS_TABLE_ITEM = ITEMS.<Item>register ("chess_table", ModItems::createChessTableItem);

  private static Item createChessTableItem () {
    return new BlockItem(ModBlocks.CHESS_TABLE.get(), new Item.Properties());
  }

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}