package me.quared.hubpvp;

import lombok.Getter;
import me.quared.hubpvp.commands.HubPvPCommand;
import me.quared.hubpvp.core.PvPManager;
import me.quared.hubpvp.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public final class HubPvP extends JavaPlugin {

	@Getter
	private static HubPvP instance;

	private PvPManager pvpManager;

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		instance = this;
		pvpManager = new PvPManager();

		Objects.requireNonNull(getCommand("hubpvp")).setExecutor(new HubPvPCommand());
		Objects.requireNonNull(getCommand("hubpvp")).setTabCompleter(new HubPvPCommand());

		Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerItemHeldListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(), this);
	}

	@Override
	public void onDisable() {
		pvpManager.disable();
	}

}
