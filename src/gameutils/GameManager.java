package gameutils;

import java.util.*;
import java.io.*;
import static java.lang.Math.min;
import utils.MathUtils;
import utils.ArrayUtils;


/**
 * 
 * @author Kyle Savery
 */
public class GameManager {
    
    public final static int PLAYER_1 = 1;
    public final static int PLAYER_2 = 2;
    public final static int EXCLUDE_TRIVIAL_LOSS = 3;
    public final static int INCLUDE_TRIVIAL_LOSS = 4;
    final int UNUSED = -2;
    final int USED = -1;
    final int VALID_CHOICES = 99;
    final int INFINITE_BOARD = 999;
    final int CONFLICTING_CHOICES = 101;
    final int VALID_CHOICE = 102;
    final int INVALID_CHOICE = 103;
    final static int FIRST_MOVE = 104;
    final static int SECOND_MOVE = 105;
    final static int UNKNOWN_POSITION = 106;
    final int GREATER_THAN = 107;
    final int LESS_THAN = 108;
    final int EQUAL = 109;
    final int[] NON_MISSABLES = {4, 5, 6, 7};
    final int MAX_EVEN_BOARD_SIZE = 500;
    final int CACHE_DISPLAY_UPDATE = 100;
    
    public class Option{
        int state;
        final int num;
        
        public Option(int option){
            this.num = option;
            this.state = UNUSED;
        }
        
        @Override
        public String toString(){
            return ((Integer)this.num).toString();
        }
    }
    
    public class Cache{
        public ArrayList<Integer> seed;
        public ArrayList<ArrayList<Cache>> outcomes;
        public int winningPosition;
        
        public Cache(ArrayList<Integer> seed){
            this.seed = seed;
            outcomes = new ArrayList<>();
            winningPosition = UNKNOWN_POSITION;
        }
        
        public Cache(int num){
            ArrayList<Integer> single = new ArrayList<>();
            single.add(num);
            this.seed = single;
            outcomes = new ArrayList<>();
            ArrayList<Cache> singleCache = new ArrayList<>();
            singleCache.add(new Cache(single));
            outcomes.add(singleCache);
            winningPosition = UNKNOWN_POSITION;
        }
        
        public void add(ArrayList<Cache> outcome){
            outcomes.add(outcome);
        }
        
        //Two cache are equal iff their seeds are equal
        public boolean equals(Cache cacheOne, Cache cacheTwo){
            return cacheOne.seed.equals(cacheTwo.seed);
        }
        
        public int size(){
            return seed.size();
        }
        
        @Override
        public String toString(){
            String winDesc = "";
            if(winningPosition == FIRST_MOVE){
                winDesc = " <FIRST>";
            }else if(winningPosition == SECOND_MOVE){
                winDesc = " <SECOND>";
            }
            return seed.toString() + winDesc;
        }
    }
    
    public ArrayList<ArrayList<Integer>> outcomes = new ArrayList<>();
    public ArrayList<Integer> init_choices;
    public int numInitChoices;
    public ArrayList<Integer> board;

    
    public GameManager(ArrayList<String> init_choices, boolean infiniteOverride) throws InvalidChoicesException, NumberFormatException {
        this.init_choices = new ArrayList<>();
        
        for(int i = 0; i < init_choices.size(); i++){
            this.init_choices = ArrayUtils.stringsToIntegers(init_choices);
            if(this.init_choices.get(i) <= 0){
                throw new InvalidChoicesException("Input must be an integer greater than 0");
            }
        }
        
        int choice_check = checkChoices(this.init_choices, infiniteOverride);
        switch(choice_check){
            case INFINITE_BOARD:
                throw new InvalidChoicesException("Initial choices cannot result in an infinite board");
            case CONFLICTING_CHOICES:
                throw new InvalidChoicesException("Initial choices are conflicting");
        }
        this.numInitChoices = this.init_choices.size();
    }
    
