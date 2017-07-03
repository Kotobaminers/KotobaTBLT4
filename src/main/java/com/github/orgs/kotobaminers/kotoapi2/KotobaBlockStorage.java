package com.github.orgs.kotobaminers.kotoapi2;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.orgs.kotobaminers.kotobatblt4.storage.BlockStorageMeta;
import com.github.orgs.kotobaminers.kotobatblt4.system.Setting;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public abstract class KotobaBlockStorage extends KotobaYamlConfiguration {


	private enum Path {
		ID("ID"),
		NAME("NAME"),
		WORLD("WORLD"),
		BLOCKS("BLOCKS"),
		CHESTS("CHESTS"),
		TOP_CORNER("TOP_CORNER"),
		BOTTOM_CORNER("BOTTOM_CORNER"),
		;
		private String path;
		private Path(String path) {
			this.path = path;
		}
		public String getPath() {
			return path;
		}
	}


	private int id;
	private String name;
	private World world;
	private Integer xMax;
	private Integer yMax;
	private Integer zMax;
	private Integer xMin;
	private Integer yMin;
	private Integer zMin;
	protected List<Integer> blocksData = new ArrayList<>();
	protected List<Chest> chests = new ArrayList<>();


	protected KotobaBlockStorage(String name) {
		this.name = name;
	}


	//Return child's instance
	protected abstract KotobaBlockStorage create(String name);

	public KotobaBlockStorage createFromSelection(int id, String name, Player player) {
		WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (worldEdit == null) {
			player.sendMessage("WorldEdit Not Load");
			return this;
		}
		Selection sel = worldEdit.getSelection(player);

		KotobaBlockStorage storage = create(name);
		storage.id = id;
		storage.name = name;
		storage.world = sel.getWorld();
		storage.xMax = sel.getMaximumPoint().getBlockX();
		storage.yMax = sel.getMaximumPoint().getBlockY();
		storage.zMax = sel.getMaximumPoint().getBlockZ();
		storage.xMin = sel.getMinimumPoint().getBlockX();
		storage.yMin = sel.getMinimumPoint().getBlockY();
		storage.zMin = sel.getMinimumPoint().getBlockZ();
		List<Block> blocks = storage.getBlocksFromWorld();
		storage.blocksData = toBlocksData(
			blocks.stream()
				.filter(b -> b.getType() != Material.AIR)
				.collect(Collectors.toList())
		);
		storage.chests = blocks.stream()
			.filter(b -> b.getState() instanceof Chest)
			.map(b -> (Chest) b.getState())
			.collect(Collectors.toList());

		return storage;
	}

	@Override
	public KotobaYamlConfiguration importYaml(YamlConfiguration config) {
		KotobaBlockStorage bs = Bukkit.getWorlds().stream()
			.filter(w -> w.getName().equalsIgnoreCase(config.getString(Path.WORLD.getPath())))
			.findFirst()
			.map(world -> {
				String name = config.getString(Path.NAME.getPath());
				KotobaBlockStorage storage = create(name);

				int id = config.getInt(Path.ID.getPath());

				Location topCorner = (Location) config.get(Path.TOP_CORNER.getPath());
				Location bottomCorner = (Location) config.get(Path.BOTTOM_CORNER.getPath());

				storage.id = id;
				storage.world = world;
				storage.xMax = topCorner.getBlockX();
				storage.yMax = topCorner.getBlockY();
				storage.zMax = topCorner.getBlockZ();
				storage.xMin = bottomCorner.getBlockX();
				storage.yMin = bottomCorner.getBlockY();
				storage.zMin = bottomCorner.getBlockZ();
				storage.blocksData = storage.importBlocksDataFromYaml();
				storage.setChestsFromYaml();

				return storage;
			})
			.orElse(create("Failed"));

		return bs;
	}

	@Override
	public void deleteYaml() {
		if(getFileEvenCreate().exists()) getFileEvenCreate().delete();
	}

	@Override
	public void saveYaml() {
		File file = getFileEvenCreate();
		deleteYaml();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		YamlConfiguration config = getConfiguration();

		config.set(Path.ID.getPath(), getId());
		config.set(Path.NAME.getPath(), getName());
		config.set(Path.WORLD.getPath(), getWorld().getName());
		config.set(Path.BOTTOM_CORNER.getPath(), new Location(world, xMin, yMin, zMin));
		config.set(Path.TOP_CORNER.getPath(), new Location(world, xMax, yMax, zMax));
		config.set(Path.BLOCKS.getPath(), blocksData);

		chests.stream()
			.forEach(chest -> {
				Block block = chest.getBlock();
				Map<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();
				Stream.iterate(0, i -> i + 1)
					.limit(chest.getInventory().getSize())
					.filter(i -> chest.getInventory().getContents()[i] != null)
					.forEach(i -> contents.put(i, chest.getInventory().getContents()[i]));
				config.set(Path.CHESTS.getPath() + "." + KotobaBlockData.toIntegerList(block), contents);
			});

		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	//Reset/Reload
	public void reset() {
		KotobaHolograms.remove(Setting.getPlugin(), this);
		replaceAir();
		KotobaBlockData.placeBlocksSafe(blocksData, getWorld());
		setChestsFromYaml();
		setDataFromWorld();
	}


	//Block I/O
	protected List<Integer> toBlocksData(List<Block> blocks) {
		return blocks.stream()
			.flatMap(b -> KotobaBlockData.toIntegerList(b).stream())
			.collect(Collectors.toList());
	}


	private List<Integer> importBlocksDataFromYaml() {
		return getConfiguration().getIntegerList(Path.BLOCKS.getPath());
	}


	private void setChestsFromYaml() {
		List<Chest> chests = new ArrayList<>();
		YamlConfiguration config = getConfiguration();
		Optional.ofNullable(config.getString(Path.WORLD.getPath()))
			.flatMap(name -> Optional.ofNullable(Bukkit.getWorld(name)))
			.ifPresent(world -> {
				if(!config.isConfigurationSection(Path.CHESTS.getPath())) return;
				config.getConfigurationSection(Path.CHESTS.getPath()).getKeys(false).stream()
				.forEach(key -> {
					Location target = new Location(world, 0, 0, 0);
					List<Integer> list = Stream.of(key.substring(1, key.length() - 1).split(", "))
						.filter(s -> KotobaUtility.isNumber(s))
						.map(s -> Integer.valueOf(s))
						.collect(Collectors.toList());
					target.setX(list.get(0));
					target.setY(list.get(1));
					target.setZ(list.get(2));
					Block block = world.getBlockAt(target);
					if(block.getState() instanceof Chest) {
						//Initialize Chest
						Chest chest = (Chest) block.getState();
						chest.getInventory().clear();
						KotobaBlockData.placeBlock(target, list.get(3), list.get(4), false);
						block = world.getBlockAt(target);
						String key2 = Path.CHESTS.getPath() + "." + key;
						config.getConfigurationSection(key2).getKeys(false).stream()
							.forEach(key3 -> chest.getInventory().setItem(
								Integer.valueOf(key3),
								config.getItemStack(key2 + "." + key3)
							));
						chests.add(chest);
					}
				});
			});
		this.chests = chests;
	}


	//From World -> Blocks and Chests
	public void setDataFromWorld() {
		setBlocksDataFromWorld();
		setChestFromWorld();
		getStorageMeta().setDataFromChest(chests);
	}


	private void setBlocksDataFromWorld() {
		blocksData = toBlocksData(
			getBlocksFromWorld().stream()
				.filter(b -> b.getType() != Material.AIR)
				.collect(Collectors.toList())
		);
	}


	private void setChestFromWorld() {
		chests = getBlocksFromWorld().stream()
			.filter(b -> b.getState() instanceof Chest)
			.map(b -> (Chest) b.getState())
			.collect(Collectors.toList());
	}


	@SuppressWarnings("deprecation")
	public void replaceAir() {
		Location target = new Location(world, xMin, yMin, zMin);
		Stream.iterate(xMin, i -> i + 1)
			.limit(xMax - xMin + 1)
			.forEach(x -> {
				target.setX(x);
				Stream.iterate(yMin, i -> i + 1)
					.limit(yMax - yMin + 1)
					.forEach(y -> {
						target.setY(y);
						Stream.iterate(zMin, i -> i + 1)
							.limit(zMax - zMin + 1)
							.forEach(z -> {
								target.setZ(z);
								BlockState state = target.getBlock().getState();
								if(state instanceof Chest) {
									((Chest) state).getInventory().clear();
								}
								if(target.getBlock().getType() != Material.AIR) {
									KotobaBlockData.placeBlock(target, Material.AIR.getId(), 0, false);
								}
							});
					});
			});
	}


	protected List<Block> getBlocksFromWorld() {
		return KotobaUtility.getBlocks(getWorld(), getXMax(), getYMax(), getZMax(), getXMin(), getYMin(), getZMin());
	}


	public Location getCenter() {
		int xCenter = (getXMax() + getXMin()) / 2;
		int yCenter = (getYMax() + getYMin()) / 2;
		int zCenter = (getZMax() + getZMin()) / 2;
		return new Location(getWorld(), xCenter + 0.5, yCenter + 0.5, zCenter + 0.5);
	}


	public boolean isOverlap(KotobaBlockStorage storage) {
		Integer xMax1 = this.getXMax();
		Integer xMin1 = this.getXMin();
		Integer xMax2 = storage.getXMax();
		Integer xMin2 = storage.getXMin();
		boolean overlapX = (xMin2 <= xMax1 && xMax1 <= xMax2) || (xMin2 <= xMin1  && xMin1 <= xMax2);

		Integer yMax1 = this.getYMax();
		Integer yMin1 = this.getYMin();
		Integer yMax2 = storage.getYMax();
		Integer yMin2 = storage.getYMin();
		boolean overlapY = (yMin2 <= yMax1 && yMax1 <= yMax2) || (yMin2 <= yMin1  && yMin1 <= yMax2);

		Integer zMax1 = this.getZMax();
		Integer zMin1 = this.getZMin();
		Integer zMin2 = storage.getZMin();
		Integer zMax2 = storage.getZMax();
		boolean overlapZ = (zMin2 <= zMax1 && zMax1 <= zMax2) || (zMin2 <= zMin1  && zMin1 <= zMax2);

		if(overlapX && overlapY && overlapZ) {
			return true;
		}
		return false;
	}


	public boolean isIn(Location location) {
		int pX = location.getBlockX();
		int pY = location.getBlockY();
		int pZ = location.getBlockZ();
		if (location.getWorld().getUID() == world.getUID() && pX <= getXMax() && pX >= getXMin() && pY <= getYMax() && pY >= getYMin() && pZ <= getZMax() && pZ >= getZMin()) {
			return true;
		}
		return false;
	}


	//Getter and Setter
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public World getWorld() {
		return world;
	}
	public Integer getXMax() {
		return xMax;
	}
	public Integer getYMax() {
		return yMax;
	}
	public Integer getZMax() {
		return zMax;
	}
	public Integer getXMin() {
		return xMin;
	}
	public Integer getYMin() {
		return yMin;
	}
	public Integer getZMin() {
		return zMin;
	}
	protected void setName(String name) {
		this.name= name;
	}

	@Override
	public String getFileName() {
		return name;
	}
	@Override
	public void setFileName(String name) {
		this.name = name;
	}
	public abstract File getDirectory();


	protected abstract BlockStorageMeta getStorageMeta();


}

