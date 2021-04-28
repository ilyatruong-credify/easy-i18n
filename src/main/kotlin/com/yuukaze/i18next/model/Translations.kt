package com.yuukaze.i18next.model

import com.intellij.openapi.util.Pair

/**
 * Represents translation state instance. IO operations will be based on this file.
 */
class Translations(val locales: List<String>, val nodes: LocalizedNode) {
  constructor() : this(
    ArrayList(),
    LocalizedNode(LocalizedNode.ROOT_KEY, ArrayList())
  )

  fun clone(children: List<LocalizedNode?>): Translations =
    Translations(locales, LocalizedNode(LocalizedNode.ROOT_KEY, children))

  fun getNode(fullPath: String): LocalizedNode? {
    return nodes.getChildren(fullPath)
  }

  fun getOrCreateNode(fullPath: String): LocalizedNode {
    var node = nodes.getChildren(fullPath)
    if (node == null) {
      node = LocalizedNode(fullPath, ArrayList())
      nodes.addChildren(node)
    }
    return node
  }

  // Root has no children
  val fullKeys: List<Pair<String, String?>>
    get() {
      val keys: MutableList<Pair<String, String?>> = ArrayList()
      if (nodes.isLeaf) { // Root has no children
        return keys
      }
      for (children in nodes.children) {
        keys.addAll(getFullKeys("", children))
      }
      return keys
    }

  val fullKeysWithoutPreview: List<String>
    get() = fullKeys.map { it.first }

  private fun getFullKeys(
    parentFullPath: String,
    localizedNode: LocalizedNode
  ): List<Pair<String, String?>> {
    val keys: MutableList<Pair<String, String?>> = ArrayList()
    if (localizedNode.isLeaf) {
      keys.add(
        Pair(
          parentFullPath + (if (parentFullPath.isEmpty()) "" else ".") + localizedNode.key,
          localizedNode.value["en"]
        )
      )
      return keys
    }
    for (children in localizedNode.children) {
      val childrenPath =
        parentFullPath + (if (parentFullPath.isEmpty()) "" else ".") + localizedNode.key
      keys.addAll(getFullKeys(childrenPath, children))
    }
    return keys
  }

  val treeKeys: TreeNode<String>
    get() {
      val node = TreeNode("ROOT")
      treeKeys(fullKeys.map { it.first!! }, node)
      return node
    }

  private fun treeKeys(keys: List<String>, parent: TreeNode<String>) {
    keys.filter { it.isNotEmpty() }.groupBy { it.substringBefore('.') }
      .forEach { (key, subKeys) ->
        val subNode = TreeNode(key)
        treeKeys(subKeys.map { it.substringAfter('.', "") }, subNode)
        subNode.parent = parent
        parent.addChild(subNode)
      }
  }

  public fun changeKey(old: String, new: String) {
    val oldNode = getNode(old) ?: throw Exception("$old not available");
    val trans = oldNode.value;
    getOrCreateNode(new).apply { this.value = trans }
    this.nodes.removeChildren(old)
  }
}