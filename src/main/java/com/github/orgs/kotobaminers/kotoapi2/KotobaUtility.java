package com.github.orgs.kotobaminers.kotoapi2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class KotobaUtility {


	public static final int LORE_LENGTH = 25;


	public static List<String> splitSentence(String sentence, int number) {
		List<String> words = Arrays.asList(sentence.split(" "));
		List<List<String>> groups = new ArrayList<>();
		int count = 0;
		List<String> group = new ArrayList<>();
		for(int i = 0; i < words.size(); i++) {
			count += words.get(i).length() + 1;
			if(count <= number) {
				group.add(words.get(i));
			} else {
				groups.add(group);
				group = new ArrayList<>();
				group.add(words.get(i));
				count = words.get(i).length() + 1;
			}
		}
		groups.add(group);
		return groups.stream().filter(g -> 0 < g.size()).map(g -> String.join(" ", g)).collect(Collectors.toList());
	}
	public static List<String> splitSentence(String sentence) {
		return splitSentence(sentence, LORE_LENGTH);
	}


	public static void shootFirework(World world, Location location) {
		Firework fw = world.spawn(location, Firework.class);
		FireworkMeta fwm = fw.getFireworkMeta();
		Random random = new Random();
		FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(Color.GREEN).withFade(Color.AQUA).with(Type.BALL_LARGE).trail(random.nextBoolean()).build();
		fwm.addEffect(effect);
		fwm.setPower(0);
		fw.setFireworkMeta(fwm);
	}


	public static List<Block> getBlocks(World world, int xMax, int yMax, int zMax, int xMin, int yMin, int zMin) {
		List<Block> blocks = new ArrayList<>();
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				for (int z = zMin; z <= zMax; z++) {
					blocks.add(world.getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}


	public static List<Block> getBlocks(Location origin, int radius) {
		List<Block> blocks = new ArrayList<>();
		int oX = origin.getBlockX();
		int oY = origin.getBlockY();
		int oZ = origin.getBlockZ();
		Vector oV = new Vector(oX, oY, oZ);
		for (int x = oX - radius; x <= oX + radius; x++) {
			for (int y = oY - radius; y <= oY + radius; y++) {
				for (int z = oZ - radius; z <= oZ + radius; z++) {
					if(new Vector(x, y, z).distance(oV) <= radius) {
						blocks.add(origin.getWorld().getBlockAt(x, y, z));
					}
				}
			}
		}
		return blocks;
	}


	public static List<Block> getSelectBlocks(Player player) {
		WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (worldEdit == null) {
			return new ArrayList<Block>();
		}
		Selection sel = worldEdit.getSelection(player);
		World world = sel.getWorld();
		int xMax = sel.getMaximumPoint().getBlockX();
		int yMax = sel.getMaximumPoint().getBlockY();
		int zMax = sel.getMaximumPoint().getBlockZ();
		int xMin = sel.getMinimumPoint().getBlockX();
		int yMin = sel.getMinimumPoint().getBlockY();
		int zMin = sel.getMinimumPoint().getBlockZ();
		return getBlocks(world, xMax, yMax, zMax, xMin, yMin, zMin);
	}


	public static List<String> toStringListFromBookMeta(BookMeta bookMeta) {
		return bookMeta.getPages().stream()
			.flatMap(page -> Stream.of(page.split("\n")).map(line -> ChatColor.stripColor(line)))
			.collect(Collectors.toList());
	}


	public static List<String> toStringListFromBookMeta(BookMeta bookMeta, int page) {
		if(bookMeta.getPageCount() <= page) {
			return Stream.of(bookMeta.getPage(page).split("\n"))
				.map(line -> ChatColor.stripColor(line))
				.collect(Collectors.toList());
		}
		return new ArrayList<>();

	}


	public static ItemStack createPlayerSkull(String owner) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
		itemMeta.setDisplayName(owner);
		itemMeta.setOwner(owner);
		skull.setItemMeta(itemMeta);
		return skull;
	}


	public static Location getEntityBase(Entity entity) {
		if(entity instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) entity;
			return le.getEyeLocation();
		}
		return entity.getLocation();
	}


	public static void lookAt(Player player, Location lookat) {
		//Clone the loc to prevent applied changes to the input loc
		 Location loc = player.getLocation().clone();
		// Values of change in distance (make it relative)
		 double dx = lookat.getX() - loc.getX();
		 double dy = lookat.getY() - loc.getY();
	     double dz = lookat.getZ() - loc.getZ();
		 // Set yaw
		 if (dx != 0) {
			   // Set yaw start value based on dx
			   if (dx < 0) {
				 loc.setYaw((float) (1.5 * Math.PI));
				   } else {
					 loc.setYaw((float) (0.5 * Math.PI));
					   }
			   loc.setYaw(loc.getYaw() - (float) Math.atan(dz / dx));
			 } else if (dz < 0) {
				   loc.setYaw((float) Math.PI);
			}
		// Get the distance from dx/dz
		double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
		// Set pitch
		loc.setPitch((float) -Math.atan(dy / dxz));
		// Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
		loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
		loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);
		player.teleport(loc);
	}


	public static Location randomizeLocation(Location location, Integer radius) {
		Random random = new Random();
		int x =  - radius + random.nextInt(radius + 1) * 2;
		int y =  - radius + random.nextInt(radius + 1) * 2;
		int z =  - radius + random.nextInt(radius + 1) * 2;
		return location.clone().add(new Vector(x, y, z));
	}


	public static boolean isNumber(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}


	public static Vector rotateVectorAroundY(Vector vector, double degrees) {
		double rad = Math.toRadians(degrees);
		double currentX = vector.getX();
		double currentZ = vector.getZ();
		double cosine = Math.cos(rad);
		double sine = Math.sin(rad);
		return new Vector((cosine * currentX - sine * currentZ), vector.getY(), (sine * currentX + cosine * currentZ));
	}


	public static Location getBlockCenter(Block block) {
		return block.getLocation().clone().add(0.5, 0.5, 0.5);
	}


	public static Map<Integer, ItemStack> getValidContents(Inventory inventory) {
		Map<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();
		Stream.iterate(0, i -> i + 1)
			.limit(inventory.getSize())
			.filter(i -> inventory.getItem(i) != null)
			.forEach(i -> contents.put(i, inventory.getItem(i)));
		return contents;
	}


}

