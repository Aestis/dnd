package de.aestis.diamondsanddeath;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Helpers {
	
	
	public boolean isInventoryEmpty(Player player){
		for(ItemStack item : player.getInventory().getContents()) {
			if(item != null) return false;
		}
		return true;
	}
}
