package biograph.graph;

import biograph.tda.MyArrayList;
import biograph.tda.MyQueue;
import biograph.tda.MyMinHeap;
import biograph.model.Edge;
import biograph.model.DijkstraResult;

/**
 * Grafo no dirigido ponderado implementado con lista de adyacencia.
 * Modela la Red de Interacción Proteína-Proteína (PPI).
 * Incluye algoritmos BFS (componentes conexos), Dijkstra (ruta más corta)
 * y cálculo de centralidad de grado (hubs).
 *
 * @author BioGraph Team
 */
public class Graph {

    /** Nombres de las proteínas; el índice sirve como identificador del vértice. */
    private MyArrayList<String> proteinNames;
    /** Lista de adyacencia: para cada vértice, una lista de aristas. */
    private MyArrayList<MyArrayList<Edge>> adjacencyList;
    /** Indica si hubo modificaciones desde la última carga/guardado. */
    private boolean modified;

    /**
     * Crea un grafo vacío.
     */
    public Graph() {
        this.proteinNames = new MyArrayList<>();
        this.adjacencyList = new MyArrayList<>();
        this.modified = false;
    }

    // ======================= OPERACIONES BASICAS =======================

    /**
     * Busca el índice de una proteína por nombre.
     * @param name Nombre de la proteína.
     * @return Índice o -1 si no existe.
     */
    public int getProteinIndex(String name) {
        for (int i = 0; i < proteinNames.size(); i++) {
            if (proteinNames.get(i).equals(name)) return i;
        }
        return -1;
    }

    /**
     * Obtiene el nombre de la proteína en el índice dado.
     * @param index Índice del vértice.
     * @return Nombre o null si el índice es inválido.
     */
    public String getProteinName(int index) {
        if (index >= 0 && index < proteinNames.size()) return proteinNames.get(index);
        return null;
    }

    /**
     * Agrega una proteína al grafo (sin conexiones iniciales).
     * Si ya existe, retorna su índice.
     * @param name Nombre de la proteína.
     * @return Índice asignado.
     */
    public int addProtein(String name) {
        int idx = getProteinIndex(name);
        if (idx != -1) return idx;
        proteinNames.add(name);
        adjacencyList.add(new MyArrayList<Edge>());
        modified = true;
        return proteinNames.size() - 1;
    }

    /**
     * Agrega una interacción (arista) entre dos proteínas.
     * Si alguna no existe, se crea automáticamente.
     * @param proteinA Nombre de la primera proteína.
     * @param proteinB Nombre de la segunda proteína.
     * @param weight Peso (costo) de la interacción.
     * @return true si se creó la arista; false si ya existía.
     */
    public boolean addInteraction(String proteinA, String proteinB, int weight) {
        int idxA = getProteinIndex(proteinA);
        int idxB = getProteinIndex(proteinB);
        if (idxA == -1) idxA = addProtein(proteinA);
        if (idxB == -1) idxB = addProtein(proteinB);
        if (idxA == idxB) return false; // No permitir auto-lazos
        if (hasEdge(idxA, idxB)) return false;
        adjacencyList.get(idxA).add(new Edge(idxB, weight));
        adjacencyList.get(idxB).add(new Edge(idxA, weight));
        modified = true;
        return true;
    }

    /**
     * Elimina una proteína y todas sus interacciones.
     * Simula el efecto de un fármaco "apagando" la proteína.
     * @param name Nombre de la proteína a eliminar.
     * @return true si se eliminó correctamente.
     */
    public boolean removeProtein(String name) {
        int index = getProteinIndex(name);
        if (index == -1) return false;

        // Eliminar aristas que apuntan a este vértice y ajustar índices
        for (int i = 0; i < adjacencyList.size(); i++) {
            if (i == index) continue;
            MyArrayList<Edge> edges = adjacencyList.get(i);
            for (int j = edges.size() - 1; j >= 0; j--) {
                int dest = edges.get(j).getDestination();
                if (dest == index) {
                    edges.remove(j);
                } else if (dest > index) {
                    edges.get(j).setDestination(dest - 1);
                }
            }
        }

        proteinNames.remove(index);
        adjacencyList.remove(index);
        modified = true;
        return true;
    }

