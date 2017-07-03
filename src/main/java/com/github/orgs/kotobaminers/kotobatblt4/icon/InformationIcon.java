package com.github.orgs.kotobaminers.kotobatblt4.icon;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.orgs.kotobaminers.kotoapi2.ItemStackIcon;
import com.github.orgs.kotobaminers.kotoapi2.KotobaUtility;

public enum InformationIcon implements ItemStackIcon {


	TEAM(200, Material.SKULL_ITEM, 3),
	PLAYER(201, Material.SKULL_ITEM, 3),
	LOCATION(202, Material.COMPASS, 0),
	RADIUS(203, Material.SNOW_BALL, 0),
	HEIGHT(204, Material.STICK, 0),
	;


	private int id;
	private Material material;
	private short data;


	private InformationIcon(int id, Material material, int data) {
		this.id = id;
		this.material = material;
		this.data = (short) data;
	}


	public static Integer findPlayerNumber(Player player) {
		return KotobaUtility.getValidContents(player.getInventory()).values().stream()
			.filter(i -> TEAM.isSame(i))
			.map(i -> i.getItemMeta().getLore())
			.filter(l -> l != null)
			.map(l -> Stream.iterate(0, i -> i + 1)
				.limit(l.size())
				.filter(i -> player.getName().toString().equals(ChatColor.stripColor(l.get(i))))
				.findAny()
				.orElse(0)
			)
			.map(i -> i + 1)
			.filter(i -> 0 < i)
			.findFirst()
			.orElse(0);
	}


	public static void updateTeam(List<Player> players, boolean keep) {
		boolean old = players.stream()
			.map(p ->
				KotobaUtility.getValidContents(p.getInventory()).values().stream()
					.filter(i -> TEAM.isSame(i))
					.findFirst()
			)
			.filter(o -> o.isPresent())
			.map(Optional::get)
			.map(i -> i.getItemMeta().getLore())
			.filter(l -> 1 < l.size())
			.map(l -> l.subList(0, l.size() - 1))
			.filter(l -> players.size() == l.size())
			.filter(l -> players.stream().allMatch(p -> l.stream().anyMatch(l2 -> l2.equals(p.getName()))))
			.findFirst()
			.isPresent();

		//Case: keep
		if(old && keep) return;

		//Case: Not keep
		players.forEach(p ->
			KotobaUtility.getValidContents(p.getInventory()).entrySet().stream()
				.filter(e -> TEAM.isSame(e.getValue()))
				.forEach(e -> p.getInventory().clear(e.getKey()))
		);

		players.forEach(p -> {
			Inventory inventory = p.getInventory();
			Stream.iterate(9, j -> j + 1)
				.limit(inventory.getSize())
				.filter(j -> inventory.getItem(j) == null)
				.findFirst()
				.ifPresent(j -> {
					ItemStack key = InformationIcon.TEAM.getIcon(1);
					ItemMeta meta = key.getItemMeta();
					List<String> lore = players.stream().map(Player::getName).collect(Collectors.toList());
					lore.add(meta.getLore().get(meta.getLore().size() - 1));
					meta.setLore(lore);
					key.setItemMeta(meta);
					inventory.setItem(j, key);
				});
			});

	}


	public static Integer findPlayerNumber(List<ItemStack> options) {
		return options.stream()
			.filter(o -> PLAYER.isSame(o))
			.findFirst()
			.map(i -> i.getAmount())
			.orElse(0);
	}


	public static Optional<Location> findLocation(List<ItemStack> options) {
		return options.stream()
			.filter(o -> LOCATION.isSame(o))
			.map(i -> i.getItemMeta().getLore())
			.filter(l -> l != null)
			.filter(l -> 6 < l.size())
			.map(l -> l.stream().map(s -> ChatColor.stripColor(s)).collect(Collectors.toList()))
			.map(l -> {
				Optional<World> world = Bukkit.getWorlds().stream().filter(w -> w.getName().equals(l.get(0))).findFirst();
				List<Float> v = l.subList(1, 6).stream().map(s -> Float.valueOf(s)).collect(Collectors.toList());
				return world.map(w -> new Location(w, v.get(0), v.get(1), v.get(2), v.get(3), v.get(4)));
			})
			.findFirst()
			.orElse(Optional.empty());
	}


	public static Integer countAmount(InformationIcon icon, List<ItemStack> options) {
		return options.stream()
			.filter(o -> icon.isSame(o))
			.mapToInt(o -> o.getAmount())
			.sum();
	}


	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public List<String> getLore() {
		return null;
	}

	@Override
	public Material getMaterial() {
		return material;
	}

	@Override
	public short getData() {
		return data;
	}



}
