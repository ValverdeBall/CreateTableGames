package com.valverdeball.createtablegames;

import com.simibubi.create.api.stress.BlockStressValues;

public class ModStressValues {

  public static void register() {
    BlockStressValues.IMPACTS.register(ModBlocks.CHESS_TABLE.get(), () -> 2.0);
  }
}