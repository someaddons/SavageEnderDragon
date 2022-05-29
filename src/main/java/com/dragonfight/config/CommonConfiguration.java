package com.dragonfight.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class CommonConfiguration
{
    public final ForgeConfigSpec                                     ForgeConfigSpecBuilder;
    public final ForgeConfigSpec.ConfigValue<Integer>                dragonDifficulty;
    public final ForgeConfigSpec.ConfigValue<Boolean>                printDragonPhases;
    public final ForgeConfigSpec.ConfigValue<Boolean>                disableDragonAreaSpawns;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> spawnoncrystaldestroy;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> spawnoncrystalrespawn;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> spawnwhilelanded;

    protected CommonConfiguration(final ForgeConfigSpec.Builder builder)
    {
        builder.push("Dragon settings");

        builder.comment("Sets the dragon difficulty modifier, the higher the more difficult the dragon gets."
                          + "Scales up mob spawn amount, dragon damage and health aswell as crystal respawn intervals. Note that the difficulty already scales on the playercount involved in the fight, this is a static bonus ontop."
                          + "default:2, vanilla:0");
        dragonDifficulty = builder.defineInRange("dragonDifficulty", 2, 0, 100);

        builder.comment("Prints the dragon phase in chat if enabled: default:false");
        printDragonPhases = builder.define("printDragonPhases", false);

        builder.comment("Disables mob spawning on the Dragon island: default:true");
        disableDragonAreaSpawns = builder.define("disableDragonAreaSpawns", true);

        builder.comment(
          "List of mobs spawning when a crystal is destroyed at range, intended to be flying or ranged to close the gap: e.g. format :  [\"minecraft:zombie\", \"minecraft:creeper\"]");
        spawnoncrystaldestroy = builder.defineList("spawnoncrystaldestroy", Lists.newArrayList("minecraft:phantom"), e -> e instanceof String);

        builder.comment(
          "List of mobs spawning on crystal respawn, intended to be ranged to ward of players from a distance: e.g. format :  [\"minecraft:zombie\", \"minecraft:creeper\"]");
        spawnoncrystalrespawn = builder.defineList("spawnoncrystalrespawn", Lists.newArrayList("minecraft:blaze"), e -> e instanceof String);

        builder.comment(
          "List of mobs spawning while the dragon is sitting in the middle, intended to be melee and not vulnerable to ranged: e.g. format :  [\"minecraft:zombie\", \"minecraft:creeper\"]");
        spawnwhilelanded = builder.defineList("spawnwhilelanded", Lists.newArrayList("minecraft:enderman"), e -> e instanceof String);

        // Escapes the current category level
        builder.pop();
        ForgeConfigSpecBuilder = builder.build();
    }
}
