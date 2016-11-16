package com.github.jonnylin13.buddychat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

public class BCDatabase {
	
	private BuddyChatPlugin plugin;
	
	private Connection c;
	private Statement statement;
	
	public BCDatabase(BuddyChatPlugin plugin) {
		this.plugin = plugin;
		connect();
	}
	
	public void connect() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:buddychat.db");
			if (this.plugin.getDebugEnabled()) {
				System.out.println("BuddyChat is in the database!");
			}
			this.statement = c.createStatement();
			// Check if table exists
			String sql = "create table if not exists buddychat(uuid1 text not null, uuid2 text not null)";
			this.statement.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not connect to database!");
		}
	}
	
	public Connection getConnection() {
		return this.c;
	}
	
	public void removeBuddy(UUID uuid1, UUID uuid2) {
		try {
			String sql = ("delete from buddychat where uuid1 = '" + uuid1 + "' and uuid2 = '" + uuid2 + "'");
			this.statement.execute(sql);
		} catch(SQLException e) {
			e.printStackTrace();
			System.out.println("BuddyChat database could not remove friendship:");
			System.out.println("UUID1: " + uuid1);
			System.out.println("UUID2: " + uuid2);
		}
	}
	
	public void storeBuddy(UUID uuid1, UUID uuid2) {
		try {
			String sql = "insert into buddychat (uuid1, uuid2) values ('" + uuid1 + "', '" + uuid2 + "')";
			this.statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("BuddyChat Database could not store a friendship:");
			System.out.println("UUID1: " + uuid1);
			System.out.println("UUID2: " + uuid2);
		}
	}
	
	public ArrayList<UUID> getBuddies(UUID uuid) {
		ArrayList<UUID> ret = new ArrayList<UUID>();
		try {
			String sql = "select * from buddychat where uuid1 = '" + uuid + "'";
			ResultSet rs = this.statement.executeQuery(sql);
			while (rs.next()) {
				String uuid2str = rs.getString("uuid2");
				UUID uuid2 = UUID.fromString(uuid2str);
				ret.add(uuid2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error fetching buddies:");
			System.out.println(uuid);
		}
		return ret;
	}

}
