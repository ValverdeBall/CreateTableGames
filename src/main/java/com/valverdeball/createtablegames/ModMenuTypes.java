package com.valverdeball.createtablegames;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModMenuTypes {
  public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "createtablegames");

  public static final DeferredHolder<MenuType<?>, MenuType<ChessTableMenu>> CHESS_TABLE_MENU = MENUS.register("chess_table_menu", () -> IMenuTypeExtension.create((windowId, inv, buf) -> new ChessTableMenu(windowId, inv, buf.readBlockPos())));

  public static void register(IEventBus eventBus) {
    MENUS.register(eventBus);
  }
}