package com.github.orgs.kotobaminers.kotoapi2;

import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.Chest;

import com.github.orgs.kotobaminers.kotobatblt4.storage.KotobaMeta;

public interface SettingChestHolder {


	List<Chest> findChests(Location location);
	Optional<? extends KotobaMeta> tryCreateMeta(Chest chest);


}

