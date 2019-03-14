package de.aestis.diamondsanddeath;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;


public class GameEvents {
	
	FileConfiguration Events;
	File EventYml = new File(Main.instance.getDataFolder() + "/gameevents.yml");
	

	public GameEvents() {
        if (!EventYml.exists()) {
            try {
            	EventYml.createNewFile();
            }  catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
		
        Events = new YamlConfiguration();
	    loadConfig();
	    
	    if (!Events.isSet("bossMob.enabled")) Events.set("bossMob.enabled", true);
	    saveConfig();
	}
	
	private void saveConfig() {
		try {
			Events.save(EventYml);
			Events.load(EventYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	private void loadConfig() {
		try {
			Events.load(EventYml);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	
	public void spawnAirdrop() {
		
		
	}
	
	public Entity spawnBoss(String bossType) {
		Location loc = new Location(Bukkit.getWorld("world"), 0, 90, 0);
		LivingEntity boss = null;
		LivingEntity rider = null;
		AttributeInstance attr = null;
		
		
		
		switch (bossType) {
			case "RIDER1":
				rider = (LivingEntity) (Bukkit.getWorld("world").spawnEntity(loc, EntityType.WITHER_SKELETON));

				attr = rider.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				attr.setBaseValue(80.0D);
				attr = rider.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
				attr.setBaseValue(0.35D);
				
				EntityEquipment riderEquip = rider.getEquipment();
				riderEquip.setItemInMainHand(new ItemStack(Material.IRON_SWORD, 1));
				riderEquip.setBoots(new ItemStack(Material.IRON_BOOTS, 1));
				riderEquip.setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
				riderEquip.setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
				rider.setHealth(80.0D);
				rider.setCustomName("Skelettkavalerie Lv.1");
				rider.setOp(true);
				
				
				boss = (LivingEntity) (Bukkit.getWorld("world").spawnEntity(loc, EntityType.SKELETON_HORSE));
				boss.addPassenger(rider);
				break;
			case "EFFACER":
				rider = (LivingEntity) (Bukkit.getWorld("world").spawnEntity(loc, EntityType.DROWNED));

				attr = rider.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				attr.setBaseValue(120.0D);
				
				rider.setHealth(120.0D);
				rider.setCustomName("Effacer");
				rider.setOp(true);
				
				
				boss = (LivingEntity) (Bukkit.getWorld("world").spawnEntity(loc, EntityType.PHANTOM));
				
				attr = boss.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				attr.setBaseValue(80.0D);
				attr = boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
				attr.setBaseValue(32.0D);
				
				boss.setHealth(80.0D);
				
				boss.addPassenger(rider);
				break;
			default:
				break;
		}
		
		boss.playEffect(EntityEffect.ENTITY_POOF);
		
		if (rider == null) {
			return boss;
		} else {
			return rider;
		}
	}
}
