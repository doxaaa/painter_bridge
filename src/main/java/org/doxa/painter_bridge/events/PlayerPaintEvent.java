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

public class PlayerPaintEvent extends BukkitScriptEvent implements Cancellable {

    // <--[event]
    // @Events
    // player paints
    //
    // @Regex ^on player paints$
    //
    // @Group Player
    //
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

    public static PlayerPaintEvent instance;
    private Player player;
    private Block block;
    private boolean isCancelledState = false;

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

    @Override
    public boolean isCancelled() {
        return this.isCancelledState || this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelledState = cancel;
        this.cancelled = cancel;
    }

    /**
     * Custom entry point to fire data blocks natively into Denizen scripts
     */
    public boolean fire(Player player, Block block) {
        this.player = player;
        this.block = block;
        this.isCancelledState = false;
        this.cancelled = false;

        this.fire();

        return this.isCancelledState;
    }
}
