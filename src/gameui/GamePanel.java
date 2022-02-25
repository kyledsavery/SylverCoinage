package gameui;

import gameutils.GameAnalyzer;
import gameutils.GameManager;
import gameutils.GameManager.Cache;
import gameutils.InvalidChoicesException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static sylvercoinage.SylverCoinage.quit;

/**
 *
 * @author Kyle Savery
 */
public class GamePanel extends JPanel implements ActionListener{

    final int SCREEN_WIDTH = 600;
    final int SCREEN_HEIGHT = 450;
    JTextField choices;
    JLabel output = null;
    
    public GamePanel(){
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.gray);
        choices = new JTextField(10);
        choices.addActionListener(this);
        
        JButton start = new JButton("Run");
        start.setFocusPainted(false);
        start.addActionListener(this);

        this.add(choices);
        this.add(start);
    }
    
    // Parse through the users input to determine initial choices
    public ArrayList<String> parseInput(String input){
        ArrayList<String> init_choices = new ArrayList<>();
        String current = "";
        input = input.strip();
        for(int i = 0; i < input.length(); i++){
            if(input.charAt(i) == ' '){
                if(!current.equals("")){
                    init_choices.add(current);
                }
                current = "";
                continue;
            }
            current += input.charAt(i);
        }
        if(!current.equals("")){
            init_choices.add(current);
        }
        return init_choices;
    }
    
    //
    public JPanel loadingSymbol(){
        return null;
    }

    @Override
    public void paintComponent(Graphics graphic){
        super.paintComponent(graphic);        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        GameManager gameManager = null;
        if(output != null){
            this.remove(output);
        }
        try {
            //Get initial choices from JTextField
            ArrayList<String> init_choices = parseInput(choices.getText());     
            gameManager = new GameManager(init_choices, false);
            gameManager.board = gameManager.setupBoard(GameManager.EXCLUDE_TRIVIAL_LOSS);
        }catch(InvalidChoicesException | NumberFormatException ex){
            quit("[SylverCoinage.main] " + ex);
        }

        if(gameManager == null){
            System.exit(1);
        }      
        
        
        //Loading Spinner
        ImageIcon imageIcon = new ImageIcon("../../src/resources/spinner.gif");
        JLabel iconLabel = new JLabel(imageIcon);
        imageIcon.setImageObserver(iconLabel);
        this.add(iconLabel);
        this.revalidate();
        this.repaint();
        //Determine winner
       // ArrayList<Cache> cache = gameManager.makeAllCachesHelper(false);
      //  GameAnalyzer gameAnalyzer = new GameAnalyzer();
       // int winner = gameAnalyzer.calcWinningPosition(cache, gameManager.init_choices.size());
       // output = new JLabel("WINNER: Player " + ((Integer)winner).toString());
        
        //Display result to user
      //  this.add(output);
       // this.revalidate();
      //  this.repaint();
        }
}