    /**
     * Elimina la interacción entre dos proteínas.
     * @param proteinA Primera proteína.
     * @param proteinB Segunda proteína.
     * @return true si se eliminó la arista.
     */
    public boolean removeInteraction(String proteinA, String proteinB) {
        int idxA = getProteinIndex(proteinA);
        int idxB = getProteinIndex(proteinB);
        if (idxA == -1 || idxB == -1) return false;

        boolean removed = false;
        MyArrayList<Edge> edgesA = adjacencyList.get(idxA);
        for (int i = 0; i < edgesA.size(); i++) {
            if (edgesA.get(i).getDestination() == idxB) {
                edgesA.remove(i);
                removed = true;
                break;
            }
        }
        MyArrayList<Edge> edgesB = adjacencyList.get(idxB);
        for (int i = 0; i < edgesB.size(); i++) {
            if (edgesB.get(i).getDestination() == idxA) {
                edgesB.remove(i);
                break;
            }
        }
        if (removed) modified = true;
        return removed;
    }

    /**
     * Actualiza el peso de una interacción existente.
     * @param proteinA Primera proteína.
     * @param proteinB Segunda proteína.
     * @param newWeight Nuevo peso.
     * @return true si se actualizó.
     */
    public boolean updateWeight(String proteinA, String proteinB, int newWeight) {
        int idxA = getProteinIndex(proteinA);
        int idxB = getProteinIndex(proteinB);
        if (idxA == -1 || idxB == -1) return false;

        boolean updated = false;
        MyArrayList<Edge> edgesA = adjacencyList.get(idxA);
        for (int i = 0; i < edgesA.size(); i++) {
            if (edgesA.get(i).getDestination() == idxB) {
                edgesA.get(i).setWeight(newWeight);
                updated = true;
                break;
            }
        }
        MyArrayList<Edge> edgesB = adjacencyList.get(idxB);
        for (int i = 0; i < edgesB.size(); i++) {
            if (edgesB.get(i).getDestination() == idxA) {
                edgesB.get(i).setWeight(newWeight);
                break;
            }
        }
        if (updated) modified = true;
        return updated;
    }

    /**
     * Verifica si existe arista entre dos vértices.
     */
    public boolean hasEdge(int idxA, int idxB) {
        MyArrayList<Edge> edges = adjacencyList.get(idxA);
        for (int i = 0; i < edges.size(); i++) {
            if (edges.get(i).getDestination() == idxB) return true;
        }
        return false;
    }

    /** @return Número total de vértices. */
    public int getNumVertices() { return proteinNames.size(); }

    /** Retorna el grado (número de conexiones) del vértice. */
    public int getDegree(int index) {
        if (index < 0 || index >= adjacencyList.size()) return 0;
        return adjacencyList.get(index).size();
    }

    /** @return Cuenta total de aristas del grafo. */
    public int getNumEdges() {
        int count = 0;
        for (int i = 0; i < adjacencyList.size(); i++) {
            count += adjacencyList.get(i).size();
        }
        return count / 2; // cada arista se cuenta dos veces
    }

    /** @return Lista de nombres de proteínas. */
    public MyArrayList<String> getProteinNames() { return proteinNames; }

    /** Retorna la lista de aristas del vértice indicado. */
    public MyArrayList<Edge> getNeighbors(int index) {
        if (index < 0 || index >= adjacencyList.size()) return new MyArrayList<>();
        return adjacencyList.get(index);
    }

    /** @return true si el grafo fue modificado desde la última carga/guardado. */
    public boolean isModified() { return modified; }

    /** @param modified Nuevo estado de modificación. */
    public void setModified(boolean modified) { this.modified = modified; }

    /** Limpia completamente el grafo. */
    public void clear() {
        proteinNames.clear();
        adjacencyList.clear();
        modified = false;
    }

    // ========================= ALGORITMOS =========================

