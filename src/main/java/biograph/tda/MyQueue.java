package biograph.tda;

/**
 * Cola genérica (FIFO) implementada con nodos enlazados.
 * Utilizada por el algoritmo BFS para la detección de componentes conexos.
 *
 * @param <T> Tipo de elementos.
 * @author BioGraph Team
 */
public class MyQueue<T> {

    /** Nodo interno de la cola. */
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> front;
    private Node<T> rear;
    private int size;

    /**
     * Crea una cola vacía.
     */
    public MyQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    /**
     * Agrega un elemento al final de la cola.
     * @param element Elemento a encolar.
     */
    public void enqueue(T element) {
        Node<T> newNode = new Node<>(element);
        if (rear != null) {
            rear.next = newNode;
        }
        rear = newNode;
        if (front == null) {
            front = rear;
        }
        size++;
    }

    /**
     * Elimina y retorna el elemento al frente de la cola.
     * @return Elemento desencolado.
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("La cola esta vacia.");
        }
        T data = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    /**
     * Retorna el elemento al frente sin eliminarlo.
     * @return Elemento al frente de la cola.
     */
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("La cola esta vacia.");
        }
        return front.data;
    }

    /**
     * Indica si la cola está vacía.
     * @return true si no tiene elementos.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Retorna la cantidad de elementos en la cola.
     * @return Tamaño actual.
     */
    public int size() {
        return size;
    }
}