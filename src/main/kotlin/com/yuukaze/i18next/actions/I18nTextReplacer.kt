package com.yuukaze.i18next.actions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.yuukaze.i18next.actions.KeyRequest.manipulateTranslationKey
import com.yuukaze.i18next.factory.TranslationExtractor
import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.service.getEasyI18nService
import com.yuukaze.i18next.ui.Icons
import com.yuukaze.i18next.utils.*
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.*
import javax.swing.Icon

@Suppress("IntentionDescriptionNotFoundInspection")
class I18nTextReplacer : PsiElementBaseIntentionAction(), IntentionAction, Iconable {
    override fun getFamilyName(): String = "EasyI18n"
    override fun getText(): String = "I18n-ize..."
    override fun getIcon(flags: Int): Icon = Icons.ToolWindowIcon

    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        ApplicationManager.getApplication()
            .invokeLater { doInvoke(editor, project, element) }
    }

    private val getExtractor = { e: PsiElement ->
        e.project.getEasyI18nService()
            .mainFactory()
            .translationExtractors()
            .filter { it.canExtract(e) }
            .whenMatches { extractors -> !extractors.any { it.isExtracted(e) } }
            ?.firstOrNull()
            ?: DefaultExtractor()
    }.memoize(1024)


    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement
    ): Boolean = editor.selectionModel.hasSelection() || getExtractor(element).canExtract(
        element
    )

    private fun doInvoke(editor: Editor, project: Project, element: PsiElement) {
        if (editor.selectionModel.hasSelection())
            Objects.requireNonNull(editor.selectionModel.selectedText)?.let {
                manipulateTranslationKey(
                    project,
                    it,
                    editor,
                    this::addToClipboard
                )
            }
        else {
            val extractor = getExtractor(element)
            val text = extractor.text(element).trim()
            manipulateTranslationKey(
                project,
                text,
                editor
            ) {
                //replaceFromPsi(key, editor, element, extractor)
            }
        }
    }

    private fun addToClipboard(s: Any) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val selection = StringSelection(
            when (s) {
                is KeyedTranslation -> "t(\"${s.key}\")"
                is SingleKeyMatcher -> "t(\"${s.key}\")"
                is VariableKeyMatcher -> "t(\"${s.key}\", ${
                    s.params.toI18nParamsObject()
                })"
                else -> throw Error("Must be a key matcher")
            }
        )
        clipboard.setContents(selection, selection)
    }

    private fun replaceFromPsi(
        key: KeyedTranslation,
        editor: Editor,
        element: PsiElement,
        extractor: TranslationExtractor
    ) {
        val document = editor.document
        val template = extractor.template(element)
        val range = extractor.textRange(element)

        CommandProcessor.getInstance().executeCommand(
            element.project,
            {
                ApplicationManager.getApplication().runWriteAction {
                    @Suppress("IntentionDescriptionNotFoundInspection")
                    document.replaceString(
                        range.startOffset,
                        range.endOffset,
                        template(key)
                    )
                }
            },
            "EasyI18n",
            UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION
        )
        extractor.postProcess(editor, range.startOffset)
        editor.caretModel.primaryCaret.removeSelection()
    }
}