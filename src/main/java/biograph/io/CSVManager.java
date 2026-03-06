package biograph.io;

import biograph.graph.Graph;
import biograph.tda.MyArrayList;
import biograph.model.Edge;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Maneja la lectura y escritura de archivos CSV para el grafo PPI.
 * Formato: Proteina_A,Proteina_B,Costo_Interaccion
 *
 * @author BioGraph Team
 */
public class CSVManager {

    /**
     * Carga un grafo desde un archivo CSV.
     * Cada línea debe tener el formato: ProteinaOrigen,ProteinaDestino,Peso
     *
     * @param filePath Ruta del archivo.
     * @return Grafo con las proteínas e interacciones leídas.
     * @throws IOException Si hay error de lectura.
     */
    public static Graph loadFromCSV(String filePath) throws IOException {
        Graph graph = new Graph();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = splitCSVLine(line);
                if (parts.length < 3) continue;

                String proteinA = parts[0].trim();
                String proteinB = parts[1].trim();

                if (proteinA.isEmpty() || proteinB.isEmpty()) continue;

                try {
                    int weight = Integer.parseInt(parts[2].trim());
                    if (weight > 0) {
                        graph.addInteraction(proteinA, proteinB, weight);
                    }
                } catch (NumberFormatException e) {
                    // Ignorar líneas con peso no numérico (posible encabezado)
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        graph.setModified(false);
        return graph;
    }

    /**
     * Guarda el grafo en un archivo CSV.
     *
     * @param graph Grafo a guardar.
     * @param filePath Ruta de destino.
     * @throws IOException Si hay error de escritura.
     */
    public static void saveToCSV(Graph graph, String filePath) throws IOException {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            int n = graph.getNumVertices();

            for (int i = 0; i < n; i++) {
                MyArrayList<Edge> neighbors = graph.getNeighbors(i);
                for (int j = 0; j < neighbors.size(); j++) {
                    Edge edge = neighbors.get(j);
                    // Solo escribir si i < destino para evitar duplicados
                    if (i < edge.getDestination()) {
                        writer.write(graph.getProteinName(i) + ","
                                + graph.getProteinName(edge.getDestination()) + ","
                                + edge.getWeight());
                        writer.newLine();
                    }
                }
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        graph.setModified(false);
    }

    /**
     * Divide una línea CSV sin usar expresiones regulares.
     * @param line Línea a dividir.
     * @return Arreglo de campos.
     */
    private static String[] splitCSVLine(String line) {
        int count = 1;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ',') count++;
        }
        String[] result = new String[count];
        int start = 0;
        int idx = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ',') {
                result[idx++] = line.substring(start, i);
                start = i + 1;
            }
        }
        result[idx] = line.substring(start);
        return result;
    }
}