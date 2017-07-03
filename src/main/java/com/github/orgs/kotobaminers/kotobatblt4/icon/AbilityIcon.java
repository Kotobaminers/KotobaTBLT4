package com.github.orgs.kotobaminers.kotobatblt4.icon;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.orgs.kotobaminers.kotoapi2.ItemStackIcon;
import com.github.orgs.kotobaminers.kotoapi2.KotobaItemStack;
import com.github.orgs.kotobaminers.kotobatblt4.interactive.BlockManipulation;
import com.github.orgs.kotobaminers.kotobatblt4.interactive.TBLTMapJoiner;
import com.github.orgs.kotobaminers.kotobatblt4.storage.TBLTMap;

public enum AbilityIcon implements ItemStackIcon {


	BLOCK_PICK(500, Material.REDSTONE_TORCH_ON, 0, "Magic Wand") {
		@Override
		public void onPlayerInteract(PlayerInteractEvent event) {
			boolean success = BlockManipulation.tryPick(event);
			if(success) {
				KotobaItemStack.consume(event.getPlayer().getInventory(), getIcon(1), 1);
			}
		}
	},


	MENU(501, Material.COMPASS, 0, "Menu") {
		@Override
		public void onPlayerInteract(PlayerInteractEvent event) {
		}
	},


	NEXT(502, Material.COMPASS, 0, "Next") {
		@Override
		public void onPlayerInteract(PlayerInteractEvent event) {
			new TBLTMap("dummy").find(event.getPlayer().getLocation()).stream()
				.map(m -> (TBLTMap) m)
				.forEach(m -> m.startNext());
		}
	},


	RETRY(503, Material.COMPASS, 0, "Retry") {
		@Override
		public void onPlayerInteract(PlayerInteractEvent event) {
			new TBLTMap("dummy").find(event.getPlayer().getLocation()).stream()
				.map(m -> (TBLTMap) m)
				.forEach(m -> {
					TBLTMapJoiner.remove(m);
					m.findPlayers().forEach(p -> TBLTMapJoiner.update(p, m));
				});
		}
	},


	;


	private int id;
	private Material material;
	private short data;
	private String name;


	private AbilityIcon(int id, Material material, int data, String name) {
		this.id = id;
		this.material = material;
		this.data = (short) data;
		this.name = name;
	}


	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<String> getLore() {
		return null;
	}

	@Override
	public Material getMaterial() {
		return material;
	}

	@Override
	public short getData() {
		return data;
	}


	public abstract void onPlayerInteract(PlayerInteractEvent event);


}
