package com.github.michaeltomlinsontuks.bitburnerplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import io.ktor.client.*
import kotlinx.coroutines.runBlocking

class GetFileNamesAction : AnAction("Get File Names from Bitburner") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val authToken = BitburnerSettings.getInstance().authToken
        val apiService = BitburnerApiService()

        val server = "home" // Replace with your actual server name

        try {
            val response = runBlocking {
                apiService.getFileNames(1, server, authToken)
            }

            if (response.error == null) {
                Messages.showMessageDialog(project, "File names: ${response.result?.joinToString(", ")}", "Success", Messages.getInformationIcon())
            } else {
                Messages.showMessageDialog(project, "Failed to get file names: ${response.error}", "Error", Messages.getErrorIcon())
            }
        } catch (ex: Exception) {
            Messages.showMessageDialog(project, "An error occurred: ${ex.message}", "Error", Messages.getErrorIcon())
        } finally {
            // client.close() // Removed this line as client is no longer created
        }
    }
}