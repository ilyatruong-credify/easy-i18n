package com.yuukaze.i18next.service

import com.intellij.openapi.project.Project
import com.yuukaze.i18next.io.TranslatorIO
import com.yuukaze.i18next.model.*
import com.yuukaze.i18next.util.IOUtil
import com.yuukaze.i18next.util.TranslationsUtil
import java.util.function.Consumer

/**
 * Singleton service to manage localized messages.
 */
class DataStore private constructor(private val project: Project) {
  private val synchronizer: MutableList<DataSynchronizer>

  /**
   * @return Current translation state
   */
  lateinit var translations: Translations
    private set
  private var searchQuery: String? = null

  /**
   * Registers a new synchronizer which will receive [.translations] updates.
   *
   * @param synchronizer Synchronizer. See [DataSynchronizer]
   */
  fun addSynchronizer(synchronizer: DataSynchronizer) {
    this.synchronizer.add(synchronizer)
  }

  /**
   * Loads all translations from disk and overrides current [.translations] state.
   */
  fun reloadFromDisk() {
    val localesPath = project.getService(
      EasyI18nSettingsService::class.java
    ).state.localesPath
    if (localesPath.isEmpty()) {
      translations = Translations(
        ArrayList(),
        LocalizedNode(LocalizedNode.ROOT_KEY, ArrayList())
      )
    } else {
      val io = IOUtil.determineFormat(localesPath)
      io.read(localesPath) { translations: Translations? ->
        if (translations != null) { // Read was successful
          this.translations = translations

          // Propagate changes
          synchronizer.forEach(Consumer { synchronizer: DataSynchronizer ->
            synchronizer.synchronize(
              translations,
              searchQuery
            )
          })
        } else {
          // If state cannot be loaded from disk, show empty instance
          this.translations = Translations(
            ArrayList(),
            LocalizedNode(LocalizedNode.ROOT_KEY, ArrayList())
          )
        }
      }
    }
  }

  /**
   * Saves the current translation state to disk. See [TranslatorIO.save]
   *
   * @param callback Complete callback. Indicates if operation was successful(true) or not
   */
  private fun saveToDisk(callback: Consumer<Boolean>) {
    val localesPath = project.getService(
      EasyI18nSettingsService::class.java
    ).state.localesPath
    if (localesPath.isEmpty()) { // Cannot save without valid path
      return
    }
    val io = IOUtil.determineFormat(localesPath)
    io.save(translations!!, localesPath, callback)
  }

  /**
   * Propagates provided search string to all synchronizer to display only relevant keys
   *
   * @param fullPath Full i18n key (e.g. user.username.title). Can be null to display all keys
   */
  fun searchByKey(fullPath: String?) {
    // Use synchronizer to propagate search instance to all views
    synchronizer.forEach(Consumer { synchronizer: DataSynchronizer ->
      synchronizer.synchronize(
        translations!!, fullPath.also { searchQuery = it })
    })
  }

  /**
   * Processes the provided update. Updates translation instance and propagates changes. See [DataSynchronizer]
   *
   * @param update The update to process. For more information see [TranslationUpdate]
   */
  fun processUpdate(update: TranslationUpdate?) {
    if (update != null) {
      if (update.isDeletion || update.isKeyChange) { // Delete origin i18n key
        val originKey = update.origin.key
        val sections = TranslationsUtil.getSections(originKey)
        val nodeKey =
          sections.removeAt(sections.size - 1) // Remove last node, which needs to be removed by parent
        var node: LocalizedNode? = translations!!.nodes
        for (section in sections) {
          if (node == null) { // Might be possible on multi-delete
            break
          }
          node = node.getChildren(section!!)
        }
        if (node != null) { // Only remove if parent exists. Might be already deleted on multi-delete
          node.removeChildren(nodeKey)

          // Parent is empty now, we need to remove it as well (except root)
          if (node.children.isEmpty() && node.key != LocalizedNode.ROOT_KEY) {
            processUpdate(
              TranslationDelete(
                KeyedTranslation(
                  TranslationsUtil.sectionsToFullPath(sections), null
                )
              )
            )
          }
        }
      }
      if (!update.isDeletion) { // Recreate with changed val / create
        val node = translations!!.getOrCreateNode(update.change.key)
        node.value = update.change.translations as MutableMap<String, String>
      }
    }

    // Persist changes and propagate them on success
    doSync()
  }

  fun doSync() {
    saveToDisk { success: Boolean ->
      if (success) {
        synchronizer.forEach(Consumer { synchronizer: DataSynchronizer ->
          synchronizer.synchronize(
            translations!!, searchQuery
          )
        })
      }
    }
  }

  companion object {
    private var INSTANCE: DataStore? = null
    @JvmStatic
    fun getInstance(project: Project): DataStore {
      return if (INSTANCE == null) DataStore(project).also {
        INSTANCE = it
      } else INSTANCE!!
    }
  }

  init {
    synchronizer = ArrayList()
  }
}