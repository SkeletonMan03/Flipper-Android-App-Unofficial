package com.flipperdevices.updater.card.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.flipperdevices.core.preference.pb.UpdateRequestServer
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.shared.ComposableDeviceInfoRowText
import com.flipperdevices.updater.card.R
import com.flipperdevices.core.ui.res.R as DesignSystem

private val servers = listOf(
    UpdateRequestServer.FLIPPER,
    UpdateRequestServer.UNLEASHED,
    UpdateRequestServer.MOMENTUM
)

@Composable
fun ComposableUpdaterFirmwareServerWithChoice(
    server: UpdateRequestServer,
    onSelectServer: (UpdateRequestServer) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var positionYParentBox by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier.onGloballyPositioned {
            val size = it.size
            val coordinateByY = it.positionInRoot().y + size.height
            positionYParentBox = coordinateByY.toInt()
        },
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier.clickableRipple { showMenu = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            ComposableDeviceInfoRowText(
                text = stringResource(getNameByUpdateRequestServer(server)),
                color = LocalPallet.current.text80
            )
            Icon(
                modifier = Modifier
                    .padding(all = 4.dp),
                painter = painterResource(DesignSystem.drawable.ic_more),
                contentDescription = stringResource(
                    id = R.string.updater_card_firmware_version_choice
                ),
                tint = LocalPallet.current.iconTint30
            )

            ComposableDropMenuFirmwareServer(
                showMenu = showMenu,
                coordinateMenuByY = positionYParentBox,
                onClickMenuItem = {
                    onSelectServer(it)
                    showMenu = false
                },
                onDismissMenu = {
                    showMenu = false
                }
            )
        }
    }
}

@Composable
private fun ComposableDropMenuFirmwareServer(
    showMenu: Boolean,
    coordinateMenuByY: Int,
    modifier: Modifier = Modifier,
    onClickMenuItem: (UpdateRequestServer) -> Unit = {},
    onDismissMenu: () -> Unit = {}
) {
    val wightDeviceInDp = LocalConfiguration.current.screenWidthDp.dp
    val wightPopupInDp = 240.dp

    val coordinateMenuByX =
        LocalDensity.current.run { (wightDeviceInDp - wightPopupInDp).toPx() }.toInt()

    if (showMenu) {
        Dialog(
            onDismissRequest = onDismissMenu,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .clickable(onClick = onDismissMenu)
            ) {
                Card(
                    modifier = Modifier
                        .width(wightPopupInDp)
                        .offset { IntOffset(coordinateMenuByX, coordinateMenuByY) }
                        .padding(end = 14.dp)
                ) {
                    ComposableFirmwareServerColumn(onClickMenuItem = onClickMenuItem)
                }
            }
        }
    }
}

@Composable
private fun ComposableFirmwareServerColumn(
    modifier: Modifier = Modifier,
    onClickMenuItem: (UpdateRequestServer) -> Unit
) {
    Column(modifier = modifier) {
        servers.forEachIndexed { index, server ->
            Column(
                modifier = Modifier
                    .clickableRipple { onClickMenuItem(server) }
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(getNameByUpdateRequestServer(server)),
                    color = LocalPallet.current.text80,
                    style = LocalTypography.current.bodyM14
                )
            }
            if (servers.lastIndex != index) {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = LocalPallet.current.divider12
                )
            }
        }
    }
}

private fun getNameByUpdateRequestServer(server: UpdateRequestServer): Int {
    return when (server) {
        UpdateRequestServer.FLIPPER -> R.string.updater_card_updater_server_flipper
        UpdateRequestServer.UNLEASHED -> R.string.updater_card_updater_server_unleashed
        UpdateRequestServer.MOMENTUM -> R.string.updater_card_updater_server_momentum
        else -> R.string.updater_card_updater_server_flipper
    }
}
