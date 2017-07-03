package com.github.orgs.kotobaminers.kotobatblt4.portal;

import org.bukkit.Location;

import com.github.orgs.kotobaminers.kotobatblt4.storage.KotobaMeta;

public interface PortalMeta extends KotobaMeta, KotobaPortal {


	Location getDestination();


}

