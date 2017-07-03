package com.github.orgs.kotobaminers.kotoapi2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class KotobaTitle {
	public enum TitleOption {
		BOLD,
		ITALIC,
		;
		public String getJson() {
			return "\"" + this.name().toLowerCase() + "\":\"true\"";
		}
	}

	public static void displayTitle(Player player, String title, ChatColor color, List<TitleOption> options) {
		String playerName = player.getName();
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), createResetCommand(playerName));
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), createTitleCommand(playerName, title, color, options));
	}
	public static void displayTitle(Player player, String title, ChatColor color, List<TitleOption> options, int fadeIn, int duration, int fadeOut) {
		String playerName = player.getName();
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), createResetCommand(playerName));
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), createTimesCommand(playerName, fadeIn, duration, fadeOut));
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), createTitleCommand(playerName, title, color, options));
	}

	private static String createTitleCommand(String playerName, String sentence, ChatColor color, List<TitleOption> options) {
		sentence = "\"text\":\"" + sentence + "\"";
		String colorStr = "\"color\":\"" + color.name().toLowerCase() + "\"";
		List<String> optionsStr = options.stream().map(o -> o.getJson()).collect(Collectors.toList());

		List<String> json = new ArrayList<String>();
		json.add(sentence);
		json.add(colorStr);
		json.addAll(optionsStr);
		return String.join(" ", Arrays.asList("title", playerName, "title", String.join("", Arrays.asList("{", String.join(",", json), "}"))));
	}

	private static String createTimesCommand(String playerName, int fadeIn, int duration, int fadeOut) {
		return String.join(" ", Arrays.asList("title", playerName, "times", String.valueOf(fadeIn * 20), String.valueOf(duration * 20), String.valueOf(fadeOut * 20)));
	}
	private static String createResetCommand(String playerName) {
		return "title " + playerName + " reset";
	}

}
