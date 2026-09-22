package one.theaq.skull.config

import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.api.SaveType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.util.Translatable
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedRegistryType
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber.Companion.setFormat
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber.Companion.withIncrement
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Blocks
import one.theaq.skull.Main
import java.text.DecimalFormat

@Translatable.Name("Skull Common")
@Translatable.Desc("Common settings for skull mod")
class Common: Config(
    identifier = Main.location("common"),
    folder = "theaq",
    name = "${Main.MOD_ID}-common"
) {

    @Translatable.Name("Spawn")
    var spawnSection = SpawnSection()
    class SpawnSection: ConfigSection() {
        @Translatable.Name("Spawn on enough players")
        @Translatable.Desc("Spawn skulls when enough players joined")
        var spawnWhenEnoughPlayers = ValidatedBoolean(true)
        @Translatable.Name("Player count requirement")
        @Translatable.Desc("Amount of people needed to spawn skulls")
        var playerCountRequirement = ValidatedInt(2, Int.MAX_VALUE, 0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
            .withIncrement(1)
        @Translatable.Name("Skull count")
        @Translatable.Desc("Amount of skulls to spawn")
        var skullCount = ValidatedInt(1, Int.MAX_VALUE, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
            .withIncrement(1)
        @Translatable.Name("Spawn dimension")
        @Translatable.Desc("Dimension in which skulls will spawn")
        var spawnDimension = "minecraft:overworld"
    }

    @Translatable.Name("Messages")
    var messagesSection = MessagesSection()
    class MessagesSection: ConfigSection() {
        @Translatable.Name("Notify on target")
        @Translatable.Desc("Notify player if skull is targeting them")
        var notifyOnTarget = ValidatedBoolean(true)

        @Translatable.Name("Kill messages")
        var killMessageGroup = ConfigGroup("kill_message")
        @Translatable.Name("Random kill message")
        @Translatable.Desc("If true sends randomized kill message")
        var randomKillMessage = ValidatedBoolean(false)
        @ConfigGroup.Pop
        @Translatable.Name("Amount of kill messages")
        @Translatable.Desc("Number of kill messages in translation file")
        var amountOfKillMessages = ValidatedInt(10, Int.MAX_VALUE, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
            .withIncrement(1)
    }

    @Translatable.Name("Skull")
    var skullSection = SkullSection()
    class SkullSection: ConfigSection() {
        @Translatable.Name("Block type")
        @Translatable.Desc("What block would skull use")
        var block = ValidatedRegistryType.of(Blocks.SKELETON_SKULL, BuiltInRegistries.BLOCK)
        @Translatable.Name("Timeout on no targets")
        @Translatable.Desc("If skull doesn't find any targets it will idle for this amount of ticks (20 ticks = 1 second)")
        var timeoutOnNoTargets = ValidatedInt(100, Int.MAX_VALUE, 0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
            .withIncrement(1)
        @Translatable.Name("Keep target")
        @Translatable.Desc("Skull keeps its target when it kills it")
        var keepTarget = ValidatedBoolean(false)
        @Translatable.Name("Disappear on kill")
        @Translatable.Desc("Skull disappears when it kills its target")
        var disappearOnKill = ValidatedBoolean(false)
        @Translatable.Name("Reset on kill")
        @Translatable.Desc("Skull teleports back to spawn and resets its target on kill")
        var resetOnKill = ValidatedBoolean(false)
        @Translatable.Name("Player grace period")
        @Translatable.Desc("After killing the player skull wont target them again for this amount of ticks (20 ticks = 1 second)")
        var playerGracePeriod = ValidatedInt(200, Int.MAX_VALUE, 0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
            .withIncrement(1)
        @Translatable.Name("Crash game on kill")
        @Translatable.Desc("Not implemented")
        var crashGameOnKill = ValidatedBoolean(false) //need to figure out how to crash peoples game

        @Translatable.Name("Blocks")
        var blockingGroup = ConfigGroup("blocking")
        @Translatable.Name("Blocks spectator switching")
        @Translatable.Desc("Skull blocks its target from switching to spectator")
        var blocksSpectatorSwitch = ValidatedBoolean(true)
        @Translatable.Name("Blocks suicides")
        @Translatable.Desc("Skull blocks its target from lethal damage that's not caused by someone else")
        var blocksSuicides = ValidatedBoolean(false)
        @Translatable.Name("Blocks dimension switching")
        @Translatable.Desc("Skull blocks its target from changing dimensions")
        @ConfigGroup.Pop
        var blocksDimensionSwitch = ValidatedBoolean(true)

        @Translatable.Name("Speed")
        @Translatable.Desc("Skull speed, scales linearly with distance")
        var speedGroup = ConfigGroup("skull_speed")
        @Translatable.Name("Base speed")
        @Translatable.Desc("Skull speed when its close to the target (0-16 blocks)")
        var baseSpeed = ValidatedDouble(0.05, Double.MAX_VALUE, 0.0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
            .withIncrement(0.05)
            .setFormat(DecimalFormat("#.##"))
        @Translatable.Name("Fast speed")
        @Translatable.Desc("Skull speed when its nearby the target (16-64 blocks)")
        var fastSpeed = ValidatedDouble(0.25, Double.MAX_VALUE, 0.0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
            .withIncrement(0.05)
            .setFormat(DecimalFormat("#.##"))
        @Translatable.Name("Faster speed")
        @Translatable.Desc("Skull speed when its far from the target (64-512 blocks)")
        var fasterSpeed = ValidatedDouble(10.0, Double.MAX_VALUE, 0.0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
            .withIncrement(0.5)
            .setFormat(DecimalFormat("#.##"))
        @Translatable.Name("Fastest speed")
        @Translatable.Desc("Skull speed when its really far away from the target (>512 blocks)")
        @ConfigGroup.Pop
        var fastestSpeed = ValidatedDouble(100.0, Double.MAX_VALUE, 0.0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
            .withIncrement(5.0)
            .setFormat(DecimalFormat("#.##"))
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