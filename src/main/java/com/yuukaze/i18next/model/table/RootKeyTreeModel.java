package com.yuukaze.i18next.model.table;

import com.intellij.openapi.project.Project;
import com.intellij.util.containers.OrderedSet;
import com.yuukaze.i18next.model.LocalizedNode;
import com.yuukaze.i18next.model.Translations;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Set;

public class RootKeyTreeModel extends DefaultTreeModel {
    private final @NotNull Project project;
    private final @NotNull Translations translations;

    public RootKeyTreeModel(@NotNull Project project, @NotNull Translations translations) {
        super(null);
        this.project = project;
        this.translations = translations;
        setRoot(generateNodes());
    }

    private DefaultMutableTreeNode generateNodes() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(LocalizedNode.ROOT_KEY);

        if (translations.getNodes().isLeaf()) { // Empty tree
            return root;
        }
        Set<String> rootKeys = new OrderedSet<String>();
        for (LocalizedNode children : translations.getNodes().getChildren()) {
            String rootKey = children.getKey().substring(0, children.getKey().indexOf("."));
            rootKeys.add(rootKey);
        }
        for (String key : rootKeys) {
            root.add(new DefaultMutableTreeNode(key));
        }
        return root;
    }
}
