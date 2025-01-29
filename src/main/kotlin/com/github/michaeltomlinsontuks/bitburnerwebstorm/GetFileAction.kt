package com.github.michaeltomlinsontuks.bitburnerplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.ui.Messages
import io.ktor.client.*
import kotlinx.coroutines.runBlocking

class GetFileAction : AnAction("Get File from Bitburner") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE) ?: return

        val authToken = BitburnerSettings.getInstance().authToken
        val apiService = BitburnerApiService()

        val filename = virtualFile.name
        val server = "home" // Replace with your actual server name

        try {
            val response = runBlocking {
                apiService.getFile(1, filename, server, authToken)
            }

            if (response.error == null) {
                Messages.showInfoMessage(project, "File content:\n${response.result}", "Success")
            } else {
                Messages.showErrorDialog(project, "Failed to get file: ${response.error}", "Error")
            }
        } catch (ex: Exception) {
            Messages.showErrorDialog(project, "An error occurred: ${ex.message}", "Error")
        }
    }
}