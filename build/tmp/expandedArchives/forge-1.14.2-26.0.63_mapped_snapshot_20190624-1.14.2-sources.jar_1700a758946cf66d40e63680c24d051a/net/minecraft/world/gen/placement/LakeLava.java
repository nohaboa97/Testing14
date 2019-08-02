package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class LakeLava extends Placement<LakeChanceConfig> {
   public LakeLava(Function<Dynamic<?>, ? extends LakeChanceConfig> p_i51368_1_) {
      super(p_i51368_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, LakeChanceConfig p_212848_4_, BlockPos pos) {
      if (random.nextInt(p_212848_4_.chance / 10) == 0) {
         int i = random.nextInt(16);
         int j = random.nextInt(random.nextInt(p_212848_2_.getMaxHeight() - 8) + 8);
         int k = random.nextInt(16);
         if (j < worldIn.getSeaLevel() || random.nextInt(p_212848_4_.chance / 8) == 0) {
            return Stream.of(pos.add(i, j, k));
         }
      }

      return Stream.empty();
   }
}