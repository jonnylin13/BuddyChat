package com.github.jonnylin13.buddychat;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BCBuddy {
	
	public enum Channel {
		GENERAL, BUDDY;
	}
	
	private BuddyChatPlugin plugin;
	
	private UUID playerUUID;
	private UUID lastWhisperedFrom;
	private String name;
	private ArrayList<UUID> buddies;
	private Channel channel;
	
	public BCBuddy (BuddyChatPlugin plugin, UUID playerUUID) {
		this.playerUUID = playerUUID;
		lastWhisperedFrom = null;
		this.name = this.getPlayer().getName();
		this.plugin = plugin;
		this.loadBuddies();
		this.channel = Channel.GENERAL;
	}
	
	public Channel getNextChannel() {
		if (this.channel.ordinal() + 1 >= Channel.values().length) {
			return Channel.values()[0];
		}
		return Channel.values()[this.channel.ordinal() + 1];
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(this.playerUUID);
	}
	
	public String getName() {
		return this.name;
	}
	
	public BuddyChatPlugin getPlugin() {
		return this.plugin;
	}
	
	public ArrayList<UUID> getBuddies() {
		return this.buddies;
	}
	
	private void loadBuddies() {
		this.buddies = this.plugin.getCDatabase().getBuddies(this.playerUUID);
	}
	
	public void addBuddy(UUID otherUUID) {
		this.buddies.add(otherUUID);
		this.plugin.getCDatabase().storeBuddy(this.playerUUID, otherUUID);
	}
	
	public void removeBuddy(UUID otherUUID) {
		this.buddies.remove(otherUUID);
		this.plugin.getCDatabase().removeBuddy(this.playerUUID, otherUUID);
	}
	
	public UUID getUUID() {
		return this.playerUUID;
	}
	
	public void setLastWhispFrom(UUID uuid) {
		this.lastWhisperedFrom = uuid;
	}
	
	public UUID getLastWhispFrom() {
		return this.lastWhisperedFrom;
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public void setChannel(Channel channel) {
		this.channel = channel;
		this.getPlayer().sendMessage(ChatColor.GREEN + "You are now in channel: " + channel);
	}

}
