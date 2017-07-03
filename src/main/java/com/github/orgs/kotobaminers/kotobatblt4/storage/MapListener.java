package com.github.orgs.kotobaminers.kotobatblt4.storage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class MapListener implements Listener {


	@EventHandler
	void onHitEnemy(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();
		if(TBLTMap.isInAny(damaged.getLocation())) {
			if(damager.getType() == EntityType.PLAYER) {
				event.setCancelled(true);
			}
		}
	}


	@EventHandler
	void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(TBLTMap.isPlayer(player)) {
				event.setCancelled(true);
			}
		}
	}


	@EventHandler
	void onPlayerChangeFoodLevel(FoodLevelChangeEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(TBLTMap.isPlayer(player)) {
				event.setCancelled(true);
				player.setFoodLevel(20);
				player.setSaturation(20);
				player.setHealth(20);
			}
		}
	}


	@EventHandler
	void onItemPickup(PlayerPickupItemEvent event) {
		if(TBLTMap.isPlayer(event.getPlayer())) {
			event.setCancelled(true);
		}
	}


	@EventHandler
	void onItemDrop(PlayerDropItemEvent event) {
		if(TBLTMap.isPlayer(event.getPlayer())) {
			event.setCancelled(true);
		}
	}


}
