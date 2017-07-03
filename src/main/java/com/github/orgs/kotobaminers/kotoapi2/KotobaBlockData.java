package com.github.orgs.kotobaminers.kotoapi2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class KotobaBlockData {


	private static final List<Material> SECOND = Arrays.asList(
		Material.STATIONARY_WATER,
		Material.WATER,
		Material.STATIONARY_LAVA,
		Material.LAVA,
		Material.TORCH,
		Material.REDSTONE_TORCH_OFF,
		Material.REDSTONE_TORCH_ON
	);


	@SuppressWarnings("deprecation")
	public static void placeBlocksSafe(List<Integer> blocksData, World world) {
		List<Integer> secondID = SECOND.stream().map(s -> s.getId()).collect(Collectors.toList());

		int size = 5;
		List<List<Integer>> all = Stream.iterate(0, i -> i + 1)
			.limit(blocksData.size() / 5)
			.map(i -> i * size)
			.map(i -> blocksData.subList(i, i + size))
			.collect(Collectors.toList());
		List<List<Integer>> first = all.stream()
			.filter(l -> !secondID.contains(l.get(3)))
			.collect(Collectors.toList());
		List<List<Integer>> second = all.stream()
			.filter(l -> secondID.contains(l.get(3)))
			.collect(Collectors.toList());

		Location target = new Location(world, 0, 0, 0);
		first.forEach(l -> {
			target.setX(l.get(0));
			target.setY(l.get(1));
			target.setZ(l.get(2));
			KotobaBlockData.placeBlock(target, l.get(3), l.get(4), false);
		});
		second.forEach(l -> {
			target.setX(l.get(0));
			target.setY(l.get(1));
			target.setZ(l.get(2));
			KotobaBlockData.placeBlock(target, l.get(3), l.get(4), false);
		});
	}


	@SuppressWarnings("deprecation")
	public static void placeBlock(Location location, int id, int data, boolean applyPhysics) {
		Block block = location.getBlock();
		block.setTypeIdAndData(id, (byte) data, applyPhysics);
	}


	@SuppressWarnings("deprecation")
	public static List<Integer> toIntegerList(Block block) {
		return Arrays.asList(block.getX(), block.getY(), block.getZ(), block.getTypeId(), (int) block.getData());
	}


}

