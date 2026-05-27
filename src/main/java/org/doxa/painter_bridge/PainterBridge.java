package org.doxa.painter_bridge;

import org.doxa.painter_bridge.listeners.MechanicPaintListener;
import org.doxa.painter_bridge.events.PlayerPaintEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PainterBridge extends JavaPlugin {

    @Override
    public void onEnable() {
        // 1. Register your Unearth mechanic block interceptor listener safely
        Bukkit.getPluginManager().registerEvents(new MechanicPaintListener(), this);

        // 2. Register the wrapper class into Denizen's syntax vocabulary registry ONLY
        // Removing the manual Bukkit registration line here completely fixes the double-firing!
        PlayerPaintEvent.DenizenWrapper denizenEvent = new PlayerPaintEvent.DenizenWrapper();
        com.denizenscript.denizencore.events.ScriptEvent.registerScriptEvent(denizenEvent);

        getLogger().info("PainterBridge successfully loaded and double-firing fixed!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PainterBridge safely disabled.");
    }
}
