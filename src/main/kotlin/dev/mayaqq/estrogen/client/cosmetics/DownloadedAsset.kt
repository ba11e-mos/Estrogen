package dev.mayaqq.estrogen.client.cosmetics

import com.google.common.hash.Hashing
import dev.mayaqq.cynosure.core.TriState
import dev.mayaqq.estrogen.Estrogen
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.minecraft.Util
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer


abstract class DownloadedAsset<T>(val cache: Path, val url: String, val hash: String = url.urlHash()) {
    private val downloadState: AtomicReference<TriState> = AtomicReference<TriState>(TriState.INTERMEDIATE)
    private var job: Job? = null

    protected fun checkOrDownload() {
        if (downloadState.get() !== TriState.INTERMEDIATE) return
        downloadState.setRelease(TriState.FALSE)
        Util.backgroundExecutor().execute { this.load(cache.resolve(hash).toFile(), url) }
    }

    protected fun load(file: File?, url: String?) {
        if (this.job == null) {
            val asset: T? = if (file != null && file.isFile) read(file.inputStream()) else null

            if (asset != null) this::consumeResult else {
                job = runDownload(
                    url!!,
                    file!!
                ) { read(it) }
            }
        }
    }

    private fun consumeResult(asset: T?) {
        downloadState.set(TriState.TRUE)
        this.onLoad(asset)
    }

    protected abstract fun onLoad(asset: T?)

    protected abstract fun read(stream: InputStream): T?

    companion object {
        val ALLOWED_DOMAINS = setOf(
            "teamresourceful.com",
            "files.teamresourceful.com",
            "raw.githubusercontent.com",
            "femboy-hooters.net",
            "images.teamresourceful.com"
        )

        val CLIENT: HttpClient = HttpClient
            .newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build()

        private fun String.urlHash() = Hashing.sha1().hashUnencodedChars(FilenameUtils.getBaseName(this)).toString()

        @OptIn(DelicateCoroutinesApi::class)
        fun runDownload(uri: String, file: File, callback: Consumer<InputStream>): Job {
            return GlobalScope.launch  {
                createUrl(uri)?.let { url ->
                    try {
                        val request: HttpRequest = HttpRequest.newBuilder(url)
                            .GET()
                            .build()

                        val stream: HttpResponse<InputStream> =
                            CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream())
                        if (stream.statusCode() / 100 != 2) {
                            Estrogen.error("Failed to download asset: {} Status Code: {}", uri, stream.statusCode())
                            return@let
                        }

                        FileUtils.copyInputStreamToFile(stream.body(), file)
                        try {
                            FileUtils.openInputStream(file).use { fileStream ->
                                callback.accept(fileStream)
                            }
                        } catch (ex: IOException) {
                            Estrogen.error("Failed to process asset: {}", uri, ex)
                        }
                    } catch (ex: IOException) {
                        Estrogen.error("Failed to download asset: {}", uri, ex)
                    } catch (ex: InterruptedException) {
                        Estrogen.error("Failed to download asset: {}", uri, ex)
                    }
                }
            }
        }

        private fun createUrl(string: String?): URI? {
            if (string == null) return null
            try {
                val url: URI = URI.create(string)
                if (!ALLOWED_DOMAINS.contains(url.host)) {
                    EstrogenCosmetics.warn("Tried to load texture from disallowed domain: {}", url.host)
                    return null
                }
                if (!url.scheme.equals("https")) return null
                return url
            } catch (_: Exception) {
                return null
            }
        }
    }
}