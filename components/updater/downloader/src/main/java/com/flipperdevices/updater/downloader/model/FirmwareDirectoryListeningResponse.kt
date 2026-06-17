package com.flipperdevices.updater.downloader.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FirmwareDirectoryListeningResponse(
    @SerialName("channels")
    val channels: List<FirmwareVersionChannel>
)

@Serializable
internal data class FirmwareVersionChannel(
    @SerialName("id")
    val id: String? = null,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("versions")
    val versions: List<FirmwareVersion>? = null
)

@Serializable
internal data class FirmwareVersion(
    @SerialName("version")
    val version: String,
    @SerialName("changelog")
    val changelog: String,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("files")
    val files: List<FirmwareFile>
)

@Serializable
internal data class FirmwareFile(
    @SerialName("url")
    val url: String,
    @SerialName("target")
    val target: Target? = null,
    @SerialName("type")
    val type: ArtifactType? = null,
    @SerialName("sha256")
    val sha256: String
)

@Serializable
internal enum class Target {
    @SerialName("any")
    ANY,

    @SerialName("f7")
    F7
}

@Serializable
internal enum class ArtifactType {
    @SerialName("update_tgz")
    UPDATE_TGZ
}
