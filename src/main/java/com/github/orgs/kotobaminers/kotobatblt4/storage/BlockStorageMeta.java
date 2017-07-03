package com.github.orgs.kotobaminers.kotobatblt4.storage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.orgs.kotobaminers.kotobatblt4.blockquiz.BlockQuiz;
import com.github.orgs.kotobaminers.kotobatblt4.icon.BlockStorageSettingIcon;
import com.github.orgs.kotobaminers.kotobatblt4.icon.InformationIcon;

public class BlockStorageMeta {


	private Map<Integer, Location> spawns = new HashMap<>();
	private Map<Integer, List<ItemStack>> starters = new HashMap<>();
	private Map<UUID, List<Block>> owners = new HashMap<>();
	private BlockQuiz quiz = null;
	private List<Location> fields = new ArrayList<>();
	private int next = 0;


	public void setDataFromChest(List<Chest> chests) {
		chests.forEach(c -> BlockStorageSettingIcon.setMetaData(this, c));
	}


	public List<ItemStack> findStarters(Player player) {
		return starters.getOrDefault(InformationIcon.findPlayerNumber(player), new ArrayList<>());
	}
	public Optional<Location> findSpawn(Player player) {
		return Optional.ofNullable(spawns.getOrDefault(InformationIcon.findPlayerNumber(player), null));
	}


	public Integer getMaxPlayerNumber() {
		return spawns.keySet().stream().max(Comparator.naturalOrder()).orElse(0);
	}


	public boolean canOperateGem(Player player, Block block) {
		Predicate<Block> isRegisteredGem = (b) -> b.getLocation().distance(block.getLocation()) == 0 && b.getType() == block.getType();
		boolean isMine = owners.getOrDefault(player.getUniqueId(), new ArrayList<>()).stream()
			.filter(isRegisteredGem)
			.findFirst()
			.isPresent();
		boolean hasOwner = owners.entrySet().stream()
			.filter(e -> !e.getKey().equals(player.getUniqueId()))
			.flatMap(e -> e.getValue().stream())
			.anyMatch(isRegisteredGem);
		return isMine || !hasOwner;
	}


	public void addOwningGem(Player player, Block block) {
		UUID uuid = player.getUniqueId();
		List<Block> ownings = owners.getOrDefault(uuid, new ArrayList<>());
		ownings.add(block);
		owners.put(uuid, ownings);
	}


	public void removeOwningGem(Player player, Block block) {
		UUID uuid = player.getUniqueId();
		List<Block> ownings = owners.getOrDefault(uuid, new ArrayList<>());
		ownings.remove(block);
		owners.put(uuid, ownings);
	}


	public boolean checkQuiz() {
		return fields.stream().anyMatch(f -> quiz.checkAnswer(f));
	}


	public Map<Integer, List<ItemStack>> getStarters() {
		return starters;
	}
	public Map<Integer, Location> getSpawns() {
		return spawns;
	}


	public BlockQuiz getQuiz() {
		return quiz;
	}
	public void setQuiz(BlockQuiz quiz) {
		this.quiz = quiz;
	}
	public List<Location> getFields() {
		return fields;
	}
	public int getNext() {
		return next;
	}
	public void setNext(int next) {
		this.next = next;
	}

}

