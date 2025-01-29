package com.github.michaeltomlinsontuks.bitburnerplugin

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import javax.swing.JComponent
import javax.swing.JTextField
import javax.swing.JCheckBox
import com.intellij.ui.dsl.builder.panel

class BitburnerSettingsComponent : Configurable {
    private val authTokenField = JTextField()
    private val scriptRootField = JTextField()
    private val fileWatcherEnabledCheckBox = JCheckBox()
    private val showPushSuccessNotificationCheckBox = JCheckBox()
    private val showFileWatcherEnabledNotificationCheckBox = JCheckBox()

    override fun createComponent(): JComponent {
        return panel {
            row("Auth Token:") { cell(authTokenField) }
            row("Script Root:") { cell(scriptRootField) }
            row("Enable File Watcher:") { cell(fileWatcherEnabledCheckBox) }
            row("Show Push Success Notification:") { cell(showPushSuccessNotificationCheckBox) }
            row("Show File Watcher Enabled Notification:") { cell(showFileWatcherEnabledNotificationCheckBox) }
        }
    }

    private fun validateSettings(): List<String> {
        val errors = mutableListOf<String>()
        if (authTokenField.text.isBlank()) {
            errors.add("Auth token cannot be empty")
        }
        if (!scriptRootField.text.startsWith("./")) {
            errors.add("Script root must start with ./")
        }
        return errors
    }

    override fun isModified(): Boolean {
        val settings = BitburnerSettings.getInstance()
        return authTokenField.text != settings.authToken ||
                scriptRootField.text != settings.scriptRoot ||
                fileWatcherEnabledCheckBox.isSelected != settings.fileWatcherEnabled ||
                showPushSuccessNotificationCheckBox.isSelected != settings.showPushSuccessNotification ||
                showFileWatcherEnabledNotificationCheckBox.isSelected != settings.showFileWatcherEnabledNotification
    }

    override fun apply() {
        val errors = validateSettings()
        if (errors.isNotEmpty()) {
            throw ConfigurationException(errors.joinToString("\n"))
        }
        
        val settings = BitburnerSettings.getInstance()
        settings.authToken = authTokenField.text
        settings.scriptRoot = scriptRootField.text
        settings.fileWatcherEnabled = fileWatcherEnabledCheckBox.isSelected
        settings.showPushSuccessNotification = showPushSuccessNotificationCheckBox.isSelected
        settings.showFileWatcherEnabledNotification = showFileWatcherEnabledNotificationCheckBox.isSelected
    }

    override fun reset() {
        val settings = BitburnerSettings.getInstance()
        authTokenField.text = settings.authToken
        scriptRootField.text = settings.scriptRoot
        fileWatcherEnabledCheckBox.isSelected = settings.fileWatcherEnabled
        showPushSuccessNotificationCheckBox.isSelected = settings.showPushSuccessNotification
        showFileWatcherEnabledNotificationCheckBox.isSelected = settings.showFileWatcherEnabledNotification
    }

    override fun getDisplayName(): String {
        return "Bitburner Settings"
    }
}