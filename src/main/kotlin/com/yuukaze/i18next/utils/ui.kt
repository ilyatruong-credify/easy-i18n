package com.yuukaze.i18next.utils

import com.intellij.openapi.ui.panel.PanelBuilder
import com.intellij.ui.components.JBLabel
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH
import com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.Component
import java.awt.LayoutManager
import javax.swing.JComponent
import javax.swing.JPanel

val factory = DefaultJComponentFactory()

inline fun dsl(init: JPanel.() -> Unit) = JPanel().apply {
    init()
}

inline fun grid(
    rows: Int,
    cols: Int,
    build: GridBuilder.() -> Unit
): JPanel {
    val panel = JPanel(GridLayoutManager(rows, cols))
    build(GridBuilder(panel))
    return panel
}

inline fun grid(
    columnsTemplate: List<String>,
    build: GridBuilder.() -> Unit
): JPanel {
    val panel = JPanel(GridLayoutManager(1, 1))
    build(GridBuilder(panel).let {
        it.columnsTemplate = columnsTemplate
        it
    })
    return panel
}

inline fun grid(build: GridBuilder.() -> Unit): JPanel = grid(1, 1, build)

inline fun border(build: BorderLayoutPanelBuilder.() -> Unit): BorderLayoutPanelBuilder =
    BorderLayoutPanelBuilder().apply(build)

class BorderLayoutPanelBuilder : BorderLayoutPanel() {
    val left = ChildBuilder { addToLeft(it) }
    val center = ChildBuilder { addToCenter(it) }
    val top = ChildBuilder { addToTop(it) }

    inner class ChildBuilder(val callback: (Component) -> Unit) {
        operator fun plusAssign(comp: Component) = callback(comp)
    }
}

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
    val layout: L
        get() = panel.layout as L
}

class GridBuilder(private val panel: JPanel) :
    DefaultPanelBuilder<GridLayoutManager>(panel) {
    private var addIndex = -1
    private val currentCol: Int
        get() = addIndex % layout.columnCount
    private val currentRow: Int
        get() = addIndex / layout.columnCount

    private fun increaseConstraint() {
        addIndex++
        if (addIndex >= layout.columnCount * layout.rowCount) {
            //increase row
            panel.layout = GridLayoutManager(layout.rowCount + 1, layout.columnCount)
        }
    }

    private fun nextConstraint() = nextConstraint(null)
    private fun nextConstraint(constraint: GridConstraints?): GridConstraints {
        increaseConstraint()
        return ((constraint
            ?: GridConstraints()).clone() as GridConstraints).apply {
            row = currentRow
            column = currentCol
            if (columnsTemplate[currentCol] == "1fr") {
                hSizePolicy = hSizePolicy or SIZEPOLICY_WANT_GROW
                fill = FILL_BOTH
            }
        }
    }

    var columnsTemplate: List<String> = listOf()
        set(value) {
            field = value
            if (value.size != layout.columnCount) {
                val oldLayout = layout
                panel.layout = GridLayoutManager(oldLayout.rowCount, value.size)
            }
        }

    override fun <T : JComponent> add(component: T): T {
        return add(component, nextConstraint())
    }

    fun <T : JComponent> add(component: T, constraint: GridConstraints): T {
        panel.add(component, constraint)
        return component
    }

    operator fun <T : JComponent> T.invoke(): T = this.invoke(null)
    operator fun <T : JComponent> T.invoke(constraint: GridConstraints?): T {
        panel.add(this, nextConstraint(constraint))
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

fun DefaultPanelBuilder<*>.label(text: String): JBLabel {
    val label = JBLabel(text)
    add(label)
    return label
}