import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class TestGUI {

    private JTextArea textMain = new JTextArea();
    private JFrame frame = new JFrame();
    private JFrame result = new JFrame();
    private String response;

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public String getInput() {
        return this.textMain.getText().trim();
    }

    private void clear() {
        this.textMain.setText("");
    }

    public void init(){

        this.result.setVisible(false);

        frame = new JFrame("GPN Test Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel context = new JPanel();
        frame.getContentPane().add(context);

        Font fieldFont = new Font("Arial", Font.PLAIN, 25);
        textMain.setFont(fieldFont);
        textMain.setBackground(Color.white);
        textMain.setForeground(Color.gray.darker());
        textMain.setPreferredSize(new Dimension( 700, 30 ) );

        context.add(textMain);

        JButton ok = new JButton("OK");

        ok.addActionListener(e -> setResponse(sendText()));

        context.add(ok);

        frame.setVisible(true);
        frame.setSize(900, 100);
    }

    private String sendText() {
        String input = this.getInput();
        clear();
        //showResults(getResponse());

        return input;
    }

    public void showResults(String response) {
        this.frame.setVisible(false);

        result = new JFrame("GPN Test Interface");
        result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel context = new JPanel();
        result.getContentPane().add(context);

        JLabel jLabel = new JLabel();
        Font fieldFont = new Font("Arial", Font.PLAIN, 25);
        jLabel.setFont(fieldFont);
        jLabel.setText(response);
        context.add(jLabel);

        result.setVisible(true);
        result.setSize(900, 900);
    }
}
