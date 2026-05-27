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
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PlayerPaintEvent extends Event implements Cancellable {

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

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Block block;
    private boolean cancelled = false;

    public PlayerPaintEvent(@NotNull Player player, Block block) {
        this.player = player;
        this.block = block;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @SuppressWarnings("unused")
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


    public static class DenizenWrapper extends BukkitScriptEvent implements Listener {

        public static DenizenWrapper instance;
        private PlayerPaintEvent currentEvent;

        public DenizenWrapper() {
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
            return new BukkitScriptEntryData(new PlayerTag(currentEvent.getPlayer()), null);
        }

        @Override
        public ObjectTag getContext(String name) {
            if (name.equals("location") && currentEvent.getBlock() != null) {
                return new LocationTag(currentEvent.getBlock().getLocation());
            }
            return super.getContext(name);
        }

        @Override
        public String getName() {
            return "PlayerPaints";
        }

        @EventHandler
        public void onPaint(PlayerPaintEvent event) {
            this.currentEvent = event;
            fire(event);
        }
    }
}
