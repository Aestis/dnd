package de.aestis.diamondsanddeath;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class EventListener implements Listener {
	
	FileConfiguration Config = Main.instance.getConfig();
	

	@EventHandler
	public <PlayerMessage> void onMessage(AsyncPlayerChatEvent event) {
		PlayerManager pm = new PlayerManager();
		Player player = event.getPlayer();
		
		if (pm.hasTeam(player.getName())) {
			event.setFormat(ChatColor.DARK_AQUA + "[" + pm.getTeam(player.getName()) + "]" + ChatColor.WHITE + " %s: %s");
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		PlayerManager pm = new PlayerManager();
		Player player = event.getPlayer();
		
		player.getInventory().clear();
		
		if (pm.hasTeam(player.getName())) {
			Bukkit.getServer().broadcastMessage("[DD] " + ChatColor.YELLOW + player.getName() + ChatColor.RED + " (" + pm.getTeam(player.getName()) + ")" + ChatColor.WHITE + " ist nun online.");
		} else {
			Bukkit.getServer().broadcastMessage("[DD] " + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " sucht noch ein Team.");
		}
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event) {
		Helpers hp = new Helpers();
		Player player = event.getPlayer();
		Inventory inv = player.getInventory();
		
		if (hp.isInventoryEmpty(player)) return;
		for (ItemStack i:inv) {
			player.getLocation().getWorld().dropItemNaturally(player.getLocation(), i);
		}
	}
	
	
	@EventHandler
	public void onEntityKill(EntityDeathEvent event) {
		if (event.getEntity().getKiller() == null) return;
		
		PlayerManager pm = new PlayerManager();
		Entity entity = event.getEntity();
		Player player = event.getEntity().getKiller();
		
		Bukkit.getServer().broadcastMessage("Tot!");
		
		//alternative exp drop system
		if (!Config.getBoolean("entity.Drop.Exp.Normal")) {
			int rndChance = ThreadLocalRandom.current().nextInt(0, 100);
			
			if (rndChance <= Config.getInt("entity.Drop.Exp.Rnd.Chance")) {
				int rndExpDrop = ThreadLocalRandom.current().nextInt(Config.getInt("entity.Drop.Exp.Rnd.Min"), Config.getInt("entity.Drop.Exp.Rnd.Max"));
				
				player.sendMessage("+" + rndExpDrop + " Exp");
				event.setDroppedExp(rndExpDrop);
			} else {
				event.setDroppedExp(-1);
			}
		}
		
		if (entity.getType() == EntityType.PLAYER) {
			Location entityLc = entity.getLocation();
			Location killerLc = player.getLocation();
			
			double distance = entityLc.distance(killerLc);
			NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN); 
	        ((DecimalFormat) nf).applyPattern("####.##"); 
			
			pm.addKill(player.getName(), "Player");
			Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " hat " + ChatColor.YELLOW + entity.getName() + ChatColor.WHITE + " erschlagen. (" + nf.format(distance) + "m)");
		} else {
			pm.addKill(player.getName(), "Mob");
		}
	}

}
