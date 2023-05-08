package Core;

import java.io.*;
import java.util.*;

public class CsvReader {

    public static HashMap<String, Double[]> readCSV(String csvFile, String csvSplitBy) {
        HashMap<String, Double[]> dataMap = new HashMap<>();
        int lineCount = 0;

        // Find "How many row includes""
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String l;
            while ((l = br.readLine()) != null){

                lineCount++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Read Columns Names
            String line = br.readLine();
            String[] columnNames = line.split(csvSplitBy);
            for (String column:columnNames) {
                dataMap.put(column,new Double[lineCount-1]);
            }
            // Rest Lines (Double Values)
            int idx = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(csvSplitBy);

                // Add value to array
                for (int i = 0; i < columnNames.length; i++) {
                    String columnName = columnNames[i];
                    dataMap.get(columnName)[idx] = Double.parseDouble(values[i]);


                }
                idx++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return dataMap;
    }

    public static double[][] getMatrixFromFileName(String csvFile,String csvSplitBy){
        return  getMatrixFromFileName(csvFile,csvSplitBy,"origin_id","destination_id","total_cost");
    }
    public static double[][] getMatrixFromFileName(String csvFile,String csvSplitBy,String target_column ){
        return  getMatrixFromFileName(csvFile,csvSplitBy,"origin_id","destination_id",target_column);
    }
    public static double[][] getMatrixFromFileName(String csvFile,String csvSplitBy,String origin_id,String destination_id,String target_column ){
        HashMap<String, Double[]> distances = CsvReader.readCSV(csvFile, csvSplitBy);
        System.out.println(distances.keySet());
        double[][] distanceMatrix = convertCSV2Matrix(distances,origin_id,destination_id,target_column);
        return  distanceMatrix;
    }

    public static List<Double> getUniqueElements(List<Double> list) {
        Set<Double> uniqueSet = new HashSet<>(list);
        return new ArrayList<>(uniqueSet);
    }
    public static double[][] convertCSV2Matrix(HashMap<String, Double[]> csv, String id1, String id2, String targetcolumn){
        int row = getUniqueElements(List.of(csv.get(id1))).size();
        int col = getUniqueElements(List.of(csv.get(id2))).size();
        double[][] matrix = new double[row][col];
        int idx = 0;
        for (int i = 0 ; i<row; i++) {
            for(int j = 0; j<col;j++){
                matrix[i][j] = csv.get(targetcolumn)[idx];
                idx++;
            }
        }
        return matrix;
    }
}