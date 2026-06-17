package com.flipperdevices.updater.downloader.api

import androidx.datastore.core.DataStore
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.preference.pb.UpdateRequestServer
import com.flipperdevices.updater.api.DownloadAndUnpackDelegateApi
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.downloader.model.ArtifactType
import com.flipperdevices.updater.downloader.model.FirmwareDirectoryListeningResponse
import com.flipperdevices.updater.downloader.model.Target
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.DownloadProgress
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.SubGhzProvisioningException
import com.flipperdevices.updater.model.SubGhzProvisioningModel
import com.flipperdevices.updater.model.VersionFiles
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.plugins.timeout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.util.EnumMap
import javax.inject.Inject

private const val FLIPPER_URL = "https://update.flipperzero.one/firmware/directory.json"
private const val UNLEASHED_URL = "https://up.unleashedflip.com/directory.json"
private const val MOMENTUM_URL = "https://up.momentum-fw.dev/firmware/directory.json"

private const val NETWORK_TIMEOUT_MS = 10_000L

@ContributesBinding(AppGraph::class, DownloaderApi::class)
class DownloaderApiImpl @Inject constructor(
    private val client: HttpClient,
    private val downloadAndUnpackDelegateApi: DownloadAndUnpackDelegateApi,
    private val storageProvider: FlipperStorageProvider,
    private val dataStoreSettings: DataStore<Settings>
) : DownloaderApi, LogTagProvider {
    override val TAG = "DownloaderApi"

    private val versionCacheMutex = Mutex()
    private var cachedVersionMap: EnumMap<FirmwareChannel, VersionFiles>? = null
    private var cachedUrl: String? = null

    override suspend fun getLatestVersion(): EnumMap<FirmwareChannel, VersionFiles> = versionCacheMutex.withLock {
        val settings = withTimeoutOrNull(NETWORK_TIMEOUT_MS) {
            dataStoreSettings.data.first()
        }
        val url = when (settings?.update_request_server) {
            UpdateRequestServer.FLIPPER -> FLIPPER_URL
            UpdateRequestServer.UNLEASHED -> UNLEASHED_URL
            UpdateRequestServer.MOMENTUM -> MOMENTUM_URL
            else -> FLIPPER_URL
        }

        val currentCachedVersionMap = cachedVersionMap
        if (currentCachedVersionMap != null && cachedUrl == url) {
            verbose { "Return cached version map for $url" }
            return@withLock currentCachedVersionMap
        }

        val versionMap: EnumMap<FirmwareChannel, VersionFiles> =
            EnumMap(FirmwareChannel::class.java)

        val response = try {
            client.get(
                urlString = url
            ) {
                timeout {
                    requestTimeoutMillis = NETWORK_TIMEOUT_MS
                    connectTimeoutMillis = NETWORK_TIMEOUT_MS
                }
            }.body<FirmwareDirectoryListeningResponse>()
        } catch (e: Exception) {
            info { "Failed to fetch firmware directory from $url: ${e.message}" }
            throw e
        }

        verbose { "Receive response from server $url" }

        response.channels.map { channel ->
            val channelEnum = when (channel.id) {
                "dev", "development" -> FirmwareChannel.DEV
                "release" -> FirmwareChannel.RELEASE
                "release-candidate" -> FirmwareChannel.RELEASE_CANDIDATE
                else -> {
                    verbose { "Unknown channel id: ${channel.id}" }
                    null
                }
            }
            channelEnum to channel.versions?.maxByOrNull { it.timestamp }
        }.mapNotNull { (channel, versions) ->
            if (channel == null || versions == null) {
                null
            } else {
                channel to versions
            }
        }.forEach { (channel, version) ->
            val updaterFile = version
                .files
                .filter { it.type == ArtifactType.UPDATE_TGZ }
                .find { it.target == Target.F7 }
                ?: return@forEach

            versionMap[channel] = VersionFiles(
                version = FirmwareVersion(
                    channel,
                    version.version.clearVersion()
                ),
                updaterFile = DistributionFile(
                    updaterFile.url,
                    updaterFile.sha256
                ),
                changelog = version.changelog
            )
        }

        verbose { "Result version map for $url is $versionMap" }

        cachedVersionMap = versionMap
        cachedUrl = url

        return@withLock versionMap
    }

    @Throws(SubGhzProvisioningException::class)
    override suspend fun getSubGhzProvisioning(): SubGhzProvisioningModel {
        return SubGhzProvisioningModel(
            countries = emptyMap(),
            country = null,
            defaults = emptyList()
        )
    }

    override fun download(
        distributionFile: DistributionFile,
        target: File,
        decompress: Boolean
    ): Flow<DownloadProgress> = channelFlow {
        info { "Request download $distributionFile" }
        if (decompress) {
            storageProvider.useTemporaryFile { tempFile ->
                downloadAndUnpackDelegateApi.download(
                    distributionFile,
                    tempFile.toFile()
                ) { processedBytes, totalBytes ->
                    send(DownloadProgress.InProgress(processedBytes, totalBytes))
                }
                info { "File downloaded in $tempFile" }

                downloadAndUnpackDelegateApi.unpack(tempFile.toFile(), target)
                info {
                    "Unpack finished in ${target.absolutePath} ${target.listFiles()?.size} files"
                }
            }
        } else {
            downloadAndUnpackDelegateApi.download(
                distributionFile,
                target
            ) { processedBytes, totalBytes ->
                send(DownloadProgress.InProgress(processedBytes, totalBytes))
            }
            info { "File downloaded in ${target.absolutePath}" }
        }
    }
}

private fun String.clearVersion(): String {
    return replace("-rc", "").trim()
}
