package com.yuukaze.i18next.service

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.project.Project
import com.yuukaze.i18next.data.InitProjectAction
import com.yuukaze.i18next.data.i18nStore
import com.yuukaze.i18next.factory.I18nAppFactory
import com.yuukaze.i18next.lang.JsxAttributeFactory
import com.yuukaze.i18next.lang.JsxAttributeWithStringTemplateFactory
import com.yuukaze.i18next.lang.JsxTextFactory
import com.yuukaze.i18next.model.SettingsState

/**
 * Persistent settings storage at project level.
 * @author yuukaze
 */
@State(name = "EasyI18nSettings")
class EasyI18nSettingsService(project: Project) :
  PersistentStateComponent<SettingsState>, Disposable {

  private var state: SettingsState
  val dataStore = DataStore(project)


  override fun getState(): SettingsState {
    return state
  }

  override fun loadState(state: SettingsState) {
    this.state = state
  }

  init {
    state = SettingsState()
    i18nStore.dispatch(InitProjectAction(project = project))
  }

  fun mainFactory(): I18nAppFactory =
    I18nAppFactory(
      listOf(
        listOf(
          JsxTextFactory(),
          JsxAttributeWithStringTemplateFactory(),
          JsxAttributeFactory()
        )
      ).flatten()
    )

  override fun dispose() {

  }
}

fun <T : Project?> T.getEasyI18nService(): EasyI18nSettingsService =
  ServiceManager.getService(this!!, EasyI18nSettingsService::class.java).let {
    it.initializeComponent()
    return it
  }

fun <T : Project?> T.getEasyI18nDataStore(): DataStore =
  this.getEasyI18nService().dataStore