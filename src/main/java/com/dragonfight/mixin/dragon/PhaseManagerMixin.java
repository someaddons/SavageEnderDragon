package com.dragonfight.mixin.dragon;

import com.dragonfight.fight.DragonFightManagerCustom;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonPhaseManager.class)
public class PhaseManagerMixin
{

    @Shadow
    private DragonPhaseInstance currentPhase;

    @Shadow
    @Final
    private EnderDragon dragon;

    @Inject(method = "setPhase", at = @At("HEAD"))
    private void onPhaseChange(final EnderDragonPhase<?> phaseType, final CallbackInfo ci)
    {
        if (currentPhase != null && phaseType != this.currentPhase.getPhase() && !dragon.level.isClientSide)
        {
            DragonFightManagerCustom.onPhaseChange(phaseType, this.currentPhase.getPhase(), dragon);
        }
    }
}
