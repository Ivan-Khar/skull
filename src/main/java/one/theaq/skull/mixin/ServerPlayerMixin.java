package one.theaq.skull.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import one.theaq.skull.config.Configs;
import one.theaq.skull.logic.SkullManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "setGameMode", at = @At("HEAD"), cancellable = true)
    private void blockGameModeSwitch(GameType mode, CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.INSTANCE.getCOMMON().getSkull().getBlocksSpectatorSwitch()) return;

        var player = ((ServerPlayer) (Object) this);
        if (!SkullManager.Companion.getINSTANCE().isTargetedBySkull(player) || mode != GameType.SPECTATOR) return;

        var titlePacket = new ClientboundSetActionBarTextPacket(Component.translatable("skull.blocked.gamemode").withColor(TextColor.GRAY));
        player.connection.send(titlePacket);
        cir.cancel();
    }

    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;", at = @At("HEAD"), cancellable = true)
    private void blockDimensionTeleport(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        if (!Configs.INSTANCE.getCOMMON().getSkull().getBlocksDimensionSwitch()) return;

        var player = ((ServerPlayer) (Object) this);
        if (!SkullManager.Companion.getINSTANCE().isTargetedBySkull(player)) return;

        var oldLevel = player.level();
        var newLevel = transition.newLevel();
        if (oldLevel.dimension() == newLevel.dimension()) return;

        var titlePacket = new ClientboundSetActionBarTextPacket(Component.translatable("skull.blocked.dimensiontp").withColor(TextColor.GRAY));
        player.connection.send(titlePacket);
        cir.cancel();
    }
}
