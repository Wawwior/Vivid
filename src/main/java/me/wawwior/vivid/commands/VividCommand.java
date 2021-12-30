package me.wawwior.vivid.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.wawwior.vivid.Vivid;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.grenadier.command.BrigadierCommand;
import net.forthecrown.grenadier.types.selectors.EntityArgument;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class VividCommand extends AbstractCommand {


    public VividCommand() {
        super("vivid", Vivid.VIVID);

        register();
    }

    @Override
    protected void createCommand(BrigadierCommand command) {
        command
                .executes(c -> {
                    c.getSource().sendMessage(ChatColor.GRAY + "§oRunning Vivid " + Vivid.VIVID.version());
                    return 1;
                })
                .then(
                        literal("item")
                                .requires(source -> source.hasPermission(Vivid.VIVID.getYamlConfig().getString("permissions.item")))
                                .executes(c -> {
                                    c.getSource().asPlayer().getInventory().addItem(Vivid.VIVID.ITEM.getItem());
                                    c.getSource().asPlayer().playSound(c.getSource().asPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
                                    return 1;
                                })
                                .then(
                                        argument("players", EntityArgument.players())
                                                .executes(c -> {
                                                    EntityArgument.getPlayers(c, "players").forEach(p -> {
                                                        p.getInventory().addItem(Vivid.VIVID.ITEM.getItem());
                                                        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
                                                    });
                                                    return 1;
                                                })
                                )
                )
                .then(
                        literal("transfer")
                                .requires(source -> source.hasPermission(Vivid.VIVID.getYamlConfig().getString("permissions.transfer")))
                                .then(
                                        argument("receiver", EntityArgument.player())
                                                .executes(c -> {
                                                    Player receiver = EntityArgument.getPlayer(c, "receiver");
                                                    Player player = c.getSource().asPlayer();
                                                    if (Vivid.VIVID.getHealthManager().get(player) - 1 <= 0) {
                                                        player.sendMessage(ChatColor.RED + "You do not have enough hearts for this!");
                                                        return 0;
                                                    }
                                                    if (Vivid.VIVID.getHealthManager().get(receiver) <= 0) {
                                                        player.sendMessage(ChatColor.RED + "You cannot transfer hearts to a dead person!");
                                                        return 0;
                                                    }
                                                    Vivid.VIVID.getHealthManager().modify(player, -1);
                                                    Vivid.VIVID.getHealthManager().modify(receiver, 1);
                                                    player.sendMessage("You gave 1 heart to " + receiver.getName() + "!");
                                                    receiver.sendMessage("You received 1 heart from " + player.getName() + "!");
                                                    return 1;
                                                })
                                                .then(
                                                        argument("amount", IntegerArgumentType.integer(1))
                                                                .executes(c -> {
                                                                    int amount = IntegerArgumentType.getInteger(c, "amount");
                                                                    Player receiver = EntityArgument.getPlayer(c, "receiver");
                                                                    Player player = c.getSource().asPlayer();
                                                                    if (Vivid.VIVID.getHealthManager().get(player) - amount <= 0) {
                                                                        player.sendMessage(ChatColor.RED + "You do not have enough hearts for this!");
                                                                        return 0;
                                                                    }
                                                                    if (Vivid.VIVID.getHealthManager().get(receiver) <= 0) {
                                                                        player.sendMessage(ChatColor.RED + "You cannot transfer hearts to a dead person!");
                                                                        return 0;
                                                                    }
                                                                    Vivid.VIVID.getHealthManager().modify(player, -1 * amount);
                                                                    Vivid.VIVID.getHealthManager().modify(EntityArgument.getPlayer(c, "receiver"), amount);
                                                                    player.sendMessage("You gave " + amount + " hearts to " + receiver.getName() + "!");
                                                                    receiver.sendMessage("You received " + amount + " hearts from " + player.getName() + "!");
                                                                    return 1;
                                                                })
                                                )
                                )
                )
                .then(
                        literal("set")
                                .requires(source -> source.hasPermission(Vivid.VIVID.getYamlConfig().getString("permissions.modify.self")))
                                .then(
                                        argument("amount", IntegerArgumentType.integer())
                                                .executes(c -> {
                                                    Vivid.VIVID.getHealthManager().set(c.getSource().asPlayer(), IntegerArgumentType.getInteger(c, "amount"));
                                                    c.getSource().sendMessage("Set " + IntegerArgumentType.getInteger(c, "amount") + " hearts for " + c.getSource().asPlayer().getName());
                                                    return 1;
                                                })
                                                .then(
                                                        argument("players", EntityArgument.players())
                                                                .requires(source -> source.hasPermission(Vivid.VIVID.getYamlConfig().getString("permissions.modify.others")))
                                                                .executes(c -> {
                                                                    List<Player> receivers = EntityArgument.getPlayers(c, "players");
                                                                    receivers.forEach(r -> {
                                                                        Vivid.VIVID.getHealthManager().set(r, IntegerArgumentType.getInteger(c, "amount"));
                                                                    });
                                                                    c.getSource().sendMessage("Set " + IntegerArgumentType.getInteger(c, "amount") + " hearts for " + c.getInput().split(" ")[3]);
                                                                    return 1;
                                                                })
                                                )
                                )
                )
                .then(
                        literal("add")
                                .requires(source -> source.hasPermission(Vivid.VIVID.getYamlConfig().getString("permissions.modify.self")))
                                .then(
                                        argument("amount", IntegerArgumentType.integer())
                                                .executes(c -> {
                                                    Vivid.VIVID.getHealthManager().modify(c.getSource().asPlayer(), IntegerArgumentType.getInteger(c, "amount"));
                                                    c.getSource().sendMessage("Gave " + IntegerArgumentType.getInteger(c, "amount") + " hearts to " + c.getSource().asPlayer().getName());
                                                    return 1;
                                                })
                                                .then(
                                                        argument("players", EntityArgument.players())
                                                                .requires(source -> source.hasPermission(Vivid.VIVID.getYamlConfig().getString("permissions.modify.others")))
                                                                .executes(c -> {
                                                                    List<Player> receivers = EntityArgument.getPlayers(c, "players");
                                                                    receivers.forEach(r -> {
                                                                        Vivid.VIVID.getHealthManager().modify(r, IntegerArgumentType.getInteger(c, "amount"));
                                                                    });
                                                                    c.getSource().sendMessage("Added " + IntegerArgumentType.getInteger(c, "amount") + " hearts to " + c.getInput().split(" ")[3]);
                                                                    return 1;
                                                                })
                                                )
                                )
                )
                .then(
                        literal("remove")
                                .requires(source -> source.hasPermission(Vivid.VIVID.getYamlConfig().getString("permissions.modify.self")))
                                .then(
                                        argument("amount", IntegerArgumentType.integer())
                                                .executes(c -> {
                                                    Vivid.VIVID.getHealthManager().modify(c.getSource().asPlayer(), -1 * IntegerArgumentType.getInteger(c, "amount"));
                                                    c.getSource().sendMessage("Removed " + IntegerArgumentType.getInteger(c, "amount") + " hearts from " + c.getSource().asPlayer().getName());

                                                    return 1;
                                                })
                                                .then(
                                                        argument("players", EntityArgument.players())
                                                                .requires(source -> source.hasPermission(Vivid.VIVID.getYamlConfig().getString("permissions.modify.others")))
                                                                .executes(c -> {
                                                                    List<Player> receivers = EntityArgument.getPlayers(c, "players");
                                                                    receivers.forEach(r -> {
                                                                        Vivid.VIVID.getHealthManager().modify(r, -1 * IntegerArgumentType.getInteger(c, "amount"));
                                                                    });
                                                                    c.getSource().sendMessage("Removed " + IntegerArgumentType.getInteger(c, "amount") + " hearts from " + c.getInput().split(" ")[3]);
                                                                    return 1;
                                                                })
                                                )
                                )
                );
    }
}
