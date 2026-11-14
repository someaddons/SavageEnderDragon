package com.dragonfight.config;

import com.dragonfight.DragonfightMod;
import com.dragonfight.fight.DragonFightManagerCustom;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Optional;

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
        for (final String entityString : data)
        {
                int nbtStart = entityString.indexOf("{");
                nbtStart = (nbtStart == -1) ? entityString.length() : nbtStart;
                String typeString = entityString.substring(0, nbtStart);
                String nbtString = entityString.substring(nbtStart);

            final Identifier id = Identifier.tryParse(typeString);
                if (id == null)
                {
                    DragonfightMod.LOGGER.error("Config entry could not be parsed, not a valid resource location " + entityString);
                    continue;
                }

            final Optional<Holder.Reference<EntityType<?>>> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type.isEmpty() || type.equals(BuiltInRegistries.ENTITY_TYPE.get((BuiltInRegistries.ENTITY_TYPE.getDefaultKey()))))
                {
                    DragonfightMod.LOGGER.error("Config entry could not be parsed, not a valid entity type" + entityString);
                    continue;
                }

                CompoundTag nbt = null;

                if (!nbtString.isEmpty())
                {
                    try
                    {
                        nbt = TagParser.parseCompoundFully(nbtString);
                    }
                    catch (Exception e)
                    {
                        DragonfightMod.LOGGER.error("Config entry NBT data could not be parsed for " + entityString, e);
                    }
                }

            builder.add(new EntitySpawnData(type.get(), nbt));
        }

        return builder.build();
    }

    public static class EntitySpawnData
    {
        public final Holder<EntityType<?>> type;
        public final CompoundTag nbt;

        public EntitySpawnData(final Holder<EntityType<?>> type, final CompoundTag nbt)
        {
            this.type = type;
            this.nbt = nbt;
        }
    }
}
