package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class OceanRuinPieces {
   private static final ResourceLocation[] field_204058_G = new ResourceLocation[]{new ResourceLocation("underwater_ruin/warm_1"), new ResourceLocation("underwater_ruin/warm_2"), new ResourceLocation("underwater_ruin/warm_3"), new ResourceLocation("underwater_ruin/warm_4"), new ResourceLocation("underwater_ruin/warm_5"), new ResourceLocation("underwater_ruin/warm_6"), new ResourceLocation("underwater_ruin/warm_7"), new ResourceLocation("underwater_ruin/warm_8")};
   private static final ResourceLocation[] field_204059_H = new ResourceLocation[]{new ResourceLocation("underwater_ruin/brick_1"), new ResourceLocation("underwater_ruin/brick_2"), new ResourceLocation("underwater_ruin/brick_3"), new ResourceLocation("underwater_ruin/brick_4"), new ResourceLocation("underwater_ruin/brick_5"), new ResourceLocation("underwater_ruin/brick_6"), new ResourceLocation("underwater_ruin/brick_7"), new ResourceLocation("underwater_ruin/brick_8")};
   private static final ResourceLocation[] field_204053_B = new ResourceLocation[]{new ResourceLocation("underwater_ruin/cracked_1"), new ResourceLocation("underwater_ruin/cracked_2"), new ResourceLocation("underwater_ruin/cracked_3"), new ResourceLocation("underwater_ruin/cracked_4"), new ResourceLocation("underwater_ruin/cracked_5"), new ResourceLocation("underwater_ruin/cracked_6"), new ResourceLocation("underwater_ruin/cracked_7"), new ResourceLocation("underwater_ruin/cracked_8")};
   private static final ResourceLocation[] field_204061_J = new ResourceLocation[]{new ResourceLocation("underwater_ruin/mossy_1"), new ResourceLocation("underwater_ruin/mossy_2"), new ResourceLocation("underwater_ruin/mossy_3"), new ResourceLocation("underwater_ruin/mossy_4"), new ResourceLocation("underwater_ruin/mossy_5"), new ResourceLocation("underwater_ruin/mossy_6"), new ResourceLocation("underwater_ruin/mossy_7"), new ResourceLocation("underwater_ruin/mossy_8")};
   private static final ResourceLocation[] field_204062_K = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_brick_1"), new ResourceLocation("underwater_ruin/big_brick_2"), new ResourceLocation("underwater_ruin/big_brick_3"), new ResourceLocation("underwater_ruin/big_brick_8")};
   private static final ResourceLocation[] field_204066_O = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_mossy_1"), new ResourceLocation("underwater_ruin/big_mossy_2"), new ResourceLocation("underwater_ruin/big_mossy_3"), new ResourceLocation("underwater_ruin/big_mossy_8")};
   private static final ResourceLocation[] field_204070_S = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_cracked_1"), new ResourceLocation("underwater_ruin/big_cracked_2"), new ResourceLocation("underwater_ruin/big_cracked_3"), new ResourceLocation("underwater_ruin/big_cracked_8")};
   private static final ResourceLocation[] field_204049_ab = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_warm_4"), new ResourceLocation("underwater_ruin/big_warm_5"), new ResourceLocation("underwater_ruin/big_warm_6"), new ResourceLocation("underwater_ruin/big_warm_7")};

   private static ResourceLocation func_204042_a(Random p_204042_0_) {
      return field_204058_G[p_204042_0_.nextInt(field_204058_G.length)];
   }

   private static ResourceLocation func_204043_b(Random p_204043_0_) {
      return field_204049_ab[p_204043_0_.nextInt(field_204049_ab.length)];
   }

   public static void func_204041_a(TemplateManager p_204041_0_, BlockPos p_204041_1_, Rotation p_204041_2_, List<StructurePiece> p_204041_3_, Random p_204041_4_, OceanRuinConfig p_204041_5_) {
      boolean flag = p_204041_4_.nextFloat() <= p_204041_5_.largeProbability;
      float f = flag ? 0.9F : 0.8F;
      func_204045_a(p_204041_0_, p_204041_1_, p_204041_2_, p_204041_3_, p_204041_4_, p_204041_5_, flag, f);
      if (flag && p_204041_4_.nextFloat() <= p_204041_5_.clusterProbability) {
         func_204047_a(p_204041_0_, p_204041_4_, p_204041_2_, p_204041_1_, p_204041_5_, p_204041_3_);
      }

   }

   private static void func_204047_a(TemplateManager p_204047_0_, Random p_204047_1_, Rotation p_204047_2_, BlockPos p_204047_3_, OceanRuinConfig p_204047_4_, List<StructurePiece> p_204047_5_) {
      int i = p_204047_3_.getX();
      int j = p_204047_3_.getZ();
      BlockPos blockpos = Template.getTransformedPos(new BlockPos(15, 0, 15), Mirror.NONE, p_204047_2_, BlockPos.ZERO).add(i, 0, j);
      MutableBoundingBox mutableboundingbox = MutableBoundingBox.createProper(i, 0, j, blockpos.getX(), 0, blockpos.getZ());
      BlockPos blockpos1 = new BlockPos(Math.min(i, blockpos.getX()), 0, Math.min(j, blockpos.getZ()));
      List<BlockPos> list = func_204044_a(p_204047_1_, blockpos1.getX(), blockpos1.getZ());
      int k = MathHelper.nextInt(p_204047_1_, 4, 8);

      for(int l = 0; l < k; ++l) {
         if (!list.isEmpty()) {
            int i1 = p_204047_1_.nextInt(list.size());
            BlockPos blockpos2 = list.remove(i1);
            int j1 = blockpos2.getX();
            int k1 = blockpos2.getZ();
            Rotation rotation = Rotation.values()[p_204047_1_.nextInt(Rotation.values().length)];
            BlockPos blockpos3 = Template.getTransformedPos(new BlockPos(5, 0, 6), Mirror.NONE, rotation, BlockPos.ZERO).add(j1, 0, k1);
            MutableBoundingBox mutableboundingbox1 = MutableBoundingBox.createProper(j1, 0, k1, blockpos3.getX(), 0, blockpos3.getZ());
            if (!mutableboundingbox1.intersectsWith(mutableboundingbox)) {
               func_204045_a(p_204047_0_, blockpos2, rotation, p_204047_5_, p_204047_1_, p_204047_4_, false, 0.8F);
            }
         }
      }

   }

   private static List<BlockPos> func_204044_a(Random p_204044_0_, int p_204044_1_, int p_204044_2_) {
      List<BlockPos> list = Lists.newArrayList();
      list.add(new BlockPos(p_204044_1_ - 16 + MathHelper.nextInt(p_204044_0_, 1, 8), 90, p_204044_2_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7)));
      list.add(new BlockPos(p_204044_1_ - 16 + MathHelper.nextInt(p_204044_0_, 1, 8), 90, p_204044_2_ + MathHelper.nextInt(p_204044_0_, 1, 7)));
      list.add(new BlockPos(p_204044_1_ - 16 + MathHelper.nextInt(p_204044_0_, 1, 8), 90, p_204044_2_ - 16 + MathHelper.nextInt(p_204044_0_, 4, 8)));
      list.add(new BlockPos(p_204044_1_ + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7)));
      list.add(new BlockPos(p_204044_1_ + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ - 16 + MathHelper.nextInt(p_204044_0_, 4, 6)));
      list.add(new BlockPos(p_204044_1_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ + 16 + MathHelper.nextInt(p_204044_0_, 3, 8)));
      list.add(new BlockPos(p_204044_1_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ + MathHelper.nextInt(p_204044_0_, 1, 7)));
      list.add(new BlockPos(p_204044_1_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ - 16 + MathHelper.nextInt(p_204044_0_, 4, 8)));
      return list;
   }

   private static void func_204045_a(TemplateManager p_204045_0_, BlockPos p_204045_1_, Rotation p_204045_2_, List<StructurePiece> p_204045_3_, Random p_204045_4_, OceanRuinConfig p_204045_5_, boolean p_204045_6_, float p_204045_7_) {
      if (p_204045_5_.field_204031_a == OceanRuinStructure.Type.WARM) {
         ResourceLocation resourcelocation = p_204045_6_ ? func_204043_b(p_204045_4_) : func_204042_a(p_204045_4_);
         p_204045_3_.add(new OceanRuinPieces.Piece(p_204045_0_, resourcelocation, p_204045_1_, p_204045_2_, p_204045_7_, p_204045_5_.field_204031_a, p_204045_6_));
      } else if (p_204045_5_.field_204031_a == OceanRuinStructure.Type.COLD) {
         ResourceLocation[] aresourcelocation2 = p_204045_6_ ? field_204062_K : field_204059_H;
         ResourceLocation[] aresourcelocation = p_204045_6_ ? field_204070_S : field_204053_B;
         ResourceLocation[] aresourcelocation1 = p_204045_6_ ? field_204066_O : field_204061_J;
         int i = p_204045_4_.nextInt(aresourcelocation2.length);
         p_204045_3_.add(new OceanRuinPieces.Piece(p_204045_0_, aresourcelocation2[i], p_204045_1_, p_204045_2_, p_204045_7_, p_204045_5_.field_204031_a, p_204045_6_));
         p_204045_3_.add(new OceanRuinPieces.Piece(p_204045_0_, aresourcelocation[i], p_204045_1_, p_204045_2_, 0.7F, p_204045_5_.field_204031_a, p_204045_6_));
         p_204045_3_.add(new OceanRuinPieces.Piece(p_204045_0_, aresourcelocation1[i], p_204045_1_, p_204045_2_, 0.5F, p_204045_5_.field_204031_a, p_204045_6_));
      }

   }

   public static class Piece extends TemplateStructurePiece {
      private final OceanRuinStructure.Type biomeType;
      private final float integrity;
      private final ResourceLocation field_204038_f;
      private final Rotation rotation;
      private final boolean isLarge;

      public Piece(TemplateManager p_i48868_1_, ResourceLocation p_i48868_2_, BlockPos p_i48868_3_, Rotation p_i48868_4_, float p_i48868_5_, OceanRuinStructure.Type p_i48868_6_, boolean p_i48868_7_) {
         super(IStructurePieceType.ORP, 0);
         this.field_204038_f = p_i48868_2_;
         this.templatePosition = p_i48868_3_;
         this.rotation = p_i48868_4_;
         this.integrity = p_i48868_5_;
         this.biomeType = p_i48868_6_;
         this.isLarge = p_i48868_7_;
         this.func_204034_a(p_i48868_1_);
      }

      public Piece(TemplateManager p_i50592_1_, CompoundNBT p_i50592_2_) {
         super(IStructurePieceType.ORP, p_i50592_2_);
         this.field_204038_f = new ResourceLocation(p_i50592_2_.getString("Template"));
         this.rotation = Rotation.valueOf(p_i50592_2_.getString("Rot"));
         this.integrity = p_i50592_2_.getFloat("Integrity");
         this.biomeType = OceanRuinStructure.Type.valueOf(p_i50592_2_.getString("BiomeType"));
         this.isLarge = p_i50592_2_.getBoolean("IsLarge");
         this.func_204034_a(p_i50592_1_);
      }

      private void func_204034_a(TemplateManager p_204034_1_) {
         Template template = p_204034_1_.getTemplateDefaulted(this.field_204038_f);
         PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
         this.setup(template, this.templatePosition, placementsettings);
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putString("Template", this.field_204038_f.toString());
         tagCompound.putString("Rot", this.rotation.name());
         tagCompound.putFloat("Integrity", this.integrity);
         tagCompound.putString("BiomeType", this.biomeType.toString());
         tagCompound.putBoolean("IsLarge", this.isLarge);
      }

      protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
         if ("chest".equals(function)) {
            worldIn.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, Boolean.valueOf(worldIn.getFluidState(pos).isTagged(FluidTags.WATER))), 2);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof ChestTileEntity) {
               ((ChestTileEntity)tileentity).setLootTable(this.isLarge ? LootTables.CHESTS_UNDERWATER_RUIN_BIG : LootTables.CHESTS_UNDERWATER_RUIN_SMALL, rand.nextLong());
            }
         } else if ("drowned".equals(function)) {
            DrownedEntity drownedentity = EntityType.DROWNED.create(worldIn.getWorld());
            drownedentity.enablePersistence();
            drownedentity.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);
            drownedentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(pos), SpawnReason.STRUCTURE, (ILivingEntityData)null, (CompoundNBT)null);
            worldIn.addEntity(drownedentity);
            if (pos.getY() > worldIn.getSeaLevel()) {
               worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            } else {
               worldIn.setBlockState(pos, Blocks.WATER.getDefaultState(), 2);
            }
         }

      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         this.placeSettings.func_215219_b().addProcessor(new IntegrityProcessor(this.integrity)).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
         int i = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
         this.templatePosition = new BlockPos(this.templatePosition.getX(), i, this.templatePosition.getZ());
         BlockPos blockpos = Template.getTransformedPos(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.rotation, BlockPos.ZERO).add(this.templatePosition);
         this.templatePosition = new BlockPos(this.templatePosition.getX(), this.func_204035_a(this.templatePosition, worldIn, blockpos), this.templatePosition.getZ());
         return super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn, p_74875_4_);
      }

      private int func_204035_a(BlockPos p_204035_1_, IBlockReader p_204035_2_, BlockPos p_204035_3_) {
         int i = p_204035_1_.getY();
         int j = 512;
         int k = i - 1;
         int l = 0;

         for(BlockPos blockpos : BlockPos.getAllInBoxMutable(p_204035_1_, p_204035_3_)) {
            int i1 = blockpos.getX();
            int j1 = blockpos.getZ();
            int k1 = p_204035_1_.getY() - 1;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(i1, k1, j1);
            BlockState blockstate = p_204035_2_.getBlockState(blockpos$mutableblockpos);

            for(IFluidState ifluidstate = p_204035_2_.getFluidState(blockpos$mutableblockpos); (blockstate.isAir() || ifluidstate.isTagged(FluidTags.WATER) || blockstate.getBlock().isIn(BlockTags.ICE)) && k1 > 1; ifluidstate = p_204035_2_.getFluidState(blockpos$mutableblockpos)) {
               --k1;
               blockpos$mutableblockpos.setPos(i1, k1, j1);
               blockstate = p_204035_2_.getBlockState(blockpos$mutableblockpos);
            }

            j = Math.min(j, k1);
            if (k1 < k - 2) {
               ++l;
            }
         }

         int l1 = Math.abs(p_204035_1_.getX() - p_204035_3_.getX());
         if (k - j > 2 && l > l1 - 2) {
            i = j + 1;
         }

         return i;
      }
   }
}