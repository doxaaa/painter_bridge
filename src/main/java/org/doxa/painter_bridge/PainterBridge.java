package org.doxa.painter_bridge;

import org.doxa.painter_bridge.listeners.MechanicPaintListener;
import org.doxa.painter_bridge.events.PlayerPaintsScriptEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.denizenscript.denizencore.events.ScriptEvent;

public final class PainterBridge extends JavaPlugin {

    public static PainterBridge instance;

    @Override
    public void onEnable() {
        instance = this;

        // 1. Register your sliding-window item listener safely into Spigot
        Bukkit.getPluginManager().registerEvents(new MechanicPaintListener(), this);

        // 2. DEPENIZEN INITIALIZATION ALIGNMENT:
        // Registers the class token directly into Denizen's engine.
        ScriptEvent.registerScriptEvent(PlayerPaintsScriptEvent.class);

        getLogger().info("PainterBridge successfully loaded using Depenizen initialization architecture!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PainterBridge safely disabled.");
    }
}
