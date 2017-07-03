package com.github.orgs.kotobaminers.kotobatblt4.portal;

import org.bukkit.event.player.PlayerPortalEvent;

public interface KotobaPortal {


	void tryOpen();
	void failOpen();
	void succeedOpen();


	void tryEnter(PlayerPortalEvent event);
	boolean canUse();


//	REGULAR,
//	PAZZLE,
//	;


}

