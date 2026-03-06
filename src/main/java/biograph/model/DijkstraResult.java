package biograph.model;

import biograph.tda.MyArrayList;

/**
 * Encapsula el resultado del algoritmo de Dijkstra:
 * la ruta más corta entre dos proteínas y su costo total.
 *
 * @author BioGraph Team
 */
public class DijkstraResult {

    private MyArrayList<String> path;
    private int totalCost;
    private boolean pathExists;

    /**
     * Constructor.
     * @param path Lista ordenada de proteínas en la ruta.
     * @param totalCost Costo acumulado de la ruta.
     * @param pathExists true si existe un camino entre origen y destino.
     */
    public DijkstraResult(MyArrayList<String> path, int totalCost, boolean pathExists) {
        this.path = path;
        this.totalCost = totalCost;
        this.pathExists = pathExists;
    }

    /** @return Lista de nombres de proteínas en la ruta. */
    public MyArrayList<String> getPath() { return path; }

    /** @return Costo total de la ruta. */
    public int getTotalCost() { return totalCost; }

    /** @return true si la ruta existe. */
    public boolean isPathExists() { return pathExists; }

    @Override
    public String toString() {
        if (!pathExists) {
            return "No existe ruta entre las proteinas seleccionadas.\n"
                 + "(Pertenecen a componentes conexos diferentes)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Ruta: ");
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i));
            if (i < path.size() - 1) sb.append(" -> ");
        }
        sb.append("\nCosto total: ").append(totalCost);
        return sb.toString();
    }
}