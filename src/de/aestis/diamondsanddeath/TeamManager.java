package de.aestis.diamondsanddeath;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class TeamManager {

	FileConfiguration teams;
	File teamsYml = new File(Main.instance.getDataFolder() + "/teams.yml");
	private static TeamManager instance;

	private TeamManager() {
        if (!teamsYml.exists()) {
            try {
            	teamsYml.createNewFile();
            }  catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
		
		teams = new YamlConfiguration();
	    loadConfig();
	}
	
	public static TeamManager getInstance() {
		if (instance == null) {
			instance = new TeamManager();
		}
		return instance;
	}
	
	private void saveConfig() {
		try {
			teams.save(teamsYml);
			teams.load(teamsYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	private void loadConfig() {
		try {
			teams.load(teamsYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}

	
	public boolean registerTeam(String teamName, String password, String playerName) {
		PlayerManager pm = PlayerManager.getInstance();
		
		if (!teams.isSet("Teams." + teamName)) {
			teams.set("Teams." + teamName + ".Leader", playerName);
			teams.set("Teams." + teamName + ".Password", password);
			teams.set("Teams." + teamName + ".Created", Calendar.getInstance().getTime());
			pm.joinTeam(playerName, teamName, 1);
		} else {
			return false;
		}
		saveConfig();
		
		return true;
	}
	
	public void unregisterTeam(String teamName) {
		teams.set("Teams." + teamName, null);
		saveConfig();
	}
	
	public String getTeamLeader(String teamName) {
		return teams.getString("Teams." + teamName + ".Leader");
	}
	
	public void setClaim(String teamName, Location location) {
		teams.set("Teams." + teamName + ".Claim.X", location.getBlockX());
		teams.set("Teams." + teamName + ".Claim.Y", location.getBlockY());
		teams.set("Teams." + teamName + ".Claim.Z", location.getBlockZ());
		saveConfig();
	}
	
	public Location getClaim(String teamName) {
		if (!teams.isSet("Teams." + teamName + ".Claim")) return null;
		
		Double locX = teams.getDouble("Teams." + teamName + ".Claim.X");
		Double locY = teams.getDouble("Teams." + teamName + ".Claim.Y");
		Double locZ = teams.getDouble("Teams." + teamName + ".Claim.Z");
		Location loc = new Location(Bukkit.getWorld("world"), locX, locY, locZ);
		
		return loc;
	}
	
	//public boolean hasTeam(String playerName) {
		//try {	
			//Statement stmt = conn.createStatement();
			//ResultSet rs = stmt.executeQuery("SELECT * FROM teams WHERE leader = " + PlayerName + "");
			
			//if (!rs.next()) return false;
			//return true;
		//} catch (SQLException ex) {
		//	System.out.println(ex.getMessage());
		//}
		//return false;
	//}
}
