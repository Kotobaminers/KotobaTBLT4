package com.github.orgs.kotobaminers.kotobatblt4.icon;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import com.github.orgs.kotobaminers.kotobatblt4.portal.PortalMeta;
import com.github.orgs.kotobaminers.kotobatblt4.portal.TeleportPortalMeta;

public enum PortalSettingIcon implements SettingChestIcon {
	TELEPORT(300, Material.ENDER_PORTAL_FRAME, 0) {
		@Override
		public PortalMeta createPortalMeta(Chest chest) {
			List<List<ItemStack>> all = SettingChestIcon.getOptions(chest);
			List<ItemStack> options = all.stream()
				.filter(o -> 0 < o.size())
				.filter(o -> this.isSame(o.get(0)))
				.flatMap(o -> o.stream().filter(o2 -> !this.isSame(o2)))
				.collect(Collectors.toList());
			Location destination = InformationIcon.findLocation(options).orElse(chest.getLocation().getWorld().getSpawnLocation());
			return new TeleportPortalMeta(destination);
		}
	},



	;

	private int id;
	private Material material;
	private short data;


	private PortalSettingIcon(int id, Material material, int data) {
		this.id = id;
		this.material = material;
		this.data = (short) data;
	}


	public abstract PortalMeta createPortalMeta(Chest chest);


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

}
