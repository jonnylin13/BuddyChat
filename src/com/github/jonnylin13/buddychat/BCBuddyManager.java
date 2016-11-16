package com.github.jonnylin13.buddychat;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BCBuddyManager implements Listener {
	
	private BuddyChatPlugin plugin;
	
	private ArrayList<BCBuddy> cPlayers;
	
	public BCBuddyManager(BuddyChatPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		cPlayers = new ArrayList<BCBuddy>();
	}
	
	
	public ArrayList<BCBuddy> getCPlayers() {
		return this.cPlayers;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		BCBuddy cb = new BCBuddy(this.plugin, p.getUniqueId());
		if (!cPlayers.contains(cb)) {
			cPlayers.add(cb);
			if (this.plugin.getDebugEnabled()) {
				System.out.println(cb.getUUID() + " joined, cPlayer created.");
			}
		}
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		BCBuddy cb = getCPlayer(p);
		if (cPlayers.contains(cb)) {
			cPlayers.remove(cb);
		}
	}
	
	public BCBuddy getCPlayer(Player p) {
		for (BCBuddy cp : this.getCPlayers()) {
			if (cp.getPlayer().getUniqueId() == p.getUniqueId()) {
				return cp;
			}
		}
		System.out.println("Could not find CPlayer from UUID: ");
		System.out.println(p.getUniqueId());
		return null;
	}

}
