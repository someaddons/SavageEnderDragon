package com.dragonfight.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfiguration
{
    public final ForgeConfigSpec                      ForgeConfigSpecBuilder;
    public final ForgeConfigSpec.ConfigValue<Integer> dragonDifficulty;
    public final ForgeConfigSpec.ConfigValue<Boolean> printDragonPhases;

    protected CommonConfiguration(final ForgeConfigSpec.Builder builder)
    {
        builder.push("Adventure mod settings");

        builder.comment("Sets the dragon difficulty modifier, the higher the more difficult the dragon gets. Roughly set it to the amount of players. default:2, vanilla:0");
        dragonDifficulty = builder.defineInRange("dragonDifficulty", 2, 0, 100);

        builder.comment("Prints the dragon phase in chat if enabled: default:false");
        printDragonPhases = builder.define("printDragonPhases", false);

        // Escapes the current category level
        builder.pop();
        ForgeConfigSpecBuilder = builder.build();
    }
}
