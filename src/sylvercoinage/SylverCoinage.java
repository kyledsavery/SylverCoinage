package sylvercoinage;

import gameutils.GameManager;
import java.util.*;
import gameutils.GameAnalyzer;
import gameutils.GameManager.Cache;
import gameutils.InvalidChoicesException;
import utils.ArrayUtils;
import utils.MathUtils;

/**
 *
 * @author Kyle Savery
 */
public class SylverCoinage {
    
    public static final String INF_STR = "\u221e"; 
    
    public static void main(String[] args) {  
          
        long start = System.currentTimeMillis();
        System.out.println("==============PROGRAM START==============\n");
        if(args.length == 0){
            System.out.println("[SylverCoinage.main] Missing Program Input");
            System.exit(0);
        } 
        
        //Program conditions
        boolean makeAllGames = false;
        boolean cacheDisplay = false;
        boolean testOdd = false;
        boolean skipFound = false;
        boolean testDisplay = true;
        boolean workingCacheDisplay = true;
        boolean getMaxEvens = false;
        boolean boardOnly = false;
        boolean evenBoardOnly = false;
        boolean linearCombTesting = false;
        
        ArrayList<String> init_choices = new ArrayList<>();
        for(int i = 0; i < args.length; i++){
            switch (args[i].toLowerCase()) {
                case "-naive":
                    makeAllGames = true;
                    break;
                case "-cdisp":
                    cacheDisplay = true;
                    break;
                case "-tstodd":
                    testOdd = true;
                    break;
                case "-skp":
                    skipFound = true;
                    break;
                case "+tdisp":
                    testDisplay = false;
                    break;
                case "+wrkdisp":
                    workingCacheDisplay = false;
                    break;
                case "-maxev":
                    getMaxEvens = true;
                    break;
                case "-bd":
                    boardOnly = true;
                    break;
                case "-evenbd":
                    evenBoardOnly = true;
                    break;
                case "-linc":
                    linearCombTesting = true;
                    break;                    
                default:
                    init_choices.add(args[i]);
                    break;
            }
        }

        if(boardOnly || evenBoardOnly){
            ArrayList<Integer> board = ArrayUtils.setupGameBoard(ArrayUtils.stringsToIntegers(init_choices), evenBoardOnly);
            System.out.println("STARTING BOARD ("+ board.size() +"): " + board + "\n");
            
        }else if(linearCombTesting){
            ArrayList<Integer> values = ArrayUtils.stringsToIntegers(init_choices);
            Integer toCheck = values.get(0);
            values.remove(toCheck);
            Collections.sort(values);
            boolean canBeMade = MathUtils.isLinearComb(toCheck, values, values.size());
            if(canBeMade){
                System.out.println(toCheck + " CAN be made from " + values);
            }else{
                System.out.println(toCheck + " CANNOT be made from " + values);
            }
            System.out.println();
        }else if(getMaxEvens){
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter max even to check: ");
            int maxCheck = Integer.parseInt(scanner.nextLine());
            for(Integer test = 4; test <= maxCheck; test+=2){
                int initChoice = Integer.parseInt(init_choices.get(0));
                int maxEvenCalc = ((initChoice-2)*(test/2)) - initChoice;
                System.out.println(test + ": " + maxEvenCalc);
            }
            
        }else if(testOdd){
            GameAnalyzer gameAnalyzer = new GameAnalyzer();
            gameAnalyzer.testOddOptionsHelper(init_choices, skipFound, testDisplay, workingCacheDisplay);
            
        }else{
            GameManager gameManager = null;
            try {
                gameManager = new GameManager(init_choices, false);
                gameManager.board = gameManager.setupBoard(GameManager.EXCLUDE_TRIVIAL_LOSS);
                System.out.println("STARTING BOARD ("+ gameManager.board.size() +"): " + gameManager.board);
            }catch(InvalidChoicesException | NumberFormatException ex){
                quit("[SylverCoinage.main] " + ex, start);
            }

            if(gameManager == null){
                System.exit(1);
            }      

            /*Generates all possible games from initial choices, board size must
            be small ( < 15)*/
            if(makeAllGames){
                gameManager.makeAllGamesHelper();
                gameManager.saveGameOutcomes();
            
            //Determines which player is in the winning position
            }else{
                ArrayList<Cache> cache = gameManager.makeAllCachesHelper(workingCacheDisplay);
                
                GameAnalyzer gameAnalyzer = new GameAnalyzer();
                int winner = gameAnalyzer.calcWinningPosition(cache, gameManager.init_choices.size());
                if(cacheDisplay){
                    gameManager.displayCaches(cache);
                }
                gameManager.displayWinningResponses(cache, gameManager.board, winner);
            }
       }

 
        System.out.println("===============PROGRAM END===============");
        System.out.println("TIME ELAPSED: " + (System.currentTimeMillis() - start) + " ms");
    }
    
    
    
    public static void quit(long start){
        quit("", start);
    }
    
    public static void quit(String quitMsg){
        quit(quitMsg, 0);
    }
    
    public static GameManager createGameManager(ArrayList<String> init_choices){
        return null;
    } 
    
    public static void quit(String quitMsg, long start){
        System.err.println(quitMsg);
        System.out.println("\n===============PROGRAM END===============");
        if(start != 0){
            System.out.println("TIME ELAPSED: " + (System.currentTimeMillis() - start) + " ms");
        }
        System.exit(0);
    }
}