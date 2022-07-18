package utils;
import java.util.*;
import static utils.MathUtils.MAX_INF_BOARD_SIZE;

/**
 *
 * @author Kyle Savery
 */
public class ArrayUtils {
    
    public static String arrayToString(int[] arr){
        String result = "[ ";
        for(int i = 0; i < arr.length; i++){
            result += ((Integer)arr[i]).toString() + " ";
        }
        result += "]";
        return result;
    }
    
    public static ArrayList<Integer> stringsToIntegers(ArrayList<String> strInput){
        ArrayList<Integer> converted = new ArrayList<>();
        for(String str : strInput){
            converted.add(Integer.parseInt(str));
        }
        return converted;
    }
    
    public static ArrayList<Integer> toArrList(int[] arr){
        ArrayList<Integer> newArrList = new ArrayList<>();
        for(int num : arr){
            newArrList.add(num);
        }
        return newArrList;
    }
    
    public static int[] toIntArray(ArrayList<Integer> toConvert){
        int[] newArr = new int[toConvert.size()];
        for(int i = 0; i < toConvert.size(); i++){
            newArr[i] = toConvert.get(i);
        }
        return newArr;
    }
    
    public static int min(int[] array){
        int min = array[0];
        for(int i = 1; i < array.length; i++){
            if(array[i] < min){
                min = array[i];
            }
        }
        return min;
    }
    
    public static int max(int[] array){
        int max = array[0];
        for(int i = 1; i < array.length; i++){
            if(array[i] > max){
                max = array[i];
            }
        }
        return max;
    }
    public static int max(ArrayList<Integer> array){
        int max = array.get(0);
        for(int i = 1; i < array.size(); i++){
            if(array.get(i) > max){
                max = array.get(i);
            }
        }
        return max;
    }
    
    // Insertion Sort
    public static void sort(int[] toSort){
        int size = toSort.length;
        for(int i = 1; i < size; i++){
            int key = toSort[i];
            int j = i - 1;
            
            while(j >= 0 && key < toSort[j]){
                toSort[j+1] = toSort[j];
                j = j - 1;
            }
            toSort[j+1] = key;
            }
    }
    
    public static void quickSort(int[] toSort, int low, int high){
        if(low < high){
            int partIndex = partition(toSort, low, high);
            quickSort(toSort, low, partIndex-1);
            quickSort(toSort, partIndex+1, high);
        }
    }
    
    public static int partition(int[] toSort, int low, int high){
        int pivot = toSort[high];
        return 0;
    }
    
    public static int[] copy(int[] toCopy){
        int size = toCopy.length;
        int[] copy = new int[size];
        for(int i = 0; i < size; i++){
            copy[i] = toCopy[i];
        }
        return copy;
    }
    
    public static ArrayList<Integer> copy(ArrayList<Integer> toCopy){
        ArrayList<Integer> copied = new ArrayList<>();
        for(Integer num : toCopy){
            copied.add(num);
        }
        return copied;
    }
    
    public static ArrayList<Integer> setupGameBoard(ArrayList<Integer> choices, boolean evensOnly){
        ArrayList<Integer> sortedChoices = ArrayUtils.copy(choices);
        Collections.sort(sortedChoices);
        ArrayList<Integer> board = new ArrayList<>();
        int evensCheck = 1;
        if(evensOnly){
            evensCheck = 2;
        }
        for(Integer i = 1; i < MAX_INF_BOARD_SIZE; i++){
            if(i % evensCheck == 0 && !MathUtils.isLinearComb(i, sortedChoices, sortedChoices.size())){
                board.add(i);
            }
        }
        return board;
    }
    
    public static void displayArray(int[] arr, String title){
        System.out.println(title);
        System.out.print("[ ");
        for(int num : arr){
            System.out.print(num + " ");
        }
        System.out.println("]");
    }
    
    public boolean compareArrList(ArrayList<Integer> listOne, ArrayList<Integer> listTwo){
        return true;
    }
}
