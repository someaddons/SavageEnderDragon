package com.dragonfight.fight;

import com.dragonfight.DragonfightMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CrystalLevelData extends SavedData
{
    public static final SavedDataType<CrystalLevelData> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(DragonfightMod.MODID, "dragonfight"),
        CrystalLevelData::new,
        CompoundTag.CODEC.xmap(CrystalLevelData::load, CrystalLevelData::save),
        DataFixTypes.LEVEL);

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
        ListTag list = nbt.getList("positions").get();
        crystalPendingRespawns.clear();
        for (final Tag tag : list)
        {
            if (tag instanceof CompoundTag)
            {
                crystalPendingRespawns.add(new BlockPos(((CompoundTag) tag).getInt("x").get(), ((CompoundTag) tag).getInt("y").get(), ((CompoundTag) tag).getInt("z").get()));
            }
        }

        if (nbt.contains("uuids"))
        {
            ListTag uuids = nbt.getList("uuids").get();
            toIgnore.clear();
            for (final Tag tag : uuids)
                if (tag instanceof CompoundTag)
                {
                    toIgnore.add(UUID.fromString(((CompoundTag) tag).getString("uuid").get()));
                    {
                }
            }
        }
    }

    public CompoundTag save()
    {
        CompoundTag nbt = new CompoundTag();
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
            tag.putString("uuid", uuid.toString());
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
        return level.getDataStorage().computeIfAbsent(TYPE);
    }
}