    public String boardToString(int[] board, int length){
        String boardStr = "[ ";
        for(int i = 0; i < length; i++){
            if(i != length - 1){
                boardStr += board[i] + ", ";
            }else{
                boardStr += board[i] + " ";
            }
        }
        boardStr += "]";
        return boardStr;
    }
    
    
    private int checkChoices(ArrayList<Integer> init_choices, boolean infiniteOverride){ 
        int num_choices = init_choices.size();
        if(!infiniteOverride){
            boolean infinite_board = true;
            for(int i = 0; i < num_choices; i++){
                for(int j = i; j < num_choices; j++){
                    if(MathUtils.gcd(init_choices.get(i), init_choices.get(j)) == 1){
                        infinite_board = false;
                    }
                }
            }
            if(infinite_board){
                return INFINITE_BOARD;
            }
        }
        
        for(int i = 1; i < num_choices; i++){
            int toCheck = init_choices.get(i);
            int[] prev_choices = new int[i];
            for(int k = 0; k < i; k++){
                prev_choices[k] = init_choices.get(k);
            }
            ArrayUtils.sort(prev_choices);
            if(MathUtils.isLinearComb(toCheck, prev_choices, prev_choices.length)){
                return CONFLICTING_CHOICES;
            }
        }
        
        return VALID_CHOICES;
    }
    
    public void displayBoard(){
        System.out.print("BOARD STATE: \n" + board + "]");
    }
    
    public ArrayList<Integer> getInitChoices(){
        ArrayList<Integer> copy = new ArrayList<>();
        for(Integer choice : this.init_choices){
            copy.add(choice);
        }
        return copy;
    }
    
    public void dispChoices(){
        for(int i = 0; i < this.init_choices.size(); i++){
            System.out.print(this.init_choices.get(i) + " ");
        }
    }
    
    public ArrayList<Integer> setupBoard(int ignoreTrivialLoss) {
        ArrayList<Integer> sortedChoices = ArrayUtils.copy(init_choices);
        Collections.sort(sortedChoices);
        int maxNumLeft = MathUtils.getFrobeniusNum(sortedChoices);
        ArrayList<Integer> gameboard = new ArrayList<>();
        int start = 1;
        if(ignoreTrivialLoss == EXCLUDE_TRIVIAL_LOSS){
            start = 4;
        }
        for(int i = start; i <= maxNumLeft; i++){
            if(!MathUtils.isLinearComb(i, sortedChoices, sortedChoices.size())){
                gameboard.add(i);
            }
        }
        
        return gameboard;
    }
    
    public void makeAllGames(ArrayList<Integer> currChoices, ArrayList<Option> options){
        boolean isEmpty = true;
        for(Option option : options){
            if(option.state == UNUSED){
                isEmpty = false;
                break;
            }
        }
            
        if(isEmpty){
            this.outcomes.add(currChoices);
        }else{

            int optionSize = options.size();
            for(int i = 0; i < optionSize; i++){
                ArrayList<Integer> newChoices = new ArrayList<>();
                for(Integer num : currChoices){
                    newChoices.add(num);
                }
                Option currOption = options.get(i);
                if(currOption.state == USED){
                    continue;
                }
                int nextChoice = currOption.num;
                newChoices.add(nextChoice);
                int[] choices = ArrayUtils.toIntArray(newChoices);
                ArrayUtils.sort(choices);
                int[] statesBeforeUpdate = new int[optionSize];
                for(int k = 0; k < optionSize; k++){
                    statesBeforeUpdate[k] = options.get(k).state;
                    if(MathUtils.isLinearComb(options.get(k).num, choices, choices.length)){
                        options.get(k).state = USED;
                    }
                }
                makeAllGames(newChoices, options);
                for(int k = 0; k < optionSize; k++){
                    options.get(k).state = statesBeforeUpdate[k];
                }
            }
        }
    }
    
    // Without Memoization
    public void makeAllGamesHelper(){
        ArrayList<Integer> currChoices = new ArrayList<>();
        for(Integer num : this.init_choices){
            currChoices.add(num);
        }
        ArrayList<Option> options = new ArrayList<>();
        for(int num : this.board){
            if(num != 1 && num != 2 && num != 3){
                options.add(new Option(num));
            }
        }
                
        makeAllGames(currChoices, options);
    }
    
    public void displayCaches(ArrayList<Cache> cache){
        System.out.println("\n=======Cache Display=======");
        System.out.println("Size of Cache: " + cache.size());
        for(Cache seed : cache){
            System.out.println("\n"+seed);
            for(ArrayList<Cache> cached : seed.outcomes){
                System.out.println("=> "+cached);
            }
        }
        System.out.println("\n===========================");
    }
    
