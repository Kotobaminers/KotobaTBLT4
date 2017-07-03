package com.github.orgs.kotobaminers.kotobatblt4.interactive;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.orgs.kotobaminers.kotoapi2.KotobaBlockData;
import com.github.orgs.kotobaminers.kotobatblt4.storage.TBLTMap;

public class BlockManipulation {


	public static final List<Material> WHITE_LIST = Arrays.asList(
		Material.STONE,
		Material.WOOD,
		Material.WOOL,
		Material.LOG,
		Material.LOG_2
	);


	public static boolean canPick(Block block) {
		if(block == null) return false;
		return WHITE_LIST.contains(block.getType());
	}
	public static boolean canPlace(Block block) {
		return block.getType() == Material.AIR;
	}


	@SuppressWarnings("deprecation")
	public static boolean tryPick(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();

		if(!canPick(block)) return false;
		if(!TBLTMap.isInAny(block.getLocation())) return false;

		event.getPlayer().getInventory().addItem(new ItemStack(block.getType(), 1, (short) 0));
		KotobaBlockData.placeBlock(block.getLocation(), Material.AIR.getId(), 0, false);

		return true;
	}


	@SuppressWarnings("deprecation")
	public static boolean tryPlace(PlayerInteractEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if(item == null) return false;
		if(item.getType() == Material.AIR) return false;

		Block block = event.getClickedBlock().getRelative(event.getBlockFace());
		if(!canPlace(block)) return false;
		if(!TBLTMap.isInAny(block.getLocation())) return false;

		KotobaBlockData.placeBlock(block.getLocation(), item.getType().getId(), item.getDurability(), false);

		return true;
	}


}

