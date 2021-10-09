import java.util.*;
import java.util.stream.Collectors;


public class Puzzle23ArrayCopy
{
    static final int MIN_NUM = 1;
    static final int MAX_NUM = 1000000; //also the array length

    //static final int[] redArray = new int[MAX_NUM];
    //static final int[] blackArray = new int[MAX_NUM];
    //static boolean readFromRed = true;
    static final int[] numArray = new int[MAX_NUM];


    private static int getDestination(int currentCup, List<Integer> pickedUpCups)
    {
        int numToTry = currentCup - 1;

        while(true)
        {
            if (numToTry < MIN_NUM)
            {
                numToTry = MAX_NUM;
            }
            else if (pickedUpCups.contains(numToTry))
            {
                numToTry -= 1;
            }
            else
            {
                return numToTry;
            }
        }
    }


    private static void stepGame()
    {
        int currentCup = numArray[0];
        int pickedUp1 = numArray[1];
        int pickedUp2 = numArray[2];
        int pickedUp3 = numArray[3];
        List<Integer> pickedUpCups = Arrays.asList(pickedUp1, pickedUp2, pickedUp3);

        int destinationCup = getDestination(currentCup, pickedUpCups);

        int readIndex = 4;
        int writeIndex = 0;
        while(readIndex < MAX_NUM)
        {
            if(numArray[readIndex] == destinationCup)
            {
                numArray[writeIndex] = numArray[readIndex];
                numArray[writeIndex+1] = pickedUp1;
                numArray[writeIndex+2] = pickedUp2;
                numArray[writeIndex+3] = pickedUp3;  

                readIndex++;
                writeIndex += 4;
            }
            else
            {
                numArray[writeIndex] = numArray[readIndex];

                readIndex++;
                writeIndex++;
            }
        }
        numArray[MAX_NUM-1] = currentCup;
    }


    private static List<Integer> twoCupsFollowingOne()
    {
        List<Integer> numArrayAsList = Arrays.stream(numArray).boxed().collect(Collectors.toList());
        int indexOfOne = numArrayAsList.indexOf(1);
        System.out.println("index of #1: " + indexOfOne);
        int firstNum = numArrayAsList.get( (indexOfOne+1) % MAX_NUM );
        int secondNum = numArrayAsList.get( (indexOfOne+2) % MAX_NUM );

        return Arrays.asList(firstNum, secondNum);
    }


    private static void myPrintArray(int[] arr)
    {
        for(int i = 0; i < arr.length; i += 10)
        {
            System.out.print(i + ": ");

            for(int j = 0; j < 10; j++)
            {
                if( (i+j) < arr.length )
                {
                    System.out.print(arr[i+j] + ", ");
                }
            }

            System.out.println();
        }
    }


    public static void main(String[] args)
    {
        System.out.println("seeding array...");
        //List<Integer> input = Arrays.asList(3, 8, 9, 1, 2, 5, 4, 6, 7); //sample
        List<Integer> input = Arrays.asList(8, 7, 2, 4, 9, 5, 1, 3, 6); //input23
        for(int i = 0; i < input.size(); i++)
        {
            numArray[i] = input.get(i);
        }
        for(int i = input.size(); i < MAX_NUM; i++)
        {
            numArray[i] = i+1;
        }

        System.out.println("stepping...");
        final int TURNS = 10 * 1000 * 1000;
        for(int i = 1; i <= TURNS; i++)
        {
            if (i % 10000 == 0)
            {
                System.out.println(i);
            }

            stepGame();
            //System.out.println("" + i + ": " + Arrays.toString(numArray));
        }

        System.out.println("final state:");
        myPrintArray(numArray);

        List<Integer> twoCups = twoCupsFollowingOne();
        System.out.println("two cups after #1: " + twoCups); // 267349 * 639000 = 170836011000
    }
}