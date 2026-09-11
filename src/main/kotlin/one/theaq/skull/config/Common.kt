package one.theaq.skull.config

import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.api.SaveType
import me.fzzyhmstrs.fzzy_config.config.Config
import net.minecraft.world.level.block.Blocks
import one.theaq.skull.Main

class Common: Config(
    identifier = Main.location("common"),
    folder = "theaq",
    name = "${Main.MOD_ID}-common") {

    var skullSpeed = 1.0 // Block/Sec
    var skullItem = Blocks.SKELETON_SKULL.asItem()
    var skullTimeoutOnNoTargets = 100 //ticks
    var skullDisappearsOnKill = false
    var skullPlayerGracePeriod = 200

    override fun defaultPermLevel(): Int {
        return 2
    }

    override fun fileType(): FileType {
        return FileType.JSON5
    }

    override fun saveType(): SaveType {
        return SaveType.SEPARATE
    }
}