package com.valverdeball.createtablegames;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class PlayerFaction implements INBTSerializable<CompoundTag> {
  public enum Side {
    NONE, WHITE, BLACK
  }
  private Side currentSide = Side.NONE;

  public Side getSide() {
    return currentSide;
  }
  public void setSide(Side side) {
    this.currentSide = side;
  }

  @Override
  public CompoundTag serializeNBT(HolderLookup.Provider provider) {
    CompoundTag tag = new CompoundTag();
    tag.putString("FactionSide", this.currentSide.name());
    return tag;
  }

  @Override
  public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
    if (tag.contains("FactionSide")) {
      try {
        this.currentSide = Side.valueOf(tag.getString("FactionSide"));
      } catch (IllegalArgumentException e) {
        this.currentSide = Side.NONE;
      }
    }
  }
}