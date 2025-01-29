package com.github.michaeltomlinsontuks.bitburnerplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import io.ktor.client.*
import kotlinx.coroutines.runBlocking

class GetAllFilesAction : AnAction("Get All Files from Bitburner") {
    private val apiService = BitburnerApiService()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val settings = BitburnerSettings.getInstance()
        if (settings.authToken.isNullOrEmpty()) {
            Messages.showErrorDialog(project, "Please set your auth token in Settings -> Bitburner Settings", "Error")
            return
        }

        runBlocking {
            try {
                val server = "home" // Replace with your actual server name
                val response = apiService.getAllFiles(1, server, settings.authToken)

                if (response.error == null) {
                    val fileContents = response.result?.entries?.joinToString("\n") { "${it.key}: ${it.value}" }
                    Messages.showInfoMessage(project, "Files:\n$fileContents", "Success")
                } else {
                    Messages.showErrorDialog(project, "Failed to get files: ${response.error}", "Error")
                }
            } catch (ex: Exception) {
                Messages.showErrorDialog(project, "An error occurred: ${ex.message}", "Error")
            }
        }
    }
}