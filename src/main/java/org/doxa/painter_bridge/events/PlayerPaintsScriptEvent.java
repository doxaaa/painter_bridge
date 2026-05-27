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
    // painter player paints block
    //
    // @Regex ^on painter player paints block$
    //
    // @Triggers when a player modifies a block layer footprint using the UnearthMechanic paintbrush tool.
    //
    // @Location true
    //
    // @Cancellable true
    //
    // @Context
    // <context.location> Returns the exact coordinate location of the block being painted as a LocationTag.
    //
    // @Plugin painter_bridge, UnearthMechanic
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

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("painter player paints block");
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

    /**
     * Custom entry point to fire data blocks natively into Denizen scripts
     * @return true if Denizen requested a cancellation, false otherwise.
     */
    public boolean fire(Player player, Block block) {
        this.player = player;
        this.block = block;
        this.cancelled = false; // Reset local state before script execution

        // Fire the event and capture the executed instance handled by Denizen
        com.denizenscript.denizencore.events.ScriptEvent processedEvent = super.fire();

        if (processedEvent != null) {
            return processedEvent.cancelled;
        }

        return this.cancelled;
    }
}
