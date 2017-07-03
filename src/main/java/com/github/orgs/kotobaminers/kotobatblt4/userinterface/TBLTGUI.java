package com.github.orgs.kotobaminers.kotobatblt4.userinterface;

import static com.github.orgs.kotobaminers.kotoapi2.KotobaPlayerMode.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.orgs.kotobaminers.kotoapi2.KotobaBlockStorage;
import com.github.orgs.kotobaminers.kotoapi2.KotobaGUIInterface;
import com.github.orgs.kotobaminers.kotoapi2.KotobaPlayerMode;
import com.github.orgs.kotobaminers.kotobatblt4.icon.InformationIcon;
import com.github.orgs.kotobaminers.kotobatblt4.icon.InventoryClickIcon;
import com.github.orgs.kotobaminers.kotobatblt4.storage.TBLTMap;

public enum TBLTGUI implements KotobaGUIInterface {


	ITEM("Item", Arrays.asList(OP, CREATOR)) {
		@Override
		public void onClick(InventoryClickEvent event) {
			ItemStack item = updateLocation(event.getCurrentItem(), event.getWhoClicked().getLocation());
			event.getWhoClicked().getInventory().addItem(item);
		}

		private ItemStack updateLocation(ItemStack item, Location location) {
			item = item.clone();
			if(InformationIcon.LOCATION.isSame(item)) {
				ItemMeta meta = item.getItemMeta();
				String world = location.getWorld().getName();
				String x = Integer.toString(location.getBlockX());
				String y = Integer.toString(location.getBlockY());
				String z = Integer.toString(location.getBlockZ());
				String yaw = Float.toString(location.getYaw());
				String pitch = Float.toString(location.getPitch());
				List<String> lore = new ArrayList<String>();
				lore.add(world);
				lore.add(x);
				lore.add(y);
				lore.add(z);
				lore.add(yaw);
				lore.add(pitch);
				lore.add(meta.getLore().get(meta.getLore().size() - 1));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			return item;
		}

	},


	MAP_LIST("Map list", Arrays.asList(OP, CREATOR)) {
		@Override
		public void onClick(InventoryClickEvent event) {
			Stream.of(InventoryClickIcon.values())
				.filter(i -> i.isSame(event.getCurrentItem()))
				.forEach(i -> i.onInventoryClick(event));
		}
	},


	MAP("Map", Arrays.asList(OP, CREATOR)) {
		@Override
		public void onClick(InventoryClickEvent event) {
			if(InventoryClickIcon.MAP.isSame(event.getCurrentItem())) {
				event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
			} else {
				Stream.of(InventoryClickIcon.values())
					.filter(i -> i.isSame(event.getCurrentItem()))
					.forEach(i -> i.onInventoryClick(event));
			}
		}
	},


	;


	private int SIZE = 9 * 6;
	private String title = "";
	private final String POSTFIX = ": KotobaTBLT";
	private List<KotobaPlayerMode> modes;


	private TBLTGUI(String title, List<KotobaPlayerMode> modes) {
		this.title = title;
		this.modes = modes;
	}


	@Override
	public String getPostfix() {
		return POSTFIX;
	}


	public static List<TBLTGUI> find(Inventory inventory) {
		return Stream.of(TBLTGUI.values())
			.filter(g -> g.isSame(inventory))
			.collect(Collectors.toList());
	}


	public List<Inventory> createGUIs(List<ItemStack> icons) {
		int number = icons.size() / SIZE + 1;
		return Stream.iterate(0, i -> i + 1)
			.limit(number)
			.map(i -> {
				Inventory inventory = Bukkit.createInventory(null, SIZE, title + getPostfix());
				Stream.iterate(0, j -> j + 1)
					.limit(Math.min(icons.size() - SIZE * i, SIZE))
					.forEach(j -> inventory.addItem(icons.get(SIZE * i + j)));
				return inventory;
			})
			.collect(Collectors.toList());
	}


	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public String getTitle() {
		return title;
	}


	//Utility
	public static List<ItemStack> createMapIcons(KotobaBlockStorage map) {
		if(map instanceof TBLTMap) {
			TBLTMap map2 = (TBLTMap) map;
			return Arrays.asList(
				InventoryClickIcon.MAP.getIcon(1, Arrays.asList(Integer.toString(map2.getId()), map2.getName())),
				InventoryClickIcon.MAP_TELEPORT.getIcon(1)
			);
		}
		return new ArrayList<>();
	}


	public boolean canInteract(Player player) {
		return modes.stream().anyMatch(m -> m.isSame(player));
	}


}

