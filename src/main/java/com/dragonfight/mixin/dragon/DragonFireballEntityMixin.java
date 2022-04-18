package com.dragonfight.mixin.dragon;

import com.dragonfight.DragonfightMod;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireball.class)
/**
 * Makes the fireballs explode
 */
public class DragonFireballEntityMixin
{
    final DragonFireball self = (DragonFireball) (Object) this;

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setPos(DDD)V"))
    private void onHit(final HitResult rayTraceResult, final CallbackInfo ci)
    {
        self.level.explode(null,
          new EntityDamageSource("weakexplosion", self.getOwner()),
          null,
          rayTraceResult.getLocation().x,
          rayTraceResult.getLocation().y,
          rayTraceResult.getLocation().z,
          1 + DragonfightMod.config.getCommonConfig().dragonDifficulty / 4f,
          false,
          Explosion.BlockInteraction.NONE);
    }
}
