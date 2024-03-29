package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.world.ServerWorld;

public class PanicTask extends Task<LivingEntity> {
   public PanicTask() {
      super(ImmutableMap.of());
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      if (func_220512_b(p_212831_2_) || func_220513_a(p_212831_2_)) {
         Brain<?> brain = p_212831_2_.getBrain();
         if (!brain.hasActivity(Activity.PANIC)) {
            brain.removeMemory(MemoryModuleType.PATH);
            brain.removeMemory(MemoryModuleType.WALK_TARGET);
            brain.removeMemory(MemoryModuleType.LOOK_TARGET);
            brain.removeMemory(MemoryModuleType.BREED_TARGET);
            brain.removeMemory(MemoryModuleType.INTERACTION_TARGET);
         }

         brain.switchTo(Activity.PANIC);
      }

   }

   public static boolean func_220513_a(LivingEntity p_220513_0_) {
      return p_220513_0_.getBrain().hasMemory(MemoryModuleType.NEAREST_HOSTILE);
   }

   public static boolean func_220512_b(LivingEntity p_220512_0_) {
      return p_220512_0_.getBrain().hasMemory(MemoryModuleType.HURT_BY);
   }
}