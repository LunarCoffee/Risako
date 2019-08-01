package dev.lunarcoffee.risakobot.framework.api.extensions

import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import dev.lunarcoffee.risakobot.bot.consts.GSON
import dev.lunarcoffee.risakobot.framework.core.DB
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.services.reloaders.*
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender
import dev.lunarcoffee.risakobot.framework.core.std.idgen.IdGenerator
import kotlinx.coroutines.future.await

// Schedules a [Reloadable] so that it can be rescheduled upon a bot restart.
internal suspend inline fun <reified T : Reloadable> CommandContext.scheduleReloadable(
    colName: String,
    reloadable: T
) {
    val col = DB.getCollection<ReloadableJson>(colName)

    // Set the RJID of the reloadable to allow identification after being retrieved from the DB
    // after a restart. This allows calls to [Reloadable#finish] to function properly.
    reloadable.rjid = IdGenerator.generate()

    // [reloadableJson] is a JSON representation of the [Reloadable] object, used so the user
    // defined class can freely define properties while keeping the API friendly.
    val reloadableJson = ReloadableJson(GSON.toJson(reloadable), reloadable.rjid)

    col.insertOne(reloadableJson)
    reloadable.schedule(event, ReloadableCollection(colName, T::class))
}

internal suspend fun CommandContext.send(sender: ContentSender) = sender.send(this)

internal suspend fun CommandContext.sendAsAuthor(content: String) {
    val webhook = event.textChannel.createWebhook("tempUser").await()
    val webhookClient = WebhookClientBuilder(webhook.url).build()

    val authorNickname = event.guild.getMember(event.author)!!.nickname ?: event.author.name
    val webhookMessage = WebhookMessageBuilder()
        .setUsername(authorNickname)
        .setAvatarUrl(event.author.effectiveAvatarUrl)
        .setContent(content)
        .build()

    webhookClient.send(webhookMessage).await()
    webhook.delete().await()
}
