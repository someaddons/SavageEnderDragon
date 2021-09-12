package com.dragonfight.mixin.dragon;

import com.dragonfight.DragonfightMod;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireballEntity.class)
/**
 * Makes the fireballs explode
 */
public class DragonFireballEntityMixin
{
    final DragonFireballEntity self = (DragonFireballEntity) (Object) this;

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/AreaEffectCloudEntity;setPos(DDD)V"))
    private void onHit(final RayTraceResult rayTraceResult, final CallbackInfo ci)
    {
        self.level.explode(null,
          new EntityDamageSource("weakexplosion", self.getOwner()),
          null,
          rayTraceResult.getLocation().x,
          rayTraceResult.getLocation().y,
          rayTraceResult.getLocation().z,
          1 + DragonfightMod.config.getCommonConfig().dragonDifficulty.get() / 4f,
          false,
          Explosion.Mode.NONE);
    }
}
