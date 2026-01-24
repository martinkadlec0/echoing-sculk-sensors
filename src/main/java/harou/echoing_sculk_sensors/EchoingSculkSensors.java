package harou.echoing_sculk_sensors;

import harou.echoing_sculk_sensors.block.ModBlocks;
import harou.echoing_sculk_sensors.block.entity.ModBlockEntities;
import harou.echoing_sculk_sensors.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoingSculkSensors implements ModInitializer {
    public static final String MOD_ID = "echoing-sculk-sensors";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Initialize blocks, block entities, and items
        // IMPORTANT: Blocks must be registered before block entities
        ModBlocks.initialize();
        ModBlockEntities.initialize();
        ModItems.initialize();
        
        // Add Echoing Sculk Sensor to the Redstone Blocks creative tab after Calibrated Sculk Sensor
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> {
            entries.addAfter(Blocks.CALIBRATED_SCULK_SENSOR.asItem(), ModItems.ECHOING_SCULK_SENSOR);
        });
        
        LOGGER.info("Echoing Sculk Sensors mod initialized!");
    }
}