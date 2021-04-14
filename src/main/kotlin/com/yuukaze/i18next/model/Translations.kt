package com.yuukaze.i18next.model;

import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents translation state instance. IO operations will be based on this file.
 *
 * @author marhali
 */
public class Translations {

    @NotNull
    private final List<String> locales;

    @NotNull
    private final LocalizedNode nodes;

    /**
     * Constructs a new translation state instance.
     *
     * @param locales List of all locales which are used for create / edit I18n-Key operations
     * @param nodes   Represents the translation state. Internally handled as a tree. See {@link LocalizedNode}
     */
    public Translations(@NotNull List<String> locales, @NotNull LocalizedNode nodes) {
        this.locales = locales;
        this.nodes = nodes;
    }

    public @NotNull List<String> getLocales() {
        return locales;
    }

    public LocalizedNode getNodes() {
        return nodes;
    }

    public @Nullable LocalizedNode getNode(@NotNull String fullPath) {
        return nodes.getChildren(fullPath);
    }

    public @NotNull LocalizedNode getOrCreateNode(@NotNull String fullPath) {
        LocalizedNode node = nodes.getChildren(fullPath);

        if (node == null) {
            node = new LocalizedNode(fullPath, new ArrayList<>());
            nodes.addChildren(node);
        }

        return node;
    }

    public @NotNull List<Pair<String, String>> getFullKeys() {
        List<Pair<String, String>> keys = new ArrayList<>();

        if (nodes.isLeaf()) { // Root has no children
            return keys;
        }

        for (LocalizedNode children : nodes.getChildren()) {
            keys.addAll(getFullKeys("", children));
        }

        return keys;
    }

    public @NotNull List<Pair<String, String>> getFullKeys(String parentFullPath, LocalizedNode localizedNode) {
        List<Pair<String, String>> keys = new ArrayList<>();

        if (localizedNode.isLeaf()) {
            keys.add(new Pair<String, String>(parentFullPath + (parentFullPath.isEmpty() ? "" : ".") + localizedNode.getKey(), localizedNode.getValue().get("en")));
            return keys;
        }

        for (LocalizedNode children : localizedNode.getChildren()) {
            String childrenPath = parentFullPath + (parentFullPath.isEmpty() ? "" : ".") + localizedNode.getKey();
            keys.addAll(getFullKeys(childrenPath, children));
        }

        return keys;
    }
}