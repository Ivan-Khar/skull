package one.theaq.template.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@MixinEnvironment(value = "client")
@Mixin(Minecraft.class)
public class MinecraftMixin {

}
