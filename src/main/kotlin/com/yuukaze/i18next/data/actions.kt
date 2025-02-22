package com.yuukaze.i18next.data

import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.lang.javascript.psi.impl.JSPsiElementFactory
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.model.Translations
import com.yuukaze.i18next.service.*
import com.yuukaze.i18next.utils.IOUtil
import com.yuukaze.reduxkotlin.thunk.Thunk
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.reduxkotlin.Store
import kotlin.streams.toList

data class InitProjectAction(val project: Project)

data class SearchAction(val text: String)

data class ReloadTranslations(val translations: Translations)

open class UpdateTranslation
    (
    val origin: KeyedTranslation?,
    val change: KeyedTranslation?
) {
    val isCreation: Boolean
        get() = origin == null
    val isDeletion: Boolean
        get() = change == null
    val isKeyChange: Boolean
        get() = origin != null && change != null && origin.key != change.key

    override fun toString(): String {
        return "UpdateTranslation{" +
                "origin=" + origin +
                ", change=" + change +
                '}'
    }
}

class CreateTranslation(translation: KeyedTranslation) :
    UpdateTranslation(null, translation)

class DeleteTranslation(translation: KeyedTranslation) :
    UpdateTranslation(translation, null) {
    override fun toString(): String = "DeleteTranslation(origin=$origin)"
}

enum class TableFilterMode {
    ALL, SHOW_MISSING, SHOW_UNUSED
}

data class TableFilterAction(val mode: TableFilterMode)

data class ReloadPsi(val map: Map<String, PsiElementSet>) {
    override fun toString(): String = "ReloadPsi(map.size=${map.size})"
}

data class SelectKeyAction(val key: String)

fun reloadI18nData(): Thunk<AppState> = { dispatch, getState, _ ->
    runBlocking {
        val project = getState().project!!
        listOf(launch {
            lateinit var translations: Translations
            val localesPath = project.getService(
                EasyI18nSettingsService::class.java
            ).state.localesPath
            if (localesPath.isEmpty()) {
                translations = Translations()
            } else {
                val io = IOUtil.determineFormat(localesPath)
                io.read(localesPath) {
                    translations = it ?: Translations()
                }
            }
            dispatch(ReloadTranslations(translations = translations))
        }, launch {
            project.getEasyI18nReferenceService().processAll {
                dispatch(ReloadPsi(map = it))
            }
        }).joinAll()
    }
}

fun selectI18nKey(key: String) {
    i18nStore.dispatch(SelectKeyAction(key = key))
}

@Suppress("FunctionName")
private fun _changeI18nKey(key: String, newKey: String): Thunk<AppState> =
    { dispatch, getState, _ ->
        runBlocking {
            val state = getState()
            val project = state.project!!
            val psiList: List<PsiElement> = (state.psiMap!!.get(key)!!).stream().toList()
            runUndoTransparentWriteAction {
                psiList.onEach {
                    if (it is JSLiteralExpression) {
                        it.replace(
                            JSPsiElementFactory.createJSExpression(
                                "\"$newKey\"",
                                it
                            )
                        )
                    }
                }
                commitPsiElementChanges(project, psiList)
            }
        }
    }

fun changeI18nKey(key: String, newKey: String) {
    i18nStore.dispatch(_changeI18nKey(key, newKey))
}

fun updateI18nTranslation(key: String, newKey: String, messages: MutableMap<String, String>): Thunk<AppState> =
    { dispatch, getState, _ ->
        dispatch(
            UpdateTranslation(
                KeyedTranslation(key, messages),
                KeyedTranslation(newKey, messages)
            )
        )
        getState().project!!.getEasyI18nDataStore().doWriteToDisk()
    }

val Store<AppState>.project
    get() = this.state.project!!

private fun Store<AppState>.executeUpdateTransAction(action: Any, callback: (() -> Unit)? = null) {
    this.dispatch(action)
    this.project.getEasyI18nDataStore().doWriteToDisk(callback)
}

fun Store<AppState>.deleteI18nTranslation(key: KeyedTranslation) {
    this.executeUpdateTransAction(DeleteTranslation(key)) {
        Notifier.notifySuccess(this.project, "Delete key \"${key.key}\" success")
    }
}

fun Store<AppState>.deleteI18nTranslation(key: String) {
    this.deleteI18nTranslation(KeyedTranslation(key, null))
}

fun Store<AppState>.duplicateI18nTranslation(key: String, newKey: String) {
    val project = state.project!!
    val translations = state.translations!!
    val keyObj = translations.getNode(key)
    translations.getOrCreateNode(newKey).apply {
        value = mutableMapOf<String, String>().apply {
            putAll(keyObj!!.value)
        }
    }
    project.getEasyI18nDataStore().doWriteToDisk {
        dispatch(selectI18nKey(newKey))
        Notifier.notifySuccess(this.project, "Duplicate key \"${key}\" to \"$newKey\" success")
    }
}