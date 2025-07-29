package com.dragonfight.mixin.dragon;

import com.dragonfight.fight.CrystalLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin
{
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onAddCrystal(
        final UseOnContext p_41176_,
        final CallbackInfoReturnable<InteractionResult> cir,
        final Level level,
        final BlockPos blockpos,
        final BlockState blockstate,
        final BlockPos blockpos1,
        final double d0,
        final double d1,
        final double d2,
        final List list,
        final EndCrystal endcrystal)
    {
        if (endcrystal != null)
        {
            CrystalLevelData.getForLevel((ServerLevel) level).ignoreUUID(endcrystal.getUUID());
        }
    }
}
