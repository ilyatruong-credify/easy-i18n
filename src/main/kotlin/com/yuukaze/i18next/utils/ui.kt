package com.yuukaze.i18next.utils

import com.intellij.openapi.ui.panel.PanelBuilder
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.LayoutManager
import javax.swing.JComponent
import javax.swing.JPanel

private val factory = DefaultJComponentFactory()

inline fun dsl(init: JPanel.() -> Unit) = JPanel().apply {
  init()
}

fun grid(
  rows: Int,
  cols: Int,
  build: GridBuilder.() -> Unit
): JPanel =
  factory.grid(
    rows, cols,
    build
  )

fun grid(build: GridBuilder.() -> Unit): JPanel = grid(1, 1, build)

fun border(build: BorderLayoutPanel.() -> Unit): BorderLayoutPanel =
  BorderLayoutPanel().apply(build)

interface JComponentFactory {
  fun grid(
    rows: Int,
    cols: Int,
    build: GridBuilder.() -> Unit
  ): JPanel
}

open class DefaultPanelBuilder<L : LayoutManager>(private val panel: JPanel) :
  PanelBuilder, JComponentFactory {
  override fun createPanel() = panel

  override fun constrainsValid(): Boolean = true

  override fun grid(
    rows: Int,
    cols: Int,
    build: GridBuilder.() -> Unit
  ): JPanel = add(factory.grid(rows, cols, build))

  open fun <T : JComponent> add(component: T): T {
    panel.add(component)
    return component
  }

  @Suppress("UNCHECKED_CAST")
  val layout = panel.layout as L
}

class GridBuilder(private val panel: JPanel) :
  DefaultPanelBuilder<GridLayoutManager>(panel) {
  override fun <T : JComponent> add(component: T): T {
    return add(component, GridConstraints())
  }

  fun <T : JComponent> add(component: T, constraint: GridConstraints): T {
    panel.add(component, constraint)
    return component
  }

  operator fun <T : JComponent> T.invoke(constraint: GridConstraints): T {
    panel.add(this, constraint)
    return this
  }

  operator fun <T2 : JComponent, T : JComponentWrapper<T2>> T.invoke(constraint: GridConstraints): T2 {
    return this.component(constraint)
  }
}

class DefaultJComponentFactory : JComponentFactory {
  override fun grid(
    rows: Int,
    cols: Int,
    build: GridBuilder.() -> Unit
  ): JPanel {
    val panel = JPanel(GridLayoutManager(rows, cols))
    build(GridBuilder(panel))
    return panel
  }
}

interface JComponentWrapper<T : JComponent> {
  val component: T
}