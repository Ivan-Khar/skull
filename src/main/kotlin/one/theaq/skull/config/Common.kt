package one.theaq.skull.config

import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.api.SaveType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import net.minecraft.world.level.block.Blocks
import one.theaq.skull.Main

class Common: Config(
    identifier = Main.location("common"),
    folder = "theaq",
    name = "${Main.MOD_ID}-common"
) {

    var skull = SkullSection()
    class SkullSection: ConfigSection() {
        var block               = Blocks.SKELETON_SKULL
        var timeoutOnNoTargets  = 100
        var disappearOnKill     = false
        var playerGracePeriod   = 200

        var speed = MovementSpeed()
        class MovementSpeed: ConfigSection() {
            var baseSpeed       = 1.0
            var fastSpeed       = 10.0
            var fasterSpeed     = 200.0
            var fastestSpeed    = 500.0
        }
    }

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