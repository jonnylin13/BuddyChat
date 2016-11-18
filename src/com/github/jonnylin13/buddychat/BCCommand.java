package com.github.jonnylin13.buddychat;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BCCommand implements CommandExecutor {
	
	public String[][] pages = {{"/bc list" + ChatColor.GRAY + " - Look at friends list",
		"/bc add <friend> " + ChatColor.GRAY + " - To add a friend",
		"/bc remove <friend>" + ChatColor.GRAY + " - Remove a friend",
		"/bc mute <player>" + ChatColor.GRAY + " - To mute a player",
		"/bc help <page>" + ChatColor.GRAY + " - For more help",
		"/cc" + ChatColor.GRAY + " - To change channels"},
			{"/r or /reply <message> " + ChatColor.GRAY + " - To send a reply",
			"/w or /whisper <friend> <message>" + ChatColor.GRAY + " - Whisper a friend"}};

	private BuddyChatPlugin plugin;
	
	public BCCommand(BuddyChatPlugin plugin) {
		
		plugin.getCommand("bc").setExecutor(this);
		plugin.getCommand("r").setExecutor(this);
		plugin.getCommand("reply").setExecutor(this);
		plugin.getCommand("whisper").setExecutor(this);
		plugin.getCommand("w").setExecutor(this);
		plugin.getCommand("cc").setExecutor(this);
		
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("bc")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				BCBuddy cPlayer = this.plugin.getCBuddyManager().getCPlayer(p);
				try {
					if (args.length < 1) {
						p.performCommand("bc help");
						return true;
					}
					String command = args[0];
					if (command.equalsIgnoreCase("help")) {
						int page = 1;
						if (args.length == 2) {
							try {
								page = Integer.parseInt(args[1]);
							} catch (Exception e) {
								p.sendMessage(ChatColor.RED + "Was that a number?");
								return true;
							}
						}
						int index = page - 1;
						p.sendMessage(ChatColor.GRAY + "-- BuddyChat Help (" + page + "/" + pages.length + ") --");
						for (int i = 0; i < pages[index].length; i++) {
							p.sendMessage(pages[index][i]);
						}
						
						return true;
					} else if (command.equalsIgnoreCase("add")) {
						if (!this.plugin.getBuddiesEnabled()) {
							sender.sendMessage(ChatColor.RED + "Buddies have been disabled!");
							return true;
						}
						if (args.length != 2) {
							p.sendMessage(ChatColor.RED + "I think you might've used the wrong arguments?");
							return true;
						}
						String otherPlayer = args[1];
						Player p2 = Bukkit.getPlayer(otherPlayer);
						if (p2 == null) {
							p.sendMessage(ChatColor.RED + "No such player on this server!");
							return true;
						}
						if (p2.getUniqueId() == p.getUniqueId() && !this.plugin.getDebugEnabled()) {
							p.sendMessage(ChatColor.RED + "You can't add yourself!");
							return true;
						}
						if (cPlayer.getBuddies().contains(p2.getUniqueId())) {
							p.sendMessage(ChatColor.RED + "That person has already been added!");
							return true;
						}
						cPlayer.addBuddy(p2.getUniqueId());
						p.sendMessage(ChatColor.GREEN + "Friend request sent!");
						p2.sendMessage(p.getName() 
								+ ChatColor.AQUA + " just added you as a friend! Type" + ChatColor.WHITE +" /bc add " 
								+ p.getName() 
								+ ChatColor.AQUA + " to add him back!");
						return true;
					} else if (command.equalsIgnoreCase("remove")) {
						if (!this.plugin.getBuddiesEnabled()) {
							sender.sendMessage(ChatColor.RED + "Buddies have been disabled!");
							return true;
						}
						if (args.length != 2) {
							p.sendMessage(ChatColor.RED + "I think you might've used the wrong arguments?");
							return true;
						}
						String otherPlayer = args[1];
						if (cPlayer.getBuddies().size() == 0) {
							p.sendMessage(ChatColor.RED + "But you don't have friends!");
							return true;
						}
						
						for (UUID buddy : cPlayer.getBuddies()) {
							Player p2 = Bukkit.getPlayer(buddy);
							if (otherPlayer.equals(p2.getName())) {
								if (p2.isOnline()) {
									cPlayer.removeBuddy(buddy);
								} else {
									this.plugin.getCDatabase().removeBuddy(p.getUniqueId(), p2.getUniqueId());
								}
								p.sendMessage(ChatColor.GREEN + "Removed!");
								return true;
							}
						}
						p.sendMessage("Could not find a match!");
						return true;
						
					} else if (command.equalsIgnoreCase("list")) {
						if (!this.plugin.getBuddiesEnabled()) {
							sender.sendMessage(ChatColor.RED + "Buddies have been disabled!");
							return true;
						}
						p.sendMessage(ChatColor.GRAY + "-- Buddy List --");
						for (UUID buddy : cPlayer.getBuddies()) {
							Player p2 = Bukkit.getPlayer(buddy);
							if (p2.isOnline()) {
								p.sendMessage(ChatColor.GREEN + p2.getName());
							} else {
								p.sendMessage(ChatColor.GRAY + p2.getName());
							}
						}
						return true;
					} else if (command.equalsIgnoreCase("mute")) {
						
						if (args.length != 2) {
							p.sendMessage(ChatColor.RED + "Wrong number of arguments.");
							return true;
						} 
						Player p2 = Bukkit.getPlayer(args[1]);
						if (p2 == null) {
							p.sendMessage(ChatColor.RED + "Could not find that player!");
							return true;
						}
						if (cPlayer.getMuted().contains(p2.getUniqueId())) {
							p.sendMessage(ChatColor.LIGHT_PURPLE + "Unmuted!");
							cPlayer.unmute(p2.getUniqueId());
						} else {
							p.sendMessage(ChatColor.LIGHT_PURPLE + "Muted!");
							cPlayer.mute(p2.getUniqueId());
						}
						return true;
						
					} else {
						sender.sendMessage(ChatColor.RED + "BuddyChat command not recognized.");
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Something broke the BC command!!!");
					p.sendMessage(ChatColor.RED + "Something went wrong with your command!");
					return true;
				}
			} else {
				sender.sendMessage("You must be a player to use these commands!");
				return true;
			}
		} else if (label.equalsIgnoreCase("whisper") || label.equalsIgnoreCase("w")) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "You must type a message!");
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player!");
				return true;
			}
			Player p = (Player) sender;
			Player to = Bukkit.getPlayer(args[0]);
			if (to == null) {
				sender.sendMessage(ChatColor.RED + "Could not find that player!");
				return true;
			}
			if (to.getUniqueId() == p.getUniqueId() && !this.plugin.getDebugEnabled()) {
				p.sendMessage(ChatColor.RED + "Please don't whisper to yourself anymore.");
				return true;
			}
			if (this.plugin.getBuddiesEnabled() && !this.plugin.getCBuddyManager().getCPlayer(to).getBuddies().contains(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "Can't send that message, that person isn't friends with you!");
				return true;
			}
			String message = parseMessage(args, 1);
			this.plugin.getCCListener().sendMessage(p, message, p, "w");
			this.plugin.getCCListener().sendMessage(p, message, to, "w");
			this.plugin.getCBuddyManager().getCPlayer(to).setLastWhispFrom(p.getUniqueId());
			return true;
		} else if (label.equalsIgnoreCase("reply") || label.equalsIgnoreCase("r")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Why did you even try this?");
				return true;
			}
			Player from = (Player) sender;
			BCBuddy cb = this.plugin.getCBuddyManager().getCPlayer(from);
			if (cb.getLastWhispFrom() == null) {
				from.sendMessage(ChatColor.RED + "You don't have anyone to reply to...");
				return true;
			}
			Player to = Bukkit.getPlayer(cb.getLastWhispFrom());
			if (to == null || !to.isOnline()) {
				from.sendMessage(ChatColor.RED + "Bukkit couldn't get the player, maybe something happened to him?");
				return true;
			}
			String message = parseMessage(args, 0);
			this.plugin.getCCListener().sendMessage(from, message, from, "w");
			this.plugin.getCCListener().sendMessage(from, message, to, "w");
			this.plugin.getCBuddyManager().getCPlayer(to).setLastWhispFrom(from.getUniqueId());
			return true;
		} else if (label.equalsIgnoreCase("cc")) {			
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Seriously now. You don't even have a channel...");
				return true;
			}
			if (!this.plugin.getBuddiesEnabled()) {
				sender.sendMessage(ChatColor.RED + "Buddies have been disabled!");
				return true;
			}
			Player p = (Player) sender;
			BCBuddy cb = this.plugin.getCBuddyManager().getCPlayer(p);
			cb.setChannel(cb.getNextChannel());
			return true;
			
		}
		return false;
	}
	
	public String parseMessage(String[] args, int startIndex) {
		String msg = "";
		for (int i = startIndex; i < args.length; i++) {
			msg += args[i] + " ";
		}
		return msg;
	}

}
