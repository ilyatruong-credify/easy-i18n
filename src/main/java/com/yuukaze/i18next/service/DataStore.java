package com.yuukaze.i18next.service;

import com.intellij.openapi.project.Project;
import com.yuukaze.i18next.io.TranslatorIO;
import com.yuukaze.i18next.model.*;
import com.yuukaze.i18next.util.IOUtil;
import com.yuukaze.i18next.util.TranslationsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Singleton service to manage localized messages.
 *
 * @author marhali
 */
public class DataStore {

    private static DataStore INSTANCE;

    private final Project project;
    private final List<DataSynchronizer> synchronizer;
//    private final TreeModelTranslator treeModel;

    private Translations translations;
    private String searchQuery;

    public static DataStore getInstance(Project project) {
        return INSTANCE == null ? INSTANCE = new DataStore(project) : INSTANCE;
    }

    private DataStore(Project project) {
        this.project = project;
        this.synchronizer = new ArrayList<>();
//        this.treeModel = new TreeModelTranslator(project);
//        this.addSynchronizer(treeModel);
    }

    /**
     * Registers a new synchronizer which will receive {@link #translations} updates.
     *
     * @param synchronizer Synchronizer. See {@link DataSynchronizer}
     */
    public void addSynchronizer(DataSynchronizer synchronizer) {
        this.synchronizer.add(synchronizer);
    }

    /**
     * Loads all translations from disk and overrides current {@link #translations} state.
     */
    public void reloadFromDisk() {
        String localesPath = project.getService(EasyI18nSettingsService.class).getState().getLocalesPath();

        if (localesPath == null || localesPath.isEmpty()) {
            translations = new Translations(new ArrayList<>(),
                    new LocalizedNode(LocalizedNode.ROOT_KEY, new ArrayList<>()));

        } else {
            TranslatorIO io = IOUtil.determineFormat(localesPath);

            io.read(localesPath, (translations) -> {
                if (translations != null) { // Read was successful
                    this.translations = translations;

                    // Propagate changes
                    synchronizer.forEach(synchronizer -> synchronizer.synchronize(translations, searchQuery));

                } else {
                    // If state cannot be loaded from disk, show empty instance
                    this.translations = new Translations(new ArrayList<>(),
                            new LocalizedNode(LocalizedNode.ROOT_KEY, new ArrayList<>()));
                }
            });
        }
    }

    /**
     * Saves the current translation state to disk. See {@link TranslatorIO#save(Translations, String, Consumer)}
     *
     * @param callback Complete callback. Indicates if operation was successful(true) or not
     */
    public void saveToDisk(@NotNull Consumer<Boolean> callback) {
        String localesPath = project.getService(EasyI18nSettingsService.class).getState().getLocalesPath();

        if (localesPath == null || localesPath.isEmpty()) { // Cannot save without valid path
            return;
        }

        TranslatorIO io = IOUtil.determineFormat(localesPath);
        io.save(translations, localesPath, callback);
    }

    /**
     * Propagates provided search string to all synchronizer to display only relevant keys
     *
     * @param fullPath Full i18n key (e.g. user.username.title). Can be null to display all keys
     */
    public void searchByKey(@Nullable String fullPath) {
        // Use synchronizer to propagate search instance to all views
        synchronizer.forEach(synchronizer -> synchronizer.synchronize(translations, this.searchQuery = fullPath));
    }

    /**
     * Processes the provided update. Updates translation instance and propagates changes. See {@link DataSynchronizer}
     *
     * @param update The update to process. For more information see {@link TranslationUpdate}
     */
    public void processUpdate(@Nullable TranslationUpdate update) {
        if (update != null) {
            if (update.isDeletion() || update.isKeyChange()) { // Delete origin i18n key
                String originKey = update.getOrigin().getKey();
                List<String> sections = TranslationsUtil.getSections(originKey);
                String nodeKey = sections.remove(sections.size() - 1); // Remove last node, which needs to be removed by parent

                LocalizedNode node = translations.getNodes();
                for (String section : sections) {
                    if (node == null) { // Might be possible on multi-delete
                        break;
                    }

                    node = node.getChildren(section);
                }

                if (node != null) { // Only remove if parent exists. Might be already deleted on multi-delete
                    node.removeChildren(nodeKey);

                    // Parent is empty now, we need to remove it as well (except root)
                    if (node.getChildren().isEmpty() && !node.getKey().equals(LocalizedNode.ROOT_KEY)) {
                        processUpdate(new TranslationDelete(new KeyedTranslation(
                                TranslationsUtil.sectionsToFullPath(sections), null)));
                    }
                }
            }

            if (!update.isDeletion()) { // Recreate with changed val / create
                LocalizedNode node = translations.getOrCreateNode(update.getChange().getKey());
                node.setValue(update.getChange().getTranslations());
            }
        }

        // Persist changes and propagate them on success
//        System.out.println(translations.getNodes().getChildren().size());
        doSync();
    }

    public void doSync() {
        saveToDisk(success -> {
            if (success) {
                synchronizer.forEach(synchronizer -> synchronizer.synchronize(translations, searchQuery));
            }
        });
    }

    /**
     * @return Current translation state
     */
    public Translations getTranslations() {
        return translations;
    }
}