package de.marhali.easyi18n.model;

import de.marhali.easyi18n.util.TranslationsUtil;

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
//        List<String> sections = TranslationsUtil.getSections(fullPath);
//
//        LocalizedNode node = nodes;
//
//        for(String section : sections) {
//            if(node == null) {
//                return null;
//            }
//            node = node.getChildren(section);
//        }
//
//        return node;
    }

    public @NotNull LocalizedNode getOrCreateNode(@NotNull String fullPath) {
//        List<String> sections = TranslationsUtil.getSections(fullPath);

        LocalizedNode node = nodes.getChildren(fullPath);

//        if(subNode == null) {
//            subNode = new LocalizedNode(section, new ArrayList<>());
//            node.addChildren(subNode);
//        }
//
//        node = subNode;

        if (node == null) {
            node = new LocalizedNode(fullPath, new ArrayList<>());
            nodes.addChildren(node);
        }

        return node;
    }

    public @NotNull List<String> getFullKeys() {
        List<String> keys = new ArrayList<>();

        if (nodes.isLeaf()) { // Root has no children
            return keys;
        }

        for (LocalizedNode children : nodes.getChildren()) {
            keys.addAll(getFullKeys("", children));
        }

        return keys;
    }

    public @NotNull List<String> getFullKeys(String parentFullPath, LocalizedNode localizedNode) {
        List<String> keys = new ArrayList<>();

        if (localizedNode.isLeaf()) {
            keys.add(parentFullPath + (parentFullPath.isEmpty() ? "" : ".") + localizedNode.getKey());
            return keys;
        }

        for (LocalizedNode children : localizedNode.getChildren()) {
            String childrenPath = parentFullPath + (parentFullPath.isEmpty() ? "" : ".") + localizedNode.getKey();
            keys.addAll(getFullKeys(childrenPath, children));
        }

        return keys;
    }
}