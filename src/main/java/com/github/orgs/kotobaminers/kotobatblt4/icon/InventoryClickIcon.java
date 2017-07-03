package com.github.orgs.kotobaminers.kotobatblt4.icon;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.github.orgs.kotobaminers.kotoapi2.ItemStackIcon;
import com.github.orgs.kotobaminers.kotoapi2.KotobaUtility;
import com.github.orgs.kotobaminers.kotobatblt4.storage.TBLTMap;
import com.github.orgs.kotobaminers.kotobatblt4.userinterface.TBLTGUI;

public enum InventoryClickIcon implements ItemStackIcon {


	MAP(300, Material.MAP, 0) {
		@Override
		public void onInventoryClick(InventoryClickEvent event) {
			String str = event.getCurrentItem().getItemMeta().getLore().get(0);
			try {
				new TBLTMap("dummy").find(Integer.valueOf(str))
					.map(m -> TBLTGUI.MAP.createGUIs(TBLTGUI.createMapIcons(m)))
					.ifPresent(l -> event.getWhoClicked().openInventory(l.get(0)));
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
	},
	MAP_TELEPORT(301, Material.COMPASS, 0) {
		@Override
		public void onInventoryClick(InventoryClickEvent event) {
			List<ItemStack> options = KotobaUtility.getValidContents(event.getInventory()).values().stream().collect(Collectors.toList());
			findMap(options).ifPresent(m -> event.getWhoClicked().teleport(m.getCenter()));
		}
	},
	;


	private int id;
	private Material material;
	private short data;


	private InventoryClickIcon(int id, Material material, int data) {
		this.id = id;
		this.material = material;
		this.data = (short) data;
	}


	public abstract void onInventoryClick(InventoryClickEvent event);


	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name();
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


	public static Optional<TBLTMap> findMap(List<ItemStack> options) {
		return options.stream()
			.filter(o -> o != null)
			.filter(o -> MAP.isSame(o))
			.findFirst()
			.map(o -> o.getItemMeta().getLore())
			.filter(l -> l != null)
			.filter(l -> 0 < l.size())
			.map(l -> ChatColor.stripColor(l.get(0)))
			.filter(s -> KotobaUtility.isNumber(s))
			.map(s -> Integer.valueOf(s))
			.map(i -> new TBLTMap("dummy").find(i).map(m -> (TBLTMap) m).orElse(null));
	}


}

