package com.valverdeball.createtablegames;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlocks {
  public static final DeferredRegister<Block> BLOCKS=
  DeferredRegister.create(Registries.BLOCK, "createtablegames");

  public static final DeferredHolder<Block, Block> CHESS_TABLE = BLOCKS.register("chess_table", () -> new ChessTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD)));

  public static void register(IEventBus eventBus) {
    BLOCKS.register(eventBus);
  }
}