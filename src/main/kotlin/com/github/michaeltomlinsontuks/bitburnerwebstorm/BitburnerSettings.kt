package com.github.michaeltomlinsontuks.bitburnerplugin

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "BitburnerSettings", storages = [Storage("BitburnerSettings.xml")])
class BitburnerSettings : PersistentStateComponent<BitburnerSettings> {
    var authToken: String = ""
    var scriptRoot: String = "./"
    var fileWatcherEnabled: Boolean = false
    var showPushSuccessNotification: Boolean = false
    var showFileWatcherEnabledNotification: Boolean = false

    override fun getState(): BitburnerSettings? {
        return this
    }

    override fun loadState(state: BitburnerSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): BitburnerSettings {
            return service()
        }
    }
}