package com.dragonfight.mixin.dragon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
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
        self.level().explode(null,
            self.damageSources().mobProjectile(self, (LivingEntity) self.getOwner()),
            null,
            rayTraceResult.getLocation().x,
            rayTraceResult.getLocation().y,
            rayTraceResult.getLocation().z,
            0.5f,
            false,
            Level.ExplosionInteraction.NONE);
    }

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void skipWrongEntities(final HitResult hitResult, final CallbackInfo ci)
    {
        if (hitResult.getType() == HitResult.Type.ENTITY && (((EntityHitResult) hitResult).getEntity() instanceof Projectile
            || ((EntityHitResult) hitResult).getEntity() instanceof EnderDragonPart || ((EntityHitResult) hitResult).getEntity() instanceof EnderDragon))
        {
            ci.cancel();
        }
    }
}
