package com.github.orgs.kotobaminers.kotoapi2;

import org.bukkit.block.Chest;
import org.bukkit.event.player.PlayerInteractEvent;

public interface KotobaInteractive {


	public void onPlayerInteract(PlayerInteractEvent event, Chest chest);


}

