package com.flipperdevices.settings.impl.composable.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.preference.pb.UpdateRequestServer
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.components.CategoryElement
import com.flipperdevices.settings.impl.composable.components.ClickableElement
import com.flipperdevices.settings.impl.composable.components.GrayDivider
import com.flipperdevices.settings.impl.composable.components.SimpleElement
import com.flipperdevices.settings.impl.composable.components.SwitchableElement
import com.flipperdevices.settings.impl.composable.elements.UpdateServerElement
import com.flipperdevices.settings.impl.model.DebugSettingAction
import com.flipperdevices.settings.impl.model.DebugSettingSwitch

@Composable
fun DebugCategory(
    settings: Settings,
    onAction: (DebugSettingAction) -> Unit,
    onDebugSettingSwitch: (DebugSettingSwitch, Boolean) -> Unit,
    onSwitchDebug: (Boolean) -> Unit,
    onChangeUpdateRequestServer: (UpdateRequestServer) -> Unit,
    modifier: Modifier = Modifier
) {
    CardCategory(modifier = modifier) {
        CategoryElement(
            titleId = R.string.debug_options,
            descriptionId = R.string.debug_options_desc,
            state = settings.enabled_debug_settings,
            onSwitchState = onSwitchDebug
        )
        if (settings.enabled_debug_settings) {
            DebugCategoryItems(
                settings = settings,
                onAction = { onAction(it) },
                onSwitch = onDebugSettingSwitch,
                onChangeUpdateRequestServer = onChangeUpdateRequestServer
            )
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun DebugCategoryItems(
    settings: Settings,
    onAction: (DebugSettingAction) -> Unit,
    onSwitch: (DebugSettingSwitch, Boolean) -> Unit,
    onChangeUpdateRequestServer: (UpdateRequestServer) -> Unit,
) {
    ClickableElement(
        titleId = R.string.debug_stress_test,
        descriptionId = R.string.debug_stress_test_desc,
        onClick = { onAction(DebugSettingAction.StressTest) }
    )
    GrayDivider()
    SimpleElement(
        titleId = R.string.debug_start_synchronization,
        onClick = { onAction(DebugSettingAction.StartSynchronization) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_ignored_unsupported_version,
        descriptionId = R.string.debug_ignored_unsupported_version_desc,
        state = settings.ignore_unsupported_version,
        onSwitchState = { onSwitch(DebugSettingSwitch.IgnoreSupportedVersion, it) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_ignored_update_version,
        descriptionId = R.string.debug_ignored_update_version_desc,
        state = settings.always_update,
        onSwitchState = { onSwitch(DebugSettingSwitch.IgnoreUpdaterVersion, it) }
    )
    GrayDivider()
    ClickableElement(
        titleId = R.string.debug_restart_rpc,
        onClick = { onAction(DebugSettingAction.RestartRPC) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_skip_autosync,
        state = settings.skip_auto_sync_in_debug,
        onSwitchState = { onSwitch(DebugSettingSwitch.SkipAutoSync, it) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_application_catalog_dev,
        state = settings.use_dev_catalog,
        onSwitchState = { onSwitch(DebugSettingSwitch.FapHubDev, it) }
    )
    GrayDivider()
    SwitchableElement(
        titleId = R.string.debug_selfupdater,
        state = settings.self_updater_debug,
        onSwitchState = { onSwitch(DebugSettingSwitch.SelfUpdaterDebug, it) }
    )
    GrayDivider()
    ClickableElement(
        titleId = R.string.debug_application_installall_dev,
        onClick = { onAction(DebugSettingAction.InstallAllFap) }
    )
    GrayDivider()
    ClickableElement(
        titleId = R.string.debug_broke_session,
        onClick = { onAction(DebugSettingAction.BrokeBytes) }
    )
    GrayDivider()
    UpdateServerElement(
        server = settings.update_request_server,
        onSelectServer = onChangeUpdateRequestServer
    )
}
