package utils;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Kyle Savery
 */
public class MathUtils {
    public static final int INFINITY = -1;
    public static final int MAX_INF_BOARD_SIZE = 1000;
    
    public String millisToSeconds(int milliseconds){
        return "";
    }
    
    public static int getFrobeniusNum(ArrayList<Integer> choices){
        if(choices.size() < 2){
            return INFINITY;
        }
        int num_choices = choices.size();
        int max = ArrayUtils.max(choices);
        int min = max * max;
        boolean finite = false;
        for(int i = 0; i < num_choices; i++){
            for(int j = i+1; j < num_choices; j++){
                if(MathUtils.gcd(choices.get(i), choices.get(j)) == 1){
                    finite = true;
                    int result = (choices.get(i)-1)*(choices.get(j)-1) - 1;
                        if(result < min){
                            min = result;
                        }
                }
            }
        }
        if(!finite){
            return MAX_INF_BOARD_SIZE;
        }
        return min;
    }
    
    public static boolean checkChoices(ArrayList<Integer> init_choices){         
        for(int i = 1; i < init_choices.size(); i++){
            int toCheck = init_choices.get(i);
            int[] prev_choices = new int[i];
            for(int k = 0; k < i; k++){
                prev_choices[k] = init_choices.get(k);
            }
            ArrayUtils.sort(prev_choices);
            if(isLinearComb(toCheck, prev_choices, prev_choices.length)){
                return false;
            }
        }
        
        return true;
    }
    
    public static ArrayList<Integer> getRemaining(ArrayList<Integer> init_choices, int max){
        ArrayList<Integer> remaining = new ArrayList<>();
        
        if(!checkChoices(init_choices)){
            return remaining;
        }
        for(Integer i = 1; i <= max; i++){
            remaining.add(i);
        }
        Collections.sort(init_choices);
        if(!init_choices.isEmpty()){
            ArrayList<Integer> toRemove = new ArrayList<>();
            for(Integer check : remaining){
                if(isLinearComb(check, init_choices, init_choices.size())){
                    toRemove.add(check);
                }
            }
            for(Integer remove : toRemove){
                remaining.remove(remove);
            }
        }
        return remaining;
    }

    
    //Euclid's Algorithm
    public static int gcd(int num1, int num2){
        if(num2 == 0){
            return num1;
        }
        return gcd(num2, num1 % num2);
    }
    
    /*
    * 'choices' is a sorted array
    * return true if toCheck can be made as a linear comination from choices,
    * false, otherwise
    */
    public static boolean isLinearComb(int toCheck, int[] choices, int num_choices){
        int min = choices[0];
        int max = choices[num_choices-1];
        if(toCheck < min && toCheck != 0){
            return false;
        }
        
        if(num_choices == 1 && (toCheck % min != 0)){
            return false;
        }
        
        if(toCheck == 0){
            return true;
        }
        
        if(isLinearComb(toCheck - max, choices, num_choices)){
            return true;
        }else{
            if(num_choices > 1){
                return isLinearComb(toCheck, choices, num_choices - 1);
            }
            return false;
        }
    }
    
    public static Integer getLastEven(ArrayList<Integer> list){
        for(int i = list.size()-1; i >= 0; i--){
            if(list.get(i) % 2 == 0){
                return list.get(i);
            }
        }
        return 0;
    }
    
    public static boolean isLinearComb(int toCheck, ArrayList<Integer> choices, int num_choices){
        Integer min = choices.get(0);
        Integer max = choices.get(num_choices-1);
        if(toCheck < min && toCheck != 0){
            return false;
        }
        
        if(num_choices == 1 && (toCheck % min != 0)){
            return false;
        }
        
        if(toCheck == 0){
            return true;
        }
        
        if(isLinearComb(toCheck - max, choices, num_choices)){
            return true;
        }else{
            if(num_choices > 1){
                return isLinearComb(toCheck, choices, num_choices - 1);
            }
            return false;
        }
    }
}
