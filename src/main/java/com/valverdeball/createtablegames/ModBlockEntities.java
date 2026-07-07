package com.valverdeball.createtablegames;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "createtablegames");

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChessTableBlockEntity>> CHESS_TABLE_BE = 
  BLOCK_ENTITIES.register("chess_table",() ->
                        BlockEntityType.Builder.of((pos, state) -> new ChessTableBlockEntity(ModBlockEntities.CHESS_TABLE_BE.get(), pos, state), ModBlocks.CHESS_TABLE.get()).build(null));

  public static void register (IEventBus eventBus) {
    BLOCK_ENTITIES.register(eventBus);
  }
}