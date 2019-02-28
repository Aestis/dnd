package de.aestis.diamondsanddeath;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
}
