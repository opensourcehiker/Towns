package town.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import town.event.Events;
import town.manager.TownManager;
import town.message.Send;
import town.town.SimpleChunk;
import town.town.Town;

public class Core extends JavaPlugin {
	public static Core core;
	public static ConsoleCommandSender log;
	private TownManager tM;
	private Map<UUID, Long> cooldowns = new HashMap<UUID, Long>();
	
	public void onEnable() {
		core = this;	
		log = this.getServer().getConsoleSender();
		tM = new TownManager();
		this.getServer().getPluginManager().registerEvents(new Events(), this);
		
	}
	
	public void onDisable() {
		tM.saveTowns();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (label.equalsIgnoreCase("t")) {
				if (args.length > 0) {
				if (args[0].equalsIgnoreCase("list")) {
					if (Core.core.getTownManager().getTowns().size() > 0) {
						String list = "";
						for (Town town : Core.core.getTownManager().getTowns()) {
							list = list + town.getName() + ", ";
						}
						list = list.substring(0, list.length() - 2);
						Send.status(player, list);
					}else{
						Send.status(player, "There are no existing towns.");
					}
				}
				if (args[0].equalsIgnoreCase("help")) {
					if (args.length == 1) {
						this.sendHelpMessage(player, 1);
					}else if (args.length == 2) {
						try {
							int i = Integer.parseInt(args[1]);
							this.sendHelpMessage(player, i);
						}catch (Exception e) {
							Send.warn(player, "Argument 2 must be a number.");
						}
					}else{
						this.sendHelpMessage(player, 1);
					}
				}
				if (args[0].equalsIgnoreCase("info")) {
					if (args.length == 2) {
						String name = args[1];
						if (Core.core.getTownManager().townExists(name)) {
							Town town = Core.core.getTownManager().getTown(name);
							this.sendInfoMessage(player, town);
						}
					}else{
						Send.warn(player, "Incorrect number of arguments.");
					}
				}
				if (args[0].equalsIgnoreCase("create")) {
					if (args.length == 2) {
						if (!Core.core.getTownManager().isInTown(player)) {
						String name = args[1];
						if (Core.core.getTownManager().townExists(name)) {
							Send.warn(player, "That town already exists.");
						}else{
							Town town = new Town(name,player);
							Core.core.getTownManager().addTown(town);
							Send.success(player, "You created the town " + ChatColor.GRAY + town.getName() + ChatColor.WHITE + ".");
						}
						}else{
							Send.error(player, "You are already in a town.");
						}
					}else{
						Send.warn(player, "Incorrect number of arguments.");
					}
				}
				if (args[0].equalsIgnoreCase("invite")) {
					if (args.length == 2) {
						if (Core.core.getTownManager().isInTown(player)) {
							Town town = Core.core.getTownManager().getTown(player);
							String name = args[1];
						Player player1 = Bukkit.getPlayer(name);
						if (player1 != null) {
							if (Core.core.getTownManager().isInTown(player1)) {
								Send.warn(player, "That player is already in a town.");
							}else{
								town.addInvited(player1.getUniqueId());
								Send.info(player,"Your invite has been sent.");
								Send.info(player1, "You have been invited to " + ChatColor.GRAY + town.getName() + ChatColor.WHITE + ".");
							}
						}else{
							@SuppressWarnings("deprecation")
							OfflinePlayer player2 = Bukkit.getOfflinePlayer(name);
							if (player2 != null) {
								if (!Core.core.getTownManager().isInTown(player2)) {
								town.addInvited(player2.getUniqueId());
								Send.info(player,"Your invite has been sent.");
								}else{
									Send.warn(player, "That player is already in a town.");
								}
							}else{
							Send.error(player, "That player does not exist.");
							}
						}
						}else{
							Send.error(player, "You are not in a town.");
						}
					}else{
						Send.warn(player, "Incorrect number of arguments.");
					}
				}
				if (args[0].equalsIgnoreCase("join")) {
					if (args.length == 2) {
						if (!Core.core.getTownManager().isInTown(player)) {
							String name = args[1];
							if (Core.core.getTownManager().townExists(name)) {
								Town town = Core.core.getTownManager().getTown(name);
								for (UUID uuid : town.getInvited()) {
									if (player.getUniqueId() == uuid) {
										town.addPlayer(uuid);
										town.removeInvited(uuid);
										Send.sendMessage(town, ChatColor.GRAY + player.getName() + ChatColor.WHITE + " has joined.");
									}
								}
							}else{
								Send.error(player, "That town does not exist.");
							}
						}else{
							Send.warn(player, "You are already in a town.");
						}
					}else{
						Send.warn(player, "Incorrect number of arguments.");
					}
				}
				if (args[0].equalsIgnoreCase("leave")) {
					if (Core.core.getTownManager().isInTown(player)) {
						Town town = Core.core.getTownManager().getTown(player);
						Send.sendMessage(town, ChatColor.GRAY + player.getName() + ChatColor.WHITE+ " has left the town.");
						town.removePlayer(player.getUniqueId());
						if (town.getPlayers().size() == 0) {
							Send.broadcastStatus(ChatColor.GRAY + town.getName() + ChatColor.WHITE + " has been disbanded.");
							getTownManager().removeTown(town);
						}
					}else{
						Send.error(player, "You are not in a town.");
					}
				}
				if (args[0].equalsIgnoreCase("disband")) {
					if (Core.core.getTownManager().isInTown(player)) {
						Town town = Core.core.getTownManager().getTown(player);
						Send.broadcastStatus(ChatColor.GRAY + town.getName() + ChatColor.WHITE + " has been disbanded.");
						getTownManager().removeTown(town);
					}else{
						Send.error(player, "You are not in a town.");
					}
				}
				if (args[0].equalsIgnoreCase("sethome")) {
					if (Core.core.getTownManager().isInTown(player)) {
						if (Core.core.getTownManager().isInTownTerrority(player.getLocation())) {
							Town town = Core.core.getTownManager().getTown(player.getLocation());
							Town town1 = Core.core.getTownManager().getTown(player);
							if (town.getUUID() == town1.getUUID()) {
						Location loc = player.getLocation();
						int x = loc.getBlockX();
						int y = loc.getBlockY();
						int z = loc.getBlockZ();
						String world = loc.getWorld().getName();
						Send.sendMessage(town, "Home has been set by " + ChatColor.GRAY + player.getName()
							  + ChatColor.WHITE + " to " + ChatColor.GRAY + x + 
								ChatColor.WHITE + "," + ChatColor.GRAY + y + 
								ChatColor.WHITE + "," + ChatColor.GRAY + z + 
								ChatColor.WHITE + " at world " + 
								ChatColor.GRAY + world + ChatColor.WHITE + ".");
						town.setHome(loc);
						return true;
							}else{
								Send.warn(player, "You must be in your town's land.");
							}
						}else{
							Send.warn(player, "You must be in your town's land.");
						}
					}else{
						Send.error(player, "You are not in a town.");
					}
				}
				if (args[0].equalsIgnoreCase("home")) {
					if (Core.core.getTownManager().isInTown(player)) {
						Town town = Core.core.getTownManager().getTown(player);
						if (town.getHome() != null) {
							int cooldownTime = 120;
							if (cooldowns.containsKey(player.getUniqueId())) {
								 long secondsLeft = ((cooldowns.get(player.getUniqueId())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
						            if(secondsLeft > 0) {
						            	Send.status(player, "You must wait " + ChatColor.GRAY + secondsLeft + ChatColor.WHITE + " seconds before using that command.");
						            	return true;
						            }
							}
						            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
						            Send.status(player, "Teleporting...");
						            player.teleport(town.getHome());
						}else{
							Send.error(player, "Your town's home has not been set.");
						}
					}else{
						Send.error(player, "You are not in a town.");
					}
				}
				if (args[0].equalsIgnoreCase("claim")) {
					if (Core.core.getTownManager().isInTown(player)) {
						if (!Core.core.getTownManager().isInTownTerrority(player.getLocation())) {
					Town town = Core.core.getTownManager().getTown(player);
					if (town.getChunks().size() < 10) {
					Chunk chunk = player.getLocation().getChunk();
					SimpleChunk spawn = new SimpleChunk(player.getWorld().getSpawnLocation().getChunk());
					SimpleChunk chunk1 = new SimpleChunk(chunk);
					if (!(chunk1.getX() <= (spawn.getX() + 5) && chunk1.getZ() <= (spawn.getZ() + 5)
							&& chunk1.getX() >= (spawn.getX() - 5) && chunk1.getZ() >= (spawn.getZ() - 5))) {
					town.addChunk(chunk1);
					Send.sendMessage(town, ChatColor.GRAY + player.getName() + ChatColor.WHITE + 
							" has claimed chunk " + ChatColor.GRAY + chunk.getX() + ChatColor.WHITE +
							"," + ChatColor.GRAY + chunk.getZ() + ChatColor.WHITE + " in world " +
							ChatColor.GRAY + chunk.getWorld().getName() + ChatColor.WHITE + 
							". You have " + ChatColor.GRAY + (10 - town.getChunks().size()) + 
							ChatColor.WHITE + " claims left.");
					}else{
						Send.warn(player, "Your town must be 5 chunks away from spawn.");
					}
					}else{
						Send.warn(player, "Your town has reached a maximum amount of claims.");
					}
						}else{
							Send.warn(player, "This land is already claimed.");
						}
					}else{
						Send.error(player, "You are not in a town.");
					}
						
				}
				if (args[0].equalsIgnoreCase("unclaim")) {
					if (Core.core.getTownManager().isInTown(player)) {
						if (Core.core.getTownManager().isInTownTerrority(player.getLocation())) {
							Town town = Core.core.getTownManager().getTown(player.getLocation());
							Town town1 = Core.core.getTownManager().getTown(player);
							if (town.getUUID() == town1.getUUID()) {
									Chunk chunk = player.getLocation().getChunk();
										town.removeChunk(chunk.getX(), chunk.getZ());
										Send.sendMessage(town, ChatColor.GRAY + player.getName() + ChatColor.WHITE + 
												" has unclaimed chunk " + ChatColor.GRAY + chunk.getX() + ChatColor.WHITE +
												"," + ChatColor.GRAY + chunk.getZ() + ChatColor.WHITE + " in world " +
												ChatColor.GRAY + chunk.getWorld().getName() + ChatColor.WHITE + 
												". You have " + ChatColor.GRAY + (10 - town.getChunks().size()) + 
												ChatColor.WHITE + " claims left.");
										if (town.getHome() != null) {
										if (town.getHome().getChunk().getX() == chunk.getX() && town.getHome().getChunk().getZ() == chunk.getZ()) {
											town.setHome(null);
											Send.sendMessage(town, "Your home has been unset due to the unclaiming of the land it was in.");
										}
										}
							}else{
								Send.error(player, "This land does not belong to your town.");
							}
						}else{
							Send.error(player, "This land does not belong to your town.");
						}
					}else{
						Send.error(player, "You are not in a town.");
					}
				}
				if (args[0].equalsIgnoreCase("title")) {
					if (args.length == 2) {
						if (Core.core.getTownManager().isInTown(player)) {
						String title = args[1];
						if (!Core.core.getTownManager().townExists(title)) {
							Town town = Core.core.getTownManager().getTown(player);
							String previousTitle = town.getName();
							town.setName(title);
							Send.sendMessage(town, ChatColor.GRAY + player.getName() + ChatColor.WHITE + 
									" has changed the name of the town from " + 
									ChatColor.GRAY + previousTitle + ChatColor.WHITE +
									" to " + ChatColor.GRAY + title + ChatColor.WHITE + ".");
						}else{
							Send.error(player, "This name is already taken.");
						}
						}else{
							Send.error(player, "You are not in a town.");
						}
					}else{
						Send.warn(player, "Incorrect number of arguments.");
					}
				}
				if (args[0].equalsIgnoreCase("unclaimall")) {
					if (Core.core.getTownManager().isInTown(player)) {
						Town town = Core.core.getTownManager().getTown(player);
						if (town.getChunks().size() > 0) {
							town.clearChunks();
							town.setHome(null);
							Send.sendMessage(town, ChatColor.GRAY + player.getName() + ChatColor.WHITE + " has unclaimed all your land.");
							Send.sendMessage(town, "Your home has been unset due to the unclaiming of the land it was in.");
						}else{
							Send.error(player, "Your town does not have any claimed land.");
						}
					}else{
						Send.error(player, "You are not in a town.");
					}
				}
			}else{
				Send.info(player, "Type " + ChatColor.GRAY + "/t help [page]" + ChatColor.WHITE + " for a list of commands.");
			}
			}
		}
		return false;
	}
	
	public void sendHelpMessage(Player player, int page) {
		if (page == 1) {
			Send.info(player, "Help Page Page 1 and 2");
			Send.command(player, "list", "Lists all the current existing towns");
			Send.command(player, "info [name]", "Shows info on a town");
			Send.command(player, "create [name]", "Creates a new town with the name");
			Send.command(player, "invite [name]", "Invites a player to your town");
			Send.command(player, "join [name]", "You try to join a town");
			Send.command(player, "leave", "Lists all the current existing towns");
		}else if (page == 2) {
			Send.info(player, "Help Page Page 2 and 2");
			Send.command(player, "sethome", "Sets the home of your town");
			Send.command(player, "home", "Teleports you to the home of your town");
			Send.command(player, "claim", "Claims the chunk you are standing in");
			Send.command(player, "unclaim", "Unclaims the chunk you are standing in");
			Send.command(player, "title [name]", "Sets the title of your town");
			Send.command(player, "disband", "Your town is disbanded.");
		}else if (page > 2) {
			Send.info(player, "Help Page Page 2 and 2");
			Send.command(player, "sethome", "Sets the home of your town");
			Send.command(player, "home", "Teleports you to the home of your town");
			Send.command(player, "claim", "Claims the chunk you are standing in");
			Send.command(player, "unclaim", "Unclaims the chunk you are standing in");
			Send.command(player, "title [name]", "Sets the title of your town");
			Send.command(player, "disband", "Your town is disbanded.");
		}else if (page < 1) {
			Send.info(player, "Help Page Page 1 and 2");
			Send.command(player, "list", "Lists all the current existing towns");
			Send.command(player, "info [name]", "Shows info on a town");
			Send.command(player, "create [name]", "Creates a new town with the name");
			Send.command(player, "invite [name]", "Invites a player to your town");
			Send.command(player, "join [name]", "You try to join a town");
			Send.command(player, "leave", "Lists all the current existing towns");
		}
	}
	
	public void sendInfoMessage(Player player, Town town) {
		Send.info(player, "The information on " + ChatColor.GRAY + town.getName() + ChatColor.WHITE + ".");
		if (town.getPlayers().size() > 1) {
		String players = "Town members are ";
		for (UUID uuid : town.getPlayers()) {
			Player player1 = Bukkit.getPlayer(uuid);
			players = players + ChatColor.GRAY + player1.getName() + ChatColor.WHITE + ", ";
		}
		players = players.substring(0, players.length() - 2);
		Send.status(player, players);
		}else{
		Send.status(player, "Town member is " + ChatColor.GRAY + Bukkit.getPlayer(town.getPlayers().get(0)).getName() + ChatColor.WHITE + ".");
		}
		Send.status(player, "Town has " + town.getChunks().size() + " land claims.");
	}

	public TownManager getTownManager() {
		return tM;
	}

	public void setTownManager(TownManager tM) {
		this.tM = tM;
	}
	
	public boolean isOnline(UUID uuid) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getUniqueId() == uuid) {
				return true;
			}
		}
		return false;
	}

}
