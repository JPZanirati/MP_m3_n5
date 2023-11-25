package cadastroclient;

import java.io.IOException;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.io.ObjectInputStream;

/**
 *
 * @author JPZanirati
 */

public class ThreadClient extends Thread {

    private final ObjectInputStream entrada;
    private final JTextArea textArea;

    public ThreadClient(ObjectInputStream entrada, JTextArea textArea) {
        this.entrada = entrada;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object objetoRecebido = entrada.readObject();

                SwingUtilities.invokeLater(() -> {
                    if (objetoRecebido instanceof String string) {
                        textArea.append(string + "\n");
                    } else if (objetoRecebido instanceof java.util.List) {
                        java.util.List<String> listaProdutos = (java.util.List<String>) objetoRecebido;
                        for (String produto : listaProdutos) {
                            textArea.append(produto + "\n");
                        }
                    }
                });
            }
        } catch (IOException | ClassNotFoundException e) {
           System.out.println(e);
        }
    }
}