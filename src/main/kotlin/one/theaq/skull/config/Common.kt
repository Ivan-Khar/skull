package one.theaq.skull.config

import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.api.SaveType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import net.minecraft.world.level.block.Blocks
//import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import one.theaq.skull.Main

class Common: Config(
    identifier = Main.location("common"),
    folder = "theaq",
    name = "${Main.MOD_ID}-common"
) {

    var spawn = SpawnSection()
    class SpawnSection: ConfigSection() {
        var spawnWhenEnoughPlayers      = true
        var playerCountRequirement      = 2
        var skullCount                  = 1
        //var spawnDimension              = BuiltinDimensionTypes.OVERWORLD // TODO: fix config
    }

    var messages = MessagesSection()
    class MessagesSection: ConfigSection() {
        var notifyOnTarget              = true

        var killMessageGroup = ConfigGroup("kill_message")
        var randomKillMessage           = false
        @ConfigGroup.Pop
        var amountOfKillMessages        = 10
    }

    var skull = SkullSection()
    class SkullSection: ConfigSection() {
        var block                       = Blocks.SKELETON_SKULL
        var timeoutOnNoTargets          = 100
        var keepTarget                  = false
        var disappearOnKill             = false
        var playerGracePeriod           = 200
        var crashGameOnKill             = false //need to figure out how to crash peoples game

        var blockingGroup = ConfigGroup("blocking")
        var blocksSpectatorSwitch       = true
        var blocksSuicides              = false
        @ConfigGroup.Pop
        var blocksDimensionSwitch       = true

        var speedGroup = ConfigGroup("skull_speed")
        var baseSpeed       = 0.05
        var fastSpeed       = 0.25
        var fasterSpeed     = 10.0
        @ConfigGroup.Pop
        var fastestSpeed    = 100.0
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