    /**
     * BFS — Encuentra todos los componentes conexos del grafo.
     * Cada componente representa un complejo proteico aislado.
     *
     * @return Lista de componentes; cada componente es una lista de nombres.
     */
    public MyArrayList<MyArrayList<String>> findConnectedComponents() {
        int n = getNumVertices();
        boolean[] visited = new boolean[n];
        MyArrayList<MyArrayList<String>> components = new MyArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                MyArrayList<String> component = new MyArrayList<>();
                MyQueue<Integer> queue = new MyQueue<>();
                queue.enqueue(i);
                visited[i] = true;

                while (!queue.isEmpty()) {
                    int current = queue.dequeue();
                    component.add(proteinNames.get(current));

                    MyArrayList<Edge> neighbors = adjacencyList.get(current);
                    for (int j = 0; j < neighbors.size(); j++) {
                        int neighbor = neighbors.get(j).getDestination();
                        if (!visited[neighbor]) {
                            visited[neighbor] = true;
                            queue.enqueue(neighbor);
                        }
                    }
                }
                components.add(component);
            }
        }
        return components;
    }

    /**
     * Dijkstra — Calcula la ruta metabólica más corta entre dos proteínas.
     * Utiliza un Min-Heap para seleccionar eficientemente el vértice con menor distancia.
     *
     * @param source Proteína origen.
     * @param destination Proteína destino.
     * @return Objeto DijkstraResult con la ruta y el costo.
     */
    public DijkstraResult dijkstra(String source, String destination) {
        int srcIdx = getProteinIndex(source);
        int destIdx = getProteinIndex(destination);

        if (srcIdx == -1 || destIdx == -1) {
            return new DijkstraResult(new MyArrayList<>(), -1, false);
        }
        if (srcIdx == destIdx) {
            MyArrayList<String> path = new MyArrayList<>();
            path.add(source);
            return new DijkstraResult(path, 0, true);
        }

        int n = getNumVertices();
        final int INF = Integer.MAX_VALUE;
        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            dist[i] = INF;
            prev[i] = -1;
        }
        dist[srcIdx] = 0;

        MyMinHeap heap = new MyMinHeap(n);
        heap.insert(srcIdx, 0);

        while (!heap.isEmpty()) {
            int[] min = heap.extractMin();
            int u = min[0];

            if (visited[u]) continue;
            visited[u] = true;
            if (u == destIdx) break;

            MyArrayList<Edge> neighbors = adjacencyList.get(u);
            for (int i = 0; i < neighbors.size(); i++) {
                Edge edge = neighbors.get(i);
                int v = edge.getDestination();
                int w = edge.getWeight();

                if (!visited[v] && dist[u] != INF && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                    heap.insert(v, dist[v]);
                }
            }
        }

        if (dist[destIdx] == INF) {
            return new DijkstraResult(new MyArrayList<>(), -1, false);
        }

        // Reconstruir ruta desde destino hacia origen
        MyArrayList<String> path = new MyArrayList<>();
        int current = destIdx;
        while (current != -1) {
            path.add(0, proteinNames.get(current));
            current = prev[current];
        }
        return new DijkstraResult(path, dist[destIdx], true);
    }

    /**
     * Calcula la centralidad de grado de cada proteína,
     * ordenada de mayor a menor grado.
     *
     * @return Arreglo bidimensional: cada fila es {nombre, grado}.
     */
    public String[][] calculateDegreeCentrality() {
        int n = getNumVertices();
        if (n == 0) return new String[0][0];

        String[][] result = new String[n][2];
        for (int i = 0; i < n; i++) {
            result[i][0] = proteinNames.get(i);
            result[i][1] = String.valueOf(getDegree(i));
        }

        // Ordenamiento burbuja descendente por grado
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (Integer.parseInt(result[j][1]) < Integer.parseInt(result[j + 1][1])) {
                    String[] temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                }
            }
        }
        return result;
    }

    /**
     * Identifica la proteína hub (mayor centralidad de grado).
     * @return Nombre de la proteína con más conexiones, o null si el grafo está vacío.
     */
    public String getMainHub() {
        int maxDegree = -1;
        String hub = null;
        for (int i = 0; i < getNumVertices(); i++) {
            int degree = getDegree(i);
            if (degree > maxDegree) {
                maxDegree = degree;
                hub = proteinNames.get(i);
            }
        }
        return hub;
    }
}