package one.theaq.skull.mixin;

import net.minecraft.server.MinecraftServer;
import one.theaq.skull.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(at = @At("HEAD"), method = "loadLevel")
    private void init(CallbackInfo info) {
         Main.INSTANCE.getLOGGER().info("wheres my mixins");
    }
}
