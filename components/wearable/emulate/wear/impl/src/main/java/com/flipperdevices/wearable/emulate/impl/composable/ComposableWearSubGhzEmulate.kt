package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.ui.ktx.onHoldPress
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.flipperdevices.wearable.emulate.impl.R

@Composable
fun ComposableWearSubGhzEmulate(
    emulateProgress: EmulateProgress?,
    keyEmulateUiApi: KeyEmulateUiApi,
    onClickEmulate: () -> Unit,
    onSinglePress: () -> Unit,
    modifier: Modifier = Modifier,
    onStopEmulate: () -> Unit
) {
    val buttonActiveModifier = Modifier.onHoldPress(
        onTap = onSinglePress,
        onLongPressStart = onClickEmulate,
        onLongPressEnd = onStopEmulate
    )

    val color = if (emulateProgress == null) {
        LocalPallet.current.actionOnFlipperSubGhzEnable
    } else {
        LocalPallet.current.actionOnFlipperSubGhzProgress
    }

    val progressColor = if (emulateProgress == null) {
        Color.Transparent
    } else {
        LocalPallet.current.actionOnFlipperSubGhzEnable
    }

    val textId = if (emulateProgress == null) {
        R.string.keyscreen_send
    } else {
        R.string.keyscreen_sending
    }

    keyEmulateUiApi.ComposableEmulateButtonRaw(
        modifier = modifier,
        buttonContentModifier = buttonActiveModifier,
        picture = null,
        textId = textId,
        color = color,
        progressColor = progressColor,
        emulateProgress = emulateProgress
    )
}
