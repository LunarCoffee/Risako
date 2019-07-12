package framework.core

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

internal val CLIENT = KMongo.createClient().coroutine
internal val DB = CLIENT.getDatabase("Risako0")
