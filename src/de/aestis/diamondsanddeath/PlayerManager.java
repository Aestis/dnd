package de.aestis.diamondsanddeath;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerManager {
	
	FileConfiguration Players;
	File PlayersYml = new File(Main.instance.getDataFolder() + "/players.yml");
	
	
	public PlayerManager() {
        if (!PlayersYml.exists()) {
            try {
            	PlayersYml.createNewFile();
            }  catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
		
		Players = new YamlConfiguration();
	    loadConfig();
	}
	
	private void saveConfig() {
		try {
			Players.save(PlayersYml);
			Players.load(PlayersYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	private void loadConfig() {
		try {
			Players.load(PlayersYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	
	public void joinTeam(String playerName, String teamName, Integer type) {
		Players.set("Players." + playerName + ".Team", teamName);
		Players.set("Players." + playerName + ".JoinedDate", Calendar.getInstance().getTime());
		if (type == 1) {
			Players.set("Players." + playerName + ".IsLeader", true);
		} else {
			Players.set("Players." + playerName + ".IsLeader", false);
		}
		saveConfig();
	}
	
	public boolean leaveTeam(String playerName) {
		TeamManager tm = new TeamManager();
		String teamName = getTeam(playerName);
		
		if (playerName.contains(tm.getTeamLeader(teamName))) tm.unregisterTeam(teamName);
		Players.set("Players." + playerName + ".Team", null);
		saveConfig();
		return true;
	}
	
	public boolean hasTeam(String playerName) {
		if (Players.get("Players." + playerName + ".Team") != null) return true;
		return false;
	}
	
	public String getTeam(String playerName) {
		if (hasTeam(playerName)) {
			return Players.getString("Players." + playerName + ".Team");
		}
		return null;
	}
	
	public boolean isTeamLeader(String playerName) {
		if (Players.getBoolean("Players." + playerName + ".IsLeader")) return true;
		return false;
	}
	
	public void addKill(String playerName, String killType) {
		String ymlKills = "Players." + playerName + ".Kills." + killType;
		
		Players.set(ymlKills, (Players.getInt(ymlKills) + 1));
		saveConfig();
	}
	
	public String getKills(String playerName, String killType) {
		String ymlKills = "Players." + playerName + ".Kills." + killType;
		
		if (Players.getString(ymlKills) != null) return Players.getString(ymlKills);
		return "0";
	}
	
}
