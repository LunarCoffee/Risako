package framework.core.std

import net.dv8tion.jda.api.entities.User

internal object UserNotFound : User {
    override fun getDefaultAvatarId() = error()
    override fun getMutualGuilds() = error()
    override fun isBot() = error()
    override fun getDefaultAvatarUrl() = error()
    override fun getName() = error()
    override fun hasPrivateChannel() = error()
    override fun getJDA() = error()
    override fun getIdLong() = error()
    override fun openPrivateChannel() = error()
    override fun isFake() = error()
    override fun getAsMention() = error()
    override fun getAvatarId() = error()
    override fun getDiscriminator() = error()
    override fun getAsTag() = error()
    override fun getAvatarUrl() = error()
    override fun getEffectiveAvatarUrl() = error()

    private fun error(): Nothing = throw IllegalArgumentException()
}
