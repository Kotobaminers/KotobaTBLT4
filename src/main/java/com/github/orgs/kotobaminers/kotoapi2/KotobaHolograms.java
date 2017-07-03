package com.github.orgs.kotobaminers.kotoapi2;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.orgs.kotobaminers.kotobatblt4.system.Setting;

public class KotobaHolograms {


	private static final double SPACE = 0.25;
	private static final int DUPLICATE =  3;
	private static final double ARMOR_STAND_HEIGHT = 1.5;


	public static void display(Chest chest) {
		Inventory inventory = chest.getInventory();
		Stream.iterate(0, i -> i + 1)
			.limit(inventory.getSize())
			.forEach(i -> {
				Optional.ofNullable(inventory.getItem(i))
					.map(item -> item.getItemMeta())
					.filter(m -> m instanceof BookMeta)
					.map(m -> (BookMeta) m)
					.ifPresent(m -> new KotobaHolograms().display(KotobaUtility.toStringListFromBookMeta(m), chest.getLocation().clone().add(0.5, 0.5 + i * 0.5, 0.5)));
			});
	}


	private boolean display(List<String> lines, Location base) {
		Collections.reverse(lines);
		if(0 < lines.size()){
			removeNear(base);
			Stream.iterate(0, i -> i + 1)
				.limit(lines.size())
				.forEach(i -> updateArmorStand(lines.get(i), base.clone().add(0, SPACE * i - ARMOR_STAND_HEIGHT, 0)));
			return true;
		}
		return false;
	}


	private void updateArmorStand(String line, Location base) {
		Stream.iterate(0, i -> i)
			.limit(DUPLICATE)
			.forEach(i -> {
				ArmorStand hologram = (ArmorStand) base.getWorld().spawnEntity(base, EntityType.ARMOR_STAND);
				hologram.setVisible(false);
				hologram.setGravity(false);
				hologram.setCustomName(line);
				hologram.setCustomNameVisible(true);
			});
	}


	public void removeNear(Location location) {
		location.getWorld().getNearbyEntities(location, 0.1, 2, 0.1).stream()
			.filter(e -> isHologramArmorStand(e))
			.forEach(Entity::remove);
	}


	public static void removeAll() {
		Setting.getPlugin().getServer().getWorlds().stream()
			.flatMap(world -> world.getEntities().stream())
			.filter(KotobaHolograms::isHologramArmorStand)
			.forEach(e -> e.remove());
	}


	public static void remove(JavaPlugin plugin, KotobaBlockStorage storage) {
		storage.getWorld().getNearbyEntities(storage.getCenter(), (storage.getYMax() - storage.getXMin()) / 2, (storage.getYMax() - storage.getYMin()) / 2, (storage.getZMax() - storage.getZMin()) / 2).stream()
			.filter(KotobaHolograms::isHologramArmorStand)
			.forEach(Entity::remove);;
	}


	private static boolean isHologramArmorStand(Entity entity) {
		if(entity instanceof ArmorStand) {
			ArmorStand stand = (ArmorStand) entity;
			if(stand.hasGravity() == false && stand.isVisible() == false && stand.isCustomNameVisible() == true) {
				return true;
			}
		}
		return false;
	}


}

