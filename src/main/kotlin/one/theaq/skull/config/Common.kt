package one.theaq.skull.config

import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.api.SaveType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import net.minecraft.world.level.block.Blocks
import one.theaq.skull.Main

class Common: Config(
    identifier = Main.location("common"),
    folder = "theaq",
    name = "${Main.MOD_ID}-common"
) {

    var messages = MessagesSection()
    class MessagesSection: ConfigSection() {
        var notifyOnTarget = true

        var killMessageGroup = ConfigGroup("kill_message")
        var randomKillMessage = true
        var amountOfKillMessages = 10
    }

    var skull = SkullSection()
    class SkullSection: ConfigSection() {
        var block                   = Blocks.SKELETON_SKULL
        var timeoutOnNoTargets      = 100
        var disappearOnKill         = false
        var playerGracePeriod       = 200

        var blockingGroup = ConfigGroup("blocking")
        var blocksSpectatorSwitch   = false
        var blocksDimensionSwitch   = false
        @ConfigGroup.Pop

        var speedGroup = ConfigGroup("skull_speed")
        var baseSpeed       = 0.05
        var fastSpeed       = 0.5
        var fasterSpeed     = 20.0
        var fastestSpeed    = 500.0
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