package scapeGoat;

import java.util.*;

public class ScapegoatTree<T extends Comparable<T>> extends AbstractSet<T> implements Set<T> {

    private static class Node<T>{
        T value = null;
        Node<T> parent = null;
        Node<T> left = null;
        Node<T> right = null;
        Node<T> sibling = null;
        Integer size = null;

        private void connectLeft(Node<T> left){
            if (this.left != null || left == null || left.parent != null) return;
            this.left = left;
            left.parent = this;
            left.sibling = this.right;
            this.size += left.size;
            this.chekUp();
        }

        private void connectRight(Node<T> right){
            if (this.right != null || right == null || right.parent != null) return;
            this.right = right;
            right.parent = this;
            right.sibling = this.left;
            this.size += right.size;
            this.chekUp();
        }

        private void removeLeftSubtree() {
            left = null;
            if (right != null)
                right.sibling = null;
            chekUp();
        }

        private void removeRightSubtree() {
            right = null;
            if (left != null)
                left.sibling = null;
            chekUp();
        }

        private boolean remove() {
            if (right != null) removeRight();
            else if (left != null) removeLeft();
            else if (parent != null) {
                Node<T> p = parent;
                if (parent.left == this) parent.removeLeftSubtree();
                else parent.removeRightSubtree();

            } else return false;
            return true;
        }

        private void removeLeft() {
            if (left != null) {
                Node<T> it = left;
                while (it.right != null) it = it.right;
                if (it.left == null) {
                    left.value = it.value;
                    it = it.parent;
                    it.removeRightSubtree();
                    it.chekUp();
                } else {
                    it.removeLeft();
                }
            }
        }

        private void removeRight() {
            if (right != null) {
                Node<T> it = right;
                while (it.left != null) it = it.left;
                if (it.right == null) {
                    right.value = it.value;
                    it = it.parent;
                    it.removeLeftSubtree();
                    it.chekUp();
                } else {
                    it.removeRight();
                }
            }
        }

        private void chekUp(){
            int l = (left == null)? 0 : left.size;
            int r = (right == null)? 0 : right.size;
            size = l + r + 1;
            if (parent != null) parent.chekUp();
        }
    }

    private Node<T> root = null;
    private Integer size = 0;
    private Double alpha = 0.75;

    private void chekRoot() {
        if (root == null) size = 0;
        else size = root.size;
    }

    public Double getAlpha() {
        return alpha;
    }

    public boolean setAlpha(Double alpha) {
        if (alpha >= 0.5 && alpha <= 1) {
            this.alpha = alpha;
            return true;
        } else return false;
    }

