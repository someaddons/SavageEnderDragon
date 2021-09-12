package com.dragonfight.mixin.dragon;

import com.dragonfight.fight.DragonFightManagerCustom;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PhaseManager.class)
public class PhaseManagerMixin
{

    @Shadow
    private IPhase currentPhase;

    @Shadow
    @Final
    private EnderDragonEntity dragon;

    @Inject(method = "setPhase", at = @At("HEAD"))
    private void onPhaseChange(final PhaseType<?> phaseType, final CallbackInfo ci)
    {
        if (currentPhase != null && phaseType != this.currentPhase.getPhase() && !dragon.level.isClientSide)
        {
            DragonFightManagerCustom.onPhaseChange(phaseType, this.currentPhase.getPhase(), dragon);
        }
    }
}
