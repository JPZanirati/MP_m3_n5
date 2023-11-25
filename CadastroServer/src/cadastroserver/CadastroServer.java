package cadastroserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author JPZanirati
 */

public class CadastroServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4321);
        System.out.println("Servidor iniciado. Aguardando conex�es...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String login = in.readLine();
            String senha = in.readLine();

            if (validarCredenciais(login, senha)) {
                out.println("Credenciais v�lidas. Aguardando requisi��es...");

                while (true) {
                    String requisicao = in.readLine();

                    if (requisicao.equals("L")) {
                        String produtos = obterConjuntoProdutos();
                        out.println(produtos);
                    } else {
                        out.println("Requisi��o inv�lida");
                    }
                }
            } else {
                out.println("Credenciais inv�lidas. Desconectando...");
                clientSocket.close();
            }
        }
    }

    private static boolean validarCredenciais(String login, String senha) {
        // Implemente a l�gica para validar as credenciais do cliente
        // Retorne true se as credenciais forem v�lidas e false caso contr�rio
        return false;
        // Implemente a l�gica para validar as credenciais do cliente
        // Retorne true se as credenciais forem v�lidas e false caso contr�rio
    }

    private static String obterConjuntoProdutos() {
        // Implemente a l�gica para obter o conjunto de produtos do servidor
        // Retorne uma string contendo os produtos
        return null;
        // Implemente a l�gica para obter o conjunto de produtos do servidor
        // Retorne uma string contendo os produtos
    }
}