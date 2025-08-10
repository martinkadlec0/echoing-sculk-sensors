package harou.echoing_sculk_sensors.data;

import harou.echoing_sculk_sensors.EchoingSculkSensors;
import harou.echoing_sculk_sensors.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends FabricRecipeProvider {
    public RecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected net.minecraft.data.recipe.RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new net.minecraft.data.recipe.RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                EchoingSculkSensors.LOGGER.info("Generating Echoing Sculk Sensor recipes...");
                
                // Generate shapeless recipe: 1 sculk sensor + 1 echo shard = 1 echoing sculk sensor
                createShapeless(RecipeCategory.REDSTONE, ModItems.ECHOING_SCULK_SENSOR)
                    .input(Items.SCULK_SENSOR)
                    .input(Items.ECHO_SHARD)
                    .criterion("has_sculk_sensor", conditionsFromItem(Items.SCULK_SENSOR))
                    .criterion("has_echo_shard", conditionsFromItem(Items.ECHO_SHARD))
                    .offerTo(exporter);
                
                EchoingSculkSensors.LOGGER.info("Echoing Sculk Sensor recipes generated successfully!");
            }
        };
    }
    
    @Override
    public String getName() {
        return "Echoing Sculk Sensor Recipes";
    }
}
