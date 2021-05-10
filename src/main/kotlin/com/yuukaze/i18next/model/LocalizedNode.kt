package com.yuukaze.i18next.model

import com.yuukaze.i18next.util.MapUtil
import java.util.*

/**
 * Represents structured tree view for translated messages.
 */
class LocalizedNode {
    val key: String
    private var _children: TreeMap<String, LocalizedNode>
    private var _value: MutableMap<String, String>

    constructor(key: String, children: List<LocalizedNode?>) {
        this.key = key
        this._children = MapUtil.convertToTreeMap(children)
        _value = HashMap()
    }

    constructor(key: String, value: MutableMap<String, String>) {
        this.key = key
        _children = TreeMap()
        this._value = value
    }

    val isLeaf: Boolean
        get() = _children.isEmpty()

    val children
        get() = _children.values

    fun getChildren(key: String): LocalizedNode? {
        return _children[key]
    }

    fun setChildren(vararg children: LocalizedNode?) {
        _value.clear()
        this._children = MapUtil.convertToTreeMap(Arrays.asList(*children))
    }

    fun addChildren(vararg children: LocalizedNode?) {
        _value.clear()
        Arrays.stream(children)
            .forEach { e: LocalizedNode? -> this._children[e!!.key] = e }
    }

    fun removeChildren(key: String) {
        _children.remove(key)
    }

    var value
        get() = _value
        set(value) {
            _children.clear()
            _value = value
        }

    /**
     * Check if node has any missing translation
     * Notice: node must be leaf, that means no children
     */
    val isMissing: Boolean
        get() {
            if (!isLeaf) return false;
            return _value.values.fold(false) { acc, it -> acc || it.isEmpty() }
        }

    companion object {
        const val ROOT_KEY = "root"
    }
}