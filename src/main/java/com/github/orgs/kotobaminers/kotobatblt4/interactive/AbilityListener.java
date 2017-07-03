package com.github.orgs.kotobaminers.kotobatblt4.interactive;

import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.orgs.kotobaminers.kotobatblt4.icon.AbilityIcon;
import com.github.orgs.kotobaminers.kotobatblt4.storage.TBLTMap;

public class AbilityListener implements Listener {
	@SuppressWarnings("deprecation")
	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if(item == null) return;
		if(item.getType() == Material.AIR) return;
		Stream.of(AbilityIcon.values())
			.filter(i -> i.isSame(item))
			.filter(i -> TBLTMap.isPlayer(event.getPlayer()))
			.map(i -> {
				i.onPlayerInteract(event);
				return true;
			})
			.filter(b -> b)
			.findFirst()
			.ifPresent(b -> event.setCancelled(true));
	}

}
