package com.github.orgs.kotobaminers.kotobatblt4.portal;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerPortalEvent;

public class TeleportPortalMeta implements PortalMeta {


	private Location destination;


	public TeleportPortalMeta(Location destination) {
		this.destination = destination;
	}


	@Override
	public Location getDestination() {
		return destination;
	}


	@Override
	public void tryOpen() {
	}


	@Override
	public void failOpen() {
	}


	@Override
	public void succeedOpen() {
	}


	@Override
	public void tryEnter(PlayerPortalEvent event) {
		event.getPlayer().sendMessage("test");
	}


	@Override
	public boolean canUse() {
		return false;
	}


}

