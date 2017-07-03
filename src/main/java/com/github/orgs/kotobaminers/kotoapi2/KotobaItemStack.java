package com.github.orgs.kotobaminers.kotoapi2;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class KotobaItemStack {
	public static ItemStack create(Material material, short data, int amount, String name, List<String> lore) {
		ItemStack itemStack = new ItemStack(material, amount, data);
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(itemMeta != null) {
			itemMeta.setDisplayName(name);
			if(lore != null) {
				itemMeta.setLore(lore);
			}
			itemStack.setItemMeta(itemMeta);
			return itemStack;
		}
		return null;
	}


	public static boolean consume(Inventory inventory, ItemStack item, int amount) {
		int max = KotobaUtility.getValidContents(inventory).values().stream()
			.filter(i -> i.isSimilar(item))
			.mapToInt(i -> i.getAmount())
			.sum();

		if(max < amount) {
			return false;
		} else {
			Map<Integer, ItemStack> contents = KotobaUtility.getValidContents(inventory);
			contents.entrySet().stream()
				.filter(e -> e.getValue().isSimilar(item))
				.forEach(e -> inventory.clear(e.getKey()));
			int remaining = max - amount;
			if(0 < remaining) {
				ItemStack add = item.clone();
				add.setAmount(remaining);
				inventory.addItem(add);
			}
			return true;
		}
	}


	public static ItemStack setColoredLore(ItemStack itemStack, ChatColor color, List<String> lore) {
		ItemStack clone = itemStack.clone();
		lore = lore.stream()
			.map(s -> "" + ChatColor.RESET + color + s)
			.collect(Collectors.toList());
		ItemMeta meta = clone.getItemMeta();
		meta.setLore(lore);
		clone.setItemMeta(meta);
		return clone;
	}


}

