package de.aestis.diamondsanddeath;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.aestis.diamondsanddeath.monuments.MonumentManager;


public class TeamManager {

	FileConfiguration Teams;
	File TeamsYml = new File(Main.instance.getDataFolder() + "/teams.yml");
	

	public TeamManager() {
        if (!TeamsYml.exists()) {
            try {
            	TeamsYml.createNewFile();
            }  catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
		
		Teams = new YamlConfiguration();
	    loadConfig();
	}
	
	public static TeamManager getInstance() {
		return null;
	}
	
	private void saveConfig() {
		try {
			Teams.save(TeamsYml);
			Teams.load(TeamsYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	private void loadConfig() {
		try {
			Teams.load(TeamsYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}

	
	public boolean registerTeam(String teamName, String password, String playerName) {
		PlayerManager pm = new PlayerManager();
		
		if (!Teams.isSet("Teams." + teamName)) {
			Teams.set("Teams." + teamName + ".Leader", playerName);
			Teams.set("Teams." + teamName + ".Password", password);
			Teams.set("Teams." + teamName + ".Created", Calendar.getInstance().getTime());
			pm.joinTeam(playerName, teamName, 1);
		} else {
			return false;
		}
		saveConfig();
		
		return true;
	}
	
	public void unregisterTeam(String teamName) {
		Teams.set("Teams." + teamName, null);
		saveConfig();
	}
	
	public String getTeamLeader(String teamName) {
		return Teams.getString("Teams." + teamName + ".Leader");
	}
	
	public Team getTeamByName(String teamName) {
		//TODO Team zurückgeben
		return null;
	}
	
	public void setClaim(String teamName, Location location) {
		Teams.set("Teams." + teamName + ".Claim.X", location.getBlockX());
		Teams.set("Teams." + teamName + ".Claim.Y", location.getBlockY());
		Teams.set("Teams." + teamName + ".Claim.Z", location.getBlockZ());
		saveConfig();
	}
	
	public Location getClaim(String teamName) {
		if (!Teams.isSet("Teams." + teamName + ".Claim")) return null;
		
		Double locX = Teams.getDouble("Teams." + teamName + ".Claim.X");
		Double locY = Teams.getDouble("Teams." + teamName + ".Claim.Y");
		Double locZ = Teams.getDouble("Teams." + teamName + ".Claim.Z");
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
