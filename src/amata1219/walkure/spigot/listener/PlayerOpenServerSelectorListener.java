package amata1219.walkure.spigot.listener;

import amata1219.walkure.Channels;
import amata1219.walkure.spigot.Constants;
import amata1219.walkure.spigot.Walkure;
import amata1219.walkure.spigot.registry.RequesterRegistry;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerOpenServerSelectorListener implements Listener {

    private final RequesterRegistry registry;

    public PlayerOpenServerSelectorListener(RequesterRegistry registry) {
        this.registry = registry;
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return;

        if (Constants.SERVER_SELECTOR.isSimilar(event.getItem())) return;

        Player player = event.getPlayer();
        long id = System.nanoTime();

        registry.register(id, player);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Channels.REQUEST);
        out.writeLong(id);

        player.sendPluginMessage(Walkure.instance(), Channels.BUNGEE_CORD, out.toByteArray());
    }

}
