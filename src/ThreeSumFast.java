import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;
import java.util.*;

public class ThreeSumFast {
    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE =  2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 10;
    static int MAXINPUTSIZE  = (int) Math.pow(2,15);
    static int MININPUTSIZE  =  1;
    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time
    static String ResultsFolderPath = "/home/codyschroeder/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {
        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        System.out.println("Running first full experiment...");
        runFullExperiment("ThreeSumFast-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment...");
        runFullExperiment("ThreeSumFast-Exp2.txt");
        System.out.println("Running third full experiment...");
        runFullExperiment("ThreeSumFast-Exp3.txt");
    }

    static void runFullExperiment(String resultsFileName){

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);

        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#InputSize    AverageTime"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        /* for each size of input we want to test: in this case starting small and doubling the size each time */
        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*=2) {
            // progress message...
            System.out.println("Running test for input size "+inputSize+" ... ");
            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;
            /* force garbage collection before each batch of trials run so it is not included in the time */
            System.gc();
            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves
            //BatchStopwatch.start(); // comment this line if timing trials individually

            // run the trials
            for (long trial = 0; trial < numberOfTrials; trial++) {
                // generate a random list of integers for each trial
                System.out.print("    Generating test data...");
                long[] newList = createRandomListOfIntegers(inputSize);
                System.out.println("...done.");
                System.out.print("    Running trial batch...");
                /* force garbage collection before each trial run so it is not included in the time */
                System.gc();

                TrialStopwatch.start(); // *** uncomment this line if timing trials individually
                /* run the function we're testing on the trial input */
                int count = threeSumFast(newList);
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
            }

            //batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f \n",inputSize, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    public static int threeSumFast(long[] list){

        //set variables
        int N = list.length;
        int count = 0;

        //sort the list
        Arrays.sort(list);

        //use combinations of pairs
        for (int i = 0; i < N; i++){
            for(int j = i + 1; j < N; j++){
                //use binary search to see if the pairs added together's opposite is in list and is greater than j index
                if(binarySearch(-(list[i] + list[j]), list) > j){
                    //if so, increase count - has triple that sums to zero
                    count++;
                }
            }
        }
        return count;
    }

    /* return index of the searched number if found, or -1 if not found */
    public static int binarySearch(long key, long[] list) {

        //set variables
        int i = 0;
        int j= list.length-1;

        //if key found, return index
        if (list[i] == key) return i;
        if (list[j] == key) return j;

        //split sorted list in half using k, return index if found
        int k = (i+j)/2;
        while(j-i > 1){
            if (list[k]== key) return k;
            else if (list[k] < key) i=k;
            else j=k;
            k=(i+j)/2;
        }
        //return -1 if not found
        return -1;
    }

    //create random integer list containing negative and positive numbers
    public static long[] createRandomListOfIntegers(int size){
        long[] newList = new long[size];
        for(int j = 0; j < size; j++) {
            newList[j] = (long) (MINVALUE + Math.random() * (MAXVALUE - MINVALUE));
        }
        return newList;
    }
}
