package harou.echoing_sculk_sensors.data;

import harou.echoing_sculk_sensors.EchoingSculkSensors;
import harou.echoing_sculk_sensors.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends FabricRecipeProvider {
    public RecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new net.minecraft.data.recipes.RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                EchoingSculkSensors.LOGGER.info("Generating Echoing Sculk Sensor recipes...");
                
                // Generate shapeless recipe: 1 sculk sensor + 1 echo shard = 1 echoing sculk sensor
                shapeless(RecipeCategory.REDSTONE, ModItems.ECHOING_SCULK_SENSOR)
                    .requires(Items.SCULK_SENSOR)
                    .requires(Items.ECHO_SHARD)
                    .unlockedBy("has_sculk_sensor", has(Items.SCULK_SENSOR))
                    .unlockedBy("has_echo_shard", has(Items.ECHO_SHARD))
                    .save(output);
                
                EchoingSculkSensors.LOGGER.info("Echoing Sculk Sensor recipes generated successfully!");
            }
        };
    }
    
    @Override
    public String getName() {
        return "Echoing Sculk Sensor Recipes";
    }
}
