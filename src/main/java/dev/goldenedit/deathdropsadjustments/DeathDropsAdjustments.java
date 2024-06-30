package dev.goldenedit.deathdropsadjustments;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeathDropsAdjustments extends JavaPlugin {
    public static IntegerFlag TOTEM_PICKUP_DELAY_FLAG;

    public void onLoad() {
        super.onLoad();
        registerFlags();
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), (Plugin)this);
    }

    private void registerFlags() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            IntegerFlag flag = new IntegerFlag("totem-pickup-delay");
            registry.register((Flag)flag);
            TOTEM_PICKUP_DELAY_FLAG = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("totem-pickup-delay");
            if (existing instanceof IntegerFlag) {
                TOTEM_PICKUP_DELAY_FLAG = (IntegerFlag)existing;
            } else {
                getLogger().severe("Totem pickup delay flag conflict: existing flag is not an IntegerFlag");
            }
        }
    }
}
