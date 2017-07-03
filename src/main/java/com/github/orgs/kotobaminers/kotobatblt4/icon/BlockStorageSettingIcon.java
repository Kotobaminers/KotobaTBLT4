package com.github.orgs.kotobaminers.kotobatblt4.icon;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import com.github.orgs.kotobaminers.kotoapi2.KotobaHolograms;
import com.github.orgs.kotobaminers.kotobatblt4.blockquiz.BlockQuiz;
import com.github.orgs.kotobaminers.kotobatblt4.storage.BlockStorageMeta;

public enum BlockStorageSettingIcon implements SettingChestIcon {


	STARTER(100, Material.CHEST, 0) {
		@Override
		protected void setMetaData(BlockStorageMeta meta, Chest chest, List<ItemStack> options) {
			Integer number = InformationIcon.findPlayerNumber(options);
			if(0 < number) {
				options.removeIf(o -> InformationIcon.PLAYER.isSame(o));
				meta.getStarters().put(number, options);
			}
		}
	},


	SPAWN(101, Material.MONSTER_EGG, 0) {
		@Override
		protected void setMetaData(BlockStorageMeta meta, Chest chest, List<ItemStack> options) {
			Integer number = InformationIcon.findPlayerNumber(options);
			if(0 < number) {
				meta.getSpawns().put(number, chest.getLocation().clone().add(0.5, 3.5, 0.5));
			}
		}
	},


	BLOCK_QUIZ(102, Material.WORKBENCH, 0) {
		@Override
		protected void setMetaData(BlockStorageMeta meta, Chest chest, List<ItemStack> options) {
			meta.setQuiz(BlockQuiz.create(chest.getLocation(), options));
		}
	},


	ANSWER_FIELDS(103, Material.FURNACE, 0) {
		@Override
		protected void setMetaData(BlockStorageMeta meta, Chest chest, List<ItemStack> options) {
			meta.getFields().add(chest.getLocation());
		}
	},


	NEXT_MAP(104, Material.CHEST, 0) {
		@Override
		protected void setMetaData(BlockStorageMeta meta, Chest chest, List<ItemStack> options) {
			InventoryClickIcon.findMap(options).ifPresent(m -> meta.setNext(m.getId()));
		}
	},


	HOLOGRAMS(105, Material.BOOK, 0) {
		@Override
		protected void setMetaData(BlockStorageMeta meta, Chest chest, List<ItemStack> options) {
			KotobaHolograms.display(chest);
		}
	},


	;


	private int id;
	private Material material;
	private short data;


	private BlockStorageSettingIcon(int id, Material material, int data) {
		this.id = id;
		this.material = material;
		this.data = (short) data;
	}


	protected abstract void setMetaData(BlockStorageMeta meta, Chest chest, List<ItemStack> options);
	public static void setMetaData(BlockStorageMeta meta, Chest chest) {
		List<List<ItemStack>> matrix = SettingChestIcon.getOptions(chest);
		matrix.forEach(i -> {
			if(0 < i.size()) {
				List<ItemStack> options = i.subList(1, i.size());
				Stream.of(BlockStorageSettingIcon.values())
					.filter(icon -> icon.isSame(i.get(0)))
					.forEach(icon -> icon.setMetaData(meta, chest, options));
			}
		});
	}


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