    @Override
    public int size() {
        return size;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        } else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }
    // метод возвращает узел, значение которого совпадает с искомым, а если такого нет,
    // то возвращает потенциального родителя для узла с таким значением

    @Override
    public boolean contains(Object o) {
        if (size == 0) return false;
        if (o.getClass() != root.value.getClass()) return false;
        @SuppressWarnings("unchecked")
        T t = ((T) o);
        Node<T> node = find(t);
        return node != null && node.value == o;
    }

    @Override
    public boolean add(T t) {
        return scapegoatAdd(t);
    }

    private boolean scapegoatAdd(T t) {
        Node<T> it = find(t);
        if (it == null) {
            root = new Node<>();
            root.value = t;
            root.size = 1;
            this.size = 1;
            return true;
        }
        else if (it.value == t) return false;
        else {
            Node<T> newNode = new Node<>();
            newNode.value = t;
            newNode.left = null;
            newNode.right = null;
            newNode.size = 1;

            int comparison = t.compareTo(it.value);
            if (comparison < 0) {
                it.connectLeft(newNode);
            } else {
                it.connectRight(newNode);
            }
            this.chekRoot();

            Node<T> scapegoat = null;
            while (it.parent != null) {
                int l = (it.left == null)? 0 : it.left.size;
                int r = (it.right == null)? 0 : it.right.size;
                if (l > alpha * it.size || r > alpha * it.size) scapegoat = it;
                it = it.parent;
            }
            if (scapegoat != null) rebuild(scapegoat);
            return true;
        }
    }

    private void rebuild(Node<T> scapegoat) {
        List<T> list = this.getSubTree(scapegoat);
        Node<T> subTreeRoot = buildTree(list);
        Node<T> p = scapegoat.parent;
        if (scapegoat == p.left){
            p.removeLeftSubtree();
            p.connectLeft(subTreeRoot);
        } else {
            p.removeRightSubtree();
            p.connectRight(subTreeRoot);
        }
        p.chekUp();
    }

    private ArrayList<T> getSubTree(Node<T> newRoot) {
        Node<T> it = newRoot;
        while (it.left != null) it = it.left;
        ArrayList<T> list = new ArrayList<>();

        // Проверить правого потомка
        //     Если существует правый потомок:
        //         направо
        //         влево до упора
        //     do {
        //         поднимись вверх, запромнив себя
        //     } while я был слева
        //     конец

        do {
            list.add(it.value);
            if (it.right != null) {
                it = it.right;
                while (it.left != null) it = it.left;
            } else {
                Node<T> previous;
                do {
                    previous = it;
                    it = it.parent;
                } while (it != null && it.left != previous);
            }
        } while (it != null && newRoot.parent != it);

        return list;
    }

    private Node<T> buildTree(List<T> list){
        if (list.size() == 0) return null;
        if (list.size() == 1) {
            Node<T> newNode = new Node<>();
            newNode.value = list.get(0);
            newNode.size = 1;
            newNode.left = null;
            newNode.right = null;
            return newNode;
        }

        List<T> l;
        List<T> r;
        int centralIndex = 1;
        if (list.size() == 2) {
            l = List.of(list.get(0));
            r = List.of();
        } else {
            centralIndex = list.size() / 2;
            l = list.subList(0, centralIndex);
            r = list.subList(centralIndex + 1, list.size());
        }
        Node<T> newNode = new Node<>();
        newNode.value = list.get(centralIndex);
        newNode.size = 1;
        newNode.connectLeft(buildTree(l));
        newNode.connectRight(buildTree(r));
        newNode.chekUp();
        return newNode;
    }

    @Override
    public boolean remove(Object o) {
        if (root == null) return false;
        if (o.getClass() != root.value.getClass()) return false;
        @SuppressWarnings("unchecked")
        T value = (T) o;
        Node<T> it = find(value);
        if (it == null) return false;
        if (!remove(it)) {
            root = null;
            size = 0;
        }
        chekRoot();
        return true;
    }

    private boolean remove(Node<T> it) {
        if (it == null) return false;
        if (!it.remove()) root = null;
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new ScapegoatTreeIterator();
    }

    public class ScapegoatTreeIterator implements  Iterator<T> {
        private Node<T> it;
        private boolean removeIsColed = false;

        private ScapegoatTreeIterator() {
            it = root;
            while (it.left != null) it = it.left;
        }

        // Проверить правого потомка
        //     Если существует правый потомок:
        //         направо
        //         влево до упора
        //     do {
        //         поднимись вверх, запромнив себя
        //     } while я был слева
        //     конец

        @Override
        public boolean hasNext() {
            return (size != 0);
        }

        @Override
        public T next() {
            removeIsColed = false;
            if (!this.hasNext()) return null;
            if (it.right != null) {
                it = it.right;
                while (it.left != null) it =it.left;
            } else {
                Node<T> previous = it;
                do {
                    if (it.parent == null){
                        while (it.left != null) it = it.left;
                        return it.value;
                    } else it = it.parent;
                } while (it.left != previous);
            }
            return it.value;
        }


        @Override
        public void remove() {
            if (removeIsColed || it == null) throw new IllegalStateException();
            if (!it.remove()) {
                root = null;
                size = 0;
            }
            removeIsColed = true;
            chekRoot();
        }
    }

}