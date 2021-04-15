package com.yuukaze.i18next.model

data class TreeNode<T>(var value: T) {
  var parent: TreeNode<T>? = null

  var children: MutableList<TreeNode<T>> = mutableListOf()

  fun addChild(node: TreeNode<T>) {
    children.add(node)
    node.parent = this
  }

  override fun toString(): String {
    var s = "$value"
    if (children.isNotEmpty()) {
      s += " {" + children.map { it.toString() } + " }"
    }
    return s
  }
}