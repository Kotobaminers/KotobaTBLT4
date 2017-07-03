package com.github.orgs.kotobaminers.kotobatblt4.userinterface;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.github.orgs.kotobaminers.kotobatblt4.storage.TBLTMap;

public class GUIListener implements Listener {


	@EventHandler
	void onInventoryClick(InventoryClickEvent event) {
		List<TBLTGUI> GUIs = TBLTGUI.find(event.getInventory());
		if(0 < GUIs.size()) {
			event.setCancelled(true);
			if(event.getCurrentItem() == null) {
				return;
			} else if(event.getCurrentItem().getType() == Material.AIR) {
				return;
			}
			GUIs.stream()
				.filter(gui -> gui.canInteract((Player) event.getWhoClicked()))
				.forEach(gui -> gui.onClick(event));
		}
	}


	@EventHandler
	void onInventoryOpen(InventoryOpenEvent event) {
		if(event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();
			if(TBLTMap.isPlayer(player)) {
				if(!(0 < TBLTGUI.find(event.getInventory()).size())) {
					event.setCancelled(true);
				}
			}
		}
	}
}

