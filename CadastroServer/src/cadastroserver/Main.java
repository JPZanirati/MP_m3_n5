package cadastroserver;

import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author JPZanirati
 */

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
        ProdutoJpaController ctrl = new ProdutoJpaController(emf);
        UsuarioJpaController ctrlUsu = new UsuarioJpaController(emf);
        ServerSocket serverSocket = null;

        try {
            int port = 4321;
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor aguardando conexões na porta " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                CadastroThread cadastroThread = new CadastroThread(ctrl, ctrlUsu, clientSocket);
                cadastroThread.start();
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
}
