package amata1219.walkure.spigot.listener;

import amata1219.walkure.spigot.Constants;
import amata1219.walkure.spigot.Walkure;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntitySnowball;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GRAY;

public class InfinitySnowballListener implements Listener {

    private final Walkure plugin = Walkure.instance();

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if (!Constants.SERVER_SELECTOR.isSimilar(event.getItem())) return;

        Player player = event.getPlayer();

        String prefix = AQUA + "???" + GRAY + " > ";
        runTaskLater(25, () -> {
            player.sendMessage(prefix + "ホウ……");
            playSound(player, Sound.BLOCK_GLASS_BREAK);
        });

        runTaskLater(25 + 50, () -> {
            playSound(player, Sound.BLOCK_GLASS_BREAK);
            player.sendMessage(prefix + "これを見つけるとは貴様、なかなかやるではないか");
        });

        runTaskLater(25 + 50 + 70, () -> {
            playSound(player, Sound.BLOCK_GLASS_BREAK);
            player.sendMessage(prefix + "そうだな、褒美にこれでもくれてやろう");
        });

        runTaskLater(25 + 50 + 70 + 65, () -> {
            playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            player.getInventory().addItem(Constants.ETERNAL_FORCE_BLIZZARD);
        });

        runTaskLater(25 + 50 + 70 + 65 + 40, () -> {
            playSound(player, Sound.BLOCK_GLASS_BREAK);
            player.sendMessage(prefix + "冬を存分に楽しむがいい");
        });

        runTaskLater(25 + 50 + 70 + 65 + 40 + 40, () -> {
            playSound(player, Sound.BLOCK_GLASS_BREAK);
            player.sendMessage(prefix + "ではな");
        });
    }

    private void runTaskLater(long delay, Runnable action) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, action, delay);
    }

    private void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1.0f, 2.0f);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return;;

        if (!Constants.ETERNAL_FORCE_BLIZZARD.isSimilar(event.getItem())) return;

        event.setCancelled(true);

        Player player = event.getPlayer();

        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityHuman entityHuman = ((CraftPlayer) player).getHandle();
        EntitySnowball entitySnowball = new EntitySnowball(world, entityHuman);
        entitySnowball.a(entityHuman, entityHuman.pitch, entityHuman.yaw, 0.0F, 1.5F, 1.0F);
        world.addEntity(entitySnowball);

        player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 0.5F, 0.4F / (Constants.RANDOM.nextFloat() * 0.4F + 0.8F));
    }

    private final FixedMetadataValue metadata = new FixedMetadataValue(plugin, this);

    @EventHandler
    public void on(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;

        Snowball snowball = (Snowball) event.getEntity();
        if (!(snowball.getShooter() instanceof Player)) return;

        Player shooter = (Player) snowball.getShooter();
        if (shooter.getInventory().getItemInMainHand().isSimilar(Constants.ETERNAL_FORCE_BLIZZARD))
            snowball.setMetadata(Constants.ETERNAL_FORCE_BLIZZARD_METADATA_NAME, metadata);
    }

    @EventHandler
    public void on(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;

        Snowball snowball = (Snowball) event.getEntity();
        if (!(snowball.hasMetadata(Constants.ETERNAL_FORCE_BLIZZARD_METADATA_NAME))) return;

        Location loc = snowball.getLocation();
        loc.setDirection(snowball.getVelocity());

        loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 16, Constants.ICE_MATERIAL_DATA);
        loc.getWorld().playSound(loc, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f + Constants.RANDOM.nextFloat());

        if (!(event.getHitEntity() instanceof Player)) return;

        Player hit = (Player) event.getHitEntity();
        if (!hit.getInventory().getItemInMainHand().isSimilar(Constants.ETERNAL_FORCE_BLIZZARD)) return;

        hit.damage(0.2, snowball);
        hit.setNoDamageTicks(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof  Player && event.getDamager() instanceof Snowball)) return;

        Snowball snowball = (Snowball) event.getDamager();
        if (snowball.hasMetadata(Constants.ETERNAL_FORCE_BLIZZARD_METADATA_NAME)) event.setCancelled(false);
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();
        EntityDamageEvent source = player.getLastDamageCause();
        if (!(source instanceof EntityDamageByEntityEvent)) return;

        Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
        if (!(damager instanceof Snowball && damager.hasMetadata(Constants.ETERNAL_FORCE_BLIZZARD_METADATA_NAME))) return;

        Player shooter = (Player) ((Snowball) damager).getShooter();

        String message = player.getName() + " は " + shooter.getName() + " ";
        //amata1219 は amata1219 の [§f雪礫] で殺害された
        switch (Constants.RANDOM.nextInt(3)) {
            case 0: {
                message += "の エターナルフォースブリザード で凍死した";
                break;
            }
            case 1: {
                message += "により周囲の大気ごと凍結されて死んだ";
                break;
            }
            default: {
                message += "が生み出した永遠の力の猛吹雪で凍え死んだ";
            }
        }

        event.setDeathMessage(message);
    }

}
