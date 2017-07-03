package com.github.orgs.kotobaminers.kotoapi2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public enum KotobaPlayerMode {
	OP(Stream.of(GameMode.values()).collect(Collectors.toList())) {
		@Override
		protected boolean check(Player player) {
			return player.isOp();
		}
		@Override
		public void become(Player player) {
			player.setGameMode(GameMode.CREATIVE);
		}
	},


	//TODO: Fix in future with white list
	CREATOR(Stream.of(GameMode.values()).collect(Collectors.toList())) {
		@Override
		protected boolean check(Player player) {
			return OP.isSame(player);
		}
		@Override
		public void become(Player player) {
			player.setGameMode(GameMode.CREATIVE);
		}
	},


	PLAYER(Arrays.asList(GameMode.ADVENTURE)) {
		@Override
		protected boolean check(Player player) {
			return true;
		}

		@Override
		public void become(Player player) {
			player.setGameMode(GameMode.ADVENTURE);
		}
	},


	;


	private List<GameMode> modes;


	private KotobaPlayerMode(List<GameMode> modes) {
		this.modes = modes;
	}


	protected abstract boolean check(Player player);
	public abstract void become(Player player);


	public boolean isSame(Player player) {
		return modes.contains(player.getGameMode()) && check(player);
	}




}

