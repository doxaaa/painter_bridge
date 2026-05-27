package org.doxa.painter_bridge.events;

import com.denizenscript.denizen.Denizen;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.utilities.inventory.SlotHelper;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
    // @Group Player
    //
    // @Location true
    //
    // @Cancellable true
    //
    // @Triggers when a player modifies a block layer footprint using the UnearthMechanic paintbrush tool.
    //
    // @Context
    // <context.location> returns the exact coordinate grid position of the block being painted.
    //
    // @Player Always.
    //
    // -->

    // FIXED: Mapped constructor name to match class name exactly
    public PlayerPaintsScriptEvent() {
        instance = this;
        registerCouldMatcher("player paints");
    }

    // FIXED: Updated instance reference type mapping
    public static PlayerPaintsScriptEvent instance;
    public Player player;
    public Block block;

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
