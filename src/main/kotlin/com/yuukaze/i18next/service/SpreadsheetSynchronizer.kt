package com.yuukaze.i18next.service

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.yuukaze.i18next.PLUGIN_DIRECTORY
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.security.GeneralSecurityException


class SpreadsheetSynchronizer @Throws(
    IOException::class,
    GeneralSecurityException::class
) constructor() {
    var sheetService: Sheets? = null
        private set
    var spreadSheetId: String? = null

    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        // Load client secrets.
        val `in` = SpreadsheetSynchronizer::class.java.getResourceAsStream(
            CREDENTIALS_FILE_PATH
        )
            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        val clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH.toUri())))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    private fun prepare() {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        sheetService = Sheets.Builder(
            httpTransport,
            JSON_FACTORY,
            getCredentials(httpTransport)
        )
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    companion object {
        private val SCOPES = listOf(SheetsScopes.SPREADSHEETS)
        private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
        private const val CREDENTIALS_FILE_PATH = "/credentials.json"
        private val TOKENS_DIRECTORY_PATH = PLUGIN_DIRECTORY.resolve("token")
        private const val APPLICATION_NAME = "IntelliJ i18n Translations"
    }

    init {
        if (!Files.exists(TOKENS_DIRECTORY_PATH))
            Files.createDirectories(TOKENS_DIRECTORY_PATH)
        prepare()
    }
}