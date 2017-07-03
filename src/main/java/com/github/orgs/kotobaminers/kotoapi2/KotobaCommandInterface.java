package com.github.orgs.kotobaminers.kotoapi2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public interface KotobaCommandInterface {


	default boolean matchArgs(String[] args) {
		if(getTree().size() <= args.length) {
			for(int i = 0; i < getTree().size(); i++) {
				if(!getTree().get(i).contains(args[i])) return false;
			}
			return true;
		}
		return false;
	}


	default String getUsage() {
		return String.join(" ",
			Arrays.asList(
				"" + ChatColor.DARK_GREEN + ChatColor.BOLD + "/" + getLabel(),
				ChatColor.GREEN + getName(),
				ChatColor.YELLOW + getOption(),
				ChatColor.RESET + " : ",
				getDescription()
			));
	}


	default String getName() {
		return String.join(" ", getTree().stream().map(list -> list.get(0)).collect(Collectors.toList()));
	}


	default List<String> takeOptions(List<String> args) {
		return Stream.iterate(0, i -> i + 1)
			.limit(Math.max(0, args.size() - getTree().size()))
			.map(i -> args.get(getTree().size() + i))
			.collect(Collectors.toList());
	}


	default boolean execute(Player player, List<String> args) {
		if(getTree().size() <= args.size()) {
			List<String> options = takeOptions(args);
			return executeWithOptions(player, options);
		}
		return false;
	}


	List<List<String>> getTree();
	String getLabel();
	String getOption();
	String getDescription();
	boolean executeWithOptions(Player player, List<String> options);


}

