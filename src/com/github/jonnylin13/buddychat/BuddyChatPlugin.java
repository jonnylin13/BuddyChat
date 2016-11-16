package com.github.jonnylin13.buddychat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BuddyChatPlugin extends JavaPlugin {
	
	private FileConfiguration config;
	private BCDatabase cDB;
	private BCBuddyManager cbManager;
	private BCCommand cCommand;
	private BCChatListener ccListener;
	
	private boolean buddiesEnabled;
	private boolean debugMode;
	
	public void onEnable() {
		
		config = getConfig();
		
		checkConfig();
		loadConfig();
		
		cDB = new BCDatabase(this);
		cbManager = new BCBuddyManager(this);
		cCommand = new BCCommand(this);
		ccListener = new BCChatListener(this);
		
		System.out.println(this.getDescription().getName() + " v" + this.getDescription().getVersion()
				+ " has been enabled!");
	}
	
	public void onDisable() {
		
		System.out.println(this.getDescription().getName() + " v" + this.getDescription().getVersion()
				+ " has been disabled!");
	}
	
	public void checkConfig() {
		try {
			config.addDefault("BuddiesEnabled", true);
			config.addDefault("DebugMode", false);
			config.options().copyDefaults(true);
			saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Config defaults could not be saved!");
		}
		
	}
	
	public void loadConfig() {
		try {
			this.buddiesEnabled = config.getBoolean("BuddiesEnabled");
			this.debugMode = config.getBoolean("DebugMode");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Config could not be loaded!");
		}
	}
	
	public BCDatabase getCDatabase() {
		return this.cDB;
	}
	
	public boolean getBuddiesEnabled() {
		return this.buddiesEnabled;
	}
	
	public BCBuddyManager getCBuddyManager() {
		return this.cbManager;
	}
	
	public BCCommand getCCommand() {
		return this.cCommand;
	}
	
	public BCChatListener getCCListener() {
		return this.ccListener;
	}
	
	public boolean getDebugEnabled() {
		return this.debugMode;
	}

}
