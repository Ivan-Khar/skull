package one.theaq.skull.command

import net.minecraft.server.permissions.Permission
import net.minecraft.server.permissions.Permissions

class SkullCommand: BaseCommand() {

    override fun getName(): String {
        return "skull"
    }

    override fun getPermissionLevel(): Permission {
        return Permissions.COMMANDS_GAMEMASTER
    }
}