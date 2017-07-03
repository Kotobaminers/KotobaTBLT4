package com.github.orgs.kotobaminers.kotobatblt4.system;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.orgs.kotobaminers.kotoapi2.KotobaCommandInterface;
import com.github.orgs.kotobaminers.kotoapi2.KotobaHolograms;
import com.github.orgs.kotobaminers.kotoapi2.KotobaPlayerMode;
import com.github.orgs.kotobaminers.kotoapi2.KotobaUtility;
import com.github.orgs.kotobaminers.kotobatblt4.icon.IconManager;
import com.github.orgs.kotobaminers.kotobatblt4.icon.InventoryClickIcon;
import com.github.orgs.kotobaminers.kotobatblt4.interactive.BlockManipulation;
import com.github.orgs.kotobaminers.kotobatblt4.interactive.TBLTMapJoiner;
import com.github.orgs.kotobaminers.kotobatblt4.storage.TBLTMap;
import com.github.orgs.kotobaminers.kotobatblt4.userinterface.TBLTGUI;


public class TBLTCommandExecutor implements CommandExecutor {


	protected static final String LABEL = "tblt";


	private enum TBLTCommand implements KotobaCommandInterface {
		TEST(Arrays.asList(Arrays.asList("test")), "", "Command Test") {
			@Override
			public boolean executeWithOptions(Player player , List<String> args) {
				return true;
			}
		},


		RELOAD(Arrays.asList(Arrays.asList("reload")), "", "Reload Plugin") {
			@Override
			public boolean executeWithOptions(Player player , List<String> args) {
				Setting.initialize(Setting.getPlugin());
				return true;
			}
		},


		ITEM(Arrays.asList(Arrays.asList("item")), "<Page>", "Items") {
			@Override
			public boolean executeWithOptions(Player player , List<String> options) {
				if(0 < options.size()) {
					if(KotobaUtility.isNumber(options.get(0))) {
						int page = Integer.parseInt(options.get(0));
						List<ItemStack> icons = IconManager.getAll().stream().map(i -> i.getIcon(1)).collect(Collectors.toList());
						List<Inventory> guis = TBLTGUI.ITEM.createGUIs(icons);
						if(page <= guis.size()) {
							player.openInventory(guis.get(page - 1));
							return true;
						}
					}
				}
				return false;
			}
		},


		INFO(Arrays.asList(Arrays.asList("info", "information")), "", "Infomation") {
			@Override
			public boolean executeWithOptions(Player player , List<String> options) {
				player.sendMessage("Available Blocks: " + BlockManipulation.WHITE_LIST);
				return true;
			}
		},


		MAP_CREATE(Arrays.asList(Arrays.asList("map", "m"), Arrays.asList("create", "c")), "<Name>", "Create a map") {
			@Override
			public boolean executeWithOptions(Player player, List<String> options) {
				if(0 < options.size()) {
					TBLTMap.create(options.get(0), player).register();
					return true;
				}
				return false;
			}
		},

		MAP_LIST(Arrays.asList(Arrays.asList("map", "m"), Arrays.asList("list", "l")), "<Page>", "List maps") {
			@Override
			public boolean executeWithOptions(Player player , List<String> options) {
				if(0 < options.size()) {
					if(KotobaUtility.isNumber(options.get(0))) {
						int page = Integer.parseInt(options.get(0));
						List<ItemStack> icons = new TBLTMap("dummy").getStorages().stream()
							.map(m -> InventoryClickIcon.MAP.getIcon(1, Arrays.asList(Integer.toString(m.getId()), m.getName())))
							.collect(Collectors.toList());
						List<Inventory> guis = TBLTGUI.MAP_LIST.createGUIs(icons);
						if(page <= guis.size()) {
							player.openInventory(guis.get(page - 1));
						}
						return true;
					}
				}
				return false;
			}
		},


		MAP_HERE(Arrays.asList(Arrays.asList("map", "m"), Arrays.asList("here", "h")), "", "Show information where you are") {
		@Override
		public boolean executeWithOptions(Player player , List<String> args) {
			return new TBLTMap("dummy").find(player.getLocation()).stream()
				.map(map -> {
					player.openInventory(TBLTGUI.MAP.createGUIs(TBLTGUI.createMapIcons(map)).get(0));
					return true;
				})
				.filter(b -> b)
				.findAny()
				.orElse(false);
			}
		},


		MAP_UPDATE(Arrays.asList(Arrays.asList("map", "m"), Arrays.asList("update", "u")), "", "Update map where you are") {
			@Override
			public boolean executeWithOptions(Player player, List<String> args) {
				return new TBLTMap("dummy").find(player.getLocation()).stream()
					.map(map -> {
						map.setDataFromWorld();
						map.saveYaml();
						return true;
					})
					.filter(b -> b)
					.findAny()
					.orElse(false);
			}
		},

