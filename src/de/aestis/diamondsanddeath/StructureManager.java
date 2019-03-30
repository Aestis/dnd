package de.aestis.diamondsanddeath;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSession.Stage;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.util.io.file.FilenameException;

public class StructureManager {

	FileConfiguration structures;
	File structuresYml = new File(Main.instance.getDataFolder() + "/config/structures.yml");
	WorldEdit worldEdit = WorldEdit.getInstance();
	private static StructureManager instance;

	private StructureManager() {
		if (!structuresYml.exists()) {
			try {
				structuresYml.createNewFile();
			}  catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		structures = new YamlConfiguration();
		loadConfig();
		saveDefaults();
		copyStructures();

	}

	public static StructureManager getInstance() {
		if (instance == null) {
			instance = new StructureManager();
		}
		return instance;
	}

	private void copyStructures() {
		Main.instance.saveResource("recources/BeaconLV1.schem", false);
	}
	
	private void saveDefaults() {
		structures.addDefault("Size", "13");
		structures.addDefault("CrystalPos.Level4.Y", "5");
		structures.addDefault("CrystalPos.Level4.X", "-6");
		structures.addDefault("CrystalPos.Level4.Z", "6");
		structures.addDefault("CrystalPos.Level3.Y", "4");
		structures.addDefault("CrystalPos.Level3.X", "-6");
		structures.addDefault("CrystalPos.Level3.Z", "6");
		structures.addDefault("CrystalPos.Level3.Y", "4");
		structures.addDefault("CrystalPos.Level2.X", "-6");
		structures.addDefault("CrystalPos.Level2.Z", "6");
		structures.addDefault("CrystalPos.Level2.Y", "4");
		structures.addDefault("CrystalPos.Level1.X", "-6");
		structures.addDefault("CrystalPos.Level1.Z", "6");

		try {
			structures.options().copyDefaults(true);
			structuresYml.getParentFile().mkdirs();
			structures.save(structuresYml);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveConfig() {
		try {
			structures.save(structuresYml);
			structures.load(structuresYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}

	private void loadConfig() {
		try {
			structures.load(structuresYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}

	public boolean pasteStructure(String formatName, String filename,  BlockVector3 toPos, World world) {

		File f = null;
		try {
			f = worldEdit.getSafeOpenFile(null, new File(Main.instance.getDataFolder() + "/recources"), filename, BuiltInClipboardFormat.SPONGE_SCHEMATIC.getPrimaryFileExtension(), ClipboardFormats.getFileExtensionArray());
		} catch (FilenameException e1) {

			e1.printStackTrace();
			return false;
		}

		if (!f.exists()) {
			Main.instance.getLogger().warning("Schematic " + filename + " does not exist!");
			return false;
		}

		ClipboardFormat format = ClipboardFormats.findByFile(f);
		if (format == null) {
			format = ClipboardFormats.findByAlias(formatName);
		}
		if (format == null) {
			Main.instance.getLogger().warning("Unknown schematic format: " + formatName);
			return false;
		}

		try (Closer closer = Closer.create()) {
			FileInputStream fis = closer.register(new FileInputStream(f));
			BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
			ClipboardReader reader = closer.register(format.getReader(bis));

			Clipboard clipboard = reader.read();
			ClipboardHolder holder = new ClipboardHolder(clipboard);
			Constructor<?> constructor = null;
			try {
				constructor = EditSession.class.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e1) {
				e1.printStackTrace();
			}
			constructor.setAccessible(true);
			EditSession editSession = null;
			try {
				editSession = (EditSession) constructor.newInstance(getWorldEdit().getWorldEdit().getEventBus(),new BukkitWorld(world), 100000 , null, new EditSessionEvent(new BukkitWorld(world), null, 100000,Stage.BEFORE_CHANGE));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
					//new EditSession(getWorldEdit().getWorldEdit().getEventBus(),new BukkitWorld(world), 100000 , null, new EditSessionEvent(new BukkitWorld(world), null, 100000,Stage.BEFORE_CHANGE) );

			Operation operation = holder
					.createPaste(editSession)
					.to(toPos)
					.ignoreAirBlocks(false)
					.build();
			Operations.completeLegacy(operation);

			return true;
		} catch (IOException e) {
			Main.instance.getLogger().warning("Schematic could not read or it does not exist: " + e.getMessage());
		} catch (MaxChangedBlocksException e) {
			Main.instance.getLogger().warning("Schematic is too big: " + e.getMessage());
		}
		return false;
	}
	
	 private WorldEditPlugin getWorldEdit() {
	        Plugin p = (Plugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
	        if (p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
	        else return null;
	    }

}
