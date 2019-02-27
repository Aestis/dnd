package de.aestis.diamondsanddeath;

import java.sql.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.aestis.diamondsanddeath.Main;
import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {
	
	Connection conn = Main.instance.connection;
	
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] argArr) {
		
		TeamManager tm = new TeamManager();
		PlayerManager pm = new PlayerManager();
		
		if (cmd.getName().equalsIgnoreCase("dd")) {
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
			
			if (argArr[0].equalsIgnoreCase("leave")) {
				
				//is currently in a team?
				if (pm.hasTeam(sender.getName())) {
					String teamName = pm.getTeam(sender.getName());
					
					if (pm.leaveTeam(sender.getName())) {
						sender.sendMessage("Du bist dem Team " + ChatColor.YELLOW + teamName + ChatColor.WHITE + " ausgetreten!");
					} else {
						sender.sendMessage("Error #003.1");
					}
				} else {
					sender.sendMessage("Du gehörst momentan keinem Team an.");
				}
			}
		}
		return false;
	}
}
