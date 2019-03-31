package de.aestis.diamondsanddeath.monuments;

import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;

import de.aestis.diamondsanddeath.Team;

public class Monument {
	
	public enum Effect{
		STRENGHT, SPEED, DIGSPEED, HEALTH
	}
	
	private int level;
	private Team owner;
	private Effect effect;
	private Location startPoint;
	private EnderCrystal crystal;
	private int size;
	
	public Monument(int level, Team owner, Effect effect, Location startPoint, int size) {
		this.level = level;
		this.owner = owner;
		this.effect = effect;
		this.startPoint = startPoint;
		this.size = size;
		for (Entity e: startPoint.getWorld().getNearbyEntities(startPoint, size, size, size)) {
			if (e instanceof EnderCrystal) {
				crystal = (EnderCrystal) e;
			}
		}
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public Team getOwner() {
		return owner;
	}
	public void setOwner(Team owner) {
		this.owner = owner;
	}

	public Effect getEffect() {
		return effect;
	}

	public Location getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Location startPoint) {
		this.startPoint = startPoint;
	}

	public EnderCrystal getCrystal() {
		return crystal;
	}

	public int getSize() {
		return size;
	}
}
