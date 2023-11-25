package cadastroclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author JPZanirati
 */

public class CadastroClientV2 {
public static void main(String[] args) {
    try (Socket socket = new Socket("localhost", 4321);
         ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

        out.writeObject("op1");
        out.writeObject("op1");
        out.writeObject(1);

        SaidaFrame saidaFrame = new SaidaFrame();
        JTextArea texto = saidaFrame.texto; 

        ThreadClient preenchimentoAsync = new ThreadClient(in, texto);
        preenchimentoAsync.start();

        OUTER:
        while (true) {
            mostrarMenu();
            String comando = lerComandoTeclado();
            if (null == comando) {
                saidaFrame.exibirMensagem("Comando inválido. Tente novamente.");
            } else {
                switch (comando) {
                    case "X" -> {
                        break OUTER;
                    }
                    case "L" -> out.writeObject("L");
                    case "E", "S" -> enviarComandoES(comando, out);
                    default -> saidaFrame.exibirMensagem("Comando inválido. Tente novamente.");
                }
            }
        }
        preenchimentoAsync.join();
    } catch (IOException e) {
        System.out.println(e);
    } catch (InterruptedException ex) {
        Logger.getLogger(CadastroClientV2.class.getName()).log(Level.SEVERE, null, ex);
    }
}

    private static void mostrarMenu() {
        System.out.println("Menu:");
        System.out.println("L - Listar");
        System.out.println("E - Entrada");
        System.out.println("S - Saída");
        System.out.println("X - Finalizar");
    }

    private static String lerComandoTeclado() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Escolha um comando: ");
        return reader.readLine().toUpperCase();
    }
    
    private static void enviarComandoES(String comando, ObjectOutputStream out) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("Id da pessoa: ");
                out.writeObject(Integer.valueOf(reader.readLine()));

                System.out.print("Id do produto: ");
                out.writeObject(Integer.valueOf(reader.readLine()));

                System.out.print("Quantidade: ");
                out.writeObject(Integer.valueOf(reader.readLine()));

                System.out.print("Valor unitário: ");
                out.writeObject(Double.valueOf(reader.readLine()));

                System.out.print("Deseja adicionar mais um item? (S/N): ");
                String continuar = reader.readLine().toUpperCase();
                if (!"S".equals(continuar)) {
                    break;
                }
            }
        }
    }
}