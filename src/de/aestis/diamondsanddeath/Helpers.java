package de.aestis.diamondsanddeath;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class Helpers {
	
	
	public int getRndInt(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		Random r = new Random();
		
		return r.nextInt((max - min) + 1) + min;
	}
	
    public String md5Hash(String string) {
        MessageDigest md = null;
        
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        byte[] hashInBytes = md.digest(string.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

	
	public boolean isInventoryEmpty(Player player){
		for(ItemStack item : player.getInventory().getContents()) {
			if(item != null) return false;
		}
		return true;
	}
	
	public final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }
	
	public boolean playerInBounds(Player player, Double locX, Double locZ, Double distance) {
		Location playerLoc = player.getLocation();
		
		if (playerLoc.getBlockX() > locX + distance || playerLoc.getBlockX() < locX - distance) return true;
		if (playerLoc.getBlockZ() > locZ + distance || playerLoc.getBlockZ() < locZ - distance) return true;
		return false;
	}
}
