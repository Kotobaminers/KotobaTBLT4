package com.github.orgs.kotobaminers.kotobatblt4.portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import com.github.orgs.kotobaminers.kotoapi2.SettingChestHolder;
import com.github.orgs.kotobaminers.kotobatblt4.icon.PortalSettingIcon;

public class PortalManager implements SettingChestHolder {


	@Override
	public List<Chest> findChests(Location location) {
		List<Chest> chests = new ArrayList<Chest>();
		Block block = location.clone().add(0, -3, 0).getBlock();
		if(block.getState() instanceof Chest) {
			chests.add((Chest) block.getState());
		}
		return chests;
	}


	@Override
	public Optional<PortalMeta> tryCreateMeta(Chest chest) {
		return Stream.of(PortalSettingIcon.values())
			.map(i -> i.createPortalMeta(chest))
			.findFirst();
	}


}

