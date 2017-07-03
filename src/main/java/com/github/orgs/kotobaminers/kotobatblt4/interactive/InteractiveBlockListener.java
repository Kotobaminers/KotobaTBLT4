package com.github.orgs.kotobaminers.kotobatblt4.interactive;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.orgs.kotobaminers.kotoapi2.ItemStackIcon;
import com.github.orgs.kotobaminers.kotoapi2.KotobaItemStack;
import com.github.orgs.kotobaminers.kotobatblt4.icon.InteractiveBlockIcon;
import com.github.orgs.kotobaminers.kotobatblt4.storage.TBLTMap;

public class InteractiveBlockListener implements Listener {


	private static final List<Action> CLICK_BLOCK = Arrays.asList(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK);
	private static final List<Action> CLICK_ALL = Arrays.asList(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_AIR, Action.RIGHT_CLICK_AIR);


	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event) {
		if(CLICK_BLOCK.contains(event.getAction())) {
			Block block = event.getClickedBlock();
			if(block == null) return;
			if(block.getType() == Material.AIR) return;

			Player player = event.getPlayer();
			if(!TBLTMap.isPlayer(player)) return;

			List<Boolean> success = Stream.of(InteractiveBlockIcon.values())
				.filter(i -> i.getMaterial() == block.getType())
				.map(i -> {
					List<Chest> chests = i.findChests(block.getLocation());
					if(0 < chests.size()) {
						i.onPlayerInteract(event, chests.get(0));
						event.setCancelled(true);
						return true;
					}
					return false;
				})
				.collect(Collectors.toList());
			if(!success.contains(true)) {
				//Case: BlockManipulation
				if(!TBLTMap.isPlayer(player)) return;
				ItemStack item = player.getInventory().getItemInMainHand();
				if(item == null) return;
				if(!ItemStackIcon.isItemStackIcon(item)) {
					boolean placed = BlockManipulation.tryPlace(event);
					if(placed) {
						KotobaItemStack.consume(player.getInventory(), item, 1);
						new TBLTMap("dummy").find(player.getLocation())
							.forEach(m -> ((TBLTMap) m).tryFinish());
					}
				}
			}
		}
	}


}
