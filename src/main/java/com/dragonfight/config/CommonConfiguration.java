package com.dragonfight.config;

import com.dragonfight.DragonfightMod;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CommonConfiguration
{
    public int          dragonDifficulty        = 2;
    public boolean      printDragonPhases       = false;
    public boolean      disableDragonAreaSpawns = true;
    public List<String> spawnoncrystaldestroy   = Lists.newArrayList("minecraft:phantom");
    public List<String> spawnoncrystalrespawn   = Lists.newArrayList("minecraft:blaze");
    public List<String> spawnwhilelanded        = Lists.newArrayList("minecraft:enderman");

    protected CommonConfiguration()
    {
    }

    public JsonObject serialize()
    {
        final JsonObject root = new JsonObject();

        final JsonObject entry = new JsonObject();
        entry.addProperty("desc:", "Sets the dragon difficulty modifier, the higher the more difficult the dragon gets."
                                     + "Scales up mob spawn amount, dragon damage and health aswell as crystal respawn intervals. Note that the difficulty already scales on the playercount involved in the fight, this is a static bonus ontop."
                                     + "default:2, vanilla:0");
        entry.addProperty("dragonDifficulty", dragonDifficulty);
        root.add("dragonDifficulty", entry);

        final JsonObject entry2 = new JsonObject();
        entry2.addProperty("desc:", "Prints the dragon phase in chat if enabled: default:false");
        entry2.addProperty("printDragonPhases", printDragonPhases);
        root.add("printDragonPhases", entry2);

        final JsonObject entry3 = new JsonObject();
        entry3.addProperty("desc:", "Disables mob spawning on the Dragon island: default:true");
        entry3.addProperty("disableDragonAreaSpawns", disableDragonAreaSpawns);
        root.add("disableDragonAreaSpawns", entry3);

        final JsonObject entry4 = new JsonObject();
        entry4.addProperty("desc:",
          "List of mobs spawning when a crystal is destroyed at range, intended to be flying or ranged to close the gap: e.g. format :  [\"minecraft:zombie\", \"minecraft:creeper\"]");
        final JsonArray list4 = new JsonArray();
        for (final String name : spawnoncrystaldestroy)
        {
            list4.add(name);
        }
        entry4.add("spawnoncrystaldestroy", list4);
        root.add("spawnoncrystaldestroy", entry4);

        final JsonObject entry5 = new JsonObject();
        entry5.addProperty("desc:",
          "List of mobs spawning on crystal respawn, intended to be ranged to ward of players from a distance: e.g. format :  [\"minecraft:zombie\", \"minecraft:creeper\"]");
        final JsonArray list5 = new JsonArray();
        for (final String name : spawnoncrystalrespawn)
        {
            list5.add(name);
        }
        entry5.add("spawnoncrystalrespawn", list5);
        root.add("spawnoncrystalrespawn", entry5);

        final JsonObject entry6 = new JsonObject();
        entry6.addProperty("desc:",
          "List of mobs spawning while the dragon is sitting in the middle, intended to be melee and not vulnerable to ranged: e.g. format :  [\"minecraft:zombie\", \"minecraft:creeper\"]");
        final JsonArray list6 = new JsonArray();
        for (final String name : spawnwhilelanded)
        {
            list6.add(name);
        }
        entry6.add("spawnwhilelanded", list6);
        root.add("spawnwhilelanded", entry6);


        return root;
    }

    public void deserialize(JsonObject data)
    {
        if (data == null)
        {
            DragonfightMod.LOGGER.error("Config file was empty!");
            return;
        }

        try
        {
            dragonDifficulty = data.get("dragonDifficulty").getAsJsonObject().get("dragonDifficulty").getAsInt();
            printDragonPhases = data.get("printDragonPhases").getAsJsonObject().get("printDragonPhases").getAsBoolean();
            disableDragonAreaSpawns = data.get("disableDragonAreaSpawns").getAsJsonObject().get("disableDragonAreaSpawns").getAsBoolean();
            spawnoncrystaldestroy = new ArrayList<>();
            for (final JsonElement element : data.get("spawnoncrystaldestroy").getAsJsonObject().get("spawnoncrystaldestroy").getAsJsonArray())
            {
                spawnoncrystaldestroy.add(element.getAsString());
            }

            spawnoncrystalrespawn = new ArrayList<>();
            for (final JsonElement element : data.get("spawnoncrystalrespawn").getAsJsonObject().get("spawnoncrystalrespawn").getAsJsonArray())
            {
                spawnoncrystalrespawn.add(element.getAsString());
            }
            spawnwhilelanded = new ArrayList<>();
            for (final JsonElement element : data.get("spawnwhilelanded").getAsJsonObject().get("spawnwhilelanded").getAsJsonArray())
            {
                spawnwhilelanded.add(element.getAsString());
            }
        }
        catch (Exception e)
        {
            DragonfightMod.LOGGER.error("Could not parse config file", e);
        }
    }
}
