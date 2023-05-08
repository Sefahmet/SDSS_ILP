import gurobi.*;

import java.io.*;
import java.util.HashMap;

import static Core.CsvReader.*;
import static ILP.model.*;


public class Main {
    public static void main(String[] args) throws IOException,  GRBException {

        /* ---------------------------------------Read Distance File--------------------------------------- */
        //Data paths
        String csvFileShortest = "Data/distances/distances_short.csv";
        String csvFileFastest = "Data/distances/distances_fast.csv";
        String csvSplitBy = ",";

        HashMap<String,Double> time = new HashMap<String,Double>();
        // Get results with capacity constraints on shortest path
        double[][] distanceMatrixShortest = getMatrixFromFileName(csvFileShortest,csvSplitBy);
        double[][] distanceMatrixFastest = getMatrixFromFileName(csvFileFastest,csvSplitBy);

        int buildingCount= distanceMatrixShortest[0].length; ;


        FileCreator("Distance_Result/distance_Fastest_threshold_10000.csv",buildingCount);
        long d1 = System.nanoTime();
        Distance (distanceMatrixFastest,"Distance_Result/distance_Fastest_threshold_10000.csv","fastest");
        long d2 = System.nanoTime();
        time.put("Distance_Fastest",(d2-d1)/1000000000.0);



        d1 = System.nanoTime();
        FileCreator("Distance_Result/distance_shortest_threshold_10000.csv",buildingCount);
        Distance (distanceMatrixShortest,"Distance_Result/distance_shortest_threshold_10000.csv","shortest");
        d2 = System.nanoTime();
        time.put("Distance_Shortest",(d2-d1)/1000000000.0);






        // Capacity Results on Shortest Path
        FileCreator("Capacity_Result/capacity_w_shortest_path.csv",buildingCount);
        for(double i =1;i<2.1;i+=0.1){
            // Min Capacity is Building Count / Fire Station and capacity (i) means min_capacity*i
            // ex: i==1.1 => capacity is min capacity + %10 of min capacity
            d1 = System.nanoTime();
            Capacity (distanceMatrixShortest,i,"Capacity_Result/capacity_w_shortest_path.csv");
            d2 = System.nanoTime();
            time.put("Capacity_Shortest"+i,(d2-d1)/1000000000.0);

        }

        // Capacity Results on Fastest Path
        FileCreator("Capacity_Result/capacity_w_fastest_path.csv",buildingCount);
        for(double i =1;i<2.1;i+=0.1){
            // Min Capacity is Building Count / Fire Station and capacity (i) means min_capacity*i
            // ex: i==1.1 => capacity is min capacity + %10 of min capacity
            d1 = System.nanoTime();
            Capacity (distanceMatrixFastest,i,"Capacity_Result/capacity_w_fastest_path.csv");
            d2 = System.nanoTime();
            time.put("Capacity_Fastest"+i,(d2-d1)/1000000000.0);
        }


        //Threshold with shortest
        FileCreator("Threshold_Result/threshold_shortest.csv",buildingCount);
        for(double i =500;i<6001;i+=500){
            d1 = System.nanoTime();
            Threshold (distanceMatrixShortest,i,"Threshold_Result/threshold_shortest.csv","shortest");
            d2 = System.nanoTime();
            time.put("Threshold_Shortest"+i,(d2-d1)/1000000000.0);
        }


        //Threshold with fastest
        FileCreator("Threshold_Result/threshold_fastest.csv",buildingCount);
        for(double i =60;i<1501;i+=60){
            d1 = System.nanoTime();
            Threshold (distanceMatrixFastest,i,"Threshold_Result/threshold_fastest.csv","fastest");
            d2 = System.nanoTime();
            time.put("Threshold_Fastest"+i,(d2-d1)/1000000000.0);
        }
        for (String key:time.keySet()) {
            System.out.println(key + " working time is: " + time.get(key) );
        }
        }
}
