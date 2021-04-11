package com.yuukaze.i18next.service

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.project.Project
import com.yuukaze.i18next.factory.I18nAppFactory
import com.yuukaze.i18next.lang.JsxAttributeFactory
import com.yuukaze.i18next.lang.JsxTextFactory
import com.yuukaze.i18next.model.SettingsState

/**
 * Persistent settings storage at project level.
 * @author yuukaze
 */
@State(name = "EasyI18nSettings")
class SettingsService : PersistentStateComponent<SettingsState> {
  private var state: SettingsState
  override fun getState(): SettingsState {
    return state
  }

  override fun loadState(state: SettingsState) {
    this.state = state
  }

  companion object {
    @JvmStatic
    fun getInstance(project: Project?): SettingsService {
      ServiceManager.getService(
        project!!, SettingsService::class.java
      ).initializeComponent()
      return ServiceManager.getService(
        project, SettingsService::class.java
      )
    }
  }

  init {
    state = SettingsState()
  }

  fun mainFactory(): I18nAppFactory =
    I18nAppFactory(
      listOf(
        listOf(JsxTextFactory(), JsxAttributeFactory())
      ).flatten()
    )
}