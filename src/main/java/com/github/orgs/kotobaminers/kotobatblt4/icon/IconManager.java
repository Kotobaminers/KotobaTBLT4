package com.github.orgs.kotobaminers.kotobatblt4.icon;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.inventory.ItemStack;

import com.github.orgs.kotobaminers.kotoapi2.ItemStackIcon;

import net.md_5.bungee.api.ChatColor;

public class IconManager {


	private static final List<Class<? extends ItemStackIcon>> IMPLEMENTERS = Arrays.asList(
			BlockStorageSettingIcon.class,
			InformationIcon.class,
			PortalSettingIcon.class,
			InteractiveBlockIcon.class,
			AbilityIcon.class
	);


	public static List<ItemStackIcon> getAll() {
		return IMPLEMENTERS.stream()
			.filter(c -> c.isEnum())
			.map(c -> c.getEnumConstants())
			.flatMap(c -> Stream.of(c))
			.collect(Collectors.toList());
	}


	public static Optional<ItemStackIcon> find(int id) {
		return getAll().stream()
			.filter(i -> i.getId() == id)
			.findFirst();
	}


	public static Integer findId(ItemStack item) {
		String last = Optional.ofNullable(item.getItemMeta().getLore())
			.map(lore -> ChatColor.stripColor(lore.get(lore.size() - 1))).orElse(ItemStackIcon.FAILED_ID.toString());
		if(last.startsWith(ItemStackIcon.PREFIX_ID)) {
			last = last.substring(ItemStackIcon.PREFIX_ID.length());
			try {
				return Integer.valueOf(last);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return ItemStackIcon.FAILED_ID;
	}


	public static Optional<ItemStackIcon> find(ItemStack item) {
		return find(findId(item));
	}


}

