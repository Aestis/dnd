package de.aestis.diamondsanddeath;

import java.sql.*;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;

import com.sk89q.worldedit.math.BlockVector3;

import de.aestis.diamondsanddeath.Main;
import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {
	
	Connection conn = Main.instance.connection;
	
	TeamManager tm = new TeamManager();
	PlayerManager pm = new PlayerManager();
	TurretManager tr = new TurretManager();
	GameEvents ge = new GameEvents();
	
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] argArr) {

		if (cmd.getName().equalsIgnoreCase("dd")) {
			if (argArr[0].equalsIgnoreCase("test")) {
				StructureManager.getInstance().pasteStructure("schem", "BeaconLV1", BlockVector3.at(((Player)sender).getLocation().getBlockX(),((Player)sender).getLocation().getBlockY(),((Player)sender).getLocation().getBlockZ()), ((Player)sender).getWorld());
			}
			if (argArr[0].equalsIgnoreCase("team")) {
				
				//show team info
				String currentTeam = pm.getTeam(sender.getName());
				
				if (currentTeam != null) {
					sender.sendMessage(ChatColor.GREEN + "Du bist derzeit im Team " + ChatColor.YELLOW + currentTeam);
					sender.sendMessage(ChatColor.GRAY + "Teamleader:   " + ChatColor.WHITE + "" + tm.getTeamLeader(currentTeam));
				} else {
					sender.sendMessage("Du gehörst derzeit keinem Team an. Trete einem bestehenden Team per /dd join <name> bei oder gründe dein Eigenes um das Spiel zu starten.");
				}
			}
			
			if (argArr[0].equalsIgnoreCase("info")) {
				
				//show player info
				sender.sendMessage(ChatColor.GREEN + "Deine Alltime-Statistiken");
				sender.sendMessage(ChatColor.GRAY + "Mob Kills:      " + pm.getKills(sender.getName(), "Mob"));
				sender.sendMessage(ChatColor.GRAY + "Spieler Kills:  " + pm.getKills(sender.getName(), "Player"));
			}
			
			if (argArr[0].equalsIgnoreCase("create")) {
				
				//is currently in a team?
				if (!pm.hasTeam(sender.getName())) {
					//team already existing?
					if (tm.registerTeam(argArr[1], argArr[2], sender.getName())) {
						sender.sendMessage("Team " + ChatColor.YELLOW + argArr[1] + ChatColor.WHITE + " erfolgreich erstellt!");
					} else {
						sender.sendMessage("Das Team gibt es bereits.");
					}
				} else {
					sender.sendMessage("Du bist bereits Mitglied eines Teams.");
				}
			}
			
			if (argArr[0].equalsIgnoreCase("join")) {
				
				//is currently in a team?
				if (!pm.hasTeam(sender.getName())) {
					//team already existing?
					pm.joinTeam(sender.getName(), argArr[1], 0);
					sender.sendMessage("Team " + ChatColor.YELLOW + argArr[1] + ChatColor.WHITE + " erfolgreich beigetreten!");
				} else {
					sender.sendMessage("Du bist bereits Mitglied eines Teams.");
				}
			}
			
			if (argArr[0].equalsIgnoreCase("leave")) {
				
				//is currently in a team?
				if (pm.hasTeam(sender.getName())) {
					String teamName = pm.getTeam(sender.getName());
					
					if (pm.leaveTeam(sender.getName())) sender.sendMessage("Du bist dem Team " + ChatColor.YELLOW + teamName + ChatColor.WHITE + " ausgetreten!");
				} else {
					sender.sendMessage("Du gehörst momentan keinem Team an.");
				}
			}
			
			if (argArr[0].equalsIgnoreCase("setclaim")) {
				
				//abfragen noch hier rein machen
				if (pm.hasTeam(sender.getName())) {
					if (pm.isTeamLeader(sender.getName())) {
						tm.setClaim(pm.getTeam(sender.getName()), Bukkit.getPlayer(sender.getName()).getLocation());
					} else {
						sender.sendMessage("Du hast hierfür keine Berechtigung!");
					}
				} else {
					sender.sendMessage("Du gehörst momentan keinem Team an.");
				}
			}
			
			if (argArr[0].equalsIgnoreCase("getclaim")) {
				
				//abfragen noch hier rein machen
				if (pm.hasTeam(sender.getName())) {
					if (pm.isTeamLeader(sender.getName())) {
						Location claimMiddle = tm.getClaim(pm.getTeam(sender.getName()));
						
					} else {
						sender.sendMessage("Du hast hierfür keine Berechtigung!");
					}
				} else {
					sender.sendMessage("Du gehörst momentan keinem Team an.");
				}
			}
			
			if (argArr[0].equalsIgnoreCase("buyturret")) {
				
				//abfragen noch hier rein machen
				if (pm.hasTeam(sender.getName())) {
					if (pm.isTeamLeader(sender.getName())) {
						if (tr.createTurret(Bukkit.getPlayer(sender.getName()), pm.getTeam(sender.getName()))) sender.sendMessage("Turret erfolgreich gekauft!");
					} else {
						sender.sendMessage("Du hast hierfür keine Berechtigung!");
					}
				} else {
					sender.sendMessage("Du gehörst momentan keinem Team an.");
				}
			}
			
			if (argArr[0].equalsIgnoreCase("boss")) {
				
				Entity boss = ge.spawnBoss(argArr[1]);
				if (boss != null) {
					Bukkit.broadcastMessage(ChatColor.DARK_RED + boss.getCustomName() + ChatColor.WHITE + " ist erschienen!");
				}
			}
		}
		return false;
	}
}
