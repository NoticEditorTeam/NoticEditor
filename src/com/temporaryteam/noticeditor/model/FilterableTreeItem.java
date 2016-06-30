package com.temporaryteam.noticeditor.model;

import java.lang.reflect.Field;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.scene.control.TreeItem;

import static javafx.scene.control.TreeItem.childrenModificationEvent;

public class FilterableTreeItem<T> extends TreeItem<T> {

    @FunctionalInterface
    public interface Predicate<T> {
        boolean test(TreeItem<T> parent, T value);
    }

    private final ObservableList<TreeItem<T>> sourceList;
    private final FilteredList<TreeItem<T>> filteredList;

    private ObjectProperty<Predicate<T>> predicate = new SimpleObjectProperty<Predicate<T>>() {
        @Override
        protected void invalidated() {
            fireChangeItem();
        }
    };

    public FilterableTreeItem(T value) {
        super(value);
        sourceList = FXCollections.observableArrayList();
        filteredList = new FilteredList<>(sourceList);
        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            return child -> {
                // Set the predicate of child items to force filtering
                if (child instanceof FilterableTreeItem) {
                    FilterableTreeItem<T> filterableChild = (FilterableTreeItem<T>) child;
                    filterableChild.setPredicate(predicate.get());
                }
                // If there is no predicate, keep this tree item
                if (predicate.get() == null)
                    return true;
                // If there are children, keep this tree item
                if (child.getChildren().size() > 0)
                    return true;
                // Otherwise ask the TreeItemPredicate
                return predicate.get().test(this, child.getValue());
            };
        }, predicate));
        setHiddenFieldChildren(filteredList);
    }

    private void setHiddenFieldChildren(ObservableList<TreeItem<T>> list) {
        try {
            Field childrenField = TreeItem.class.getDeclaredField("children"); //$NON-NLS-1$
            childrenField.setAccessible(true);
            childrenField.set(this, list);

            Field declaredField = TreeItem.class.getDeclaredField("childrenListener"); //$NON-NLS-1$
            declaredField.setAccessible(true);
            list.addListener((ListChangeListener<? super TreeItem<T>>) declaredField.get(this));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("Could not set TreeItem.children", e); //$NON-NLS-1$
        }
    }

    /**
     * Returns the list of children that is backing the filtered list.
     *
     * @return underlying list of children
     */
    public ObservableList<TreeItem<T>> getInternalChildren() {
        return sourceList;
    }

    /**
     * @return the predicate property
     */
    public final ObjectProperty<Predicate<T>> predicateProperty() {
        return predicate;
    }

    /**
     * @return the predicate
     */
    public final Predicate<T> getPredicate() {
        return predicate.get();
    }

    /**
     * Set the predicate
     *
     * @param predicate the predicate
     */
    public final void setPredicate(Predicate<T> predicate) {
        this.predicate.set(predicate);
    }

    protected void fireChangeItem() {
        Event.fireEvent(this, new TreeModificationEvent(childrenModificationEvent(), this));
    }
}
