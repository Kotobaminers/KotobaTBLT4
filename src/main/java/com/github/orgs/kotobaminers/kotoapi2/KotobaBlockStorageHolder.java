package com.github.orgs.kotobaminers.kotoapi2;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;

public interface KotobaBlockStorageHolder {


	Set<? extends KotobaBlockStorage> getStorages();
	void register();
	void deregister();


	default Optional<? extends KotobaBlockStorage> find(int id) {
		return getStorages().stream().filter(s -> s.getId() == id).findFirst();
	}


	default List<? extends KotobaBlockStorage> find(Location location) {
		return getStorages().stream()
			.filter(storage -> storage.isIn(location))
			.collect(Collectors.toList());
	}


	default int findIncrementalId() {
		return getStorages().stream()
			.mapToInt(s -> s.getId())
			.max()
			.orElse(0) + 1;
	}


	void importAllYaml();


}

