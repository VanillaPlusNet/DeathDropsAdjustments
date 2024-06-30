package dev.goldenedit.deathdropsadjustments;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeathDropsAdjustments extends JavaPlugin{

    public static IntegerFlag TOTEM_PICKUP_DELAY_FLAG;

    @Override
    public void onLoad() {
        super.onLoad();
        registerFlags();

    }
    @Override
    public void onEnable() {
        // Register the event listeners
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
    }

    private void registerFlags() {
        FlagRegistry registry = com.sk89q.worldguard.WorldGuard.getInstance().getFlagRegistry();
        try {
            // Create and register the custom flag for the totem pickup delay flag
            IntegerFlag flag = new IntegerFlag("totem-pickup-delay");
            registry.register(flag);
            TOTEM_PICKUP_DELAY_FLAG = flag;
        } catch (FlagConflictException e) {
            // Handle case where the flag has already been registered
            Flag<?> existing = registry.get("totem-pickup-delay");
            if (existing instanceof StateFlag) {
                TOTEM_PICKUP_DELAY_FLAG = (IntegerFlag) existing;
            } else {
                // Problem: the flag is already registered, but it's not an IntegerFlag
                getLogger().severe("Totem pickup delay flag conflict: existing flag is not an IntegerFlag");
            }
        }
    }
}
