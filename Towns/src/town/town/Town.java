package town.town;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import town.core.Core;

public class Town {
	private List<UUID> players = new ArrayList<UUID>();
	private List<UUID> invited = new ArrayList<UUID>();
	private String name = "";
	private List<SimpleChunk> chunks = new ArrayList<SimpleChunk>();
	private UUID uuid;
	private Location home;
	
	public Town(String name, Player player) {
		this.name = name;
		this.players.add(player.getUniqueId());
		this.uuid = UUID.randomUUID();
	}
	
	public Town(String name, Player player, UUID uuid) {
		this.name = name;
		this.players.add(player.getUniqueId());
		this.uuid = uuid;
	}
	
	public Town(String name, UUID uuid, List<UUID> players) {
		this.name = name;
		this.players = players;
		this.uuid = uuid;
	}
	

	public List<UUID> getPlayers() {
		return players;
	}

	public void setPlayers(List<UUID> players) {
		this.players = players;
	}
	
	public void addPlayer(UUID uuid) {
		players.add(uuid);
	}
	
	public void removePlayer(UUID uuid) {
		players.remove(uuid);
		File file = new File(Core.core.getDataFolder() + "/data.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		List<String> serialized = new ArrayList<String>();
		for (UUID actualUUID : this.getPlayers()) {
			serialized.add(actualUUID.toString());
		}
		cfg.set("towns." + this.getName() + ".players", serialized);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SimpleChunk> getChunks() {
		return chunks;
	}

	public void setChunks(List<SimpleChunk> chunks) {
		this.chunks = chunks;
	}
	
	public void addChunk(int x, int z) {
		chunks.add(new SimpleChunk(x,z));
	}
	
	public void addChunk(SimpleChunk chunk) {
		chunks.add(chunk);
	}
	
	public void removeChunk(int x, int z) {
		SimpleChunk chunk1 = null;
		for (SimpleChunk chunk : chunks) {
			if (chunk.getX() == x && chunk.getZ() == z) {
				chunk1 = chunk;
			}
		}
		if (chunk1 != null) {
			chunks.remove(chunk1);
			if (this.getChunks().size() > 0) {
				File file = new File(Core.core.getDataFolder() + "/data.yml");
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				List<String> serialized = new ArrayList<String>();
			for (SimpleChunk chunk : this.getChunks()) {
				String serialize = "" + chunk.getX() + "," + chunk.getZ();
				serialized.add(serialize);
			}
			cfg.set("towns." + this.getName() + ".chunks", serialized);
			}
		}
	}
	
	public void clearChunks() {
		chunks.clear();
			File file = new File(Core.core.getDataFolder() + "/data.yml");
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		    cfg.set("towns." + this.getName() + ".chunks", null);
		    try {
				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	public void removeChunk(SimpleChunk chunk) {
		chunks.remove(chunk);
		if (this.getChunks().size() > 0) {
			File file = new File(Core.core.getDataFolder() + "/data.yml");
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			List<String> serialized = new ArrayList<String>();
		for (SimpleChunk chunk1 : this.getChunks()) {
			String serialize = "" + chunk1.getX() + "," + chunk1.getZ();
			serialized.add(serialize);
		}
		cfg.set("towns." + this.getName() + ".chunks", serialized);
		}
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public List<UUID> getInvited() {
		return invited;
	}

	public void setInvited(List<UUID> invited) {
		this.invited = invited;
	}
	
	public void addInvited(UUID uuid) {
		invited.add(uuid);
	}
	
	public void removeInvited(UUID uuid) {
		invited.remove(invited);
	}

	public Location getHome() {
		return home;
	}

	public void setHome(Location home) {
		this.home = home;
		if (home == null) {
			File file = new File(Core.core.getDataFolder() + "/data.yml");
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			cfg.set("towns." + this.getName() + ".home",null);
			try {
				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
