package de.aestis.diamondsanddeath;

import org.bukkit.plugin.java.JavaPlugin;

import de.aestis.diamondsanddeath.CommandManager;

import java.sql.*;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;


public class Main extends JavaPlugin {
	
	public static Main instance;
	
	private String host, dbname, user, pass;
	private Integer port;
	public Connection connection;
	
	
	public void onEnable() {
		
		//init instance
		instance=this;
		
		//establish connection
		host = "localhost";
        port = 3306;
        dbname = "test";
        user = "root";
        pass = "";
        
        try {     
            openConnection();
            Statement statement = connection.createStatement();          
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
		//init configs, import cmdmgr, evtlistener, etc
		try {
			setupConfigs();
			getServer().getPluginManager().registerEvents((Listener) new EventListener(), this);
			getCommand("dd").setExecutor((CommandExecutor) new CommandManager());
		} catch (Exception ex) {
			System.out.println("Error whilst enabling: " + ex);
			return;
		}
		
		System.out.println("Diamonds and Death Reloaded sucessfully enabled.");
	}
	public void onDisable() {
		System.out.println("Diamonds and Death Reloaded disabled.");
	}
	
	
	private void setupConfigs() {
		
		//create config.yml if not exist
        FileConfiguration config = getConfig();
        
        if (!config.isSet("buyinCost")) {config.set("buyinCost", 1500);}
        if (!config.isSet("maxTeamsize")) {config.set("maxTeamsize", 4);}
        if (!config.isSet("spawn.World")) {config.set("spawn.World", "world");}
        if (!config.isSet("spawn.X")) {config.set("spawn.X", "0");}
        if (!config.isSet("spawn.Y")) {config.set("spawn.Y", "90");}
        if (!config.isSet("spawn.Z")) {config.set("spawn.Z", "0");}
        if (!config.isSet("entity.Drop.Exp.Normal")) {config.set("entity.Drop.Exp.Normal", false);}
        if (!config.isSet("entity.Drop.Exp.Rnd.Chance")) {config.set("entity.Drop.Exp.Rnd.Chance", 10);}
        if (!config.isSet("entity.Drop.Exp.Rnd.Min")) {config.set("entity.Drop.Exp.Rnd.Min", 1);}
        if (!config.isSet("entity.Drop.Exp.Rnd.Max")) {config.set("entity.Drop.Exp.Rnd.Max", 7);}
        
        saveConfig();
    }
	
	
	public void openConnection() throws SQLException, ClassNotFoundException {
	    if (connection != null && !connection.isClosed()) {
	    	System.out.println("Connection to database already established.");
	        return;
	    }
	 
	    synchronized (this) {
	        if (connection != null && !connection.isClosed()) {
	            return;
	        } 
	        Class.forName("com.mysql.jdbc.Driver");
	        connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.dbname, this.user, this.pass);
	        System.out.println("Connection to database successfully established!");
	    }
	}

}