    public ArrayList<Integer> getWinningResponses(ArrayList<Cache> cache, 
                                       ArrayList<Integer> topLevel, int winner){
        ArrayList<Integer> responses = new ArrayList<>();
        for(Cache seed : cache){
            if(topLevel.equals(seed.seed)){  
                for(ArrayList<Cache> cached : seed.outcomes){
                    if(cached.size() > 1 && cached.get(1).winningPosition == SECOND_MOVE){
                        responses.add(cached.get(0).seed.get(0));
                    }
                }
            }
        }
        return responses;
    }
    
    public void displayWinningResponses(ArrayList<Cache> cache, 
                                       ArrayList<Integer> topLevel, int winner){
        String winnerStr = "Player 1";
        if(winner == PLAYER_2){
            winnerStr = "Player 2";
        }
        if(board.size() == 0){
            System.out.println("\n" + winnerStr + " wins after choices: " + init_choices);
        }else{
            System.out.println("\n"+winnerStr + " is in a winning position after"
                + " the initial choices: " + init_choices);
        }
        
        ArrayList<Integer> responses = getWinningResponses(cache, topLevel, winner);
        if(!responses.isEmpty()){
            String responseDesc = "Winning Response";
            if(responses.size() > 1){
                responseDesc += "s";
            }
            System.out.println(responseDesc + ": " + responses);
        }
        
        if((init_choices.size() % 2 == 0 && winner == PLAYER_1)
            ||(init_choices.size() % 2 == 1 && winner == PLAYER_2)){
            System.out.println("These choices result in a FIRST TO MOVE board\n");
        }else{
            System.out.println("These choices result in a SECOND TO MOVE board\n");                    
        }
    }
    
    // With Memoization
    public ArrayList<Cache> makeAllCachesHelper(boolean workingCacheDisplay){
        ArrayList<Cache> cache = new ArrayList<>();
        //ArrayList<Integer> boardList = ArrayUtils.toArrList(board);
        System.out.println();
        makeAllCaches(workingCacheDisplay, cache, board, init_choices);
        if(workingCacheDisplay){
            System.out.println("Final Cache Size: " + cache.size() 
                                               + "                           ");
        }
        return cache;
    }
    
    public void addInOrder(ArrayList<Cache> cache, Cache toAdd){
        int insertIndex = binarySearch(cache, toAdd.seed);
        cache.add(insertIndex, toAdd); 
    }
    
    public int binarySearch(ArrayList<Cache> cache, ArrayList<Integer> target){
        int left = 0;
        int right = cache.size()-1;
        int middle = (left + right) / 2;
        while(true){
            if(left > right){
                return left;
            }
            //current > target
            int check = compareArrLists(target, cache.get(middle).seed);
            switch (check) {
                case EQUAL -> {
                    return middle;
                }
                case GREATER_THAN -> left = middle + 1;
                default -> // target > current
                    right = middle - 1;
            }
            middle = (left + right) / 2;
        }
    }
    
    public int compareArrLists(ArrayList<Integer> listOne, ArrayList<Integer> listTwo){
        int smallestSize = min(listOne.size(), listTwo.size());
        for(int i = 0; i < smallestSize; i++){
            if(listOne.get(i) > listTwo.get(i)){
                return GREATER_THAN;
            }else if(listOne.get(i) < listTwo.get(i)){
                return LESS_THAN;
            }
        }
        if(listOne.size() == listTwo.size()){
            return EQUAL;
        }else if(listOne.size() > listTwo.size()){
            return GREATER_THAN;
        }
        return LESS_THAN;
    }
    
