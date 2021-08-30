package test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Commands {

    // Default IO interface
    public interface DefaultIO {
        public String readText();

        public void write(String text);

        public float readVal();

        public void write(float val);

        // you may add default methods here
    }

    // the default IO to be used in all commands
    DefaultIO dio;

    public Commands(DefaultIO dio) {
        this.dio = dio;
    }

    // you may add other helper classes here


    // the shared state of all commands
    private class SharedState {
        ;
        // implement here whatever you need
        String trainFile;
        String testFile;
        int numOfLines;
        List<AnomalyReport> anomalyReportList;
        float threshold = (float) 0.9;
    }

    private SharedState sharedState = new SharedState();

    // Command abstract class
    public abstract class Command {
        protected String description;

        public Command(String description) {
            this.description = description;
        }

        public abstract void execute();
    }

    public class MenuCommand extends Command {

        public MenuCommand() {
            super("Welcome to the Anomaly Detection Server.\n" +
                    "Please choose an option:\n" +
                    "1. upload a time series csv file\n" +
                    "2. algorithm settings\n" +
                    "3. detect anomalies\n" +
                    "4. display results\n" +
                    "5. upload anomalies and analyze results\n" +
                    "6. exit\n");
        }

        @Override
        public void execute() {
            dio.write(description);
        }
    }

    public class UploadCommand extends Command {
        UploadCommand() {
            super("UploadFileWrapper");
        }

        @Override
        public void execute() {
            this.getFile("Train");
            this.getFile("Test");
        }

        public void readAndWriteToCSV(String csvFile) {
            try {
                FileWriter fileWriter = new FileWriter(csvFile);
                String inputText = dio.readText();

                //read csv until "done"
                while (!inputText.equals("done")) {
                    fileWriter.write(inputText);
                    fileWriter.write("\n");
                    fileWriter.flush();
                    inputText = dio.readText();
                }

                fileWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        //Get file (train or test) and write it to csv
        public void getFile(String file) {
            String csvFile = "anomaly" + file + ".csv";
            PreUploadFile preUploadFile = new PreUploadFile(file.toLowerCase() + " CSV");
            preUploadFile.execute();
            this.readAndWriteToCSV(csvFile);
            new PostUploadFile().execute();
        }
    }

    public class PreUploadFile extends Command {

        public PreUploadFile(String fileMode) {
            super("Please upload your local " + fileMode + " file.\n");
        }

        @Override
        public void execute() {
            dio.write(description);
        }

    }

    public class PostUploadFile extends Command {

        public PostUploadFile() {
            super("Upload complete.\n");
        }

        @Override
        public void execute() {
            dio.write(description);
        }

    }

    public class AlgorithmCommand extends Command {
        public AlgorithmCommand() {
            super("Algorithm command\n");
        }

        @Override
        public void execute() {
            dio.write("The current correlation threshold is 0.9\n" + "Type a new threshold\n");
            String input = dio.readText();
            while (Float.parseFloat(input) < 0 || Float.parseFloat(input) > 1) {
                dio.write("please choose a value between 0 and 1.");
                input = dio.readText();
            }
            sharedState.threshold = Float.parseFloat(input);
        }
    }

    public class DetectCommand extends Command {
        public DetectCommand() {
            super("anomaly detection.\n");
        }

        public void getNumOfLinesCSV(String fileName) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                int lines = 0;
                while (reader.readLine() != null)
                    lines++;
                reader.close();
                sharedState.numOfLines = lines - 1;
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        @Override
        public void execute() {
            SimpleAnomalyDetector simpleAnomalyDetector = new SimpleAnomalyDetector();
            simpleAnomalyDetector.learnNormal(new TimeSeries("anomalyTrain.csv", sharedState.threshold));
            sharedState.anomalyReportList = simpleAnomalyDetector.detect(new TimeSeries("anomalyTest.csv"));
            this.getNumOfLinesCSV("anomalyTest.csv");
            dio.write("anomaly detection complete.\n");
        }
    }

    public class DisplayReportsCommand extends Command {
        DisplayReportsCommand() {
            super("display reports");
        }

        @Override
        public void execute() {
            for (AnomalyReport ar : sharedState.anomalyReportList) {
                String line = ar.timeStep + "\t" + ar.description;
                dio.write(line + '\n');
            }
            dio.write("done.\n");
        }
    }

    public class AnalyzeCommand extends Command {

        AnalyzeCommand() {
            super("Anomaly analyze");

        }

        public ArrayList<ArrayList<Integer>> getServerAnomaliesInArray() {
            ArrayList<ArrayList<Integer>> server_arrs = new ArrayList<>();
            ArrayList<Integer> serv_arr = new ArrayList<>();

            for (AnomalyReport ar : sharedState.anomalyReportList) {
                if (sharedState.anomalyReportList.indexOf(ar) == 0) {
                    serv_arr.add((int) ar.timeStep);
                    if (sharedState.anomalyReportList.indexOf(ar) == sharedState.anomalyReportList.size() - 1) {
                        server_arrs.add(serv_arr);
                    }


                } else if (sharedState.anomalyReportList.get(sharedState.anomalyReportList.indexOf(ar) - 1).timeStep + 1 == ar.timeStep) {
                    serv_arr.add((int) ar.timeStep);
                    if (sharedState.anomalyReportList.indexOf(ar) == sharedState.anomalyReportList.size() - 1) {
                        server_arrs.add(serv_arr);
                    }

                } else {
                    server_arrs.add(serv_arr);
                    serv_arr = new ArrayList<>();
                    serv_arr.add((int) ar.timeStep);
                    if (sharedState.anomalyReportList.indexOf(ar) == sharedState.anomalyReportList.size() - 1) {
                        server_arrs.add(serv_arr);
                    }

                }

            }
            return server_arrs;
        }

        public int calcTP(ArrayList<ArrayList<Integer>> user_arrs, ArrayList<ArrayList<Integer>> servers_anomalies) {
            int TP = 0;
            for (ArrayList<Integer> ua : user_arrs) {
                for (int val : ua) {
                    boolean flag = false;
                    for (ArrayList<Integer> sa : servers_anomalies) {
                        if (sa.contains(val)) {
                            TP++;
                            flag = true;
                            break;
                        }
                    }
                    if (flag)
                        break;
                }
            }
            return TP;
        }

        public int calcFP(ArrayList<ArrayList<Integer>> user_arrs, ArrayList<ArrayList<Integer>> servers_anomalies) {
            int FP = 0;
            boolean flag;
            for (ArrayList<Integer> ua : servers_anomalies) {
                flag = false;
                for (int val : ua) {
                    for (ArrayList<Integer> sa : user_arrs) {
                        if (sa.contains(val)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag)
                        break;

                }

                if (!flag)
                    FP++;
            }
            return FP;
        }

        @Override
        public void execute() {
            int P = 0;
            int N = sharedState.numOfLines;

            PreUploadFile preUploadFile = new PreUploadFile("anomalies");
            preUploadFile.execute();
            String input = dio.readText();
            ArrayList<ArrayList<Integer>> servers_anomalies = getServerAnomaliesInArray();

            ArrayList<ArrayList<Integer>> user_arrs = new ArrayList<>();
            while (!input.equals("done")) {
                ArrayList<Integer> arr = new ArrayList<>();
                P++;
                String str[] = input.split(",", 2);
                int firstNum = Integer.parseInt(str[0]);
                int secondNum = Integer.parseInt(str[1]);
                for (int i = firstNum; i <= secondNum; i++) {
                    arr.add(i);
                }
                N -= (secondNum - firstNum) + 1;
                user_arrs.add(arr);
                input = dio.readText();
            }

            int TP = calcTP(user_arrs, servers_anomalies);
            int FP = calcFP(user_arrs, servers_anomalies);
            double tp_rate = Math.floor((double) TP / P * 1000) / 1000;
            double fp_rate = Math.floor((double) FP / N * 1000) / 1000;
            new PostUploadFile().execute();
//                    dio.write("Analyzing...\n");
            dio.write("True Positive Rate: " + tp_rate + '\n');
            dio.write("False Positive Rate: " + fp_rate + '\n');

        }
    }

}
