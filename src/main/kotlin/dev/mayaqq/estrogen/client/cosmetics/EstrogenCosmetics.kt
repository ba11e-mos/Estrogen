package dev.mayaqq.estrogen.client.cosmetics

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

object EstrogenCosmetics : Logger by LoggerFactory.getLogger(EstrogenCosmetics::class.java) {
    private val DISABLED = hashSetOf<UUID>()

    fun init() {

    }
}