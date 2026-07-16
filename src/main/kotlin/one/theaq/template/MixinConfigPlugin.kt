package one.theaq.template

import one.theaq.template.platform.Platform.isModLoaded
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class MixinConfigPlugin : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String) {
    }

    override fun getRefMapperConfig(): String? {
        return null
    }

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        val compatIdentifier = "mixin.compat."
        val compatIndex = mixinClassName.indexOf(compatIdentifier)
        if (compatIndex == -1) return true

        val compatMixinName = mixinClassName.substring(compatIndex + compatIdentifier.length)
        val compatId = compatMixinName.substring(0, compatMixinName.indexOf('.'))
        return isModLoaded(compatId)
    }

    override fun acceptTargets(
        myTargets: Set<String>,
        otherTargets: Set<String>
    ) {
    }

    override fun getMixins(): List<String>? {
        return null
    }

    override fun preApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
    }

    override fun postApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
    }
}