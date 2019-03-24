package de.aestis.diamondsanddeath;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerManager {
	
	FileConfiguration players;
	File playersYml = new File(Main.instance.getDataFolder() + "/players.yml");
	private static PlayerManager instance;
	
	private PlayerManager() {
        if (!playersYml.exists()) {
            try {
            	playersYml.createNewFile();
            }  catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
		
		players = new YamlConfiguration();
	    loadConfig();
	}
	
	public static PlayerManager getInstance() {
		if (instance == null) {
			instance = new PlayerManager();
		}
		return instance;
	}
	
	private void saveConfig() {
		try {
			players.save(playersYml);
			players.load(playersYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	private void loadConfig() {
		try {
			players.load(playersYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	
	public void joinTeam(String playerName, String teamName, Integer type) {
		players.set("Players." + playerName + ".Team", teamName);
		players.set("Players." + playerName + ".JoinedDate", Calendar.getInstance().getTime());
		if (type == 1) {
			players.set("Players." + playerName + ".IsLeader", true);
		} else {
			players.set("Players." + playerName + ".IsLeader", false);
		}
		saveConfig();
	}
	
	public boolean leaveTeam(String playerName) {
		TeamManager tm = TeamManager.getInstance();
		String teamName = getTeam(playerName);
		
		if (playerName.contains(tm.getTeamLeader(teamName))) tm.unregisterTeam(teamName);
		players.set("Players." + playerName + ".Team", null);
		saveConfig();
		return true;
	}
	
	public boolean hasTeam(String playerName) {
		if (players.get("Players." + playerName + ".Team") != null) return true;
		return false;
	}
	
	public String getTeam(String playerName) {
		if (hasTeam(playerName)) {
			return players.getString("Players." + playerName + ".Team");
		}
		return null;
	}
	
	public boolean isTeamLeader(String playerName) {
		if (players.getBoolean("Players." + playerName + ".IsLeader")) return true;
		return false;
	}
	
	public void addKill(String playerName, String killType) {
		String ymlKills = "Players." + playerName + ".Kills." + killType;
		
		players.set(ymlKills, (players.getInt(ymlKills) + 1));
		saveConfig();
	}
	
	public String getKills(String playerName, String killType) {
		String ymlKills = "Players." + playerName + ".Kills." + killType;
		
		if (players.getString(ymlKills) != null) return players.getString(ymlKills);
		return "0";
	}
	
}
