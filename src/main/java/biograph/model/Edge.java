package biograph.model;

/**
 * Representa una arista (interacción) entre dos proteínas en el grafo PPI.
 * Almacena el índice del vértice destino y el peso de la interacción.
 *
 * @author BioGraph Team
 */
public class Edge {

    /** Índice del vértice destino en la lista de proteínas del grafo. */
    private int destination;
    /** Peso (costo de interacción). Menor valor = mejor conexión. */
    private int weight;

    /**
     * Constructor.
     * @param destination Índice del vértice destino.
     * @param weight Peso de la interacción.
     */
    public Edge(int destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    /** @return Índice del vértice destino. */
    public int getDestination() { return destination; }

    /** @param destination Nuevo índice del destino. */
    public void setDestination(int destination) { this.destination = destination; }

    /** @return Peso de la interacción. */
    public int getWeight() { return weight; }

    /** @param weight Nuevo peso. */
    public void setWeight(int weight) { this.weight = weight; }

    @Override
    public String toString() {
        return "Edge{dest=" + destination + ", w=" + weight + "}";
    }
}