package one.theaq.skull.client.mixin;

import dev.kikugie.fletching_table.mixin.MixinEnvironment;
import net.minecraft.client.Minecraft;
import one.theaq.skull.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment(value = "client")
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(at = @At("HEAD"), method = "run")
    private void init(CallbackInfo info) {
        Main.INSTANCE.getLOGGER().info("client load mixin");
    }
}
