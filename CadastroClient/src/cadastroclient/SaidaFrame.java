package cadastroclient;

import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

/**
 *
 * @author JPZanirati
 */

public class SaidaFrame extends JDialog {

    public JTextArea texto;

    private SaidaFrame saidaFrame;

    public SaidaFrame() {
        setBounds(100, 100, 400, 300);

        setModal(false);

        texto = new JTextArea();
        texto.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(texto);
        getContentPane().add(scrollPane);

        setTitle("Saída do Cliente");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    public class MensagemJanela { 

        public MensagemJanela() {
            saidaFrame = new SaidaFrame();
        }   
    }
    
    public void exibirMensagem(String mensagem) {
        
        saidaFrame.texto.append(mensagem + "\n");

        saidaFrame.setVisible(true);
    } 
}