package one.theaq.skull.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.LivingEntity;
import one.theaq.skull.logic.SkullManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CombatTracker.class)
public class CombatTrackerMixin {
	@Shadow
	@Final
	private LivingEntity mob;
	
	@Inject(method = "getDeathMessage", at = @At("HEAD"), cancellable = true)
	private void customSkullDeathMessage(CallbackInfoReturnable<Component> cir) {
		if (!SkullManager.Companion.getINSTANCE().removeFromKilledBySkull(mob)) return;
		
		cir.setReturnValue(Component.translatable("skull.kill", mob.getDisplayName()));
	}
}
