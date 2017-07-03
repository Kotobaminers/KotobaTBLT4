package com.github.orgs.kotobaminers.kotobatblt4.icon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.orgs.kotobaminers.kotoapi2.ItemStackIcon;

public interface SettingChestIcon extends ItemStackIcon {
	public static List<List<ItemStack>> getOptions(Chest chest) {
		Inventory inventory = chest.getInventory();
		List<Integer> index = Stream.iterate(0, i -> i + 1)
			.limit(inventory.getSize())
			.filter(i -> inventory.getItem(i) != null)
			.collect(Collectors.toList());
		List<List<Integer>> index2 = new ArrayList<>();
		List<Integer> tmp = new ArrayList<>();
		for(int i = 0; i < index.size(); i++) {
			if(i == 0) {
				tmp.add(index.get(i));
			} else if(index.get(i) - index.get(i - 1) == 1) {
				tmp.add(index.get(i));
			} else {
				index2.add(tmp);
				tmp = new ArrayList<>();
				tmp.add(index.get(i));
			}
		}
		index2.add(tmp);
		return index2.stream().map(i -> i.stream().map(i2 -> inventory.getItem(i2)).collect(Collectors.toList())).collect(Collectors.toList());
	}

}
