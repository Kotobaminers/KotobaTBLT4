package com.github.orgs.kotobaminers.kotoapi2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.orgs.kotobaminers.kotobatblt4.icon.IconManager;

public interface ItemStackIcon {


	static final String PREFIX_ID = "TBLT4: ";
	static final Integer FAILED_ID = -1;


	public int getId();
	public String getName();
	public List<String> getLore();
	public Material getMaterial();
	public short getData();


	default public boolean isSame(ItemStack item) {
		return IconManager.findId(item) == getId();
	}


	default public ItemStack getIcon(int amount) {
		ItemStack itemStack = new ItemStack(getMaterial(), amount, (short) getData());
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(getName());
		List<String> lore = getLore() == null ? new ArrayList<>() : getLore();
		lore.add(PREFIX_ID + Integer.toString(getId()));
		meta.setLore(lore);

		if(meta instanceof BookMeta) {
			BookMeta bookMeta = (BookMeta) meta;
			bookMeta.setLore(Arrays.asList(getName()));
			bookMeta.setPages(Arrays.asList("Settings"));
			itemStack.setItemMeta(bookMeta);
		} else {
			itemStack.setItemMeta(meta);
		}

		return itemStack;
	}


	default public ItemStack getIcon(int amount, List<String> lore) {
		ItemStack itemStack = new ItemStack(getMaterial(), amount, (short) getData());
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(getName());
		List<String> newLore = new ArrayList<>();
		if(lore != null) {
			newLore.addAll(lore);
		}
		newLore.add(PREFIX_ID + Integer.toString(getId()));
		meta.setLore(newLore);

		if(meta instanceof BookMeta) {
			BookMeta bookMeta = (BookMeta) meta;
			bookMeta.setLore(Arrays.asList(getName()));
			bookMeta.setPages(Arrays.asList("Settings"));
			itemStack.setItemMeta(bookMeta);
		} else {
			itemStack.setItemMeta(meta);
		}

		return itemStack;
	}


	default public boolean contains(Chest chest) {
		return Stream.of(chest.getInventory().getContents())
			.filter(i -> i != null)
			.anyMatch(i -> isSame(i));
	}


	public static boolean isItemStackIcon(ItemStack item) {
		if(item == null) return false;
		if(item.getType() == Material.AIR) return false;
		List<String> lore = item.getItemMeta().getLore();
		if(lore == null) return false;
		return ChatColor.stripColor(lore.get(lore.size() - 1)).startsWith(PREFIX_ID);
	}

}

