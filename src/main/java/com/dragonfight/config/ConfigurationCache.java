package com.dragonfight.config;

import com.dragonfight.DragonfightMod;
import com.dragonfight.fight.DragonFightManagerCustom;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class ConfigurationCache
{
    public static void onConfigChanged()
    {
        DragonFightManagerCustom.spawnOnCrystalDeath = parseEntityTypes(DragonfightMod.config.getCommonConfig().spawnoncrystaldestroy);
        DragonFightManagerCustom.spawnOnCrystalRespawn = parseEntityTypes(DragonfightMod.config.getCommonConfig().spawnoncrystalrespawn);
        DragonFightManagerCustom.spawnOnDragonSitting = parseEntityTypes(DragonfightMod.config.getCommonConfig().spawnwhilelanded);
    }

    private static ImmutableList<EntityType> parseEntityTypes(final List<String> data)
    {
        final ImmutableList.Builder<EntityType> builder = ImmutableList.builder();
        for (final String entry : data)
        {
            final String[] splitEntry = entry.split(",");
            for (final String entityString : splitEntry)
            {
                final ResourceLocation id = ResourceLocation.tryParse(entityString);
                if (id == null)
                {
                    DragonfightMod.LOGGER.error("Config entry could not be parsed, not a valid resource location " + entityString);
                    continue;
                }

                final EntityType type = BuiltInRegistries.ENTITY_TYPE.get(id);
                if (type.equals(BuiltInRegistries.ENTITY_TYPE.get(BuiltInRegistries.ENTITY_TYPE.getDefaultKey())))
                {
                    DragonfightMod.LOGGER.error("Config entry could not be parsed, not a valid entity type" + entityString);
                    continue;
                }
                builder.add(type);
            }
        }

        return builder.build();
    }
}
