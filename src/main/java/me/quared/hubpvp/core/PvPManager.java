package me.quared.hubpvp.core;

import lombok.Getter;
import me.quared.hubpvp.HubPvP;
import me.quared.hubpvp.util.ItemUtil;
import me.quared.hubpvp.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class PvPManager {

	private final Map<Player, PvPState> playerPvpStates;
	private final Map<Player, BukkitRunnable> currentTimers;
	private final List<OldPlayerData> oldPlayerDataList;

	private ItemStack weapon, chestplate, leggings;

	public PvPManager() {
		playerPvpStates = new HashMap<>();
		currentTimers = new HashMap<>();
		oldPlayerDataList = new ArrayList<>();

		loadItems();
	}

	public void loadItems() {
		weapon = new ItemUtil(Material.DIAMOND_SWORD)
				.setName(StringUtil.colorize("&#559eff&lPvP Sword"))
				.addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
				.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
				.addItemFlag(ItemFlag.HIDE_ENCHANTS)
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
				.setUnbreakable()
				.toItemStack();

		chestplate = new ItemUtil(Material.DIAMOND_CHESTPLATE)
				.setName(" ")
				.addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
				.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
				.addItemFlag(ItemFlag.HIDE_ENCHANTS)
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
				.setUnbreakable()
				.toItemStack();

		leggings = new ItemUtil(Material.DIAMOND_LEGGINGS)
				.setName(" ")
				.addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
				.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
				.addItemFlag(ItemFlag.HIDE_ENCHANTS)
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
				.setUnbreakable()
				.toItemStack();
	}

	public void enablePvP(Player player) {
		setPlayerState(player, PvPState.ON);

		if (getOldData(player) != null) getOldPlayerDataList().remove(getOldData(player));
		getOldPlayerDataList().add(new OldPlayerData(player, player.getInventory().getArmorContents(), player.getAllowFlight()));

		player.setAllowFlight(false);
		player.getInventory().setChestplate(getChestplate());
		player.getInventory().setLeggings(getLeggings());

		player.sendMessage(StringUtil.colorize(HubPvP.getInstance().getConfig().getString("lang.pvp-enabled")));
	}

	public void setPlayerState(Player player, PvPState state) {
		playerPvpStates.put(player, state);
	}

	public OldPlayerData getOldData(Player player) {
		return oldPlayerDataList.stream().filter(data -> data.player().equals(player)).findFirst().orElse(null);
	}

	public void removePlayer(Player player) {
		disablePvP(player);
		playerPvpStates.remove(player);
	}

	public void disablePvP(Player player) {
		setPlayerState(player, PvPState.OFF);

		OldPlayerData oldPlayerData = getOldData(player);
		if (oldPlayerData != null) {
			player.getInventory().setHelmet(oldPlayerData.armor()[3] == null ? new ItemStack(Material.AIR) : oldPlayerData.armor()[3]);
			player.getInventory().setChestplate(oldPlayerData.armor()[2] == null ? new ItemStack(Material.AIR) : oldPlayerData.armor()[2]);
			player.getInventory().setLeggings(oldPlayerData.armor()[1] == null ? new ItemStack(Material.AIR) : oldPlayerData.armor()[1]);
			player.getInventory().setBoots(oldPlayerData.armor()[0] == null ? new ItemStack(Material.AIR) : oldPlayerData.armor()[0]);
			player.setAllowFlight(oldPlayerData.canFly());
		}

		player.sendMessage(StringUtil.colorize(HubPvP.getInstance().getConfig().getString("lang.pvp-disabled")));
	}

	public void disable() {
		for (Player player : playerPvpStates.keySet()) {
			if (isInPvP(player)) {
				disablePvP(player);
			}
		}
		playerPvpStates.clear();
	}

	public boolean isInPvP(Player player) {
		return getPlayerState(player) == PvPState.ON || getPlayerState(player) == PvPState.DISABLING;
	}

	public PvPState getPlayerState(Player player) {
		return playerPvpStates.get(player);
	}

	public void giveWeapon(Player player) {
		player.getInventory().setItem(4, getWeapon());
	}

	public void putTimer(Player player, BukkitRunnable timerTask) {
		if (getCurrentTimers().containsKey(player)) {
			getCurrentTimers().get(player).cancel();
		}
		getCurrentTimers().put(player, timerTask);
	}

	public void removeTimer(Player player) {
		if (getCurrentTimers().containsKey(player)) {
			getCurrentTimers().get(player).cancel();
		}
		getCurrentTimers().remove(player);
	}

}
