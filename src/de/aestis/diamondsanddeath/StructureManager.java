package de.aestis.diamondsanddeath;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.util.io.file.FilenameException;

public class StructureManager {

	WorldEdit worldEdit = WorldEdit.getInstance();
	private static StructureManager instance;

	private StructureManager() {
		
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
	
	public Collection<Chunk> getSurroundingChunks(Chunk c) {
        int[] offset = {-1,0,1};

        World world = c.getWorld();
        int baseX = c.getX();
        int baseZ = c.getZ();

        Collection<Chunk> chunksAroundPlayer = new HashSet<>();
        for(int x : offset) {
            for(int z : offset) {
                Chunk chunk = world.getChunkAt(baseX + x, baseZ + z);
                chunksAroundPlayer.add(chunk);
            }
        } return chunksAroundPlayer;
    }
	
	public void loadChunks(Collection<Chunk> chunks) {
		for (Chunk c: chunks) {
			c.load();
		}
	}
	
	public boolean pasteStructure(String formatName, String filename, Location loc) {

		BlockVector3 toPos = BlockVector3.at(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		Chunk c = loc.getChunk();
		loadChunks(getSurroundingChunks(c));
		
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
			fis.close();
			bis.close();
			reader.close();

			Region region = clipboard.getRegion();
			BlockVector3 from = clipboard.getOrigin();

			ForwardExtentCopy copy = new ForwardExtentCopy(clipboard, region, from, new BukkitWorld(loc.getWorld()), toPos);
			try {
				Operations.complete(copy);
			} catch (WorldEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		} catch (IOException e) {
			Main.instance.getLogger().warning("Schematic could not read or it does not exist: " + e.getMessage());
		}
		return false;
	}

}
