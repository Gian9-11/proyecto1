package biograph.tda;

/**
 * Min-Heap (cola de prioridad mínima) para enteros.
 * Almacena pares (vértice, distancia) y extrae el de menor distancia.
 * Utilizado por el algoritmo de Dijkstra.
 *
 * @author BioGraph Team
 */
public class MyMinHeap {

    private int[] vertices;
    private int[] distances;
    private int size;
    private int capacity;

    /**
     * Crea un min-heap con la capacidad indicada.
     * @param capacity Capacidad inicial.
     */
    public MyMinHeap(int capacity) {
        this.capacity = Math.max(capacity, 10);
        this.vertices = new int[this.capacity];
        this.distances = new int[this.capacity];
        this.size = 0;
    }

    /**
     * Inserta un par (vértice, distancia) en el heap.
     * @param vertex Índice del vértice.
     * @param distance Distancia asociada.
     */
    public void insert(int vertex, int distance) {
        if (size >= capacity) {
            resize();
        }
        vertices[size] = vertex;
        distances[size] = distance;
        size++;
        bubbleUp(size - 1);
    }

    /**
     * Extrae y retorna el par con menor distancia.
     * @return Arreglo de dos posiciones: {vértice, distancia}.
     */
    public int[] extractMin() {
        if (isEmpty()) {
            throw new RuntimeException("El heap esta vacio.");
        }
        int minVertex = vertices[0];
        int minDist = distances[0];
        size--;
        if (size > 0) {
            vertices[0] = vertices[size];
            distances[0] = distances[size];
            bubbleDown(0);
        }
        return new int[]{minVertex, minDist};
    }

    /**
     * Indica si el heap está vacío.
     * @return true si no contiene elementos.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Sube el elemento en la posición dada hasta restaurar la propiedad del heap. */
    private void bubbleUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (distances[index] < distances[parent]) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    /** Baja el elemento en la posición dada hasta restaurar la propiedad del heap. */
    private void bubbleDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;
            if (left < size && distances[left] < distances[smallest]) {
                smallest = left;
            }
            if (right < size && distances[right] < distances[smallest]) {
                smallest = right;
            }
            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    /** Intercambia dos posiciones en los arreglos internos. */
    private void swap(int i, int j) {
        int tv = vertices[i]; vertices[i] = vertices[j]; vertices[j] = tv;
        int td = distances[i]; distances[i] = distances[j]; distances[j] = td;
    }

    /** Duplica la capacidad interna. */
    private void resize() {
        int newCap = capacity * 2;
        int[] nv = new int[newCap];
        int[] nd = new int[newCap];
        for (int i = 0; i < size; i++) {
            nv[i] = vertices[i];
            nd[i] = distances[i];
        }
        vertices = nv;
        distances = nd;
        capacity = newCap;
    }
}