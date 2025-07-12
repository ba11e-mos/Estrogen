package dev.mayaqq.estrogen.client.cosmetics

import dev.mayaqq.cynosure.client.models.animations.AnimationDefinition
import dev.mayaqq.cynosure.utils.file.GlobalStorage
import dev.mayaqq.estrogen.MOD_ID
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.InputStream
import java.nio.file.Path

class CosmeticAnimation(url: String) : DownloadedAsset<AnimationDefinition>(CACHE, url) {

    @Volatile
    private var result: AnimationDefinition? = null

    fun getResult(): AnimationDefinition? {
        checkOrDownload()
        return result
    }

    override fun onLoad(asset: AnimationDefinition?) {
        result = asset
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun read(stream: InputStream): AnimationDefinition? {
        try {
            return Json.decodeFromStream<AnimationDefinition>(stream)
        } catch (e: Exception) {
            EstrogenCosmetics.error("Failed to load cosmetic from url [{}]: {}", url, e)
            return null
        }
    }

    companion object {
        val CACHE: Path = GlobalStorage.getCache(MOD_ID).resolve("cosmetics").resolve("animations")

        fun fromLocalFile(file: File): CosmeticAnimation {
            if (!file.isFile) throw IllegalArgumentException("File is not a file")
            val animation = CosmeticAnimation("")
            animation.load(file, "")
            return animation
        }
    }
}