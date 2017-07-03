package com.github.orgs.kotobaminers.kotobatblt4.blockquiz;

import static com.github.orgs.kotobaminers.kotobatblt4.icon.InformationIcon.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockQuiz {


	private Location chest = null;
	private int radius = 0;
	private int height = 0;
	private Map<Vector, SimpleEntry<Material, Byte>> answers = new HashMap<>();
	private static final int LIMIT = 10;
	private static final int Y_OFFSET = 3;

	private BlockQuiz(Location chest, int radius, int height) {
		this.chest = chest;
		this.radius = Math.min(radius, LIMIT);
		this.height = Math.min(height, LIMIT);
		setAnswers();
	}


	public static BlockQuiz create(Location chest, List<ItemStack> l) {
		BlockQuiz quiz = new BlockQuiz(chest, countAmount(RADIUS, l), countAmount(HEIGHT, l));
		return quiz;
	}


	@SuppressWarnings("deprecation")
	public boolean checkAnswer(Location location) {
		return answers.entrySet().stream()
			.allMatch(e -> {
				Block block = location.clone().add(e.getKey()).getBlock();
				return block.getType() == e.getValue().getKey() && block.getData() == e.getValue().getValue();
			});
	}

	@SuppressWarnings("deprecation")
	private void setAnswers() {
		List<Location> locations = Stream.iterate(0, x -> x + 1)
			.limit(2 * radius - 1)
			.flatMap(x ->
				Stream.iterate(0, z -> z + 1)
					.limit(2 * radius - 1)
					.flatMap(z ->
						Stream.iterate(0, y -> y + 1)
							.limit(height)
							.map(y -> new Vector(x, y, z))
					)
			)
			.map(o -> (Vector) o)
			.map(v -> v.add(new Vector(1-radius, Y_OFFSET, 1-radius)))
			.map(v -> toLocation(v))
			.collect(Collectors.toList());
		locations.forEach(l -> {
			Block block = l.getBlock();
			answers.put(toVector(l), new SimpleEntry<>(block.getType(), block.getData()));
		});
	}


	private Vector toVector(Location location) {
		return location.clone().subtract(chest).toVector();
	}


	private Location toLocation(Vector vector) {
		return chest.clone().add(vector);
	}


	public Map<Vector, SimpleEntry<Material, Byte>> getAnswers() {
		return answers;
	}


}

