package dev.goldenedit.deathdropsadjustments;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class PlayerDeathListener implements Listener {

    private JavaPlugin plugin;
    public static IntegerFlag TOTEM_PICKUP_DELAY_FLAG;

    public PlayerDeathListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location location = player.getLocation();



        // Convert the Bukkit location to a WorldEdit location for querying
        com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);

        // Obtain the RegionContainer and create a query
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        Integer delay = null; // Initialize delay to null
        ApplicableRegionSet regions = query.getApplicableRegions(weLocation);
        for (ProtectedRegion region : regions) {
            Integer value = region.getFlag(TOTEM_PICKUP_DELAY_FLAG);
            if (value != null) {
                delay = value; // Found a valid delay value
                break; // Exit loop once a value is found
            }
        }

        if (delay != null && delay > 0) {
            final int finalDelay = delay;
            event.getDrops().forEach(itemStack -> {
                if (itemStack.getType().toString().contains("TOTEM")) {
                    final Particle.DustTransition dustTransition = new Particle.DustTransition(
                            Color.fromRGB(255, 170, 0), // Start color (Gold)
                            Color.fromRGB(229, 192, 0), // End color (Slightly darker gold)
                            1.0f // Size of the particle
                    );
                    // Debug message to confirm this block is executed

                    // Adjust location if necessary, e.g., slightly lower the Y-coordinate
                    Location particleLocation = location.clone().add(0, +1, 0);

                    BukkitTask particleTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        particleLocation.getWorld().spawnParticle(Particle.TOTEM, location, 20, 0.01, 0.2, 0.01, 0);
                    }, 0, 5); // Run this task now and then every second for a simple repeating effect

                    // Drop the item after the delay and cancel the particle task
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        location.getWorld().dropItemNaturally(location, itemStack);
                        particleTask.cancel(); // Stop showing particles when the totem spawns
                    }, finalDelay * 20L); // Convert seconds to delay in ticks
                }
            });
            event.getDrops().removeIf(item -> item.getType().toString().contains("TOTEM"));
        }
        event.getDrops().forEach(itemStack -> {
            // Generate a random location within 1 to 2 blocks of the death location
            double xOffset = Math.random() * 2 - 1;
            double zOffset = Math.random() * 2 - 1;
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation().add(xOffset, 0, zOffset), itemStack);
        });

        // Clear the original drops to prevent duplication
        event.getDrops().clear();
    }
}
