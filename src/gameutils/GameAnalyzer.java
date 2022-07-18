package gameutils;
import gameutils.GameManager.Cache;
import static gameutils.GameManager.FIRST_MOVE;
import static gameutils.GameManager.PLAYER_1;
import static gameutils.GameManager.PLAYER_2;
import static gameutils.GameManager.SECOND_MOVE;
import java.util.*;
import static sylvercoinage.SylverCoinage.quit;
/**
 * @author Kyle Savery
 */
public class GameAnalyzer {
    final static int NO_WINNING_RESPONSE = 1;
    final String P1_STR = "Player 1";
    final String P2_STR = "Player 2";
    final String[] PLAYER_STR = {P1_STR, P2_STR};
    final int[] PLAYERS = {PLAYER_1, PLAYER_2};
    
    public int calcWinningPosition(ArrayList<GameManager.Cache> cache, int numInitChoices){  
        GameManager.Cache topLevelCache = null;
        if(cache.size() == 1){
            Cache onlyCache = cache.get(0);
            //The board is already empty, winner depends on turn
            if(onlyCache.seed.isEmpty()){
                return convertMoveToPlayer(numInitChoices, topLevelCache);
            }

            if(onlyCache.outcomes.get(0).size() == 1){
                onlyCache.winningPosition =  FIRST_MOVE;
            }else{
                onlyCache.winningPosition = SECOND_MOVE;
            }
            
            topLevelCache = onlyCache;
        }else{
            int maxCacheLength = 0;
            for(GameManager.Cache cached : cache){
                if(cached.seed.size() > maxCacheLength){
                    maxCacheLength = cached.seed.size();
                }
                if(cached.seed.size() == 2){
                    if(cached.outcomes.get(0).size() == 1){
                        cached.winningPosition = FIRST_MOVE;
                    }else{
                        cached.winningPosition = SECOND_MOVE;
                    }
                }
            }


            for(int length = 3; length <= maxCacheLength; length++){
                for(int i = 0; i < cache.size(); i++){
                    GameManager.Cache cached = cache.get(i);
                    if(cached.seed.size() == length){
                        if(length == maxCacheLength){
                            topLevelCache = cached;
                        }


                        boolean wayOut = false;
                        for(ArrayList<GameManager.Cache> built : cached.outcomes){
                            if((built.size() == 1 ) 
                                    || (built.size() == 2 && built.get(1).winningPosition == SECOND_MOVE)){
                                wayOut = true;
                            }
                        }
                        if(wayOut){
                            cached.winningPosition = FIRST_MOVE;
                        }else{
                            cached.winningPosition = SECOND_MOVE;
                        }
                    }
                }
            }
        }
        return convertMoveToPlayer(numInitChoices, topLevelCache);
    }
    
    public int convertMoveToPlayer(int numInitChoices, Cache topLevel){
        if(numInitChoices % 2 == 0){
            if(topLevel == null){
                return PLAYER_2;
            }else if(topLevel.winningPosition == FIRST_MOVE){
                return PLAYER_1;
            }
            return PLAYER_2;
        }
        if(topLevel == null){
            return PLAYER_1;
        }else if(topLevel.winningPosition == FIRST_MOVE){
            return PLAYER_2;
        }
        return PLAYER_1;
    }
    
    public void testOddOptionsHelper(ArrayList<String> init_choices, 
                                            boolean skipFound, boolean testingDisplay,
                                            boolean workingCacheDisplay){
        Scanner scanner = new Scanner(System.in);

        boolean validInput = false;
        int lowerBd = 0;
        int upperBd = 0;
        while(!validInput){
            validInput = true;
            System.out.print("\nEnter lower bound ( >= 5) : ");
            String lowerBdStr = scanner.nextLine();

            System.out.print("Enter upper bound: ");
            String upperBdStr = scanner.nextLine();
            try{
                lowerBd = Integer.parseInt(lowerBdStr);
                upperBd = Integer.parseInt(upperBdStr);
                if(lowerBd < 5){
                    System.out.println("Lower bound must be greater than or equal to 5");
                    validInput = false;
                }else if( lowerBd > upperBd){
                    System.out.println("The upper bound must be greater than lower bound");
                    validInput = false;
                }else if(lowerBd % 2 == 0){
                    lowerBd += 1;
                    System.out.println("\n***Lower bound cannot be even, using " + lowerBd);
                }
            }catch(NumberFormatException nfe){
                System.err.println("[GameAnalyzer.testOddOptions] " + nfe);
            }
        }
        int[] bounds = {lowerBd, upperBd};
        int winningResponse = testOddOptions(init_choices, bounds, skipFound,
                                            testingDisplay, workingCacheDisplay);
        int winnerInd = init_choices.size() % 2;
        if(winningResponse != NO_WINNING_RESPONSE){
            System.out.println(PLAYER_STR[winnerInd] + " can lose"
             + " if " + PLAYER_STR[(winnerInd+1)%2] + " selects: " + winningResponse);
        }else{
            System.out.print("\n=============================================");
            System.out.println("\n"+PLAYER_STR[winnerInd] + " cannot win with selected odd numbers");
            System.out.println("=============================================\n\n");
        }
    }
    
    public Integer testOddOptions(ArrayList<String> init_choices, int[] bounds, 
                                      boolean skipFound, boolean testingDisplay,
                                      boolean workingCacheDisplay){
        ArrayList<String> boards = init_choices;
        ArrayList<Integer> seen = new ArrayList<>();
        int winnerInd = init_choices.size() % 2;
        int lowerBd = bounds[0];
        int upperBd = bounds[1];
        try{
            for(Integer i = lowerBd; i <= upperBd; i+=2){
                if(testingDisplay){
                    System.out.println("\n=========");
                    System.out.println("Checking: " + i);
                }
                
                if(skipFound && seen.contains(i)){
                    if(testingDisplay){
                        System.out.println("Response already found for: " + i);
                        continue;
                    }
                }
                
                boards.add(i.toString());
                GameManager gameManager = new GameManager(boards, false);
                gameManager.board = gameManager.setupBoard(GameManager.EXCLUDE_TRIVIAL_LOSS);
                ArrayList<Cache> cache = gameManager.makeAllCachesHelper(workingCacheDisplay);
                int winner = calcWinningPosition(cache, boards.size());
                
                if(winner == PLAYERS[winnerInd]){
                    return i;
                }
                
                ArrayList<Integer> newSeen = gameManager.getWinningResponses(cache,
                                        gameManager.board, PLAYERS[(winnerInd+1)%2]);
                if(testingDisplay){
                    gameManager.displayWinningResponses(cache,
                                        gameManager.board, PLAYERS[(winnerInd+1)%2]);
                }
                
                for(Integer justSeen : newSeen){
                    if(!seen.contains(justSeen)){
                        seen.add(justSeen);
                    }
                }
                boards.remove(i.toString());
                cache.clear();
            }
            
        }catch(InvalidChoicesException | NumberFormatException ex){
            quit("[GameAnalyzer.testOptions] " + ex);
        }
        
    return NO_WINNING_RESPONSE;
    }
}
