package biograph.gui;

import biograph.graph.Graph;
import biograph.io.CSVManager;
import biograph.model.DijkstraResult;
import biograph.model.Edge;
import biograph.tda.MyArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

/**
 * Ventana principal de la aplicación BioGraph.
 * Proporciona la interfaz gráfica para todas las operaciones del sistema:
 * carga/guardado de archivos, modificación del grafo, visualización,
 * detección de complejos, cálculo de rutas y análisis de hubs.
 *
 * @author BioGraph Team
 */
public class MainFrame extends JFrame {

    // ============== Datos ==============
    private Graph graph;
    private String currentFilePath;

    // ============== Componentes GUI ==============
    private JTextArea resultsArea;
    private JLabel statusLabel;
    private JComboBox<String> cmbOrigin;
    private JComboBox<String> cmbDestination;

    // Colores del tema
    private static final Color BG_MAIN = new Color(240, 242, 245);
    private static final Color BG_PANEL = Color.WHITE;
    private static final Color COLOR_PRIMARY = new Color(37, 99, 235);
    private static final Color COLOR_DANGER = new Color(220, 38, 38);
    private static final Color COLOR_SUCCESS = new Color(5, 150, 105);
    private static final Color COLOR_ACCENT = new Color(99, 102, 241);
    private static final Color COLOR_TEXT = new Color(31, 41, 55);

    /**
     * Constructor: inicializa el grafo vacío, construye la GUI
     * y carga el dataset maestro si existe.
     */
    public MainFrame() {
        this.graph = new Graph();
        this.currentFilePath = null;
        initComponents();
        loadDefaultDataset();
    }


    /** Construye todos los componentes de la interfaz gráfica. */
    private void initComponents() {
        setTitle("BioGraph - Analisis de Interacciones Proteicas para el Descubrimiento de Farmacos");
        setSize(1050, 700);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        setJMenuBar(createMenuBar());
        setLayout(new BorderLayout(8, 8));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(8, 8, 8, 8));

        add(createLeftPanel(), BorderLayout.WEST);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    /** Crea la barra de menú. */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(BG_PANEL);

        JMenu menuFile = new JMenu("Archivo");
        JMenuItem miLoad = new JMenuItem("Cargar CSV...");
        miLoad.addActionListener(e -> loadFile());
        JMenuItem miSave = new JMenuItem("Guardar");
        miSave.addActionListener(e -> saveFile());
        JMenuItem miSaveAs = new JMenuItem("Guardar como...");
        miSaveAs.addActionListener(e -> saveFileAs());
        JMenuItem miExit = new JMenuItem("Salir");
        miExit.addActionListener(e -> confirmExit());
        menuFile.add(miLoad);
        menuFile.add(miSave);
        menuFile.add(miSaveAs);
        menuFile.addSeparator();
        menuFile.add(miExit);

        JMenu menuAnalysis = new JMenu("Analisis");
        JMenuItem miGraph = new JMenuItem("Mostrar Grafo");
        miGraph.addActionListener(e -> showGraphVisualization());
        JMenuItem miComp = new JMenuItem("Detectar Complejos");
        miComp.addActionListener(e -> detectComplexes());
        JMenuItem miHubs = new JMenuItem("Identificar Hubs");
        miHubs.addActionListener(e -> identifyHubs());
        menuAnalysis.add(miGraph);
        menuAnalysis.add(miComp);
        menuAnalysis.add(miHubs);

        JMenu menuHelp = new JMenu("Ayuda");
        JMenuItem miAbout = new JMenuItem("Acerca de...");
        miAbout.addActionListener(e -> showAbout());
        menuHelp.add(miAbout);

