package org.doxa.painter_bridge;

import org.doxa.painter_bridge.listeners.MechanicPaintListener;
import org.doxa.painter_bridge.events.PlayerPaintsScriptEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PainterBridge extends JavaPlugin {

    @Override
    public void onEnable() {
        // 1. Register your sliding-window item listener safely into Spigot
        Bukkit.getPluginManager().registerEvents(new MechanicPaintListener(), this);

        // 2. Register the standalone top-level event directly into Denizen's engine
        PlayerPaintsScriptEvent denizenEvent = new PlayerPaintsScriptEvent();
        com.denizenscript.denizencore.events.ScriptEvent.registerScriptEvent(denizenEvent);

        getLogger().info("PainterBridge successfully loaded and mapped natively to Denizen!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PainterBridge safely disabled.");
    }
}
