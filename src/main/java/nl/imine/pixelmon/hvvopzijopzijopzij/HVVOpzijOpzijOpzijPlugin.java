package nl.imine.pixelmon.hvvopzijopzijopzij;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.ChangeGameModeEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Plugin(id = "hvvopzijopzijopzij", name = "HVVOpzijOpzijOpzij", version = "1.0", description = "Makes sure the player enjoys the map without going too fast")
public class HVVOpzijOpzijOpzijPlugin {

    private final int JUMPBOOST_MODIFIER = 128;

    @Listener
    public void onGameReload(GameReloadEvent gameReloadEvent) {
        Task.builder()
                .delayTicks(100) //just to make sure everything is loaded,
                .execute(e -> Sponge.getServer()
                        .getOnlinePlayers()
                        .forEach(this::updateRunAndJump))
                .submit(this);
    }

    @Listener
    public void onGameModeSwitch(ChangeGameModeEvent changeGameModeEvent) {
        Player player = (Player) changeGameModeEvent.getTargetEntity();
        updateRunAndJump(player);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join clientConnectionEvent) {
        Player player = clientConnectionEvent.getTargetEntity();
        updateRunAndJump(player);
    }

    private void updateRunAndJump(Player player){
        if (player.gameMode() == GameModes.ADVENTURE || player.gameMode() == GameModes.SURVIVAL) {
            preventRunning(player);
            preventJumping(player);
        } else {
            allowRunning(player);
            allowJumping(player);
        }
    }

    private void preventRunning(Player player) {
        player.offer(Keys.FOOD_LEVEL, 6);
        player.offer(Keys.SATURATION, Double.MAX_VALUE);
    }

    private void allowRunning(Player player) {
        player.offer(Keys.FOOD_LEVEL, 20);
        player.offer(Keys.SATURATION, Double.MAX_VALUE);
    }

    private void preventJumping(Player player) {
        PotionEffect potionEffect = PotionEffect.builder()
                .duration(Integer.MAX_VALUE)
                .amplifier(JUMPBOOST_MODIFIER)
                .particles(false)
                .build();

        List<PotionEffect> potionEffects = player.get(Keys.POTION_EFFECTS).orElseGet(ArrayList::new);

        potionEffects.add(potionEffect);

        player.offer(Keys.POTION_EFFECTS, potionEffects);
    }

    private void allowJumping(Player player) {

        List<PotionEffect> potionEffects = player.get(Keys.POTION_EFFECTS).orElseGet(ArrayList::new);

        new ArrayList<>(potionEffects).forEach(effect ->        {
            if(effect.getAmplifier() == JUMPBOOST_MODIFIER){
                potionEffects.remove(effect);
            }
        });

        player.offer(Keys.POTION_EFFECTS, potionEffects);
    }

}
