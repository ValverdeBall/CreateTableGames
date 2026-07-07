package com.valverdeball.createtablegames;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Direction;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionResult;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.content.kinetics.base.IRotate;

public class ChessTableBlock extends Block implements EntityBlock, IBE<ChessTableBlockEntity>, IRotate {

  private static final VoxelShape SHAPE = Shapes.or(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D), Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D));

  private static final ResourceKey<DamageType> CHESS_SHARP_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("createtablegames", "chess_sharp"));
  
  public ChessTableBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ChessTableBlockEntity(ModBlockEntities.CHESS_TABLE_BE.get(), pos, state);
  }

  @Override
  public Class<ChessTableBlockEntity> getBlockEntityClass() {
    return ChessTableBlockEntity.class;
  }

  @Override
  public BlockEntityType<ChessTableBlockEntity> getBlockEntityType() {
    return ModBlockEntities.CHESS_TABLE_BE.get();
  }

  @Override
  public Direction.Axis getRotationAxis(BlockState state) {
    return Direction.Axis.Y;
  }

  @Override
  public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction facing) {
    return facing == Direction.DOWN;
  }
  
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    if (!level.isClientSide) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof MenuProvider provider) {
        player.openMenu(provider, buf -> buf.writeBlockPos(pos));
      }
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
    if (!(entity instanceof LivingEntity livingEntity)) {
      super.stepOn(level, pos, state, entity);
      return;
    }

    if (livingEntity.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {     livingEntity.hurt(level.damageSources().source(CHESS_SHARP_KEY), 1.0F);
    }

    super.stepOn(level, pos, state, entity);
  }
}