		MAP_RELOAD(Arrays.asList(Arrays.asList("map", "m"), Arrays.asList("reload", "r")), "", "Reload map from config where you are") {
			@Override
			public boolean executeWithOptions(Player player, List<String> args) {
				return new TBLTMap("dummy").find(player.getLocation()).stream()
					.map(arena -> {
						arena.reset();
						return true;
					})
					.filter(b -> b)
					.findAny()
					.orElse(false);
			}
		},

		MAP_DELETE(Arrays.asList(Arrays.asList("map", "m"), Arrays.asList("delete", "d")), "", "Delete map where you are") {
			@Override
			public boolean executeWithOptions(Player player, List<String> args) {
				return new TBLTMap("dummy").find(player.getLocation()).stream()
					.filter(s -> s instanceof TBLTMap)
					.map(s -> (TBLTMap) s)
					.map(m -> {
						m.deleteYaml();
						m.deregister();
						return true;
					})
					.filter(b -> b)
					.findAny()
					.orElse(false);
			}
		},

		ARENA_RESIZE(Arrays.asList(Arrays.asList("map", "m"), Arrays.asList("resize")), "", "Resize map") {
			@Override
			public boolean executeWithOptions(Player player , List<String> args) {
				return new TBLTMap("dummy").find(player.getLocation()).stream()
					.filter(map -> map instanceof TBLTMap)
					.map(map -> (TBLTMap) map)
					.map(map -> {
						map.resize(player);
						return true;
					})
					.filter(b -> b)
					.findAny()
					.orElse(false);
			}
		},


		MAP_JOIN(Arrays.asList(Arrays.asList("map", "m"), Arrays.asList("join", "j")), "", "Join map") {
			@Override
			public boolean executeWithOptions(Player player , List<String> args) {
				return new TBLTMap("dummy").find(player.getLocation()).stream()
					.filter(map -> map instanceof TBLTMap)
					.map(map -> (TBLTMap) map)
					.map(map -> {
						TBLTMapJoiner.remove(map);
						Bukkit.getOnlinePlayers().stream()
							.filter(p -> map.isIn(p.getLocation()))
							.forEach(p -> TBLTMapJoiner.update(p, map));
						return true;
					})
					.filter(b -> b)
					.findAny()
					.orElse(false);
			}
		},



		HOLOGRAMS_REMOVE(Arrays.asList(Arrays.asList("hologram", "h", "holograms"), Arrays.asList("remove", "r")), "", "Remove all holograms") {
			@Override
			public boolean executeWithOptions(Player player , List<String> args) {
				KotobaHolograms.removeAll();
				return true;
			}
		},


		//		ARENA_RENAME_HERE(Arrays.asList(Arrays.asList("arena", "a"), Arrays.asList("renamehere")), "", "Rename an arena where you are", PermissionEnum.OP) {
//			@Override
//			public boolean executeWithOptions(Player player, String[] args) {
//				List<String> options = takeOptions(args);
//				if(0 < options.size()) {
//					return new TBLTArenaMap().findUnique(player.getLocation())
//						.map(arena -> {
//							if(arena.rename(options.get(0))) {
//								arena.save();
//								arena.load();
//								return true;
//							}
//							return false;
//						}).orElse(false);
//					}
//				return false;
//				}
//		},


		;


		private List<List<String>> tree;
		private String option;
		private String description;
		private KotobaPlayerMode mode = KotobaPlayerMode.OP;


		private TBLTCommand(List<List<String>> tree, String option, String description) {
			this.tree = tree;
			this.option = option;
			this.description = description;
		}


		@Override
		public List<List<String>> getTree() {
			return tree;
		}

		@Override
		public String getOption() {
			return option;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getLabel() {
			return LABEL;
		}


		public static List<TBLTCommand> find(List<String> args) {
			return Stream.of(TBLTCommand.values())
				.filter(c -> c.tree.size() <= args.size())
				.filter(c ->
					Stream.iterate(0, i -> i + 1)
						.limit(c.tree.size())
						.allMatch(i -> c.tree.get(i).stream().anyMatch(a -> a.equalsIgnoreCase(args.get(i))))
				)
				.collect(Collectors.toList());
		}


	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(label.equalsIgnoreCase(label)) {
				List<String> list = Arrays.asList(args);
				List<Boolean> results = TBLTCommand.find(list).stream()
					.filter(c -> c.mode.isSame(player))
					.map(c -> {
						boolean success = c.execute(player, list);
						if(success) {
							return true;
						}
						player.sendMessage(c.getUsage());
						return true;
					})
					.collect(Collectors.toList());

				if(!results.contains(true)) {
					Stream.of(TBLTCommand.values()).forEach(e -> player.sendMessage(e.getUsage()));
				}
			}
		}
		return true;
	}


}

