package one.theaq.skull.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.portal.TeleportTransition;
import one.theaq.skull.config.Configs;
import one.theaq.skull.logic.Skull;
import one.theaq.skull.logic.SkullManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "setGameMode", at = @At("HEAD"), cancellable = true)
    private void blockGameModeSwitch(GameType mode, CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.INSTANCE.getCOMMON().getSkullSection().getBlocksSpectatorSwitch().get()) return;

        var player = ((ServerPlayer) (Object) this);
        if (!SkullManager.Companion.getINSTANCE().isTargetedBySkull(player) || mode != GameType.SPECTATOR) return;

        var titlePacket = new ClientboundSetActionBarTextPacket(Component.translatable("skull.blocked.gamemode").withColor(TextColor.GRAY));
        player.connection.send(titlePacket);
        cir.cancel();
    }

    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;", at = @At("HEAD"), cancellable = true)
    private void blockDimensionTeleport(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        if (!Configs.INSTANCE.getCOMMON().getSkullSection().getBlocksDimensionSwitch().get()) return;

        var player = ((ServerPlayer) (Object) this);
        if (!SkullManager.Companion.getINSTANCE().isTargetedBySkull(player)) return;

        var oldLevel = player.level();
        var newLevel = transition.newLevel();
        if (oldLevel.dimension() == newLevel.dimension()) return;

        Skull.Companion.sendSubTitle(player, "skull.blocked.dimensiontp");
        cir.setReturnValue(player);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void blockSuicide(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.INSTANCE.getCOMMON().getSkullSection().getBlocksSuicides().get()) return;

        var player = ((ServerPlayer) (Object) this);
        if (source.is(DamageTypes.GENERIC_KILL)) return;
        if (!SkullManager.Companion.getINSTANCE().isTargetedBySkull(player)) return;

        if (source.getEntity() != null || source.getDirectEntity() != null) return;
        if (player.getHealth() - damage > 0) return;
        else player.setHealth(1.0f);
        
        Skull.Companion.sendSubTitle(player, "skull.blocked.suicide");
        cir.setReturnValue(false);
    }
}
