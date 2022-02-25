package gameui;

import javax.swing.JFrame;

/**
 *
 * @author Kyle Savery
 */
public class GameFrame extends JFrame{
    public GameFrame(){
        this.add(new GamePanel());
        this.setTitle("Syvler Coinage");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
