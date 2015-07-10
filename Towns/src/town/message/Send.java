package town.message;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import town.town.Town;

public class Send {
	
	public static void info(Player player, String message) {
		String info = ChatColor.DARK_AQUA + "Info> " + ChatColor.WHITE;
		player.sendMessage(info + message);
	}
	
	public static void warn(Player player, String message) {
		String info = ChatColor.GOLD + "Warn> " + ChatColor.WHITE;
		player.sendMessage(info + message);
	}
	
	public static void query(Player player, String message) {
		String info = ChatColor.DARK_PURPLE + "Query> " + ChatColor.WHITE;
		player.sendMessage(info + message);
	}
	
	public static void error(Player player, String message) {
		String info = ChatColor.RED + "Error> " + ChatColor.WHITE;
		player.sendMessage(info + message);
	}
	
	public static void status(Player player, String message) {
		String info = ChatColor.YELLOW + "Status> " + ChatColor.WHITE;
		player.sendMessage(info + message);
	}
	
	public static void success(Player player, String message) {
		String info = ChatColor.GREEN + "Success> " + ChatColor.WHITE;
		player.sendMessage(info + message);
	}
	
	public static void command(Player player, String command, String desc) {
		player.sendMessage(ChatColor.DARK_PURPLE + "t " + command + ChatColor.WHITE + " " + desc);
	}
	
	public static void broadcastInfo(String message) {
		String info = ChatColor.DARK_AQUA + "Info> " + ChatColor.WHITE;
		Bukkit.broadcastMessage(info + message);
	}
	
	public static void broadcastWarn(String message) {
		String info = ChatColor.GOLD + "Warn> " + ChatColor.WHITE;
		Bukkit.broadcastMessage(info + message);
	}
	
	public static void broadcastQuery(String message) {
		String info = ChatColor.DARK_PURPLE + "Query> " + ChatColor.WHITE;
		Bukkit.broadcastMessage(info + message);
	}
	
	public static void broadcastError(String message) {
		String info = ChatColor.RED + "Error> " + ChatColor.WHITE;
		Bukkit.broadcastMessage(info + message);
	}
	
	public static void broadcastStatus(String message) {
		String info = ChatColor.YELLOW + "Status> " + ChatColor.WHITE;
		Bukkit.broadcastMessage(info + message);
	}
	
	public static void broadcastSuccess(String message) {
		String info = ChatColor.GREEN + "Success> " + ChatColor.WHITE;
		Bukkit.broadcastMessage(info + message);
	}
	
	public static void broadcastCommand(Player player, String command, String desc) {
		Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "t " + command + ChatColor.WHITE + " " + desc);
	}
	
	public static void sendMessage(Town town, String message) {
		String info = ChatColor.AQUA + town.getName() + "> " + ChatColor.WHITE;
		for (UUID uuid : town.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				player.sendMessage(info + message);
			}
		}
	}
	
	
}
