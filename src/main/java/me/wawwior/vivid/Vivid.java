package me.wawwior.vivid;

import me.wawwior.core.command.CommandRegistry;
import me.wawwior.core.core.CorePlugin;
import me.wawwior.core.item.CoreItem;
import me.wawwior.core.item.JsonRecipe;
import me.wawwior.core.pack.PackLoader;
import me.wawwior.vivid.commands.VividCommand;
import me.wawwior.vivid.event.VividEventHandler;
import me.wawwior.vivid.health.HealthManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Vivid extends CorePlugin {

    public static Vivid VIVID;

    private HealthManager healthManager;

    public CoreItem ITEM;

    @Override
    public String version() {
        return "1.2.0";
    }

    @Override
    protected void load() {

        VIVID = this;

        PackLoader.register(this, 0);

        if (!new File("./plugins/Vivid/recipe.json").exists())
            saveResource("recipe.json", false);

        healthManager = new HealthManager();
        configProvider.register(healthManager);
    }

    @Override
    public void enable() {
        Bukkit.getOnlinePlayers().forEach(p -> healthManager.update(p));

        Bukkit.getPluginManager().registerEvents(new VividEventHandler(), this);

        CommandRegistry.register(new VividCommand());


        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(7777);
        meta.displayName(Component.text(ChatColor.LIGHT_PURPLE + "Heart Artifact"));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.GRAY + "Rightclick to use!"));
        meta.lore(lore);
        item.setItemMeta(meta);

        ITEM = itemFactory.make("heart_artifact", item);

        Bukkit.addRecipe(JsonRecipe.getFromPath(configProvider.pathName + "recipe.json").getRecipe(this, "heart", ITEM.getItem()));
    }

    public HealthManager getHealthManager() {
        return healthManager;
    }
}
