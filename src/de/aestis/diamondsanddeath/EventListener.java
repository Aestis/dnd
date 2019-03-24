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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

public class EventListener implements Listener {
	
	FileConfiguration config = Main.instance.getConfig();
	
	
	@EventHandler
	public <PlayerMessage> void onMessage(AsyncPlayerChatEvent event) {
		PlayerManager pm = PlayerManager.getInstance();
		Player player = event.getPlayer();
		
		if (pm.hasTeam(player.getName())) {
			String teamStr = "[" + pm.getTeam(player.getName()) + "] ";
			if (pm.isTeamLeader(player.getName())) teamStr = "[**]" + teamStr;
			event.setFormat(ChatColor.DARK_AQUA + teamStr + ChatColor.WHITE + "%s: %s");
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		PlayerManager pm = PlayerManager.getInstance();
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
		
		PlayerManager pm = PlayerManager.getInstance();
		Entity entity = event.getEntity();
		Player player = event.getEntity().getKiller();

		//alternative exp drop system
		if (!config.getBoolean("entity.Drop.Exp.Normal")) {
			int rndChance = ThreadLocalRandom.current().nextInt(0, 100);
			
			if (entity.isOp()) {
				event.setDroppedExp(rndChance);
				Bukkit.broadcastMessage(entity.getCustomName() + " wurde von " + player.getName() + " erlegt.");
			} else {
				if (rndChance <= config.getInt("entity.Drop.Exp.Rnd.Chance")) {
					int rndExpDrop = ThreadLocalRandom.current().nextInt(config.getInt("entity.Drop.Exp.Rnd.Min"), config.getInt("entity.Drop.Exp.Rnd.Max"));
					
					player.sendMessage("+" + rndExpDrop + " Exp");
					event.setDroppedExp(rndExpDrop);
				} else {
					event.setDroppedExp(-1);
				}
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
		Helpers hp = new Helpers();
		Block blk = event.getBlock();
		Location blkLoc = blk.getLocation();
		Material blkMat = blk.getBlockData().getMaterial();
		
		if (config.getBoolean("block.Drop.Ore.Enabled")) {
			//generate rnd drops
			int rndChance = ThreadLocalRandom.current().nextInt(0, 100);
			if (blkMat == Material.DIAMOND_ORE) {
				if (rndChance <= config.getInt("block.Drop.Ore.Diamond.Chance")) {
					int dropCount = hp.getRndInt(config.getInt("block.Drop.Ore.Diamond.Min"), config.getInt("block.Drop.Ore.Diamond.Max"));
					Bukkit.getServer().broadcastMessage("drpcnt: " + dropCount);
					blkLoc.getWorld().dropItemNaturally(blkLoc, new ItemStack(Material.EMERALD, dropCount));
				}
			} else if (blkMat == Material.GOLD_ORE) {
				Bukkit.getServer().broadcastMessage("rnd extra drop gold");
				if (rndChance <= config.getInt("block.Drop.Ore.Gold.Chance")) {
					int dropCount = ThreadLocalRandom.current().nextInt(config.getInt("block.Drop.Ore.Gold.Min"), config.getInt("block.Drop.Ore.Gold.Max"));
					blk.getLocation().getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.GOLDEN_APPLE, dropCount));
				}
			} else if (blkMat == Material.IRON_ORE) {
				Bukkit.getServer().broadcastMessage("rnd extra iron");
				if (rndChance <= config.getInt("block.Drop.Ore.Iron.Chance")) {
					int dropCount = ThreadLocalRandom.current().nextInt(config.getInt("block.Drop.Ore.Iron.Min"), config.getInt("block.Drop.Ore.Iron.Max"));
					blk.getLocation().getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.STONE_BRICKS, dropCount));
				}
			}
		}
	}
	
	//new block durability
	@EventHandler
	public void onBlockMining (PlayerInteractEvent event) {
		Helpers hp = new Helpers();
		Player player = event.getPlayer();
		Block blk = hp.getTargetBlock(player, 6);
		Material blkMat = blk.getType();
		
		//player.sendMessage("PlayerInteractEvent " + blkMat.name());
		
		if (blkMat == Material.ACACIA_STAIRS) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 3));
		} else {
			player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
		}
	}
	
	@EventHandler
	public void playerAttack(EntityDamageByEntityEvent event) {
		Helpers hp = new Helpers();
		
		if (hp.playerInBounds(Bukkit.getPlayer("Guerkchen385"), 0.0, 0.0, 20.0)) {
			Bukkit.broadcastMessage("Hey! Warum greifst du mich an?");
		}
	}

}
