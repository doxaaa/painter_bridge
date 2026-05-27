package org.doxa.painter_bridge;

import org.doxa.painter_bridge.events.PlayerPaintsScriptEvent;
import com.denizenscript.denizencore.events.ScriptEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class PainterBridge extends JavaPlugin {

    public static PainterBridge instance;

    @Override
    public void onEnable() {
        instance = this;

        // Register the script instance directly into Denizen's listener architecture pipeline.
        ScriptEvent.registerScriptEvent(new PlayerPaintsScriptEvent());

        getLogger().info("PainterBridge successfully loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PainterBridge safely disabled.");
    }
}
