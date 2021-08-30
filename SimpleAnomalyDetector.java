package test;

import java.util.*;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {

    List<CorrelatedFeatures> correlatedFeaturesList = new ArrayList<>();;
    List<AnomalyReport> anomalyReportList = new ArrayList<>();;

   /* SimpleAnomalyDetector() {
        correlatedFeaturesList = new ArrayList<>();
        anomalyReportList = new ArrayList<>();
    }*/

    @Override
    public void learnNormal(TimeSeries ts) {
        correlatedFeaturesList.clear();
        ArrayList<String> listOfFeatures = ts.csvColumns;
        for (String f : listOfFeatures) {
            for (String f2 : listOfFeatures) {
                if (listOfFeatures.indexOf(f) < listOfFeatures.indexOf(f2)) {
                    float[] f_arr = ts.getColumnValuesInArray(f);
                    float[] f2_arr = ts.getColumnValuesInArray(f2);
                    float f_correlation = StatLib.pearson(f_arr, f2_arr);
                    if (f_correlation > ts.correlation_threshold) {

                        Point[] points = new Point[f_arr.length];
                        for (int i = 0; i < f_arr.length; i++) {
                            Point p = new Point(f_arr[i], f2_arr[i]);
                            points[i] = p;
                        }

                        Line l = StatLib.linear_reg(points);
                        float threshold = 0;
                        for (Point p : points) {
                            float d = StatLib.dev(p, l);
                            if (d > threshold) {
                                threshold = d;
                            }

                        }

                        correlatedFeaturesList.add(new CorrelatedFeatures(f, f2, f_correlation, StatLib.linear_reg(points), threshold + (float) (0.026)));

                    }
                }
            }
        }

    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        anomalyReportList.clear();

        String timeColumn = ts.csvColumns.get(0);
        for (CorrelatedFeatures f : correlatedFeaturesList) {
            String f1 = f.feature1;
            String f2 = f.feature2;
            float[] f_arr = ts.getColumnValuesInArray(f1);
            float[] f2_arr = ts.getColumnValuesInArray(f2);
            float[] times_arr = ts.getColumnValuesInArray(timeColumn);

            for (int i = 0; i < f_arr.length; i++) {
                Point p = new Point(f_arr[i], f2_arr[i]);
                if (StatLib.dev(p, f.lin_reg) > f.threshold) {
                    AnomalyReport ar = new AnomalyReport(f1 + "-" + f2, i + 1);
                    anomalyReportList.add(ar);
//                    break;
                }
            }

        }
        return anomalyReportList;
    }

    public List<CorrelatedFeatures> getNormalModel() {
        return this.correlatedFeaturesList;
    }
}
