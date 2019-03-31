package de.aestis.diamondsanddeath.monuments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.aestis.diamondsanddeath.Helpers;
import de.aestis.diamondsanddeath.Main;
import de.aestis.diamondsanddeath.StructureManager;
import de.aestis.diamondsanddeath.Team;
import de.aestis.diamondsanddeath.TeamManager;
import de.aestis.diamondsanddeath.monuments.Monument.Effect;

public class MonumentManager {

	private FileConfiguration config;
	private File monumentYml = new File(Main.instance.getDataFolder() + "/config/structures.yml");
	private List<Monument> monuments = new ArrayList<>();
	private static MonumentManager instance;

	private MonumentManager() {
		if (!monumentYml.exists()) {
			try {
				monumentYml.createNewFile();
			}  catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		config = new YamlConfiguration();
		loadConfig();
		saveDefaults();

		if (config.getString("Monuments." + Effect.HEALTH.name() + ".World") == null) {
			generateMonuments();
		}
		
		for (Effect effect : Monument.Effect.values()) {
			World w = Bukkit.getWorld(config.getString("Monuments." + effect.name() + ".World"));
			int x = config.getInt("Monuments." + effect.name() + ".Location.X");
			int y = config.getInt("Monuments." + effect.name() + ".Location.X");
			int z = config.getInt("Monuments." + effect.name() + ".Location.X");
			int level = config.getInt("Monuments." + effect.name() + ".Level");
			Location loc = new Location(w,x,y,z);
			Team team = TeamManager.getInstance().getTeamByName(config.getString("Monuments." + effect.name() + ".Owner"));
			
			monuments.add(new Monument(level, team, effect, loc, config.getInt("General.Size")));
		}
	}

	public static MonumentManager getInstance() {
		if (instance == null) {
			instance = new MonumentManager();
		}
		return instance;
	}

	public void generateMonuments() {
		for (Effect effect : Monument.Effect.values()) {
			Location loc = Helpers.randomLocation();
			config.set("Monuments." + effect.name() + ".World",loc.getWorld().getName());
			config.set("Monuments." + effect.name() + ".Location.X", loc.getBlockX());
			config.set("Monuments." + effect.name() + ".Location.X", loc.getBlockY());
			config.set("Monuments." + effect.name() + ".Location.X", loc.getBlockZ());
			config.set("Monuments." + effect.name() + ".Level", 1);
			Team team = TeamManager.getInstance().getTeamByName(config.getString("Monuments." + effect.name() + ".Owner"));
			
			saveConfig();
			StructureManager.getInstance().pasteStructure("schem", "BeaconLV1", loc);
			monuments.add(new Monument(1, team, effect, loc, config.getInt("General.Size")));
		}
		
	}
	
	private void saveDefaults() {
		config.addDefault("General.Size", "13");
		config.addDefault("General.CrystalPos.Level4.Y", "5");
		config.addDefault("General.CrystalPos.Level4.X", "-6");
		config.addDefault("General.CrystalPos.Level4.Z", "6");
		config.addDefault("General.CrystalPos.Level3.Y", "4");
		config.addDefault("General.CrystalPos.Level3.X", "-6");
		config.addDefault("General.CrystalPos.Level3.Z", "6");
		config.addDefault("General.CrystalPos.Level3.Y", "4");
		config.addDefault("General.CrystalPos.Level2.X", "-6");
		config.addDefault("General.CrystalPos.Level2.Z", "6");
		config.addDefault("General.CrystalPos.Level2.Y", "4");
		config.addDefault("General.CrystalPos.Level1.X", "-6");
		config.addDefault("General.CrystalPos.Level1.Z", "6");

		try {
			config.options().copyDefaults(true);
			monumentYml.getParentFile().mkdirs();
			config.save(monumentYml);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveConfig() {
		try {
			config.save(monumentYml);
			config.load(monumentYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}

	private void loadConfig() {
		try {
			config.load(monumentYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}

}
