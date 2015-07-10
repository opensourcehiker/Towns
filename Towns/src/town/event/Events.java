package town.event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import town.core.Core;
import town.message.Send;
import town.town.SimpleChunk;
import town.town.Town;

public class Events implements Listener{
	
	private List<UUID> noJump = new ArrayList<UUID>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		for (Town town : Core.core.getTownManager().getTowns()) {
			for (SimpleChunk chunk : town.getChunks()) {
				Chunk chunk1 = Bukkit.getWorld("world").getChunkAt(chunk.getX(), chunk.getZ());
				chunk1.load(true);
			}
			for (UUID uuid : town.getInvited()) {
				if (e.getPlayer().getUniqueId() == uuid);
				Send.info(e.getPlayer(), "You have been invited to " + ChatColor.GRAY + town.getName() + ChatColor.WHITE + ".");
			}
		}
			if (Core.core.getTownManager().isInTownTerrority(e.getPlayer().getLocation())) {
				Town town = Core.core.getTownManager().getTown(e.getPlayer().getLocation());
				Send.status(e.getPlayer(), "You have now entered " + ChatColor.GRAY + town.getName() + ChatColor.WHITE + ".");
				}else{
				Send.status(e.getPlayer(), "You have now entered " + ChatColor.GRAY + "Wilderness" + ChatColor.WHITE + ".");
					}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		for (Town town : Core.core.getTownManager().getTowns()) {
			for (SimpleChunk chunk : town.getChunks()) {
				Chunk chunk1 = Bukkit.getWorld("world").getChunkAt(chunk.getX(), chunk.getZ());
				chunk1.load(true);
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
	    if(e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
			if (noJump.contains(e.getPlayer().getUniqueId())) {
				noJump.remove(e.getPlayer().getUniqueId());
		}
		}
		Location to = e.getTo();
		Location from = e.getFrom();
		if (Core.core.getTownManager().isInTownTerrority(to) 
		&& !Core.core.getTownManager().isInTownTerrority(from)) {
		Town town = Core.core.getTownManager().getTown(to);
		Send.status(e.getPlayer(), "You have now entered " + ChatColor.GRAY + town.getName() + ChatColor.WHITE + ".");
		}
		if (!Core.core.getTownManager().isInTownTerrority(to) 
				&& Core.core.getTownManager().isInTownTerrority(from)) {
			if (Core.core.getTownManager().isInTownTerrority(from)) {
		Send.status(e.getPlayer(), "You have now entered " + ChatColor.GRAY + "Wilderness" + ChatColor.WHITE + ".");
			}
				
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Location loc = e.getBlock().getLocation();
		if (Core.core.getTownManager().isInTownTerrority(loc)) {
			Town town = Core.core.getTownManager().getTown(loc);
			if (Core.core.getTownManager().isInTown(e.getPlayer())) {
			Town town1 = Core.core.getTownManager().getTown(e.getPlayer());
			if (town.getUUID() != town1.getUUID()) {
				e.setCancelled(true);
			}
			}else{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Location loc = e.getBlock().getLocation();
		if (Core.core.getTownManager().isInTownTerrority(loc)) {
			Town town = Core.core.getTownManager().getTown(loc);
			if (Core.core.getTownManager().isInTown(e.getPlayer())) {
			Town town1 = Core.core.getTownManager().getTown(e.getPlayer());
			if (town.getUUID() != town1.getUUID()) {
				e.setCancelled(true);
			}
			}else{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (Core.core.getTownManager().isInTownTerrority(player.getLocation())) {
			if (Core.core.getTownManager().isInTown(player)) {
				Town town = Core.core.getTownManager().getTown(player);
				Town town1 = Core.core.getTownManager().getTown(player.getLocation());
				if (town.getUUID().equals(town1.getUUID())) {
					e.setCancelled(true);
					Bukkit.broadcastMessage("Cancelled");
				}
			}
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (Core.core.getTownManager().isInTown(e.getPlayer())) {
			Town town = Core.core.getTownManager().getTown(e.getPlayer());
			String format = "";
			format = format + ChatColor.YELLOW + town.getName() + " " + ChatColor.WHITE + e.getPlayer().getName() + " " + ChatColor.GRAY + e.getMessage();
			e.setFormat(format);
		}else{
			String format = "";
			format = format + ChatColor.WHITE + e.getPlayer().getName() + " " + ChatColor.GRAY + e.getMessage();
			e.setFormat(format);
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		if (e.getEntity().getType() == EntityType.PRIMED_TNT) {
			for (Block block : e.blockList()) {
				Location loc = block.getLocation();
				if (Core.core.getTownManager().isInTownTerrority(loc)) {
					Block block1 = Bukkit.getWorld("world").getBlockAt(loc);
					block1.setType(block.getType());
				}
			}
		}
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent e) {
		e.setMotd("" + ChatColor.AQUA + ChatColor.BOLD + "Dext" + ChatColor.YELLOW + ChatColor.BOLD + "erity");
	}
	
}
