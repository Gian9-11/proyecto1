package biograph;

import biograph.gui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class BioGraph {

    /**
     * Método principal. Configura el Look and Feel e inicia la interfaz gráfica.
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Intentar usar Nimbus para un aspecto moderno
                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Usar Look and Feel por defecto
                }

                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}