package org.doxa.painter_bridge.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class PlayerPaintsScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player paints
    //
    // @Regex ^on player paints$
    //
    // @Triggers when a player modifies a block using a paintbrush tool.
    //
    // @Location true
    //
    // @Cancellable true
    // @Context
    // <context.location> returns the exact coordinate of the block/s being painted.
    //
    // @Plugin Painter_Bridge, unearthMechanic, Mechanics
    //
    // @Player Always.
    //
    // @Group PainterBridge
    //
    // -->

    public static PlayerPaintsScriptEvent instance;
    public Player player;
    public Block block;

    public PlayerPaintsScriptEvent() {
        instance = this;
    }

    // THE ONLY PLACE "player paints" IS USED NATIVELY IN THE LOGIC
    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("player paints");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return super.matches(path);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(new PlayerTag(player), null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("location") && block != null) {
            return new LocationTag(block.getLocation());
        }
        return super.getContext(name);
    }

    // REMOVED "PlayerPaints" HARDCODED METHOD.
    // Denizen's engine automatically derives the proper system name by processing
    // the couldMatch string directly, preventing double naming anomalies.

    /**
     * Custom entry point to fire data blocks natively into Denizen scripts
     * @return true if Denizen requested a cancellation, false otherwise.
     */
    public boolean fire(Player player, Block block) {
        this.player = player;
        this.block = block;
        this.cancelled = false; // Reset local state before script execution

        // 1. Fire the event and capture the executed instance handled by Denizen
        com.denizenscript.denizencore.events.ScriptEvent processedEvent = super.fire();

        // 2. Read the cancellation state directly from the execution instance
        if (processedEvent != null) {
            return processedEvent.cancelled;
        }

        return this.cancelled;
    }
}
