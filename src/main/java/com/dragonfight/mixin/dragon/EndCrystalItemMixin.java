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
        final UseOnContext context,
        final CallbackInfoReturnable<InteractionResult> cir,
        final Level level,
        final BlockPos pos,
        final BlockState blockState,
        final BlockPos above,
        final double x,
        final double y,
        final double z,
        final List entities,
        final ServerLevel serverLevel,
        final EndCrystal endcrystal)
    {
        if (endcrystal != null)
        {
            CrystalLevelData.getForLevel((ServerLevel) level).ignoreUUID(endcrystal.getUUID());
        }
    }
}
