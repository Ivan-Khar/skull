package one.theaq.skull.client.mixin;

import dev.kikugie.fletching_table.mixin.MixinEnvironment;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@MixinEnvironment(value = "client")
@Mixin(Minecraft.class)
public class MinecraftMixin {

}
