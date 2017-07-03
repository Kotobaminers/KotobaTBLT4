package com.github.orgs.kotobaminers.kotobatblt4.icon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.orgs.kotobaminers.kotoapi2.KotobaBlockData;
import com.github.orgs.kotobaminers.kotoapi2.KotobaInteractive;
import com.github.orgs.kotobaminers.kotoapi2.KotobaUtility;
import com.github.orgs.kotobaminers.kotoapi2.SettingChestHolder;
import com.github.orgs.kotobaminers.kotobatblt4.interactive.TBLTMapJoiner;
import com.github.orgs.kotobaminers.kotobatblt4.storage.KotobaMeta;

public enum InteractiveBlockIcon implements SettingChestIcon, KotobaInteractive, SettingChestHolder {


	TELEPORT_SINGLE(400, Material.ENCHANTMENT_TABLE, 0) {
		@Override
		public void onPlayerInteract(PlayerInteractEvent event, Chest chest) {
			List<ItemStack> options = Stream.of(chest.getInventory().getContents())
				.filter(i -> i != null)
				.collect(Collectors.toList());
			InformationIcon.findLocation(options)
				.ifPresent(l -> event.getPlayer().teleport(l));
		}
	},
	TELEPORT_MULTIPLE(401, Material.ENCHANTMENT_TABLE, 0) {
		@Override
		public void onPlayerInteract(PlayerInteractEvent event, Chest chest) {
		}
	},
	MAP_JOINER(402, Material.ENCHANTMENT_TABLE, 0) {
		@Override
		public void onPlayerInteract(PlayerInteractEvent event, Chest chest) {
			SettingChestIcon.getOptions(chest).stream()
				.filter(l -> l.stream().anyMatch(i -> isSame(i)))
				.findFirst()
				.ifPresent(options -> {
					InventoryClickIcon.findMap(options)
						.ifPresent(m -> TBLTMapJoiner.update(event.getPlayer(), m));
				});
		}
	},


	GATE(403, Material.ENCHANTMENT_TABLE, 0) {
		@Override
		public void onPlayerInteract(PlayerInteractEvent event, Chest chest) {
			SettingChestIcon.getOptions(chest).stream()
			.filter(l -> l.stream().anyMatch(i -> isSame(i)))
			.findFirst()
			.ifPresent(options -> {
				Player player = event.getPlayer();
				if(InformationIcon.findPlayerNumber(options) == InformationIcon.findPlayerNumber(player)) {
					teleportTheOtherSide(player, event.getClickedBlock().getLocation());
				}
			});
		}
	},


	ONE_TIME_GATE(404, Material.ENCHANTMENT_TABLE, 0) {
		@Override
		public void onPlayerInteract(PlayerInteractEvent event, Chest chest) {
			SettingChestIcon.getOptions(chest).stream()
			.filter(l -> l.stream().anyMatch(i -> isSame(i)))
			.findFirst()
			.ifPresent(options -> {
				Player player = event.getPlayer();
				if(InformationIcon.findPlayerNumber(options) == 0) {
					teleportTheOtherSide(player, event.getClickedBlock().getLocation());
					closeGate(chest);
				} else if(InformationIcon.findPlayerNumber(options) == InformationIcon.findPlayerNumber(player)) {
					teleportTheOtherSide(player, event.getClickedBlock().getLocation());
					closeGate(chest);
				}
			});
		}

		@SuppressWarnings("deprecation")
		private void closeGate(Chest chest) {
			Location target = chest.getLocation().clone().subtract(POSITION);
			Stream.iterate(0, i -> i + 1)
				.limit(3)
				.map(i -> target.clone().add(0, i, 0))
				.forEach(l -> KotobaBlockData.placeBlock(l, Material.BEDROCK.getId(), 0, false));
		}
	},
	;


	private int id;
	private Material material;
	private short data;
	private static final Vector POSITION = new Vector(0, -3, 0);


	private InteractiveBlockIcon(int id, Material material, int data) {
		this.id = id;
		this.material = material;
		this.data = (short) data;
	}


	protected void teleportTheOtherSide(Player player, Location location) {
		List<BlockFace> horizontal = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
		List<Block> airs = horizontal.stream()
				.map(f -> location.getBlock().getRelative(f))
				.filter(b -> b.getType() == Material.AIR)
				.collect(Collectors.toList());
		if(airs.size() == 2) {
			Vector direction = player.getLocation().getDirection().clone();
			airs.stream()
				.sorted(Comparator.comparing(a -> a.getLocation().distance(player.getLocation()), Comparator.reverseOrder()))
				.findFirst()
				.map(b -> KotobaUtility.getBlockCenter(b))
				.ifPresent(l -> player.teleport(l.clone().setDirection(direction)));
		}

	}


	@Override
	public List<Chest> findChests(Location location) {
		return Optional.ofNullable(location)
			.map(l -> l.clone().add(POSITION).getBlock().getState())
			.filter(s -> s instanceof Chest)
			.map(s -> (Chest) s)
			.filter(c ->
				Stream.of(c.getInventory().getContents())
					.filter(i -> i != null)
					.anyMatch(i -> isSame(i))
			)
			.map(c -> Arrays.asList(c))
			.orElse(new ArrayList<>());
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

	@Override
	public Optional<? extends KotobaMeta> tryCreateMeta(Chest chest) {
		return Optional.empty();
	}

}

