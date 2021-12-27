package me.wawwior.vivid.health;

import me.wawwior.config.Configurable;
import me.wawwior.config.IConfig;
import me.wawwior.vivid.Vivid;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.*;

public class HealthManager extends Configurable<HealthManager.HealthConfig> {

    public static class HealthConfig implements IConfig {

        public Map<UUID, Integer> health = new HashMap<>();

        public List<UUID> dead = new ArrayList<>();

    }

    public HealthManager() {
        super(HealthConfig.class, "/", "health", Vivid.VIVID.getConfigProvider());
    }

    public void modify(Player player, int i) {
        int health = (config.health.containsKey(player.getUniqueId())) ?
                config.health.get(player.getUniqueId()) + i * 2 : 20 + i * 2;
        config.health.remove(player.getUniqueId());
        config.health.put(player.getUniqueId(), health);

        update(player);
    }

    public void update(Player player) {
        int health = config.health.getOrDefault(player.getUniqueId(), 20);
        config.health.remove(player.getUniqueId());
        config.health.put(player.getUniqueId(), health);
        if (config.health.get(player.getUniqueId()) <= 0) {
            config.health.replace(player.getUniqueId(), 0);
            config.dead.add(player.getUniqueId());
            if (Vivid.VIVID.getYamlConfig().getBoolean("ban")) {
                player.banPlayerFull("You reached 0 hearts...");
            } else {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage("You reached 0 hearts and were put into spectator mode...");
            }
        }
        if (config.health.get(player.getUniqueId()) > 0 && config.dead.contains(player.getUniqueId())) {
            config.dead.remove(player.getUniqueId());
            if (!player.isOnline()) {
                player.banPlayerFull("You got revived.", new Date());
            }
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(0);
            player.sendMessage("You got revived...");
        }

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(config.health.get(player.getUniqueId()));
    }

    public int get(Player player) {
        return config.health.getOrDefault(player.getUniqueId(), 20);
    }

}
