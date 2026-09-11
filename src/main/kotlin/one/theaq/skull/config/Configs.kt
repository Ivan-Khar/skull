package one.theaq.skull.config

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType

object Configs {
    val COMMON = ConfigApi.registerAndLoadConfig(::Common, RegisterType.BOTH)
}