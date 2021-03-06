package com.github.jonnylin13.buddychat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.jonnylin13.buddychat.BCBuddy.Channel;

public class BCChatListener implements Listener {
	
	private BuddyChatPlugin plugin;
	
	public BCChatListener(BuddyChatPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void sendMessage(Player from, String message, Player to, String type) {
		if (type.equals("w")) {
			message = ChatColor.GRAY + message;
			from.sendMessage(ChatColor.WHITE + "<" + from.getDisplayName() + "->" + to.getDisplayName() + ChatColor.WHITE + "> " 
					+ message);
			to.sendMessage(ChatColor.WHITE + "<" + from.getDisplayName() + "->" + to.getDisplayName() + ChatColor.WHITE + "> " 
					+ message);
			return;
		} else if (type.equals("b")) {
			message = ChatColor.AQUA + message;
		}
		to.sendMessage(ChatColor.WHITE + "<" + from.getDisplayName() + ChatColor.WHITE +  "> " + message);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		System.out.println("<" + event.getPlayer().getDisplayName() + "> " + event.getMessage());
		if (!this.plugin.getCBuddyManager().getCPlayer(event.getPlayer()).getMuted().contains(event.getPlayer().getUniqueId())) {
			sendMessage(event.getPlayer(), event.getMessage(), event.getPlayer(), "");
		}
		for(Player p : this.plugin.getServer().getOnlinePlayers()) {
			BCBuddy from = this.plugin.getCBuddyManager().getCPlayer(event.getPlayer());
			BCBuddy to = this.plugin.getCBuddyManager().getCPlayer(p);
			if (from.getUUID() == to.getUUID()) {
				continue;
			}
			if (to.getMuted().contains(from.getUUID())) {
				continue;
			}
			if (from.getChannel() == Channel.GENERAL) {
				if (to.getChannel() == Channel.GENERAL) {
					sendMessage(event.getPlayer(), event.getMessage(), p, "");
				}
			} else if (from.getChannel() == Channel.BUDDY) {
				if (to.getChannel() == Channel.BUDDY) {
					if (from.getBuddies().contains(to.getUUID()) && to.getBuddies().contains(from.getUUID())) {
						sendMessage(event.getPlayer(), event.getMessage(), p, "");
					}
				} else {
					// Friend is in general, send him a buddy message
					sendMessage(event.getPlayer(), event.getMessage(), p, "b");
				}
			}
		}
	}
}
