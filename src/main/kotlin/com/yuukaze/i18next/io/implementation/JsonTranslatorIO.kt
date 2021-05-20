package com.yuukaze.i18next.io.implementation

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.vfs.VirtualFile
import com.yuukaze.i18next.io.TranslatorIO
import com.yuukaze.i18next.model.LocalizedNode
import com.yuukaze.i18next.model.Translations
import com.yuukaze.i18next.utils.IOUtil
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

/**
 * Implementation for JSON translation files.
 */
class JsonTranslatorIO : TranslatorIO {
    override fun read(directoryPath: String, callback: Consumer<Translations?>) {
//        ApplicationManager.getApplication().saveAll() // Save opened files (required if new locales were added)
        val x = 1
        runReadAction {
            val directory = IOUtil.getFile(directoryPath)
            require(!(directory == null || directory.children == null)) { "Specified folder is invalid ($directoryPath)" }
            val files =
                Arrays.stream(directory.children).map { f: VirtualFile -> f.children[0] }.collect(Collectors.toList())
            val locales: MutableList<String> = ArrayList()
            val nodes = LocalizedNode(LocalizedNode.ROOT_KEY, ArrayList())
            try {
                for (file in files) {
                    val localeName = file.parent.nameWithoutExtension
                    locales.add(localeName)
                    val tree = JsonParser.parseReader(InputStreamReader(file.inputStream, file.charset)).asJsonObject
                    readTree(localeName, tree, nodes)
                }
                callback.accept(Translations(locales, nodes))
            } catch (e: IOException) {
                e.printStackTrace()
                callback.accept(null)
            }
        }
    }

    override fun save(translations: Translations, directoryPath: String, callback: Consumer<Boolean>) {
        val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
        runWriteAction {
            try {
                for (locale in translations.locales) {
                    val content = JsonObject()
                    writeTree(locale, content, translations.nodes)
                    val fullPath = "$directoryPath/$locale/translation.$FILE_EXTENSION"
                    val file = IOUtil.getFile(fullPath)
                    Objects.requireNonNull(file)!!.setBinaryContent(gson.toJson(content).toByteArray(file!!.charset))

                }
                callback.accept(true)
            } catch (e: IOException) {
                e.printStackTrace()
                callback.accept(false)
            }
        }
    }

    private fun writeTree(locale: String, parent: JsonObject, node: LocalizedNode) {
        if (node.isLeaf && node.key != LocalizedNode.ROOT_KEY) {
            if (node.value[locale]?.let {
                    it.isNotEmpty() && it.isNotBlank()
                } == true) {
                parent.add(node.key, JsonPrimitive(node.value[locale]))
            }
        } else {
            for (children in node.children) {
                if (children.isLeaf) {
                    writeTree(locale, parent, children)
                } else {
                    val childrenJson = JsonObject()
                    writeTree(locale, childrenJson, children)
                    if (childrenJson.size() > 0) {
                        parent.add(children.key, childrenJson)
                    }
                }
            }
        }
    }

    private fun readTree(locale: String, json: JsonObject, data: LocalizedNode) {
        for ((key, value) in json.entrySet()) {
            try {
                // Try to go one level deeper
                val childObject = value.asJsonObject
                var childrenNode = data.getChildren(key)
                if (childrenNode == null) {
                    childrenNode = LocalizedNode(key, ArrayList())
                    data.addChildren(childrenNode)
                }
                readTree(locale, childObject, childrenNode)
            } catch (e: IllegalStateException) { // Reached end for this node
                var leafNode = data.getChildren(key)
                if (leafNode == null) {
                    leafNode = LocalizedNode(key, HashMap())
                    data.addChildren(leafNode)
                }
                val messages = leafNode.value
                messages[locale] = value.asString
                leafNode.value = messages
            }
        }
    }

    companion object {
        private const val FILE_EXTENSION = "json"
    }
}