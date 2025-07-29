package com.dragonfight.fight;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CrystalLevelData extends SavedData
{
    public static final String ID = "dragonfight";

    private Set<BlockPos> crystalPendingRespawns = new HashSet<>();
    private Set<UUID>     toIgnore               = new HashSet<>();

    public CrystalLevelData()
    {

    }

    public static CrystalLevelData load(CompoundTag tag)
    {
        CrystalLevelData data = new CrystalLevelData();
        data.read(tag);
        return data;
    }

    public void read(CompoundTag nbt)
    {
        ListTag list = nbt.getList("positions", Tag.TAG_COMPOUND);
        crystalPendingRespawns.clear();
        for (final Tag tag : list)
        {
            if (tag instanceof CompoundTag)
            {
                crystalPendingRespawns.add(new BlockPos(((CompoundTag) tag).getInt("x"), ((CompoundTag) tag).getInt("y"), ((CompoundTag) tag).getInt("z")));
            }
        }

        if (nbt.contains("uuids"))
        {
            ListTag uuids = nbt.getList("uuids", Tag.TAG_COMPOUND);
            toIgnore.clear();
            for (final Tag tag : uuids)
            {
                if (tag instanceof CompoundTag)
                {
                    toIgnore.add(((CompoundTag) tag).getUUID("uuid"));
                }
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider)
    {
        ListTag list = new ListTag();
        for (final BlockPos data : crystalPendingRespawns)
        {
            CompoundTag tag = new CompoundTag();
            tag.putInt("x", data.getX());
            tag.putInt("y", data.getY());
            tag.putInt("z", data.getZ());
            list.add(tag);
        }

        nbt.put("positions", list);

        ListTag uuidList = new ListTag();
        for (final UUID uuid : toIgnore)
        {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("uuid", uuid);
            uuidList.add(tag);
        }

        nbt.put("uuids", uuidList);

        setDirty(false);
        return nbt;
    }

    public void addPosition(final BlockPos pos)
    {
        setDirty();
        crystalPendingRespawns.add(pos);
    }

    public void removePosition(final BlockPos pos)
    {
        setDirty();
        crystalPendingRespawns.remove(pos);
    }

    public void ignoreUUID(final UUID toIgnore)
    {
        if (this.toIgnore.size() > 500)
        {
            this.toIgnore.clear();
        }

        setDirty();
        this.toIgnore.add(toIgnore);
    }

    public boolean isIgnored(final UUID toCheck)
    {
        return toIgnore.contains(toCheck);
    }

    public Set<BlockPos> getCrystalPendingRespawns()
    {
        return crystalPendingRespawns;
    }

    public static CrystalLevelData getForLevel(final ServerLevel level)
    {
        return level.getDataStorage().computeIfAbsent(new Factory<>(CrystalLevelData::new, (nbt, provider) -> load(nbt), DataFixTypes.LEVEL), CrystalLevelData.ID);
    }
}
