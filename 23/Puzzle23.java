import java.util.*;


public class Puzzle23
{
    private static Integer getDestination(Integer currentCup, List<Integer> pickedUpCups, List<Integer> otherCups)
    {
        Integer numToTry = currentCup - 1;

        while(true)
        {
            if (numToTry < 1)
            {
                return otherCups.stream().max(Integer::compare).get(); //probably a faster way to determine this...
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

    private static void stepGame(LinkedList<Integer> state)
    {
        Integer currentCup = state.poll();

        Integer pickedUp1 = state.poll();
        Integer pickedUp2 = state.poll();
        Integer pickedUp3 = state.poll();
        List<Integer> pickedUpCups = Arrays.asList(pickedUp1, pickedUp2, pickedUp3);

        Integer destinationCup = getDestination(currentCup, pickedUpCups, state);
        int destinationIndex = state.indexOf(destinationCup);

        state.addAll(destinationIndex+1, pickedUpCups);
        state.add(currentCup);
    }

    private static List<Integer> twoCupsFollowingOne(List<Integer> state)
    {
        /*
        (let [ index-of-one (.indexOf cups 1)
             , [left-part right-part] (split-at (inc index-of-one) cups)
             , rearranged-cups (concat right-part left-part)
             ]
            (vec (take 2 rearranged-cups))
        )  
        */
        int indexOfOne = state.indexOf(new Integer(1)); //fingers crossed
        return Arrays.asList(state.get(indexOfOne+1), state.get(indexOfOne+2)); //betting that 1 doesn't land at the end...
    }

    public static void main(String[] args)
    {
        LinkedList<Integer> sampleInput = new LinkedList<>(Arrays.asList(3, 8, 9, 1, 2, 5, 4, 6, 7));

        System.out.println("filling up list...");
        for(int x = 10; x <= 1000000; x++)
        {
            sampleInput.add(x);
        }

        System.out.println("stepping...");
        final int TURNS = 10 * 1000 * 1000;
        for(int i = 1; i <= TURNS; i++)
        {
            if (i % 10000 == 0)
            {
                System.out.println(i);
            }

            stepGame(sampleInput);
            //System.out.println("" + i + ": " + sampleInput);
        }

        List<Integer> twoCups = twoCupsFollowingOne(sampleInput);
        System.out.println("two cups after #1: " + twoCups);
    }
}
