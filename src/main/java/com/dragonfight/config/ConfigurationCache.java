package com.dragonfight.config;

import com.dragonfight.DragonfightMod;
import com.dragonfight.fight.DragonFightManagerCustom;
import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

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

                final EntityType type = ForgeRegistries.ENTITY_TYPES.getValue(id);
                if (type.equals(ForgeRegistries.ENTITY_TYPES.getValue((ForgeRegistries.ENTITY_TYPES.getDefaultKey()))))
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
