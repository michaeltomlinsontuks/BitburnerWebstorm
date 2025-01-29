package com.github.michaeltomlinsontuks.bitburnerplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.ktor.client.*
import kotlinx.coroutines.runBlocking

class PushFileAction : AnAction("Push File to Bitburner") {
    override fun update(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        
        e.presentation.isEnabled = project != null && 
                                  file != null && 
                                  GameConfig.isValidFileExtension(file.name) &&
                                  BitburnerSettings.getInstance().authToken.isNotBlank()
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val notifier = BitburnerNotifier(project)

        if (!GameConfig.isValidFileExtension(virtualFile.name)) {
            notifier.notifyError("Invalid file extension. Supported: ${GameConfig.VALID_FILE_EXTENSIONS}")
            return
        }

        val settings = BitburnerSettings.getInstance()
        if (settings.authToken.isBlank()) {
            notifier.notifyError("Auth token not configured. Please set it in Settings -> Bitburner Settings")
            return
        }

        val client = HttpClient()
        val apiService = BitburnerApiService()

        try {
            val filename = virtualFile.name
            val content = String(virtualFile.contentsToByteArray())
            val server = "home" // Default server

            val response = runBlocking {
                apiService.pushFile(1, filename, content, server, settings.authToken)
            }

            if (response.error == null) {
                notifier.notifyInfo("Successfully pushed ${filename} to Bitburner")
            } else {
                notifier.notifyError("Failed to push file: ${response.error}")
            }
        } catch (ex: Exception) {
            notifier.notifyError("An error occurred: ${ex.message}")
        } finally {
            client.close()
        }
    }
}