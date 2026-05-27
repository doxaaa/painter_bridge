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

        Bukkit.getPluginManager().registerEvents(new MechanicPaintListener(), this);

        // Registers the class token directly into Denizen's engine.
        ScriptEvent.registerScriptEvent(PlayerPaintsScriptEvent.class);

        getLogger().info("PainterBridge successfully loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PainterBridge safely disabled.");
    }
}