        menuBar.add(menuFile);
        menuBar.add(menuAnalysis);
        menuBar.add(menuHelp);
        return menuBar;
    }

    /** Crea el panel izquierdo con los botones de operación. */
    private JPanel createLeftPanel() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(BG_MAIN);
        left.setPreferredSize(new Dimension(260, 0));

        // --- Sección Archivo ---
        JPanel filePanel = createSection("Archivo");
        filePanel.add(createButton("Cargar CSV...", COLOR_PRIMARY, e -> loadFile()));
        filePanel.add(Box.createVerticalStrut(5));
        filePanel.add(createButton("Guardar Cambios", COLOR_SUCCESS, e -> saveFile()));
        left.add(filePanel);
        left.add(Box.createVerticalStrut(8));

        // --- Sección Visualización ---
        JPanel viewPanel = createSection("Visualizacion");
        viewPanel.add(createButton("Mostrar Grafo", COLOR_ACCENT, e -> showGraphVisualization()));
        left.add(viewPanel);
        left.add(Box.createVerticalStrut(8));

        // --- Sección Modificar Grafo ---
        JPanel modPanel = createSection("Modificar Grafo");
        modPanel.add(createButton("Agregar Proteina", COLOR_SUCCESS, e -> addProteinDialog()));
        modPanel.add(Box.createVerticalStrut(5));
        modPanel.add(createButton("Eliminar Proteina", COLOR_DANGER, e -> removeProteinDialog()));
        modPanel.add(Box.createVerticalStrut(5));
        modPanel.add(createButton("Agregar Interaccion", COLOR_PRIMARY, e -> addInteractionDialog()));
        modPanel.add(Box.createVerticalStrut(5));
        modPanel.add(createButton("Eliminar Interaccion", COLOR_DANGER, e -> removeInteractionDialog()));
        left.add(modPanel);
        left.add(Box.createVerticalStrut(8));

        // --- Sección Análisis ---
        JPanel analysisPanel = createSection("Analisis");
        analysisPanel.add(createButton("Detectar Complejos (BFS)", COLOR_ACCENT, e -> detectComplexes()));
        analysisPanel.add(Box.createVerticalStrut(5));
        analysisPanel.add(createButton("Identificar Hubs", COLOR_ACCENT, e -> identifyHubs()));
        analysisPanel.add(Box.createVerticalStrut(10));

        analysisPanel.add(new JLabel("Ruta mas corta (Dijkstra):"));
        analysisPanel.add(Box.createVerticalStrut(3));
        JPanel originPanel = new JPanel(new BorderLayout(5, 0));
        originPanel.setOpaque(false);
        originPanel.add(new JLabel("Origen: "), BorderLayout.WEST);
        cmbOrigin = new JComboBox<>();
        originPanel.add(cmbOrigin, BorderLayout.CENTER);
        analysisPanel.add(originPanel);
        analysisPanel.add(Box.createVerticalStrut(3));

        JPanel destPanel = new JPanel(new BorderLayout(5, 0));
        destPanel.setOpaque(false);
        destPanel.add(new JLabel("Destino:"), BorderLayout.WEST);
        cmbDestination = new JComboBox<>();
        destPanel.add(cmbDestination, BorderLayout.CENTER);
        analysisPanel.add(destPanel);
        analysisPanel.add(Box.createVerticalStrut(5));
        analysisPanel.add(createButton("Calcular Ruta", COLOR_PRIMARY, e -> calculateShortestPath()));
        left.add(analysisPanel);

        left.add(Box.createVerticalGlue());
        return left;
    }

    /** Crea el panel central con el área de resultados. */
    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG_PANEL);
        center.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)),
                " Resultados ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13), COLOR_TEXT));

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultsArea.setMargin(new Insets(10, 10, 10, 10));
        resultsArea.setBackground(new Color(250, 250, 252));
        resultsArea.setForeground(COLOR_TEXT);

        JScrollPane scroll = new JScrollPane(resultsArea);
        scroll.setBorder(null);
        center.add(scroll, BorderLayout.CENTER);
        return center;
    }

    /** Crea la barra de estado inferior. */
    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(31, 41, 55));
        bar.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusLabel = new JLabel("Listo");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    // ============== UTILIDADES DE GUI ==============

    /** Crea un panel de sección con título y borde. */
    private JPanel createSection(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(209, 213, 219)),
                        " " + title + " ",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("SansSerif", Font.BOLD, 12), COLOR_TEXT),
                new EmptyBorder(5, 8, 8, 8)));
        return panel;
    }

    /** Crea un botón estilizado. */
    private JButton createButton(String text, Color bg, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btn.addActionListener(action);
        return btn;
    }

    /** Actualiza los JComboBox de proteínas con la información actual del grafo. */
    private void refreshProteinCombos() {
        cmbOrigin.removeAllItems();
        cmbDestination.removeAllItems();
        for (int i = 0; i < graph.getNumVertices(); i++) {
            String name = graph.getProteinName(i);
            cmbOrigin.addItem(name);
            cmbDestination.addItem(name);
        }
    }

    /** Actualiza la barra de estado con información del grafo. */
    private void updateStatus() {
        String mod = graph.isModified() ? " [Modificado]" : "";
        statusLabel.setText("Proteinas: " + graph.getNumVertices()
                + "  |  Interacciones: " + graph.getNumEdges()
                + "  |  Archivo: " + (currentFilePath != null ? currentFilePath : "Ninguno")
                + mod);
    }

    /** Agrega texto al área de resultados. */
    private void appendResult(String text) {
        resultsArea.append(text + "\n");
        resultsArea.setCaretPosition(resultsArea.getDocument().getLength());
    }

    /** Limpia y escribe texto en el área de resultados. */
    private void setResult(String text) {
        resultsArea.setText(text + "\n");
    }

    // ======================== OPERACIONES DE ARCHIVO ========================

    /** Carga el dataset maestro al iniciar la aplicación. */
    private void loadDefaultDataset() {
        String[] possiblePaths = {
            "dataset_maestro.csv",
            "data/dataset_maestro.csv",
            "src/dataset_maestro.csv"
        };
        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists()) {
                try {
                    graph = CSVManager.loadFromCSV(path);
                    currentFilePath = path;
                    refreshProteinCombos();
                    updateStatus();
                    showWelcomeMessage();
                    return;
                } catch (IOException e) {
                    // Intentar siguiente ruta
                }
            }
        }
        // Si no encuentra archivo, mostrar mensaje de bienvenida
        showWelcomeMessage();
        updateStatus();
    }

    /** Muestra el mensaje de bienvenida con resumen del grafo. */
    private void showWelcomeMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("  BioGraph - Analisis de Interacciones Proteicas\n");
        sb.append("  para el Descubrimiento de Farmacos\n");
        sb.append("============================================================\n\n");

        if (graph.getNumVertices() > 0) {
            sb.append("  Grafo cargado exitosamente.\n");
            sb.append("  - Proteinas:      ").append(graph.getNumVertices()).append("\n");
            sb.append("  - Interacciones:  ").append(graph.getNumEdges()).append("\n");

            MyArrayList<MyArrayList<String>> comps = graph.findConnectedComponents();
            sb.append("  - Complejos:      ").append(comps.size()).append("\n");

            String hub = graph.getMainHub();
            if (hub != null) {
                sb.append("  - Hub principal:  ").append(hub);
                sb.append(" (grado ").append(graph.getDegree(graph.getProteinIndex(hub))).append(")\n");
            }
        } else {
            sb.append("  No se encontro el dataset maestro.\n");
            sb.append("  Use 'Cargar CSV...' para seleccionar un archivo.\n");
        }

        sb.append("\n============================================================\n");
        sb.append("  Use los botones a la izquierda para operar el sistema.\n");
        sb.append("============================================================\n");
        setResult(sb.toString());
    }

    /** Carga un archivo CSV seleccionado por el usuario. */
    private void loadFile() {
        if (graph.isModified()) {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Hay cambios sin guardar. Desea guardar antes de cargar un nuevo archivo?",
                    "Cambios sin guardar", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                saveFile();
            } else if (opt == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        JFileChooser chooser = new JFileChooser(".");
        chooser.setDialogTitle("Seleccionar archivo CSV de interacciones");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV (*.csv, *.txt)", "csv", "txt"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                graph = CSVManager.loadFromCSV(file.getAbsolutePath());
                currentFilePath = file.getAbsolutePath();
                refreshProteinCombos();
                updateStatus();
                setResult("Archivo cargado exitosamente: " + file.getName() + "\n"
                        + "Proteinas: " + graph.getNumVertices()
                        + " | Interacciones: " + graph.getNumEdges());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al leer el archivo:\n" + ex.getMessage(),
                        "Error de Lectura", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Guarda los cambios en el archivo actual. */
    private void saveFile() {
        if (currentFilePath == null) {
            saveFileAs();
            return;
        }
        try {
            CSVManager.saveToCSV(graph, currentFilePath);
            updateStatus();
            appendResult("\n>> Archivo guardado exitosamente en: " + currentFilePath);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el archivo:\n" + ex.getMessage(),
                    "Error de Escritura", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Guarda el grafo en un nuevo archivo seleccionado por el usuario. */
    private void saveFileAs() {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setDialogTitle("Guardar archivo CSV");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";
            try {
                CSVManager.saveToCSV(graph, path);
                currentFilePath = path;
                updateStatus();
                appendResult("\n>> Archivo guardado como: " + path);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ======================== MODIFICAR GRAFO ========================

    /** Dialogo para agregar una nueva proteína con conexiones opcionales. */
    private void addProteinDialog() {
        String name = JOptionPane.showInputDialog(this,
                "Ingrese el nombre de la nueva proteina:",
                "Agregar Proteina", JOptionPane.PLAIN_MESSAGE);

        if (name == null || name.trim().isEmpty()) return;
        name = name.trim().toUpperCase();

        if (graph.getProteinIndex(name) != -1) {
            JOptionPane.showMessageDialog(this,
                    "La proteina '" + name + "' ya existe en el grafo.",
                    "Proteina Duplicada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        graph.addProtein(name);

        // Preguntar si desea agregar conexiones
        int addConn = JOptionPane.showConfirmDialog(this,
                "Proteina '" + name + "' agregada.\nDesea agregar interacciones con otras proteinas?",
                "Agregar Conexiones", JOptionPane.YES_NO_OPTION);

        if (addConn == JOptionPane.YES_OPTION && graph.getNumVertices() > 1) {
            addConnectionsForProtein(name);
        }

        refreshProteinCombos();
        updateStatus();
        appendResult("\n>> Proteina '" + name + "' agregada al grafo.");
    }

    /** Permite agregar múltiples conexiones para una proteína recién creada. */
    private void addConnectionsForProtein(String proteinName) {
        boolean addMore = true;
        while (addMore) {
            JComboBox<String> cmbTarget = new JComboBox<>();
            for (int i = 0; i < graph.getNumVertices(); i++) {
                String pn = graph.getProteinName(i);
                if (!pn.equals(proteinName)) {
                    cmbTarget.addItem(pn);
                }
            }
            JTextField txtWeight = new JTextField("10");

            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            panel.add(new JLabel("Conectar con:"));
            panel.add(cmbTarget);
            panel.add(new JLabel("Peso:"));
            panel.add(txtWeight);

            int res = JOptionPane.showConfirmDialog(this, panel,
                    "Conexion para " + proteinName, JOptionPane.OK_CANCEL_OPTION);

            if (res == JOptionPane.OK_OPTION) {
                String target = (String) cmbTarget.getSelectedItem();
                try {
                    int weight = Integer.parseInt(txtWeight.getText().trim());
                    if (weight <= 0) throw new NumberFormatException();
                    if (graph.addInteraction(proteinName, target, weight)) {
                        appendResult("   Interaccion agregada: " + proteinName + " <-> " + target + " (peso " + weight + ")");
                    } else {
                        JOptionPane.showMessageDialog(this, "Esa interaccion ya existe.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "El peso debe ser un entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                addMore = JOptionPane.showConfirmDialog(this,
                        "Desea agregar otra conexion?", "Continuar",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            } else {
                addMore = false;
            }
        }
    }

    /** Dialogo para eliminar una proteína (simula efecto de fármaco). */
    private void removeProteinDialog() {
        if (graph.getNumVertices() == 0) {
            JOptionPane.showMessageDialog(this, "El grafo esta vacio.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> cmbRemove = new JComboBox<>();
        for (int i = 0; i < graph.getNumVertices(); i++) {
            cmbRemove.addItem(graph.getProteinName(i));
        }

        int res = JOptionPane.showConfirmDialog(this, cmbRemove,
                "Seleccione proteina a eliminar", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            String name = (String) cmbRemove.getSelectedItem();
            int degree = graph.getDegree(graph.getProteinIndex(name));

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Eliminar proteina '" + name + "'?\n"
                    + "Grado actual: " + degree + " interacciones.\n\n"
                    + "Esto simula el efecto de un farmaco\n'apagando' esta proteina.",
                    "Confirmar Eliminacion", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                graph.removeProtein(name);
                refreshProteinCombos();
                updateStatus();
                appendResult("\n>> Proteina '" + name + "' eliminada (farmaco aplicado). "
                        + "Se removieron " + degree + " interacciones.");
            }
        }
    }

    /** Dialogo para agregar una interacción entre dos proteínas existentes. */
    private void addInteractionDialog() {
        if (graph.getNumVertices() < 2) {
            JOptionPane.showMessageDialog(this, "Se necesitan al menos 2 proteinas.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> cmbA = new JComboBox<>();
        JComboBox<String> cmbB = new JComboBox<>();
        for (int i = 0; i < graph.getNumVertices(); i++) {
            cmbA.addItem(graph.getProteinName(i));
            cmbB.addItem(graph.getProteinName(i));
        }
        if (cmbB.getItemCount() > 1) cmbB.setSelectedIndex(1);
        JTextField txtW = new JTextField("10");

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Proteina A:"));
        panel.add(cmbA);
        panel.add(new JLabel("Proteina B:"));
        panel.add(cmbB);
        panel.add(new JLabel("Peso:"));
        panel.add(txtW);

        int res = JOptionPane.showConfirmDialog(this, panel, "Agregar Interaccion", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            String pA = (String) cmbA.getSelectedItem();
            String pB = (String) cmbB.getSelectedItem();

            if (pA.equals(pB)) {
                JOptionPane.showMessageDialog(this, "No se permiten auto-lazos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int weight = Integer.parseInt(txtW.getText().trim());
                if (weight <= 0) throw new NumberFormatException();
                if (graph.addInteraction(pA, pB, weight)) {
                    updateStatus();
                    appendResult("\n>> Interaccion agregada: " + pA + " <-> " + pB + " (peso " + weight + ")");
                } else {
                    JOptionPane.showMessageDialog(this, "Esa interaccion ya existe.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El peso debe ser un entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Dialogo para eliminar una interacción entre dos proteínas. */
    private void removeInteractionDialog() {
        if (graph.getNumVertices() < 2) {
            JOptionPane.showMessageDialog(this, "El grafo no tiene suficientes proteinas.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> cmbA = new JComboBox<>();
        JComboBox<String> cmbB = new JComboBox<>();
        for (int i = 0; i < graph.getNumVertices(); i++) {
            cmbA.addItem(graph.getProteinName(i));
            cmbB.addItem(graph.getProteinName(i));
        }
        if (cmbB.getItemCount() > 1) cmbB.setSelectedIndex(1);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Proteina A:"));
        panel.add(cmbA);
        panel.add(new JLabel("Proteina B:"));
        panel.add(cmbB);

        int res = JOptionPane.showConfirmDialog(this, panel, "Eliminar Interaccion", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            String pA = (String) cmbA.getSelectedItem();
            String pB = (String) cmbB.getSelectedItem();
            if (graph.removeInteraction(pA, pB)) {
                updateStatus();
                appendResult("\n>> Interaccion eliminada: " + pA + " <-> " + pB);
            } else {
                JOptionPane.showMessageDialog(this, "No existe interaccion entre " + pA + " y " + pB + ".",
                        "No Encontrada", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // ======================== VISUALIZACION ========================

    /** Muestra el grafo usando GraphStream con colores por componente conexo. */
    private void showGraphVisualization() {
        if (graph.getNumVertices() == 0) {
            JOptionPane.showMessageDialog(this, "El grafo esta vacio. Cargue un archivo primero.",
                    "Grafo Vacio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            System.setProperty("org.graphstream.ui", "swing");

            org.graphstream.graph.implementations.SingleGraph gsGraph =
                    new org.graphstream.graph.implementations.SingleGraph("BioGraph - PPI Network");

            gsGraph.setAttribute("ui.stylesheet", getGraphCSS());
            gsGraph.setAttribute("ui.quality");
            gsGraph.setAttribute("ui.antialias");

            // Agregar nodos
            for (int i = 0; i < graph.getNumVertices(); i++) {
                String name = graph.getProteinName(i);
                org.graphstream.graph.Node node = gsGraph.addNode(name);
                node.setAttribute("ui.label", name + " [" + graph.getDegree(i) + "]");
            }

            // Agregar aristas
            int edgeCount = 0;
            for (int i = 0; i < graph.getNumVertices(); i++) {
                MyArrayList<Edge> neighbors = graph.getNeighbors(i);
                for (int j = 0; j < neighbors.size(); j++) {
                    Edge edge = neighbors.get(j);
                    if (i < edge.getDestination()) {
                        String eid = "e" + (edgeCount++);
                        org.graphstream.graph.Edge gsEdge = gsGraph.addEdge(eid,
                                graph.getProteinName(i),
                                graph.getProteinName(edge.getDestination()));
                        gsEdge.setAttribute("ui.label", String.valueOf(edge.getWeight()));
                    }
                }
            }

            // Colorear por componente conexo
            MyArrayList<MyArrayList<String>> components = graph.findConnectedComponents();
            String[] colors = {"#e74c3c", "#3498db", "#2ecc71", "#f39c12",
                               "#9b59b6", "#1abc9c", "#e67e22", "#34495e"};

            for (int c = 0; c < components.size(); c++) {
                String color = colors[c % colors.length];
                MyArrayList<String> comp = components.get(c);
                for (int p = 0; p < comp.size(); p++) {
                    org.graphstream.graph.Node node = gsGraph.getNode(comp.get(p));
                    if (node != null) {
                        node.setAttribute("ui.style", "fill-color: " + color + ";");
                    }
                }
            }

            // Resaltar el hub principal con mayor tamaño
            String hub = graph.getMainHub();
            if (hub != null) {
                org.graphstream.graph.Node hubNode = gsGraph.getNode(hub);
                if (hubNode != null) {
                    hubNode.setAttribute("ui.style",
                            hubNode.getAttribute("ui.style") + " size: 40px; stroke-width: 3px;");
                }
            }

            org.graphstream.ui.view.Viewer viewer = gsGraph.display();
            viewer.setCloseFramePolicy(org.graphstream.ui.view.Viewer.CloseFramePolicy.HIDE_ONLY);

            appendResult("\n>> Grafo visualizado. Componentes coloreados. Hub resaltado.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al visualizar el grafo:\n" + ex.getMessage()
                    + "\n\nAsegurese de incluir las librerias GraphStream:\n"
                    + "- gs-core-2.0.jar\n- gs-ui-swing-2.0.jar",
                    "Error de Visualizacion", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Retorna la hoja de estilo CSS para GraphStream. */
    private String getGraphCSS() {
        return "graph { padding: 40px; }"
             + "node {"
             + "  size: 28px;"
             + "  fill-color: #3498db;"
             + "  text-alignment: above;"
             + "  text-size: 14px;"
             + "  text-color: #2c3e50;"
             + "  text-style: bold;"
             + "  stroke-mode: plain;"
             + "  stroke-color: #2c3e50;"
             + "  stroke-width: 2px;"
             + "}"
             + "edge {"
             + "  fill-color: #95a5a6;"
             + "  size: 2px;"
             + "  text-alignment: along;"
             + "  text-size: 12px;"
             + "  text-color: #c0392b;"
             + "  text-style: bold;"
             + "}";
    }

    // ======================== ALGORITMOS / ANALISIS ========================

    /** Ejecuta BFS para detectar complejos proteicos (componentes conexos). */
    private void detectComplexes() {
        if (graph.getNumVertices() == 0) {
            JOptionPane.showMessageDialog(this, "El grafo esta vacio.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        MyArrayList<MyArrayList<String>> components = graph.findConnectedComponents();

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("  DETECCION DE COMPLEJOS PROTEICOS (BFS)\n");
        sb.append("============================================================\n\n");
        sb.append("Se encontraron ").append(components.size()).append(" complejo(s) proteico(s):\n\n");

        for (int i = 0; i < components.size(); i++) {
            MyArrayList<String> comp = components.get(i);
            sb.append("  Complejo #").append(i + 1).append(" (").append(comp.size()).append(" proteinas):\n");
            sb.append("  { ");
            for (int j = 0; j < comp.size(); j++) {
                sb.append(comp.get(j));
                if (j < comp.size() - 1) sb.append(", ");
            }
            sb.append(" }\n\n");
        }

        sb.append("------------------------------------------------------------\n");
        sb.append("Los complejos representan grupos de proteinas que\n");
        sb.append("interactuan entre si, aislados del resto de la red.\n");
        sb.append("============================================================\n");

        setResult(sb.toString());
    }

    /** Ejecuta Dijkstra para encontrar la ruta metabólica más corta. */
    private void calculateShortestPath() {
        if (graph.getNumVertices() < 2) {
            JOptionPane.showMessageDialog(this, "Se necesitan al menos 2 proteinas.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String origin = (String) cmbOrigin.getSelectedItem();
        String dest = (String) cmbDestination.getSelectedItem();

        if (origin == null || dest == null) {
            JOptionPane.showMessageDialog(this, "Seleccione proteina origen y destino.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (origin.equals(dest)) {
            JOptionPane.showMessageDialog(this, "Origen y destino deben ser diferentes.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DijkstraResult result = graph.dijkstra(origin, dest);

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("  RUTA METABOLICA MAS CORTA (DIJKSTRA)\n");
        sb.append("============================================================\n\n");
        sb.append("  Origen:  ").append(origin).append("\n");
        sb.append("  Destino: ").append(dest).append("\n\n");

        if (result.isPathExists()) {
            sb.append("  RUTA ENCONTRADA:\n\n");
            MyArrayList<String> path = result.getPath();
            sb.append("  ");
            for (int i = 0; i < path.size(); i++) {
                sb.append(path.get(i));
                if (i < path.size() - 1) {
                    int idxA = graph.getProteinIndex(path.get(i));
                    int idxB = graph.getProteinIndex(path.get(i + 1));
                    // Buscar peso de la arista
                    MyArrayList<Edge> neighbors = graph.getNeighbors(idxA);
                    int w = 0;
                    for (int j = 0; j < neighbors.size(); j++) {
                        if (neighbors.get(j).getDestination() == idxB) {
                            w = neighbors.get(j).getWeight();
                            break;
                        }
                    }
                    sb.append(" --[").append(w).append("]--> ");
                }
            }
            sb.append("\n\n  Costo total: ").append(result.getTotalCost());
            sb.append("\n  (Menor costo = Mejor conexion / Menor resistencia)\n");
        } else {
            sb.append("  NO EXISTE RUTA entre estas proteinas.\n");
            sb.append("  Pertenecen a complejos proteicos diferentes.\n");
        }

        sb.append("\n============================================================\n");
        setResult(sb.toString());
    }

    /** Calcula y muestra la centralidad de grado (hubs). */
    private void identifyHubs() {
        if (graph.getNumVertices() == 0) {
            JOptionPane.showMessageDialog(this, "El grafo esta vacio.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[][] degrees = graph.calculateDegreeCentrality();

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("  IDENTIFICACION DE HUBS (CENTRALIDAD DE GRADO)\n");
        sb.append("============================================================\n\n");
        sb.append(String.format("  %-6s %-15s %-10s %s\n", "Rank", "Proteina", "Grado", "Rol"));
        sb.append("  ").append(repeat("-", 50)).append("\n");

        for (int i = 0; i < degrees.length; i++) {
            String role = "";
            int deg = Integer.parseInt(degrees[i][1]);
            if (i == 0) role = "<-- HUB PRINCIPAL (Diana Terapeutica)";
            else if (deg >= 3) role = "<-- Hub secundario";

            sb.append(String.format("  %-6d %-15s %-10s %s\n",
                    (i + 1), degrees[i][0], degrees[i][1], role));
        }

        sb.append("\n  ").append(repeat("-", 50)).append("\n");
        sb.append("\n  La proteina con mayor grado es la diana terapeutica\n");
        sb.append("  primaria: al ser anulada, causa el mayor impacto\n");
        sb.append("  en la red del patogeno.\n");
        sb.append("\n============================================================\n");

        setResult(sb.toString());
    }

    /** Repite un carácter n veces (utilidad para formato). */
    private String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    // ======================== UTILIDADES ========================

    /** Confirma la salida de la aplicación. */
    private void confirmExit() {
        if (graph.isModified()) {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Hay cambios sin guardar. Desea guardar antes de salir?",
                    "Confirmar Salida", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                saveFile();
                System.exit(0);
            } else if (opt == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            // CANCEL: no hacer nada
        } else {
            System.exit(0);
        }
    }

    /** Muestra el diálogo "Acerca de". */
    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "BioGraph v1.0\n\n"
              + "Analisis de Interacciones Proteicas\n"
              + "para el Descubrimiento de Farmacos.\n\n"
              + "Estructuras de Datos - BPTSP06\n"
              + "Universidad Metropolitana\n"
              + "Trimestre 2526-2",
                "Acerca de BioGraph", JOptionPane.INFORMATION_MESSAGE);
    }
}