package dev.lunarcoffee.risako.framework.core

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val CLIENT = KMongo.createClient().coroutine
val DB = CLIENT.getDatabase("Risako0")
