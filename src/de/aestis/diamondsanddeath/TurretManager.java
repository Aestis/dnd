package de.aestis.diamondsanddeath;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TurretManager {

	FileConfiguration Turrets;
	File TurretYml = new File(Main.instance.getDataFolder() + "/turrets.yml");
	private static TurretManager instance;

	private TurretManager() {
        if (!TurretYml.exists()) {
            try {
            	TurretYml.createNewFile();
            }  catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
		
        Turrets = new YamlConfiguration();
	    loadConfig();
	    
	    spawnTurrets();
	}
	
	public static TurretManager getInstance() {
		if (instance == null) {
			instance = new TurretManager();
		}
		return instance;
	}
	
	private void saveConfig() {
		try {
			Turrets.save(TurretYml);
			Turrets.load(TurretYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	private void loadConfig() {
		try {
			Turrets.load(TurretYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	private void spawnTurrets() {
		Set<String> arr = Turrets.getKeys(true);
		System.out.print(arr);
	}
	
	
	public boolean createTurret(Player player, String teamName) {
		Helpers hp = new Helpers();
		String tID = hp.md5Hash(Calendar.getInstance().getTime().toString());
		
		Turrets.set("Turrets." + teamName + ".Turret" + tID + ".ID", tID);
		Turrets.set("Turrets." + teamName + ".Turret" + tID + ".Position.X", player.getLocation().getBlockX());
		Turrets.set("Turrets." + teamName + ".Turret" + tID + ".Position.Y", player.getLocation().getBlockY());
		Turrets.set("Turrets." + teamName + ".Turret" + tID + ".Position.Z", player.getLocation().getBlockZ());
		
		saveConfig();
		return true;
	}
}
