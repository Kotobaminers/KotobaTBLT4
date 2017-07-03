package com.github.orgs.kotobaminers.kotobatblt4.system;

import com.github.orgs.kotobaminers.kotobatblt4.storage.TBLTMap;

public class Setting {
	private static KotobaTBLT4 plugin;


	public static void initialize(KotobaTBLT4 plugin) {
		Setting.plugin = plugin;
		new TBLTMap("dummy").importAllYaml();
	}


	public static KotobaTBLT4 getPlugin() {
		return plugin;
	}

	public static void saveAll() {
	}


}

