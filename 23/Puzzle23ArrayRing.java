import java.util.*;
import java.util.stream.Collectors;


public class Puzzle23ArrayRing
{
    static final int MIN_NUM = 1;
    static final int MAX_NUM = 1000 * 1000; //also the array length

    //static final int[] redArray = new int[MAX_NUM];
    //static final int[] blackArray = new int[MAX_NUM];
    //static boolean readFromRed = true;
    static final int[] numArray = new int[MAX_NUM];
    static int currentPosition = 0;


    private static int getDestination(int currentCup, int[] pickedUpCups)
    {
        int numToTry = currentCup - 1;
        
        //System.out.println("currentCup=" + currentCup + ", pickedUpCups=" + pickedUpCups[0] + "," + pickedUpCups[1] + "," + pickedUpCups[2]);
        
        while(true)
        {
            if (numToTry < MIN_NUM)
            {
                numToTry = MAX_NUM;
            }
            else if (numToTry == pickedUpCups[0] || numToTry == pickedUpCups[1] || numToTry == pickedUpCups[2])
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
        int currentCup = numArray[currentPosition];
        int pickedUp1 = numArray[(currentPosition+1) % MAX_NUM];
        int pickedUp2 = numArray[(currentPosition+2) % MAX_NUM];
        int pickedUp3 = numArray[(currentPosition+3) % MAX_NUM];
        //List<Integer> pickedUpCups = Arrays.asList(pickedUp1, pickedUp2, pickedUp3);
        int[] pickedUpCups = {pickedUp1, pickedUp2, pickedUp3};

        int destinationCup = getDestination(currentCup, pickedUpCups);
        //System.out.println("destination=" + destinationCup);

        int i = (currentPosition+1) % MAX_NUM;
        while(i != currentPosition)
        {
            if(numArray[(i+3) % MAX_NUM] == destinationCup)
            {
                numArray[(i+0) % MAX_NUM] = destinationCup;
                numArray[(i+1) % MAX_NUM] = pickedUp1;
                numArray[(i+2) % MAX_NUM] = pickedUp2;
                numArray[(i+3) % MAX_NUM] = pickedUp3;  

                break;
            }
            else
            {
                numArray[i] = numArray[(i+3) % MAX_NUM];
            }

            i = (i+1) % MAX_NUM;
        }

        currentPosition = (currentPosition+1) % MAX_NUM;
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
        List<Integer> input = Arrays.asList(3, 8, 9, 1, 2, 5, 4, 6, 7); //sample
        //List<Integer> input = Arrays.asList(8, 7, 2, 4, 9, 5, 1, 3, 6); //input23
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

            //System.out.println("" + i + ": " + Arrays.toString(numArray));
            stepGame();
        }

        System.out.println("final state:");
        myPrintArray(numArray);

        List<Integer> twoCups = twoCupsFollowingOne();
        System.out.println("two cups after #1: " + twoCups);
    }
}
