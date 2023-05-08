package ILP;

import gurobi.*;

import java.io.*;

public class model {

    public static void Distance(double[][] distanceMatrix, String filePathWay, String distancePathType)  {
        Threshold(distanceMatrix,99999999,filePathWay,distancePathType);
    }
    public static void Capacity(double[][] distanceMatrix, double capacityConst, String filePathWay) throws GRBException {
        /*
         * Variable distanceMatrix(2d Matrix) ->> Each columns represents buildings and rows represents Fire Stations
           Cells of distanceMatrix is shortest or fastest path type(double)
         * CapacityConst(double) => is greater equal than 1, and 1 means minimum capacity for each fire stations.
         * filePathWay(str) => is using as file name for results.
         */

        int buildinCount= distanceMatrix[0].length; ;
        int fireStationsCount = distanceMatrix.length;


        double capacity_d = (double) buildinCount / (double) fireStationsCount;
        double capacity =  Math.ceil(capacity_d*capacityConst);



        // Initialize Gurobi
        GRBEnv env = new GRBEnv("Solution/solution_capacity_fastest_"+(int) ((capacityConst*100)-100)+".log");
        GRBModel model = new GRBModel(env);

        // Set Objective
        model.setObjective(model.getObjective(), GRB.MINIMIZE);

        // Variables
        GRBVar[][] assign = new GRBVar[fireStationsCount][buildinCount];

        for (int i = 0; i < buildinCount; i++) {
            for (int j = 0; j < fireStationsCount; j++) {
                assign[j][i] = model.addVar(0.0, 1.0,Math.pow(distanceMatrix[j][i],2), GRB.BINARY, "assign_" + i + "_" + j);
            }
        }
        // Each building can assign one fire station
        for (int i = 0; i < buildinCount; i++) {
            GRBLinExpr constraintExpr = new GRBLinExpr();
            for (int j = 0; j < fireStationsCount; j++) {
                constraintExpr.addTerm(1.0, assign[j][i]);
            }
            model.addConstr(constraintExpr, GRB.EQUAL, 1.0, "constraint_building_" + i);
        }
        // Capacity
        for (int i = 0; i < fireStationsCount; i++) {
            GRBLinExpr constraintExpr = new GRBLinExpr();
            for (int j = 0; j < buildinCount; j++) {
                constraintExpr.addTerm(1.0, assign[i][j]);
            }
            model.addConstr(constraintExpr, GRB.LESS_EQUAL, capacity,"constraint_building_capacity_" + i);

        }


        // Model Optimization
        model.optimize();





        try (BufferedReader br = new BufferedReader(new FileReader(filePathWay))) {
            FileWriter writer = new FileWriter(filePathWay+"_tmp");
            String line = br.readLine();
            writer.write(line+"%"+(int) (capacityConst*10-10)*10+","+ System.lineSeparator());
            for (int i = 0; i < buildinCount; i++) {
                for (int j = 0; j < fireStationsCount; j++) {
                    if (assign[j][i].get(GRB.DoubleAttr.X) > 0) {
                        line = br.readLine();
                        String result = (j + 1) + ",";
                        writer.write(line+result + System.lineSeparator());
                    }
                }
            }
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        File oldFile = new File(filePathWay);
        oldFile.delete();

        // And rename tmp file's name to old file name
        File newFile = new File(filePathWay+"_tmp");
        newFile.renameTo(oldFile);



        model.dispose();
        env.dispose();



    }
    public static void Threshold(double[][] distances, double threshold,String filePathWay,String distancePathType) {
        int buildingsCount= distances[0].length ;
        int fireStationsCount = distances.length;
        try {
            String logName;
            if(threshold!=99999999) {
                logName = "Solution/threshold_" + distancePathType + "_" + (int) threshold + ".log";
            }else{
                logName = "Solution/shortest_" + distancePathType + ".log";
            }
            GRBEnv env = new GRBEnv(logName);
            GRBModel model = new GRBModel(env);

            model.setObjective(model.getObjective(), GRB.MINIMIZE);


            // Add variables
            GRBVar[][] assign = new GRBVar[buildingsCount][fireStationsCount];
            for (int i = 0; i <buildingsCount; i++) {
                for (int j = 0; j < fireStationsCount; j++) {
                    if (distances[j][i]<=threshold){
                        assign[i][j] = model.addVar(0.0, 1.0, Math.pow(distances[j][i],2), GRB.BINARY, "assign_" + (i+1) + "_" + (j+1));

                    }else{
                        assign[i][j] = null;
                    }
                }
            }

            // Each Building can assign 1 or 0 building
            // If a building has not any Fire Station in Threshold Value, this building is not considered
            for (int i = 0; i <buildingsCount; i++) {
                boolean flag = false;
                GRBLinExpr constraintExpr = new GRBLinExpr();
                for (int j = 0; j < fireStationsCount; j++) {
                    if (assign[i][j]!=null){
                        constraintExpr.addTerm(1.0, assign[i][j]);
                        flag = true;
                    }

                }
                if(flag){
                    model.addConstr(constraintExpr, GRB.EQUAL, 1.0, "constraint_building_" + (i+1));
                }
            }




            // Optimize Model
            model.optimize();

            // Writin Results
            try (BufferedReader br = new BufferedReader(new FileReader(filePathWay))) {
                FileWriter writer = new FileWriter(filePathWay+"_tmp");
                String line = br.readLine();
                writer.write(line+(int) threshold+","+ System.lineSeparator());
                for (int i = 0; i <buildingsCount; i++) {
                    boolean flag   = true;
                    for (int j = 0; j < fireStationsCount; j++) {

                        if (flag && assign[i][j]!=null && assign[i][j].get(GRB.DoubleAttr.X) > 0) {
                            line = br.readLine();
                            String result =  (j + 1)+",";
                            writer.write(line+result + System.lineSeparator());
                            flag = false;
                        }

                    }
                    if(flag){
                        line = br.readLine();
                        String result =  "9999," ;

                        writer.write(line + result + System.lineSeparator());
                    }
                }
                writer.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            File oldFile = new File(filePathWay);
            oldFile.delete();

            // And rename tmp file's name to old file name
            File newFile = new File(filePathWay+"_tmp");
            newFile.renameTo(oldFile);



            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            e.printStackTrace();
        }
    }
    public static void FileCreator(String filePathWay,int length) throws IOException {
        FileWriter writer = new FileWriter(filePathWay);
        writer.write("id," + System.lineSeparator());
        for (int i = 0; i < length; i++) {
                    writer.write(i+1+"," + System.lineSeparator());


        }
        writer.close();
    }

}
