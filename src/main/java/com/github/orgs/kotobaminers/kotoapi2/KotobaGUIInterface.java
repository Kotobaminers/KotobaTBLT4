package com.github.orgs.kotobaminers.kotoapi2;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface KotobaGUIInterface {


	int getSize();
	String getTitle();
	String getPostfix();
	public void onClick(InventoryClickEvent event);


	default List<Inventory> createGUIs(List<ItemStack> icons) {
		int number = icons.size() / getSize() + 1;
		return Stream.iterate(0, i -> i + 1)
			.limit(number)
			.map(i -> {
				Inventory inventory = Bukkit.createInventory(null, getSize(), getTitle());
				Stream.iterate(0, j -> j + 1)
					.limit(Math.min(icons.size() - getSize() * i, getSize()))
					.forEach(j -> inventory.addItem(icons.get(getSize() * i + j)));
				return inventory;
			})
			.collect(Collectors.toList());
	}


	default boolean isSame(Inventory inventory) {
		return inventory.getTitle().equals(getTitle() + getPostfix());
	}


}

