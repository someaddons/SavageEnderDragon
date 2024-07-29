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

public class CrystalLevelData extends SavedData
{
    public static final String ID = "dragonfight";

    private final Set<BlockPos> crystalPendingRespawns = new HashSet<>();

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

    public Set<BlockPos> getCrystalPendingRespawns()
    {
        return crystalPendingRespawns;
    }

    public static CrystalLevelData getForLevel(final ServerLevel level)
    {
        return level.getDataStorage().computeIfAbsent(new Factory<>(CrystalLevelData::new, (nbt, provider) -> load(nbt), DataFixTypes.LEVEL), CrystalLevelData.ID);
    }
}
