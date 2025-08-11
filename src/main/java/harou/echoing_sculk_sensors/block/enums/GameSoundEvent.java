package harou.echoing_sculk_sensors.block.enums;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.event.GameEvent;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum GameSoundEvent implements StringIdentifiable {
    NONE("none", 0, null), // Represents no stored sound
    STEP("step", 1, GameEvent.STEP.registryKey()),
    SWIM("swim", 1, GameEvent.SWIM.registryKey()),
    FLAP("flap", 1, GameEvent.FLAP.registryKey()),
    PROJECTILE_LAND("projectile_land", 2, GameEvent.PROJECTILE_LAND.registryKey()),
    HIT_GROUND("hit_ground", 2, GameEvent.HIT_GROUND.registryKey()),
    SPLASH("splash", 2, GameEvent.SPLASH.registryKey()),
    ITEM_INTERACT_FINISH("item_interact_finish", 3, GameEvent.ITEM_INTERACT_FINISH.registryKey()),
    PROJECTILE_SHOOT("projectile_shoot", 3, GameEvent.PROJECTILE_SHOOT.registryKey()),
    INSTRUMENT_PLAY("instrument_play", 3, GameEvent.INSTRUMENT_PLAY.registryKey()),
    ENTITY_ACTION("entity_action", 4, GameEvent.ENTITY_ACTION.registryKey()),
    ELYTRA_GLIDE("elytra_glide", 4, GameEvent.ELYTRA_GLIDE.registryKey()),
    UNEQUIP("unequip", 4, GameEvent.UNEQUIP.registryKey()),
    ENTITY_DISMOUNT("entity_dismount", 5, GameEvent.ENTITY_DISMOUNT.registryKey()),
    EQUIP("equip", 5, GameEvent.EQUIP.registryKey()),
    ENTITY_INTERACT("entity_interact", 6, GameEvent.ENTITY_INTERACT.registryKey()),
    SHEAR("shear", 6, GameEvent.SHEAR.registryKey()),
    ENTITY_MOUNT("entity_mount", 6, GameEvent.ENTITY_MOUNT.registryKey()),
    ENTITY_DAMAGE("entity_damage", 7, GameEvent.ENTITY_DAMAGE.registryKey()),
    DRINK("drink", 8, GameEvent.DRINK.registryKey()),
    EAT("eat", 8, GameEvent.EAT.registryKey()),
    CONTAINER_CLOSE("container_close", 9, GameEvent.CONTAINER_CLOSE.registryKey()),
    BLOCK_CLOSE("block_close", 9, GameEvent.BLOCK_CLOSE.registryKey()),
    BLOCK_DEACTIVATE("block_deactivate", 9, GameEvent.BLOCK_DEACTIVATE.registryKey()),
    BLOCK_DETACH("block_detach", 9, GameEvent.BLOCK_DETACH.registryKey()),
    CONTAINER_OPEN("container_open", 10, GameEvent.CONTAINER_OPEN.registryKey()),
    BLOCK_OPEN("block_open", 10, GameEvent.BLOCK_OPEN.registryKey()),
    BLOCK_ACTIVATE("block_activate", 10, GameEvent.BLOCK_ACTIVATE.registryKey()),
    BLOCK_ATTACH("block_attach", 10, GameEvent.BLOCK_ATTACH.registryKey()),
    PRIME_FUSE("prime_fuse", 10, GameEvent.PRIME_FUSE.registryKey()),
    NOTE_BLOCK_PLAY("note_block_play", 10, GameEvent.NOTE_BLOCK_PLAY.registryKey()),
    BLOCK_CHANGE("block_change", 11, GameEvent.BLOCK_CHANGE.registryKey()),
    BLOCK_DESTROY("block_destroy", 12, GameEvent.BLOCK_DESTROY.registryKey()),
    FLUID_PICKUP("fluid_pickup", 12, GameEvent.FLUID_PICKUP.registryKey()),
    BLOCK_PLACE("block_place", 13, GameEvent.BLOCK_PLACE.registryKey()),
    FLUID_PLACE("fluid_place", 13, GameEvent.FLUID_PLACE.registryKey()),
    ENTITY_PLACE("entity_place", 14, GameEvent.ENTITY_PLACE.registryKey()),
    LIGHTNING_STRIKE("lightning_strike", 14, GameEvent.LIGHTNING_STRIKE.registryKey()),
    TELEPORT("teleport", 14, GameEvent.TELEPORT.registryKey()),
    ENTITY_DIE("entity_die", 15, GameEvent.ENTITY_DIE.registryKey()),
    EXPLODE("explode", 15, GameEvent.EXPLODE.registryKey()),
    RESONATE_1("resonate_1", 1, GameEvent.RESONATE_1.registryKey()),
    RESONATE_2("resonate_2", 2, GameEvent.RESONATE_2.registryKey()),
    RESONATE_3("resonate_3", 3, GameEvent.RESONATE_3.registryKey()),
    RESONATE_4("resonate_4", 4, GameEvent.RESONATE_4.registryKey()),
    RESONATE_5("resonate_5", 5, GameEvent.RESONATE_5.registryKey()),
    RESONATE_6("resonate_6", 6, GameEvent.RESONATE_6.registryKey()),
    RESONATE_7("resonate_7", 7, GameEvent.RESONATE_7.registryKey()),
    RESONATE_8("resonate_8", 8, GameEvent.RESONATE_8.registryKey()),
    RESONATE_9("resonate_9", 9, GameEvent.RESONATE_9.registryKey()),
    RESONATE_10("resonate_10", 10, GameEvent.RESONATE_10.registryKey()),
    RESONATE_11("resonate_11", 11, GameEvent.RESONATE_11.registryKey()),
    RESONATE_12("resonate_12", 12, GameEvent.RESONATE_12.registryKey()),
    RESONATE_13("resonate_13", 13, GameEvent.RESONATE_13.registryKey()),
    RESONATE_14("resonate_14", 14, GameEvent.RESONATE_14.registryKey()),
    RESONATE_15("resonate_15", 15, GameEvent.RESONATE_15.registryKey());

    private final String name;
    private final int frequency;
    private final RegistryKey<GameEvent> gameEventKey;

    private static final Map<RegistryKey<GameEvent>, GameSoundEvent> BY_REGISTRY_KEY = Arrays.stream(values())
            .filter(e -> e.gameEventKey != null)
            .collect(Collectors.toMap(e -> e.gameEventKey, Function.identity()));

    GameSoundEvent(String name, int frequency, RegistryKey<GameEvent> gameEventKey) {
        this.name = name;
        this.frequency = frequency;
        this.gameEventKey = gameEventKey;
    }

    @Override
    public String asString() {
        return name;
    }

    public int getFrequency() {
        return frequency;
    }

    public RegistryKey<GameEvent> getGameEventKey() {
        return gameEventKey;
    }

    public static GameSoundEvent fromRegistryKey(RegistryKey<GameEvent> key) {
        return BY_REGISTRY_KEY.getOrDefault(key, NONE);
    }
}
