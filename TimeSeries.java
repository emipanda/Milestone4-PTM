package test;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class TimeSeries {
    String csv;

    ArrayList<String> csvColumns;
    Map<String, ArrayList<Float>> csvRows;
    float correlation_threshold = (float) 0.9;

    public TimeSeries(String csvFileName) {
        this.csv = csvFileName;
        this.csvColumns = new ArrayList<>();
        this.csvRows = new LinkedHashMap<>();
        this.init();
    }

    public TimeSeries(String csvFileName, float ct) {
        this.csv = csvFileName;
        this.csvColumns = new ArrayList<>();
        this.csvRows = new LinkedHashMap<>();
        this.correlation_threshold = ct;
        this.init();
    }


    public void initColumnNames() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.csv));
            String header = br.readLine();
            if (header != null) {
                String[] columns = header.split(",");
                this.csvColumns.addAll(Arrays.asList(columns));
            }

        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public void initRows() {
        String row;
        for (String s : this.csvColumns) {
            this.csvRows.put(s, new ArrayList<>());
        }

        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(this.csv));
            int counter = 0;
            while ((row = csvReader.readLine()) != null) {

                if (counter != 0) {
                    String[] data = row.split(",");
                    for (int j = 0; j < data.length; j++) {
                        float value = Float.parseFloat(data[j]);
                        String feature = this.csvColumns.get(j);
                        this.csvRows.get(feature).add(value);
                    }

                }
                counter++;
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public Float getFeatureAtTime(String feature, Float time) {
        ArrayList<Float> times = this.csvRows.get(this.csvColumns.get(0));
        ArrayList<Float> featureData = this.csvRows.get(feature);
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i).equals(time))
                return featureData.get(i);
        }
        return null;
    }


    public void init() {
        this.initColumnNames();
        this.initRows();

    }

    public static float[] convertArrayListToArray(ArrayList<Float> arrlist) {
        float[] floatArray = new float[arrlist.size()];
        int i = 0;

        for (Float f : arrlist) {
            floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }
        return floatArray;
    }

    public float[] getColumnValuesInArray(String feature) {
        return convertArrayListToArray(this.csvRows.get(feature));
    }
}

