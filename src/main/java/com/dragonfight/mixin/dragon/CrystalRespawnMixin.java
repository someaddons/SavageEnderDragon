package com.dragonfight.mixin.dragon;

import com.dragonfight.fight.DragonFightManagerCustom;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderCrystalEntity.class)
public class CrystalRespawnMixin
{
    final EnderCrystalEntity self = (EnderCrystalEntity) (Object) this;

    @Inject(method = "onDestroyedBy", at = @At("HEAD"))
    private void test(final DamageSource damageSource, final CallbackInfo ci)
    {
        DragonFightManagerCustom.onCrystalDeath(self, damageSource);
    }
}
