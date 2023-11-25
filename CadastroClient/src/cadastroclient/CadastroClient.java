package cadastroclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author JPZanirati
 */

public class CadastroClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4321);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            String login = "op1";
            String senha = "op1";
            out.writeObject(login);
            out.writeObject(senha);

            out.writeObject("L");

            List<String> listaDeEntidades = (List<String>) in.readObject();

            System.out.println("Entidades recebidas:");
            for (String entidade : listaDeEntidades) {
                System.out.println(entidade);
            }

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
           System.out.println(e);
        }
    }
}