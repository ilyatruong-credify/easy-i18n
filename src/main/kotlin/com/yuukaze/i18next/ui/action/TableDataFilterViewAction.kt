package com.yuukaze.i18next.ui.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.util.containers.toArray
import com.yuukaze.i18next.data.TableFilterAction
import com.yuukaze.i18next.data.TableFilterMode
import com.yuukaze.i18next.data.i18nStore
import com.yuukaze.uiUtils.createReduxBindData
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TableDataFilterViewAction :
    ActionGroup("Filter", null, AllIcons.General.Filter) {
    private val currentFilter =
        i18nStore.createReduxBindData(TableFilterMode.ALL) { it.filter }
    private val myActions = listOf(
        FilterToggleAction("Show all", TableFilterMode.ALL),
        FilterToggleAction("Show missing", TableFilterMode.SHOW_MISSING),
        FilterToggleAction("Show unused", TableFilterMode.SHOW_UNUSED),
    )

    init {
        isPopup = true
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return myActions.toArray(AnAction.EMPTY_ARRAY)
    }

    inner class FilterToggleAction(
        val text: String,
        private val filterAction: TableFilterMode
    ) : ToggleAction(text) {
        override fun isSelected(e: AnActionEvent): Boolean =
            filterAction == currentFilter.value

        override fun setSelected(e: AnActionEvent, state: Boolean) {
            if (state)
                i18nStore.dispatch(TableFilterAction(mode = filterAction))
        }

    }
}