package com.dragonfight;

import com.dragonfight.config.Configuration;
import com.dragonfight.event.ClientEventHandler;
import com.dragonfight.event.EventHandler;
import com.dragonfight.event.ModEventHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.dragonfight.DragonfightMod.MODID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MODID)
public class DragonfightMod
{
    public static final String        MODID  = "dragonfight";
    public static final Logger        LOGGER = LogManager.getLogger();
    public static       Configuration config = new Configuration();
    public static       Random        rand   = new Random();

    public DragonfightMod()
    {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (c, b) -> true));
        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(ModEventHandler.class);
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(EventHandler.class);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event)
    {
        // Side safe client event handler
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ClientEventHandler.class);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info(MODID + " mod initialized");
    }
}
