package com.yuukaze.i18next.utils

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.yuukaze.i18next.io.TranslatorIO
import com.yuukaze.i18next.io.implementation.JsonTranslatorIO
import com.yuukaze.i18next.io.implementation.PropertiesTranslatorIO
import java.io.File
import java.util.*

/**
 * IO operations utility.
 */
object IOUtil {
    var getFile: (String) -> VirtualFile? =
        { file: String -> LocalFileSystem.getInstance().findFileByIoFile(File(file)) }

    /**
     * Determines the [TranslatorIO] which should be used for the specified directoryPath
     * @param directoryPath The full path to the parent directory which holds the translation files
     * @return IO handler to use for file operations
     */
    fun determineFormat(directoryPath: String): TranslatorIO {
        val directory = getFile(directoryPath)
        require(!(directory == null || directory.children == null)) { "Specified folder is invalid ($directoryPath)" }
        val any = Arrays.stream(directory.children).map { f: VirtualFile -> f.children[0] }.findAny()
        check(any.isPresent) { "Could not determine i18n format. At least one locale file must be defined" }
        return when (any.get().fileType.defaultExtension.toLowerCase()) {
            "json" -> JsonTranslatorIO()
            "properties" -> PropertiesTranslatorIO()
            else -> throw UnsupportedOperationException(
                "Unsupported i18n locale file format: " +
                        any.get().fileType.defaultExtension
            )
        }
    }
}