package one.theaq.skull.mixin;

import dev.kikugie.fletching_table.mixin.MixinEnvironment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@MixinEnvironment("default")
@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("handleKillingBlow")
    void skull$handleKillingBlow();

    @Invoker("dropAllDeathLoot")
    void skull$dropAllDeathLoot(final ServerLevel level, final DamageSource source);
}
