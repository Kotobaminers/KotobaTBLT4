package com.github.orgs.kotobaminers.kotobatblt4.system;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.orgs.kotobaminers.kotobatblt4.interactive.AbilityListener;
import com.github.orgs.kotobaminers.kotobatblt4.interactive.InteractiveBlockListener;
import com.github.orgs.kotobaminers.kotobatblt4.portal.PortalListener;
import com.github.orgs.kotobaminers.kotobatblt4.storage.MapListener;
import com.github.orgs.kotobaminers.kotobatblt4.userinterface.GUIListener;

public class KotobaTBLT4 extends JavaPlugin {


	@Override
	public void onEnable() {
		Setting.initialize(this);

		this.getCommand(TBLTCommandExecutor.LABEL).setExecutor(new TBLTCommandExecutor());

		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new GUIListener(), this);
		pluginManager.registerEvents(new PortalListener(), this);
		pluginManager.registerEvents(new InteractiveBlockListener(), this);
		pluginManager.registerEvents(new AbilityListener(), this);
		pluginManager.registerEvents(new MapListener(), this);
	}


	@Override
	public void onDisable() {
	}


}

