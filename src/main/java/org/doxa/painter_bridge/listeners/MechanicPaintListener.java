package org.doxa.painter_bridge.listeners;

import org.doxa.painter_bridge.events.PlayerPaintEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

// DIRECT UNEARTHMECHANIC PRE-STAGE APPLY EVENT IMPORT
import dev.wuason.unearthMechanic.events.PreApplyStageEvent;

public class MechanicPaintListener implements Listener {

    // Tracks: Key ("PlayerUUID:X,Y,Z") -> Value (The exact Bukkit tick integer when it ran)
    private final HashMap<String, Integer> slidingTickCache = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUnearthPreStageApply(PreApplyStageEvent event) {
        Player player = null;
        Block block = null;

        try {
            // 1. SAFE METHOD PLAYER SCAN
            for (Method method : event.getClass().getDeclaredMethods()) {
                if (method.getParameterCount() == 0) {
                    Class<?> returnType = method.getReturnType();

                    if (Player.class.isAssignableFrom(returnType) || returnType.getName().toLowerCase().contains("player")) {
                        method.setAccessible(true);
                        Object result = method.invoke(event);
                        if (result == null) continue;

                        if (result instanceof Player) {
                            player = (Player) result;
                            break;
                        }

                        for (Method subMethod : result.getClass().getDeclaredMethods()) {
                            if (subMethod.getParameterCount() == 0 && Player.class.isAssignableFrom(subMethod.getReturnType())) {
                                subMethod.setAccessible(true);
                                Object possiblePlayer = subMethod.invoke(result);
                                if (possiblePlayer instanceof Player) {
                                    player = (Player) possiblePlayer;
                                    break;
                                }
                            }
                        }
                        if (player != null) break;
                    }
                }
            }

            // 2. SAFE FIELD PLAYER SCAN
            if (player == null) {
                for (Field field : event.getClass().getDeclaredFields()) {
                    if (Player.class.isAssignableFrom(field.getType()) || field.getType().getName().toLowerCase().contains("player")) {
                        field.setAccessible(true);
                        Object value = field.get(event);
                        if (value == null) continue;

                        if (value instanceof Player) {
                            player = (Player) value;
                            break;
                        }

                        for (Method subMethod : value.getClass().getDeclaredMethods()) {
                            if (subMethod.getParameterCount() == 0 && Player.class.isAssignableFrom(subMethod.getReturnType())) {
                                subMethod.setAccessible(true);
                                Object possiblePlayer = subMethod.invoke(value);
                                if (possiblePlayer instanceof Player) {
                                    player = (Player) possiblePlayer;
                                    break;
                                }
                            }
                        }
                        if (player != null) break;
                    }
                }
            }

            // 3. DYNAMIC BLOCK / LOCATION SCANNER
            for (Method method : event.getClass().getDeclaredMethods()) {
                if (method.getParameterCount() == 0) {
                    if (Block.class.isAssignableFrom(method.getReturnType())) {
                        method.setAccessible(true);
                        Object result = method.invoke(event);
                        if (result instanceof Block) {
                            block = (Block) result;
                            break;
                        }
                    } else if (org.bukkit.Location.class.isAssignableFrom(method.getReturnType())) {
                        method.setAccessible(true);
                        Object result = method.invoke(event);
                        if (result instanceof org.bukkit.Location) {
                            block = ((org.bukkit.Location) result).getBlock();
                            break;
                        }
                    }
                }
            }

            if (block == null) {
                for (Field field : event.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(event);
                    if (value instanceof Block) {
                        block = (Block) value;
                        break;
                    } else if (value instanceof org.bukkit.Location) {
                        block = ((org.bukkit.Location) value).getBlock();
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}

        if (player == null) {
            return;
        }

        if (block == null) {
            block = player.getTargetBlockExact(5);
        }

        if (block == null) {
            return;
        }

        // 4. SLIDING TICK-WINDOW DEBOUNCE
        int currentTick = Bukkit.getCurrentTick();
        String blockKey = player.getUniqueId() + ":" + block.getX() + "," + block.getY() + "," + block.getZ();

        if (slidingTickCache.containsKey(blockKey)) {
            int lastPaintedTick = slidingTickCache.get(blockKey);
            if ((currentTick - lastPaintedTick) <= 2) {
                return;
            }
        }

        slidingTickCache.put(blockKey, currentTick);

        if (slidingTickCache.size() > 150) {
            slidingTickCache.entrySet().removeIf(entry -> (currentTick - entry.getValue()) > 20);
        }

        // 5. Fire straight into Denizen natively, skipping the middleman Bukkit event pipeline
        if (PlayerPaintEvent.instance != null) {
            boolean shouldCancel = PlayerPaintEvent.instance.fire(player, block);
            if (shouldCancel) {
                event.setCancelled(true);
            }
        }
    }
}
