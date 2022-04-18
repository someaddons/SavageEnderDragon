package com.dragonfight.mixin.dragon;

import com.dragonfight.fight.DragonFightManagerCustom;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystal.class)
public class CrystalRespawnMixin
{
    final EndCrystal self = (EndCrystal) (Object) this;

    @Inject(method = "onDestroyedBy", at = @At("HEAD"))
    private void test(final DamageSource damageSource, final CallbackInfo ci)
    {
        DragonFightManagerCustom.onCrystalDeath(self, damageSource);
    }
}
