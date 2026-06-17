package com.flipperdevices.updater.card.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.info.shared.ComposableDeviceInfoRow
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.core.preference.pb.UpdateRequestServer
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.card.viewmodel.UpdateRequestViewModel
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateRequest

@Composable
fun ComposableFirmwareUpdaterContent(
    updateRequestViewModel: UpdateRequestViewModel,
    version: FirmwareVersion?,
    updateCardState: UpdateCardState,
    selectedServer: UpdateRequestServer,
    onSelectServer: (UpdateRequestServer) -> Unit,
    onSelectFirmwareChannel: (FirmwareChannel) -> Unit,
    modifier: Modifier = Modifier,
    onStartUpdateRequest: (UpdateRequest) -> Unit
) {
    val inProgress = version == null
    ComposableDeviceInfoRow(
        titleId = R.string.updater_card_updater_preferred_firmware,
        inProgress = false,
        content = {
            ComposableUpdaterFirmwareServerWithChoice(
                modifier = it,
                onSelectServer = onSelectServer,
                server = selectedServer
            )
        },
        modifier = modifier
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRow(
        titleId = R.string.updater_card_updater_channel,
        inProgress = false,
        content = {
            ComposableUpdaterFirmwareVersionWithChoice(
                modifier = it,
                onSelectFirmwareChannel = onSelectFirmwareChannel,
                version = version
            )
        },
        modifier = modifier
    )
    ComposableInfoDivider()
    ComposableUpdateButton(
        updateCardState = updateCardState,
        inProgress = inProgress,
        onStartUpdateRequest = onStartUpdateRequest,
        updateRequestViewModel = updateRequestViewModel
    )
}
