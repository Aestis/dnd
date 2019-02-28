package de.aestis.diamondsanddeath;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

public class Helpers {
	
	
	public int getRndInt(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		Random r = new Random();
		
		return r.nextInt((max - min) + 1) + min;
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
}
