package net.minecraft.world.lighting;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class LightEngine<M extends LightDataMap<M>, S extends SectionLightStorage<M>> extends LevelBasedGraph implements IWorldLightListener {
   private static final Direction[] DIRECTIONS = Direction.values();
   protected final IChunkLightProvider chunkProvider;
   protected final LightType type;
   protected final S storage;
   private boolean field_215629_e;
   private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();
   private final long[] recentPositions = new long[2];
   private final IBlockReader[] recentChunks = new IBlockReader[2];

   public LightEngine(IChunkLightProvider p_i51296_1_, LightType p_i51296_2_, S p_i51296_3_) {
      super(16, 256, 8192);
      this.chunkProvider = p_i51296_1_;
      this.type = p_i51296_2_;
      this.storage = p_i51296_3_;
      this.invalidateCaches();
   }

   protected void scheduleUpdate(long worldPos) {
      this.storage.func_215532_c();
      if (this.storage.func_215518_g(SectionPos.worldToSection(worldPos))) {
         super.scheduleUpdate(worldPos);
      }

   }

   @Nullable
   private IBlockReader func_215615_a(int p_215615_1_, int p_215615_2_) {
      long i = ChunkPos.asLong(p_215615_1_, p_215615_2_);

      for(int j = 0; j < 2; ++j) {
         if (i == this.recentPositions[j]) {
            return this.recentChunks[j];
         }
      }

      IBlockReader iblockreader = this.chunkProvider.getChunkForLight(p_215615_1_, p_215615_2_);

      for(int k = 1; k > 0; --k) {
         this.recentPositions[k] = this.recentPositions[k - 1];
         this.recentChunks[k] = this.recentChunks[k - 1];
      }

      this.recentPositions[0] = i;
      this.recentChunks[0] = iblockreader;
      return iblockreader;
   }

   private void invalidateCaches() {
      Arrays.fill(this.recentPositions, ChunkPos.SENTINEL);
      Arrays.fill(this.recentChunks, (Object)null);
   }

   protected VoxelShape getRenderShape(long localPos, @Nullable AtomicInteger opacity) {
      if (localPos == Long.MAX_VALUE) {
         if (opacity != null) {
            opacity.set(0);
         }

         return VoxelShapes.empty();
      } else {
         int i = SectionPos.toChunk(BlockPos.unpackX(localPos));
         int j = SectionPos.toChunk(BlockPos.unpackZ(localPos));
         IBlockReader iblockreader = this.func_215615_a(i, j);
         if (iblockreader == null) {
            if (opacity != null) {
               opacity.set(16);
            }

            return VoxelShapes.fullCube();
         } else {
            this.scratchPos.setPos(localPos);
            BlockState blockstate = iblockreader.getBlockState(this.scratchPos);
            boolean flag = blockstate.isSolid() && blockstate.func_215691_g();
            if (opacity != null) {
               opacity.set(blockstate.getOpacity(this.chunkProvider.getWorld(), this.scratchPos));
            }

            return flag ? blockstate.getRenderShape(this.chunkProvider.getWorld(), this.scratchPos) : VoxelShapes.empty();
         }
      }
   }

   public static int func_215613_a(IBlockReader p_215613_0_, BlockState p_215613_1_, BlockPos p_215613_2_, BlockState p_215613_3_, BlockPos p_215613_4_, Direction p_215613_5_, int p_215613_6_) {
      boolean flag = p_215613_1_.isSolid() && p_215613_1_.func_215691_g();
      boolean flag1 = p_215613_3_.isSolid() && p_215613_3_.func_215691_g();
      if (!flag && !flag1) {
         return p_215613_6_;
      } else {
         VoxelShape voxelshape = flag ? p_215613_1_.getRenderShape(p_215613_0_, p_215613_2_) : VoxelShapes.empty();
         VoxelShape voxelshape1 = flag1 ? p_215613_3_.getRenderShape(p_215613_0_, p_215613_4_) : VoxelShapes.empty();
         return VoxelShapes.doAdjacentCubeSidesFillSquare(voxelshape, voxelshape1, p_215613_5_) ? 16 : p_215613_6_;
      }
   }

   protected boolean isRoot(long pos) {
      return pos == Long.MAX_VALUE;
   }

   /**
    * Computes level propagated from neighbors of specified position with given existing level, excluding the given
    * source position.
    */
   protected int computeLevel(long pos, long excludedSourcePos, int level) {
      return 0;
   }

   protected int getLevel(long sectionPosIn) {
      return sectionPosIn == Long.MAX_VALUE ? 0 : 15 - this.storage.func_215521_h(sectionPosIn);
   }

   protected int func_215622_a(NibbleArray p_215622_1_, long p_215622_2_) {
      return 15 - p_215622_1_.get(SectionPos.mask(BlockPos.unpackX(p_215622_2_)), SectionPos.mask(BlockPos.unpackY(p_215622_2_)), SectionPos.mask(BlockPos.unpackZ(p_215622_2_)));
   }

   protected void setLevel(long sectionPosIn, int level) {
      this.storage.func_215517_b(sectionPosIn, Math.min(15, 15 - level));
   }

   /**
    * Returns level propagated from start position with specified level to the neighboring end position.
    */
   protected int getEdgeLevel(long startPos, long endPos, int startLevel) {
      return 0;
   }

   public boolean func_215619_a() {
      return this.needsUpdate() || this.storage.needsUpdate() || this.storage.func_215527_a();
   }

   public int func_215616_a(int p_215616_1_, boolean p_215616_2_, boolean p_215616_3_) {
      if (!this.field_215629_e) {
         if (this.storage.needsUpdate()) {
            p_215616_1_ = this.storage.processUpdates(p_215616_1_);
            if (p_215616_1_ == 0) {
               return p_215616_1_;
            }
         }

         this.storage.func_215522_a(this, p_215616_2_, p_215616_3_);
      }

      this.field_215629_e = true;
      if (this.needsUpdate()) {
         p_215616_1_ = this.processUpdates(p_215616_1_);
         this.invalidateCaches();
         if (p_215616_1_ == 0) {
            return p_215616_1_;
         }
      }

      this.field_215629_e = false;
      this.storage.func_215533_d();
      return p_215616_1_;
   }

   protected void setData(long sectionPosIn, @Nullable NibbleArray array) {
      this.storage.setData(sectionPosIn, array);
   }

   @Nullable
   public NibbleArray getData(SectionPos p_215612_1_) {
      return this.storage.func_222858_h(p_215612_1_.asLong());
   }

   public int getLightFor(BlockPos worldPos) {
      return this.storage.func_215525_d(worldPos.toLong());
   }

   @OnlyIn(Dist.CLIENT)
   public String func_215614_b(long p_215614_1_) {
      return "" + this.storage.getLevel(p_215614_1_);
   }

   public void checkLight(BlockPos worldPos) {
      long i = worldPos.toLong();
      this.scheduleUpdate(i);

      for(Direction direction : DIRECTIONS) {
         this.scheduleUpdate(BlockPos.offset(i, direction));
      }

   }

   public void func_215623_a(BlockPos p_215623_1_, int p_215623_2_) {
   }

   public void updateSectionStatus(SectionPos p_215566_1_, boolean p_215566_2_) {
      this.storage.func_215519_c(p_215566_1_.asLong(), p_215566_2_);
   }

   public void func_215620_a(ChunkPos p_215620_1_, boolean p_215620_2_) {
      long i = SectionPos.func_218169_f(SectionPos.asLong(p_215620_1_.x, 0, p_215620_1_.z));
      this.storage.func_215532_c();
      this.storage.func_215526_b(i, p_215620_2_);
   }

   public void func_223129_b(ChunkPos p_223129_1_, boolean p_223129_2_) {
      long i = SectionPos.func_218169_f(SectionPos.asLong(p_223129_1_.x, 0, p_223129_1_.z));
      this.storage.func_223113_c(i, p_223129_2_);
   }
}