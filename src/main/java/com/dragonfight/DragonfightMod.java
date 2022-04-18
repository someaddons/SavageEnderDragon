package com.dragonfight;

import com.dragonfight.config.Configuration;
import com.dragonfight.event.EventHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file
public class DragonfightMod implements ModInitializer
{
    public static final String          MODID  = "dragonfight";
    public static final Logger          LOGGER = LogManager.getLogger();
    public static       Configuration   config = new Configuration();
    public static       Random          rand   = new Random();
    public static       MinecraftServer server = null;

    public DragonfightMod()
    {
    }

    @Override
    public void onInitialize()
    {
        config.load();
        ServerTickEvents.END_WORLD_TICK.register(EventHandler::onWorldTick);
        ServerLifecycleEvents.SERVER_STARTING.register(tserver -> {server = tserver;});
        ServerLifecycleEvents.SERVER_STOPPING.register(tserver -> {server = null;});
    }
}
