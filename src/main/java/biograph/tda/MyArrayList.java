package biograph.tda;

/**
 * Lista dinámica genérica implementada desde cero.
 * Reemplaza java.util.ArrayList sin usar ninguna librería externa.
 *
 * @param <T> Tipo de elementos almacenados en la lista.
 * @author BioGraph Team
 */
public class MyArrayList<T> {

    /** Arreglo interno que almacena los elementos. */
    private Object[] data;
    /** Cantidad actual de elementos en la lista. */
    private int size;
    /** Capacidad inicial por defecto. */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Crea una lista vacía con capacidad inicial por defecto.
     */
    public MyArrayList() {
        this.data = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    /**
     * Crea una lista vacía con la capacidad inicial indicada.
     * @param initialCapacity Capacidad inicial del arreglo interno.
     */
    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 1) {
            initialCapacity = DEFAULT_CAPACITY;
        }
        this.data = new Object[initialCapacity];
        this.size = 0;
    }

    /**
     * Agrega un elemento al final de la lista.
     * @param element Elemento a agregar.
     */
    public void add(T element) {
        ensureCapacity();
        data[size++] = element;
    }

    /**
     * Inserta un elemento en la posición indicada, desplazando los demás.
     * @param index Posición donde insertar.
     * @param element Elemento a insertar.
     */
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Indice: " + index + ", Tamano: " + size);
        }
        ensureCapacity();
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = element;
        size++;
    }

    /**
     * Obtiene el elemento en la posición indicada.
     * @param index Índice del elemento.
     * @return El elemento en dicha posición.
     */
    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }

    /**
     * Reemplaza el elemento en la posición indicada.
     * @param index Índice de la posición.
     * @param element Nuevo elemento.
     * @return Elemento anterior en dicha posición.
     */
    @SuppressWarnings("unchecked")
    public T set(int index, T element) {
        checkIndex(index);
        T old = (T) data[index];
        data[index] = element;
        return old;
    }

    /**
     * Elimina y retorna el elemento en la posición indicada.
     * @param index Índice del elemento a eliminar.
     * @return El elemento eliminado.
     */
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[--size] = null;
        return removed;
    }

    /**
     * Elimina la primera ocurrencia del elemento dado.
     * @param element Elemento a eliminar.
     * @return true si se encontró y eliminó; false en caso contrario.
     */
    public boolean removeElement(T element) {
        int idx = indexOf(element);
        if (idx >= 0) {
            remove(idx);
            return true;
        }
        return false;
    }

    /**
     * Retorna la cantidad de elementos en la lista.
     * @return Tamaño actual.
     */
    public int size() {
        return size;
    }

    /**
     * Indica si la lista está vacía.
     * @return true si no contiene elementos.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Verifica si la lista contiene el elemento dado.
     * @param element Elemento a buscar.
     * @return true si se encuentra en la lista.
     */
    public boolean contains(T element) {
        return indexOf(element) >= 0;
    }

    /**
     * Retorna el índice de la primera ocurrencia del elemento, o -1 si no existe.
     * @param element Elemento a buscar.
     * @return Índice del elemento o -1.
     */
    public int indexOf(T element) {
        if (element == null) {
            for (int i = 0; i < size; i++) {
                if (data[i] == null) return i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (element.equals(data[i])) return i;
            }
        }
        return -1;
    }

    /**
     * Elimina todos los elementos de la lista.
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    /**
     * Duplica la capacidad del arreglo interno cuando se llena.
     */
    private void ensureCapacity() {
        if (size >= data.length) {
            int newCapacity = data.length * 2;
            Object[] newData = new Object[newCapacity];
            for (int i = 0; i < size; i++) {
                newData[i] = data[i];
            }
            data = newData;
        }
    }

    /**
     * Valida que el índice esté dentro de los límites.
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indice: " + index + ", Tamano: " + size);
        }
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}