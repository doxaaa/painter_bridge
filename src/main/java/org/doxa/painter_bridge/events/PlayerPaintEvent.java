package org.doxa.painter_bridge.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

// <--[event]
// @Events
// player paints
//
// @Plugin painter_bridge
// @Regex ^on player paints$
// @Group Player
// @Cancellable true
//
// @Triggers when a player modifies a block layer footprint using the UnearthMechanic paintbrush tool.
//
// @Context
// <context.location> returns the exact coordinate grid position of the block being painted as a LocationTag.
//
// @Player Always.
//
// -->
public class PlayerPaintEvent extends BukkitScriptEvent implements Cancellable {

    public static PlayerPaintEvent instance;
    private Player player;
    private Block block;
    private boolean isPluginCancelled = false;

    public PlayerPaintEvent() {
        instance = this;
        registerCouldMatcher("player paints");
    }

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

    @Override
    public String getName() {
        return "PlayerPaints";
    }

    // Required by Denizen's core engine to let scripts interact with determine cancelled
    @Override
    public boolean isCancelled() {
        return this.isPluginCancelled || this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isPluginCancelled = cancel;
        this.cancelled = cancel; // Sync with Denizen's internal Boolean state tracker
    }

    /**
     * Custom entry point to fire data blocks natively into Denizen scripts
     */
    public boolean fire(Player player, Block block) {
        this.player = player;
        this.block = block;
        this.isPluginCancelled = false;
        this.cancelled = false; // Reset Denizen's background state before execution loop

        // 1. Triggers Denizen script processing execution frames natively
        this.fire();

        // 2. FIXED: Explicitly query Denizen's internal field tracker.
        // If a script ran '- determine cancelled', Denizen sets 'this.cancelled' to true.
        return this.isCancelled();
    }
}
