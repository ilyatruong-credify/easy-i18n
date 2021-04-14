package com.yuukaze.i18next.service

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.project.Project
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
class EasyI18nSettingsService(private val project: Project) :
  PersistentStateComponent<SettingsState> {

  private var state: SettingsState
  val dataStore: DataStore = DataStore.getInstance(project)


  override fun getState(): SettingsState {
    return state
  }

  override fun loadState(state: SettingsState) {
    this.state = state
  }

  init {
    state = SettingsState()
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
}

fun <T : Project?> T.getEasyI18nService(): EasyI18nSettingsService =
  ServiceManager.getService(this!!, EasyI18nSettingsService::class.java).let {
    it.initializeComponent()
    return it
  }

fun <T : Project?> T.getEasyI18nDataStore(): DataStore =
  this.getEasyI18nService().dataStore