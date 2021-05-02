package com.yuukaze.i18next.service

import com.intellij.lang.javascript.JSElementTypes
import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.lang.javascript.TypeScriptJSXFileType
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.yuukaze.i18next.data.ReloadPsi
import com.yuukaze.i18next.data.i18nStore

typealias PsiElementSet = MutableSet<PsiElement>

class I18nKeyReferenceManager(private val project: Project) {
  companion object {
    val jsFileTypes =
      listOf(TypeScriptFileType.INSTANCE, TypeScriptJSXFileType.INSTANCE)
  }

  val map = mutableMapOf<String, PsiElementSet>()

  private val searchScope = GlobalSearchScope.projectScope(project)

  private val virtualJsFiles: Collection<VirtualFile>
    get() = jsFileTypes.map {
      FileTypeIndex.getFiles(
        it,
        searchScope
      )
    }.flatten()

  private fun addElementToMap(key: String, element: PsiElement) {
    val unquotedKey = key.removeSurrounding("\"")
    if (map[unquotedKey] == null)
      map[unquotedKey] = mutableSetOf()
    map[unquotedKey]!!.add(element)
  }

  private fun <T : PsiFile> T.getI18nEntryElement(callback: (String, PsiElement) -> Unit) =
    PsiTreeUtil.processElements(this, I18nEntryProcessor(callback))

  fun processAll(callback: (Map<String, PsiElementSet>) -> Unit) {
    runBackgroundableTask("Scanning i18n entries...", project, false) {
      it.isIndeterminate = false
      ReadAction
        .nonBlocking {
          val length = virtualJsFiles.size
          var i = 0
          for (f in virtualJsFiles) {
            it.fraction = (++i).toDouble() / length
            PsiManager.getInstance(project).findFile(f)
              ?.getI18nEntryElement(this::addElementToMap)
          }
          callback(map)
        }.inSmartMode(project)
        .executeSynchronously()
    }

  }

  fun processAll() {
    processAll {
      i18nStore.dispatch(ReloadPsi(map = it))
    }
  }

  class I18nEntryProcessor(val callback: (String, PsiElement) -> Unit) :
    PsiElementProcessor<PsiElement> {
    private fun processHookCall(element: PsiElement): Boolean {
      val validate = element.elementType == JSElementTypes.CALL_EXPRESSION
          && (element as? JSCallExpression)?.methodExpression.let { refExpr ->
        refExpr?.lastChild?.textMatches("t") ?: false
      }
      if (validate) {
        val keyElement =
          (element as? JSCallExpression)!!.argumentList!!.arguments[0]
        val key = keyElement.text
        callback(key, keyElement)
      }
      return validate
    }

    override fun execute(element: PsiElement): Boolean {
      listOf(this::processHookCall).fold(true) { r, t ->
        if (r) r && t(element)
        else r
      }
      return true
    }
  }
}

fun <T : Project?> T.getEasyI18nReferenceService(): I18nKeyReferenceManager =
  ServiceManager.getService(this!!, I18nKeyReferenceManager::class.java)