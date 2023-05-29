import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * From
 * @author cs2030
 * An immutable implementation of {@code ArrayList}. 
 */

public class ImList<E> implements Iterable<E> {
    private final ArrayList<E> elems;

    public ImList() {
        this.elems = new ArrayList<E>();
    }

    public ImList(List<? extends E> list) {
        this.elems = new ArrayList<E>(list);
    }

    public ImList<E> add(E elem) {
        ImList<E> newList = new ImList<E>(this.elems);
        newList.elems.add(elem);
        return newList;
    }

    public ImList<E> addAll(List<? extends E> list) {
        ImList<E> newList = new ImList<E>(this.elems);
        newList.elems.addAll(list);
        return newList;
    }

    public E get(int index) {
        return this.elems.get(index);
    }

    public int indexOf(Object obj) {
        return this.elems.indexOf(obj);
    }

    public boolean isEmpty() {
        return this.elems.isEmpty();
    }

    public Iterator<E> iterator() {
        return this.elems.iterator();
    }

    public ImList<E> remove(int index) {
        ImList<E> newList = new ImList<E>(this.elems);
        newList.elems.remove(index);
        return newList;
    }

    public ImList<E> set(int index, E elem) {
        ImList<E> newList = new ImList<E>(this.elems);
        newList.elems.set(index, elem);
        return newList;
    }

    public int size() {
        return this.elems.size();
    }

    @Override
    public String toString() {
        return this.elems.toString();
    }
}