    public Cache getCache(ArrayList<Cache> cache, ArrayList<Integer> seed){
        int left = 0;
        int right = cache.size()-1;
        int middle = (left + right) / 2;
        while(true){
            if(left > right){
                return null;
            } 
            ArrayList<Integer> cached = cache.get(middle).seed;
            if(cached.equals(seed)){
                return cache.get(middle);
            }
            if(compareArrLists(seed, cached) == GREATER_THAN){
                left = middle + 1;
            }else{
                right = middle - 1;
            }
            middle = (left + right) / 2;
        }
        }
    
    
    public Cache makeAllCaches(boolean workingDisplay, ArrayList<Cache> cache, 
                  ArrayList<Integer> remaining, ArrayList<Integer> currChoices){
        if(workingDisplay && cache.size() % CACHE_DISPLAY_UPDATE == 0){
            System.out.print("Working Cache Size: " + cache.size()+"\r");
        }
        Cache newSeed = new Cache(remaining);
        addInOrder(cache, newSeed);
        
        for(Integer choice : newSeed.seed){
            ArrayList<Integer> newRemaining = new ArrayList<>();
            ArrayList<Integer> newChoices = new ArrayList<>();
            for(Integer num : currChoices){
                newChoices.add(num);
            }
            newChoices.add(choice);
            ArrayList<Integer> copiedChoices = ArrayUtils.copy(newChoices);
            Collections.sort(copiedChoices);
            for(Integer toCheck : remaining){
                  if(!MathUtils.isLinearComb(toCheck, copiedChoices, copiedChoices.size())){
                      newRemaining.add(toCheck);
                  }
            }
            
            ArrayList<Cache> newOutcomes = new ArrayList<>();
            newOutcomes.add(new Cache(choice));
            if(newRemaining.size() == 1){
                newOutcomes.add(new Cache(newRemaining.get(0)));
            }else if(newRemaining.size() > 1){
                Cache existing = getCache(cache, newRemaining);
                if(existing == null){    
                    existing = makeAllCaches(workingDisplay, cache, newRemaining, newChoices);
                }
                newOutcomes.add(existing);
            }
            newSeed.add(newOutcomes);
        }
        return newSeed;
    }
       
    public void displayOutcomes(int max){
        if(max > this.outcomes.size()){
            max = this.outcomes.size();
        }
        for(int i = 0; i < max; i++){
            System.out.println(this.outcomes.get(i));
        }
    }
    
    public void displayOutcomes(){
        String winner;
        for(ArrayList<Integer> outcome : this.outcomes){
            if(outcome.size() % 2 == 0){
                winner = " <2>";
            }else{
                winner = " <1>";
            }
            System.out.println(outcome + winner);
        }
        
        int[] wins = getWinSpread();
        System.out.println("Total Games: " + (wins[0] + wins[1]));
        System.out.println("P1 Wins: " + wins[0] + ", P2 Wins: " + wins[1]);
    }
    
    public int[] getWinSpread(){
        int P1Wins = 0;
        int P2Wins = 0;
        for(ArrayList<Integer> outcome : this.outcomes){
            if(outcome.size() % 2 == 0){
                P2Wins += 1;
            }else{
                P1Wins += 1;
            }
        }
        int[] result = {P1Wins, P2Wins};
        return result;
    }
    
    public void saveGameOutcomes(){
        FileWriter output = null;
        if(this.outcomes.isEmpty()){
            System.out.println("[GameManager.saveGameOutcomes] No outcomes exist, file not created");
        }else{
            try {
                String fileName = "";
                for(Integer choice : this.init_choices){
                    fileName += choice.toString() + "_";
                }
                output = new FileWriter("../../output/" + fileName + ".txt");
                output.write("SYLVER COINAGE OUTPUT (Java)\n");
                output.write("Initial Choices = " + this.init_choices + "\n");
                int[] spread = this.getWinSpread();
                int totalGames = spread[0] + spread[1];
                output.write("Total Games: " + totalGames + ", P1Wins: " + spread[0] + ", P2Wins: " + spread[1] + "\n\n");
                for(ArrayList<Integer> outcome : this.outcomes){
                    String winner = "[1]";
                    if(outcome.size() % 2 == 0){
                        winner = "[2]";
                    }
                    output.write(outcome.toString()+" "+winner+"\n");
                }
                System.out.println("[GameManager.saveGameOutcomes] File: " + fileName + ".txt" + " has been created in the output folder");
            }
            catch(IOException ioe){
                System.err.println("[GameManager.saveGameOutcomes] Output file failed to open " + ioe);
            }finally {
                if(output != null){
                    try {
                        output.close();
                    }catch(IOException ioe){
                        System.err.println("[GameManager.saveGameOutcomes] Output file failed to close " + ioe);
                    }
                }
            }
        }
    }
}
