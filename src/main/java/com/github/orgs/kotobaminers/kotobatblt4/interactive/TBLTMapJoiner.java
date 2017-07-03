package com.github.orgs.kotobaminers.kotobatblt4.interactive;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.orgs.kotobaminers.kotoapi2.KotobaTitle;
import com.github.orgs.kotobaminers.kotobatblt4.storage.TBLTMap;

public class TBLTMapJoiner {


	private Set<UUID> members = new HashSet<>();
	private TBLTMap to;


	private static Set<TBLTMapJoiner> JOINERS = new HashSet<>();


	private TBLTMapJoiner(TBLTMap to) {
		this.to = to;
	}


	private static Optional<TBLTMapJoiner> find(TBLTMap to) {
		return JOINERS.stream()
			.filter(j -> j.to.getId() == to.getId())
			.findFirst();
	}


	public static void update(Player player, TBLTMap to) {
		TBLTMapJoiner joiner = find(to).orElse(null);

		if(joiner == null) {
			TBLTMapJoiner newJoiner = new TBLTMapJoiner(to);
			newJoiner.updateMember(player);
			JOINERS.add(newJoiner);
		} else {
			joiner.to = to;
			joiner.updateMember(player);
			List<Player> players = joiner.findPlayers();
			if(joiner.to.getStorageMeta().getMaxPlayerNumber() == players.size()) {
				to.start(players);
				joiner.remove();
			}
		}
	}


	private void updateMember(Player player) {
		UUID uuid = player.getUniqueId();
		if(members.contains(uuid)) {
			members.removeIf(u -> u.equals(uuid));
			KotobaTitle.displayTitle(player, "Quit", ChatColor.GREEN, new ArrayList<>());
		} else {
			members.add(uuid);
			KotobaTitle.displayTitle(player, "Join", ChatColor.GREEN, new ArrayList<>());
		}
	}


	private List<Player> findPlayers() {
		return Bukkit.getOnlinePlayers().stream()
			.filter(p -> members.contains(p.getUniqueId()))
			.collect(Collectors.toList());
	}


	private void remove() {
		JOINERS.removeIf(j -> j.to.getId() == to.getId());
	}


	public static void remove(TBLTMap map) {
		JOINERS.removeIf(j -> map.getId() == j.to.getId());
	}


}

