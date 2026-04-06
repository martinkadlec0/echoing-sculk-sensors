package harou.echoing_sculk_sensors.block.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.gameevent.GameEvent;

public enum GameSoundEvent implements StringRepresentable {
	NONE("none", 0, null), // Represents no stored sound
	STEP("step", 1, GameEvent.STEP.key()),
	SWIM("swim", 1, GameEvent.SWIM.key()),
	FLAP("flap", 1, GameEvent.FLAP.key()),
	PROJECTILE_LAND("projectile_land", 2, GameEvent.PROJECTILE_LAND.key()),
	HIT_GROUND("hit_ground", 2, GameEvent.HIT_GROUND.key()),
	SPLASH("splash", 2, GameEvent.SPLASH.key()),
	ITEM_INTERACT_FINISH("item_interact_finish", 3, GameEvent.ITEM_INTERACT_FINISH.key()),
	PROJECTILE_SHOOT("projectile_shoot", 3, GameEvent.PROJECTILE_SHOOT.key()),
	INSTRUMENT_PLAY("instrument_play", 3, GameEvent.INSTRUMENT_PLAY.key()),
	ENTITY_ACTION("entity_action", 4, GameEvent.ENTITY_ACTION.key()),
	ELYTRA_GLIDE("elytra_glide", 4, GameEvent.ELYTRA_GLIDE.key()),
	UNEQUIP("unequip", 4, GameEvent.UNEQUIP.key()),
	ENTITY_DISMOUNT("entity_dismount", 5, GameEvent.ENTITY_DISMOUNT.key()),
	EQUIP("equip", 5, GameEvent.EQUIP.key()),
	ENTITY_INTERACT("entity_interact", 6, GameEvent.ENTITY_INTERACT.key()),
	SHEAR("shear", 6, GameEvent.SHEAR.key()),
	ENTITY_MOUNT("entity_mount", 6, GameEvent.ENTITY_MOUNT.key()),
	ENTITY_DAMAGE("entity_damage", 7, GameEvent.ENTITY_DAMAGE.key()),
	DRINK("drink", 8, GameEvent.DRINK.key()),
	EAT("eat", 8, GameEvent.EAT.key()),
	CONTAINER_CLOSE("container_close", 9, GameEvent.CONTAINER_CLOSE.key()),
	BLOCK_CLOSE("block_close", 9, GameEvent.BLOCK_CLOSE.key()),
	BLOCK_DEACTIVATE("block_deactivate", 9, GameEvent.BLOCK_DEACTIVATE.key()),
	BLOCK_DETACH("block_detach", 9, GameEvent.BLOCK_DETACH.key()),
	CONTAINER_OPEN("container_open", 10, GameEvent.CONTAINER_OPEN.key()),
	BLOCK_OPEN("block_open", 10, GameEvent.BLOCK_OPEN.key()),
	BLOCK_ACTIVATE("block_activate", 10, GameEvent.BLOCK_ACTIVATE.key()),
	BLOCK_ATTACH("block_attach", 10, GameEvent.BLOCK_ATTACH.key()),
	PRIME_FUSE("prime_fuse", 10, GameEvent.PRIME_FUSE.key()),
	NOTE_BLOCK_PLAY("note_block_play", 10, GameEvent.NOTE_BLOCK_PLAY.key()),
	BLOCK_CHANGE("block_change", 11, GameEvent.BLOCK_CHANGE.key()),
	BLOCK_DESTROY("block_destroy", 12, GameEvent.BLOCK_DESTROY.key()),
	FLUID_PICKUP("fluid_pickup", 12, GameEvent.FLUID_PICKUP.key()),
	BLOCK_PLACE("block_place", 13, GameEvent.BLOCK_PLACE.key()),
	FLUID_PLACE("fluid_place", 13, GameEvent.FLUID_PLACE.key()),
	ENTITY_PLACE("entity_place", 14, GameEvent.ENTITY_PLACE.key()),
	LIGHTNING_STRIKE("lightning_strike", 14, GameEvent.LIGHTNING_STRIKE.key()),
	TELEPORT("teleport", 14, GameEvent.TELEPORT.key()),
	ENTITY_DIE("entity_die", 15, GameEvent.ENTITY_DIE.key()),
	EXPLODE("explode", 15, GameEvent.EXPLODE.key()),
	RESONATE_1("resonate_1", 1, GameEvent.RESONATE_1.key()),
	RESONATE_2("resonate_2", 2, GameEvent.RESONATE_2.key()),
	RESONATE_3("resonate_3", 3, GameEvent.RESONATE_3.key()),
	RESONATE_4("resonate_4", 4, GameEvent.RESONATE_4.key()),
	RESONATE_5("resonate_5", 5, GameEvent.RESONATE_5.key()),
	RESONATE_6("resonate_6", 6, GameEvent.RESONATE_6.key()),
	RESONATE_7("resonate_7", 7, GameEvent.RESONATE_7.key()),
	RESONATE_8("resonate_8", 8, GameEvent.RESONATE_8.key()),
	RESONATE_9("resonate_9", 9, GameEvent.RESONATE_9.key()),
	RESONATE_10("resonate_10", 10, GameEvent.RESONATE_10.key()),
	RESONATE_11("resonate_11", 11, GameEvent.RESONATE_11.key()),
	RESONATE_12("resonate_12", 12, GameEvent.RESONATE_12.key()),
	RESONATE_13("resonate_13", 13, GameEvent.RESONATE_13.key()),
	RESONATE_14("resonate_14", 14, GameEvent.RESONATE_14.key()),
	RESONATE_15("resonate_15", 15, GameEvent.RESONATE_15.key());

	private final String name;
	private final int frequency;
	private final ResourceKey<GameEvent> gameEventKey;

	private static final Map<ResourceKey<GameEvent>, GameSoundEvent> BY_REGISTRY_KEY = Arrays.stream(values())
			.filter(e -> e.gameEventKey != null)
			.collect(Collectors.toMap(e -> e.gameEventKey, Function.identity()));

	GameSoundEvent(String name, int frequency, ResourceKey<GameEvent> gameEventKey) {
		this.name = name;
		this.frequency = frequency;
		this.gameEventKey = gameEventKey;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public int getFrequency() {
		return frequency;
	}

	public ResourceKey<GameEvent> getGameEventKey() {
		return gameEventKey;
	}

	public static GameSoundEvent fromRegistryKey(ResourceKey<GameEvent> key) {
		return BY_REGISTRY_KEY.getOrDefault(key, NONE);
	}
}
