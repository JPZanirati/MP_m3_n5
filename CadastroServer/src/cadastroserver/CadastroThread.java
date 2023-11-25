package cadastroserver;

import controller.ProdutoJpaController;
import controller.UsuarioJpaController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CadastroThread extends Thread {

    private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;
    private Socket s1;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public CadastroThread(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.s1 = s1;

        try {
            this.out = new ObjectOutputStream(s1.getOutputStream());
            this.in = new ObjectInputStream(s1.getInputStream());
        } catch (IOException e) {
           System.out.println(e);
        }
    }

    @Override
    public void run() {
        try {
            String login = (String) in.readObject();
            String senha = (String) in.readObject();
            Integer id =(Integer) in.readObject();

            if (validarCredenciais(login, senha, id)) {
                out.writeObject("Bem-vindo ao servidor!");

                while (true) {
                    String comando = (String) in.readObject();

                    if ("L".equals(comando)) {
                        enviarConjuntoProdutos();
                    } else if ("E".equals(comando) || "S".equals(comando)) {
                        processarMovimento(comando);
                    } else if ("Q".equals(comando)) {                       
                        break;
                    }
                }
            } else {
                out.writeObject("Credenciais inválidas. Desconectando...");
                s1.close();
            }
        } catch (IOException | ClassNotFoundException e) {
           System.out.println(e);
        }
    }

    private boolean validarCredenciais(String login, String senha, Integer id) {
        return ctrlUsu.findUsuario(login, senha, id) != null;
    }

    private void enviarConjuntoProdutos() {
        try {
            out.writeObject(ctrl.listaDeProdutos());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void processarMovimento(String comando) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}