package il.co.biosignals.minecraftlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.UUID;

public class EventListener implements Listener
{
  private final JavaPlugin plugin;
  private final HologramManager hologramManager;
  private final DatabaseQuerier databaseQuerier;

  EventListener(JavaPlugin _plugin, HologramManager _hologramManager, DatabaseQuerier _databaseQuerier)
  {
    this.plugin = _plugin;
    this.hologramManager = _hologramManager;
    this.databaseQuerier = _databaseQuerier;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e)
  {
    final UUID userID = e.getPlayer().getUniqueId();
    final Player player = e.getPlayer();

    this.databaseQuerier.addPlayer(userID, player.getName(), Bukkit.getScheduler());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e)
  {
    this.databaseQuerier.removePlayer(e.getPlayer().getUniqueId());
    this.hologramManager.removePlayer(e.getPlayer());
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e)
  {
    this.hologramManager.updateHologramLocationForPlayer(e.getPlayer());
  }

  @EventHandler
  public void onPlayerResourcePackStatusEvent(PlayerResourcePackStatusEvent e)
  {
    if (e.getStatus().equals(PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED))
    {
      Bukkit.getScheduler().runTaskLater(MinecraftLayer.getInstance(), () -> {
        this.databaseQuerier.setLoadedPack(e.getPlayer().getUniqueId());
        this.databaseQuerier.playSound(e.getPlayer());
      }, 20);
    }
  }
}
