package com.github.orgs.kotobaminers.kotobatblt4.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.orgs.kotobaminers.kotoapi2.KotobaBlockStorage;
import com.github.orgs.kotobaminers.kotoapi2.KotobaBlockStorageHolder;
import com.github.orgs.kotobaminers.kotoapi2.KotobaPlayerMode;
import com.github.orgs.kotobaminers.kotoapi2.KotobaTitle;
import com.github.orgs.kotobaminers.kotobatblt4.icon.AbilityIcon;
import com.github.orgs.kotobaminers.kotobatblt4.icon.InformationIcon;
import com.github.orgs.kotobaminers.kotobatblt4.system.Setting;

public class TBLTMap extends KotobaBlockStorage implements KotobaBlockStorageHolder {


	private static final File DIRECTORY = new File(Setting.getPlugin().getDataFolder().getAbsolutePath() + "/Map/");
	private static Set<TBLTMap> maps = new HashSet<>();
	private BlockStorageMeta meta = new BlockStorageMeta();


	public TBLTMap(String name) {
		super(name);
	}


	public static TBLTMap create(String name, Player player) {
		int id = new TBLTMap("dummy").findIncrementalId();
		return (TBLTMap) (new TBLTMap("dummy")).createFromSelection(id, name, player);
	}


	//Return TBLTMap instance
	@Override
	protected KotobaBlockStorage create(String name) {
		return new TBLTMap(name);
	}


	private void start(Player player) {
		KotobaPlayerMode.PLAYER.become(player);
		meta.findStarters(player).forEach(i -> player.getInventory().addItem(i));
		meta.findSpawn(player).ifPresent(l -> player.teleport(l));
	}


	public void start(List<Player> players) {
		reset();
		players.forEach(p -> p.getInventory().clear());
		InformationIcon.updateTeam(players, true);
		players.forEach(p -> start(p));
	}


	public void startNext() {
		TBLTMap map = find(meta.getNext()).map(m -> (TBLTMap) m).orElse(null);
		List<Player> players = findPlayers();
		if(map == null) {
			players.forEach(p -> p.teleport(p.getWorld().getSpawnLocation()));
			players.forEach(p -> p.getInventory().clear());
		} else {
			map.start(players);
			reset();
		}
	}


	public void tryFinish() {
		if(meta.checkQuiz()) {
			findPlayers().stream()
				.forEach(p -> {
					p.getInventory().clear();
					KotobaTitle.displayTitle(p, "Clear", ChatColor.GREEN, new ArrayList<>());
					Bukkit.getScheduler().scheduleSyncDelayedTask(Setting.getPlugin(), new Runnable() {
						@Override
						public void run() {
							p.getInventory().addItem(AbilityIcon.NEXT.getIcon(1));
							p.getInventory().addItem(AbilityIcon.RETRY.getIcon(1));
						}
					}, 20 * 5);
				});;
		}
	}


	@Override
	public void importAllYaml() {
		final String extension = ".yml";
		List<TBLTMap> imported = Stream.of(getDirectory().listFiles())
			.filter(f -> f.getName().endsWith(extension))
			.map(f -> YamlConfiguration.loadConfiguration(f))
			.map(c -> importYaml(c))
			.filter(c -> c instanceof TBLTMap)
			.map(c -> (TBLTMap) c)
			.collect(Collectors.toList());
		imported.forEach(i -> i.setDataFromWorld());
		maps.addAll(imported);
	}


	public void resize(Player player) {
		KotobaBlockStorage storage = createFromSelection(getId(), getName(), player);
		if(storage instanceof TBLTMap) {
			TBLTMap map = (TBLTMap) storage;
			map.register();
			map.saveYaml();
		}
	}


	@Override
	public File getDirectory() {
		return DIRECTORY;
	}


	public List<Player> findPlayers() {
		return Bukkit.getServer().getOnlinePlayers().stream()
			.filter(p -> KotobaPlayerMode.PLAYER.isSame(p))
			.filter(p -> isIn(p.getLocation()))
			.collect(Collectors.toList());
	}


	@Override
	public BlockStorageMeta getStorageMeta() {
		return meta;
	}


	@Override
	public Set<TBLTMap> getStorages() {
		return maps;
	}


	@Override
	public void register() {
		deregister();
		getStorages().add(this);
		this.saveYaml();
	}


	@Override
	public void deregister() {
		getStorages().removeIf(s -> s.getId() == getId());
	}


	//Need to fix: Performance
	public static boolean isInAny(Location location) {
		return maps.stream().anyMatch(m -> m.isIn(location));
	}

	//Need to fix: Performance
	public static boolean isPlayer(Player player) {
		return isInAny(player.getLocation()) && KotobaPlayerMode.PLAYER.isSame(player);
	}


}

