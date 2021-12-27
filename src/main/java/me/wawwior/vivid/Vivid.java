package me.wawwior.vivid;

import com.google.gson.Gson;
import io.papermc.paper.inventory.ItemRarity;
import me.wawwior.config.ConfigProvider;
import me.wawwior.core.Core;
import me.wawwior.core.command.CommandRegistry;
import me.wawwior.core.util.JsonRecipe;
import me.wawwior.vivid.commands.VividCommand;
import me.wawwior.vivid.event.VividEventHandler;
import me.wawwior.vivid.health.HealthManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public final class Vivid extends JavaPlugin {

    public static Vivid VIVID;

    public static final String version = "v1.1";

    private HealthManager healthManager;

    public final ItemStack ITEM = new ItemStack(Material.TOTEM_OF_UNDYING);

    private ConfigProvider configProvider;

    @Override
    public void onEnable() {

        VIVID = this;


        saveDefaultConfig();
        saveResource("recipe.json", false);
        configProvider = Core.getDefaultProvider(this);
        healthManager = new HealthManager();
        configProvider.register(healthManager);
        configProvider.load();

        Bukkit.getOnlinePlayers().forEach(p -> healthManager.update(p));

        Bukkit.getPluginManager().registerEvents(new VividEventHandler(), this);

        CommandRegistry.register(new VividCommand());

        ItemMeta meta = ITEM.getItemMeta();
        meta.setCustomModelData(7777);
        meta.displayName(Component.text(ChatColor.LIGHT_PURPLE + "Heart Artifact"));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.GRAY + "Rightclick to use!"));
        meta.lore(lore);
        ITEM.setItemMeta(meta);

        JsonRecipe jsonRecipe = JsonRecipe.getFromPath(configProvider.pathName + "recipe.json");
        ShapedRecipe recipe = jsonRecipe.getRecipe(this, "heart", ITEM);
        Bukkit.addRecipe(recipe);
    }

    @Override
    public void onDisable() {
        configProvider.save();
    }

    public ConfigProvider getConfigProvider() {
        return configProvider;
    }

    public FileConfiguration getYamlConfig() {
        return getConfig();
    }

    public HealthManager getHealthManager() {
        return healthManager;
    }
}
