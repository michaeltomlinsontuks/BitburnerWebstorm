package com.github.michaeltomlinsontuks.bitburnerplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import io.ktor.client.*
import kotlinx.coroutines.runBlocking

class GetDefinitionFileAction : AnAction("Get Definition File from Bitburner") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val authToken = BitburnerSettings.getInstance().authToken
        val apiService = BitburnerApiService()

        try {
            val response = runBlocking {
                apiService.getDefinitionFile(1, authToken)
            }

            if (response.error == null) {
                Messages.showInfoMessage(project, "Definition file:\n${response.result}", "Success")
            } else {
                Messages.showErrorDialog(project, "Failed to get definition file: ${response.error}", "Error")
            }
        } catch (ex: Exception) {
            Messages.showErrorDialog(project, "An error occurred: ${ex.message}", "Error")
        }
    }
}