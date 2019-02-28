package de.aestis.diamondsanddeath;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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
			event.setFormat(ChatColor.DARK_AQUA + "[" + pm.getTeam(player.getName()) + "] " + ChatColor.WHITE + "%s: %s");
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		PlayerManager pm = new PlayerManager();
		Player player = event.getPlayer();
		
		player.getInventory().clear();
		
		if (pm.hasTeam(player.getName())) {
			Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + "[" + pm.getTeam(player.getName()) + "] " + ChatColor.YELLOW + player.getName() + " ist nun online.");
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
	
	
	//new killing system + stats
	@EventHandler
	public void onEntityKill(EntityDeathEvent event) {
		if (event.getEntity().getKiller() == null) return;
		
		PlayerManager pm = new PlayerManager();
		Entity entity = event.getEntity();
		Player player = event.getEntity().getKiller();

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
			
			ItemStack itemType = player.getInventory().getItemInMainHand();
			String killMsg = null;
			if (itemType == new ItemStack(Material.BOW)) {
				killMsg = "erschossen";
			} else {
				killMsg = "erschlagen";
			}
			
			((DecimalFormat) nf).applyPattern("####.##"); 
			
			pm.addKill(player.getName(), "Player");
			Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " hat " + ChatColor.YELLOW + entity.getName() + ChatColor.WHITE + " " + killMsg + ". (" + nf.format(distance) + "m)");
		} else {
			pm.addKill(player.getName(), "Mob");
		}
	}
	
	//new mining system + stats
	@EventHandler
	public void onBlockBreak (BlockBreakEvent event) {
		Block blk = event.getBlock();
		Location blkLoc = blk.getLocation();
		Material blkMat = blk.getBlockData().getMaterial();
		
		if (Config.getBoolean("block.Drop.Ore.Enabled")) {
			//generate rnd drops
			int rndChance = ThreadLocalRandom.current().nextInt(0, 100);
			if (rndChance <= Config.getInt("block.Drop.Ore.Diamond.Chance")) {
				if (blkMat == Material.DIAMOND_ORE) {
					blk.getLocation().getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.EMERALD, 1));
				}
			}
		}
	}

}
