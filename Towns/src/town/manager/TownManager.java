package town.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import town.core.Core;
import town.town.SimpleChunk;
import town.town.Town;

public class TownManager {
	private List<Town> towns = new ArrayList<Town>();

	public List<Town> getTowns() {
		return towns;
	}

	public void setTowns(List<Town> towns) {
		this.towns = towns;
	}
	
	public void addTown(Town town) {
		towns.add(town);
	}
	
	public void removeTown(Town town) {
		towns.remove(town);
	}
	
	public boolean hasPlayer(Town town, Player player) {
		for (UUID player1 : town.getPlayers()) {
			if (player.getUniqueId() == player1) {
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	public Town getTown(Player player) {
		for (Town town : towns) {
			for (UUID player1 : town.getPlayers()) {
				if (player1.equals(player.getUniqueId())) {
					return town;
				}
			}
		}
		return null;
	}
	
	public boolean isInTown(Player player) {
		for (Town town : towns) {
			for (UUID player1 : town.getPlayers()) {
				if (player1.equals(player.getUniqueId())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isInTown(OfflinePlayer player) {
		for (Town town : towns) {
			for (UUID player1 : town.getPlayers()) {
				if (player1.equals(player.getUniqueId())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isInTownTerrority(Location loc) {
		Chunk chunk = loc.getChunk();
		for (Town town : towns) {
			for (SimpleChunk chunk1 : town.getChunks()) {
				if (chunk.getX() == chunk1.getX() && chunk.getZ() == chunk1.getZ()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Town getTown(Location loc) {
		Chunk chunk = loc.getChunk();
		for (Town town : towns) {
			for (SimpleChunk chunk1 : town.getChunks()) {
				if (chunk.getX() == chunk1.getX() && chunk.getZ() == chunk1.getZ()) {
					return town;
				}
			}
		}
		return null;
	}
	
	public Town getTown(UUID uuid) {
		for (Town town : towns) {
			if (town.getUUID().equals(uuid)) {
				return town;
			}
		}
		return null;
	}
	
	public Town getTown(String name) {
		for (Town town : towns) {
			if (name.equalsIgnoreCase(town.getName())) {
				return town;
			}
		}
		return null;
	}
	
	public boolean townExists(String name) {
		for (Town town : towns) {
			if (name.equalsIgnoreCase(town.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public TownManager() {
		File file = new File(Core.core.getDataFolder() + "/data.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		if (!file.exists()) {
			Core.log.sendMessage("[Towns] " + ChatColor.YELLOW + "Data file does not exist. Creating new copy...");
		try {
			cfg.save(file);
			Core.log.sendMessage("[Towns] " + ChatColor.GREEN + "Successfully created new copy of data file.");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		}else{
			Core.log.sendMessage("[Towns] " + ChatColor.YELLOW + "Loading data...");
		try {
			int townCount = 0;
			if (cfg.contains("towns")) {
			for (String name : cfg.getConfigurationSection("towns").getKeys(false)) {
				UUID uuid = UUID.fromString(cfg.getString("towns." + name + ".uuid"));
				List<UUID> players = new ArrayList<UUID>();
				for (String uuidString : cfg.getStringList("towns." + name + ".players")) {
					UUID player = UUID.fromString(uuidString);
					players.add(player);
				}
				Town town = new Town(name,uuid,players);
				if (cfg.contains("towns." + name + ".chunks")) {
				List<SimpleChunk> chunks = new ArrayList<SimpleChunk>();
				for (String chunkString : cfg.getStringList("towns." + name + ".chunks")) {
					int x = Integer.parseInt(chunkString.split(",")[0]);
					int z = Integer.parseInt(chunkString.split(",")[1]);
					Bukkit.broadcastMessage(x + "," + z);
					SimpleChunk chunk = new SimpleChunk(x,z);
					chunks.add(chunk);
					Bukkit.getWorld("world").loadChunk(x, z);
				}
				town.setChunks(chunks);
				}
				if (cfg.contains("towns." + name + ".home")) {
					World world = Bukkit.getWorld(cfg.getString("towns." + name + ".home.world"));
					double x = cfg.getDouble("towns." + town + ".home.x");
					double y = cfg.getDouble("towns." + town + ".home.x");
					double z = cfg.getDouble("towns." + town + ".home.x");
					float pitch = Float.parseFloat(String.valueOf(cfg.getDouble("towns." + town + ".home.pitch")));
					float yaw = Float.parseFloat(String.valueOf(cfg.getDouble("towns." + town + ".home.yaw")));
					Location home =  new Location(world,x,y,z,pitch,yaw);
					town.setHome(home);
				}
				
				this.addTown(town);
				
				townCount++;
				
			}
			cfg.save(file);
			Core.log.sendMessage("[Towns] " + ChatColor.GREEN + "Data loaded. " + ChatColor.YELLOW + townCount + ChatColor.GREEN + " towns created.");
			
			}
		} catch (IOException e) {
			Core.log.sendMessage("[Towns] " + ChatColor.RED + "Failed to load data.");
		}
		}
		Core.log.sendMessage("[Towns] " + ChatColor.GREEN + "Loading done.");
	}
	
	public void saveTowns() {
		Core.log.sendMessage("[Towns] " + ChatColor.YELLOW + "Saving data...");
		File file = new File(Core.core.getDataFolder() + "/data.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		try {
		int townCount = 0;
		if (towns.size() > 0) {
		for (Town town : towns) {
			if (town.getChunks().size() > 0) {
				List<String> serialized = new ArrayList<String>();
			for (SimpleChunk chunk : town.getChunks()) {
				String serialize = "" + chunk.getX() + "," + chunk.getZ();
				serialized.add(serialize);
			}
			cfg.set("towns." + town.getName() + ".chunks", serialized);
			}
			if (town.getHome() != null) {
				Location home = town.getHome();
				cfg.set("towns." + town.getName() + ".home.world", home.getWorld().getName());
				cfg.set("towns." + town.getName() + ".home.x", home.getX());
				cfg.set("towns." + town.getName() + ".home.y", home.getY());
				cfg.set("towns." + town.getName() + ".home.z", home.getZ());
				cfg.set("towns." + town.getName() + ".home.pitch", home.getPitch());
				cfg.set("towns." + town.getName() + ".home.yaw", home.getYaw());
			}
			cfg.set("towns." + town.getName() + ".uuid", town.getUUID().toString());
			List<String> serialized = new ArrayList<String>();
			for (UUID actualUUID : town.getPlayers()) {
				serialized.add(actualUUID.toString());
			}
			cfg.set("towns." + town.getName() + ".players", serialized);
			townCount++;
			
		}
		cfg.save(file);
		Core.log.sendMessage("[Towns] " + ChatColor.GREEN + "Data saved. " + ChatColor.YELLOW + townCount + ChatColor.GREEN + " towns saved.");
		}
		}catch (IOException e) {
		Core.log.sendMessage("[Towns] " + ChatColor.RED + "Failed to save towns.");
		}
		Core.log.sendMessage("[Towns] " + ChatColor.GREEN + "Saving done.");
	}
	
}
