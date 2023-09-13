package com.dragonfight.config;

import com.dragonfight.DragonfightMod;
import com.dragonfight.fight.DragonFightManagerCustom;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
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

    private static ImmutableList<EntitySpawnData> parseEntityTypes(final List<String> data)
    {
        final ImmutableList.Builder<EntitySpawnData> builder = ImmutableList.builder();
        for (final String entry : data)
        {
            final String[] splitEntry = entry.split(",");
            for (final String entityString : splitEntry)
            {
                // TODO: Test with example
                int nbtStart = entityString.indexOf("{");
                nbtStart = (nbtStart == -1) ? entityString.length() : nbtStart;
                String typeString = entityString.substring(0, nbtStart);
                String nbtString = entityString.substring(nbtStart);

                final ResourceLocation id = ResourceLocation.tryParse(typeString);
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

                CompoundTag nbt = null;

                if (!nbtString.isEmpty())
                {
                    try
                    {
                        nbt = TagParser.parseTag(nbtString);
                    }
                    catch (Exception e)
                    {
                        DragonfightMod.LOGGER.error("Config entry NBT data could not be parsed for " + entityString, e);
                    }
                }

                builder.add(new EntitySpawnData(type, nbt));
            }
        }

        return builder.build();
    }

    public static class EntitySpawnData
    {
        public final EntityType  type;
        public final CompoundTag nbt;

        public EntitySpawnData(final EntityType type, final CompoundTag nbt)
        {
            this.type = type;
            this.nbt = nbt;
        }
    }
}
