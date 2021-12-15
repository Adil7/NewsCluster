import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*
3. Implement a document clustering system. The dataset can be downloaded here:http://mlg.ucd.ie/datasets/bbc.html.
There are two datasets, one is BBC news and the other is BBC Sports news. You can choose either one, please download the raw text files.
There are 5 classes of articles in each dataset. These class labels can be used as the gold standard when you measure the external
evaluation metrics. You can use the programs from the two assignments (with some modifications) to process the documents and build
the TFIDF weight vectors for these documents. Then, you can implement one of the clustering algorithms and run it on this dataset.
Two options are: k-means clustering (k=5) and hierachical agglomerateive clustering (with a selected similarity threshold,
you can also get 5 clusters).

Calculate the following metrics: tightness of each cluster (sum of squared distances between each document in the cluster and the centroid
of the cluster),
one of the external criteria (purity or rand index).
Note:Interface is optional. You are welcome to create one to show the clustered results (e.g., similar to the Scatter/Gather
example we show in class, or simply showing the cluster ID and all the document titles in the cluster).
 */

public class Cluster {
    private static Map<Integer,Document> documents ;
    private static Map<String,Integer> dfValues = new HashMap<>();
    private static Map<String,Double> idfValues = new HashMap<>();
    private static boolean stemInput = false;
    private static boolean stopWordInput = false;

    // optimize
    private static HashMap<Integer, HashMap<String, Double>> docVectors = new HashMap<>();
    private static TreeMap<Integer, HashMap<String, Double>> centroid = new TreeMap<>();
    private static List<Set<Integer>> clusters = new ArrayList<>();

    private static Set<Integer> c1 = new TreeSet<>();
    private static Set<Integer> c2 = new TreeSet<>();
    private static Set<Integer> c3 = new TreeSet<>();
    private static Set<Integer> c4 = new TreeSet<>();
    private static Set<Integer> c5 = new TreeSet<>();

    public static void setDocumentVector(Map<String, Integer> dfValues){
        double idf;
        int N = documents.size();
        for (int id : documents.keySet()){
            Document curDoc = documents.get(id);
            HashMap<String, Double> wordWeights = new HashMap<>();
            for (String word : curDoc.getTerms()){
                double tf = curDoc.getTermFreq(word);
                tf = 1 + Math.log10(tf);
                int df = dfValues.get(word);
                if (df != 0){
                    idf = Math.log10((double)N / df);
                }
                else
                    idf = 0.0;
                if (!idfValues.containsKey(word))
                    idfValues.put(word,idf);
                curDoc.setWeight(word, tf * idf);
                wordWeights.put(word, tf * idf);
            }
            docVectors.put(id, wordWeights);
        }
    }

    public static void kMeans(){
        // init centroids
        centroid.put(1, docVectors.get(1001));
        centroid.put(2, docVectors.get(1002));
        centroid.put(3, docVectors.get(1003));
        centroid.put(4, docVectors.get(1004));
        centroid.put(5, docVectors.get(1005));

        c1.add(1001);
        c2.add(1002);
        c3.add(1003);
        c4.add(1004);
        c5.add(1005);

        startIteration();
        repeatIteration();
        Set<Integer> tempC1 = c1;
        Set<Integer> tempC2 = c2;
        Set<Integer> tempC3 = c3;
        Set<Integer> tempC4 = c4;
        Set<Integer> tempC5 = c5;

        int i = 0;

        while (i < 100){
            repeatIteration();
            i++;
            if (tempC1.equals(c1) && tempC2.equals(c2) && tempC3.equals(c3) && tempC4.equals(c4) && tempC5.equals(c5)){
                System.out.println("Number of iterations to converge: " + i+2);
                break;
            } else {
                tempC1 = c1;
                tempC2 = c2;
                tempC3 = c3;
                tempC4 = c4;
                tempC5 = c5;
            }
        }




    }

    public static void startIteration(){
        for (Map.Entry<Integer, HashMap<String, Double>> curDoc : docVectors.entrySet()){
            List<Double> scores = new ArrayList<>();
            if (curDoc.getKey() == 1001 || curDoc.getKey() == 1002 || curDoc.getKey() == 1003 || curDoc.getKey() == 1004 || curDoc.getKey() == 1005)
                continue;
            for (Map.Entry<Integer, HashMap<String, Double>> curCent : centroid.entrySet()){
                double simResult = getCosineSimScore(curCent.getValue(), curDoc.getValue());
                scores.add(simResult);
            }
            int resultantCluster = scores.indexOf(Collections.max(scores)) + 1;
            addDocToCluster(resultantCluster, curDoc.getKey());

        }
    }

    public static void repeatIteration() {
        updateCentroid();
        c1 = new TreeSet<>();
        c2 = new TreeSet<>();
        c3 = new TreeSet<>();
        c4 = new TreeSet<>();
        c5 = new TreeSet<>();
        double simResult = 0.0;
        int resultantCluster = 0;
        for (Map.Entry<Integer, HashMap<String, Double>> curDoc : docVectors.entrySet()){
            List<Double> scores = new ArrayList<>();
            for (Map.Entry<Integer, HashMap<String, Double>> curCent : centroid.entrySet()){
                simResult = getCosineSimScore(curCent.getValue(), curDoc.getValue());
                scores.add(simResult);
            }
            resultantCluster = scores.indexOf(Collections.max(scores)) + 1;
            addDocToCluster(resultantCluster, curDoc.getKey());
        }
    }



    private static void updateCentroid() {
        centroid.clear();
        averageCentroidWeight(1, c1);
        averageCentroidWeight(2, c2);
        averageCentroidWeight(3, c3);
        averageCentroidWeight(4, c4);
        averageCentroidWeight(5, c5);
    }
    private static void averageCentroidWeight(int clusterNum, Set<Integer> cluster) {
        HashMap<String, Double> sumWeights = new HashMap<>();
        HashMap<String, Double> avgWeights = new HashMap<>();
        for (int docID : cluster){
            for (Map.Entry<String, Double> entry : docVectors.get(docID).entrySet()){
                String word = entry.getKey();
                double weight = entry.getValue();
                if (!sumWeights.containsKey(word)){
                    sumWeights.put(word, weight);
                } else {
                    sumWeights.put(word, sumWeights.get(word) + weight);
                }
            }
        }
        for (Map.Entry<String, Double> entry : sumWeights.entrySet()){
            String word = entry.getKey();
            double weight = entry.getValue();
            double avg = weight / cluster.size();
            avgWeights.put(word, avg);
        }
        centroid.put(clusterNum, avgWeights);
    }

    public static void addDocToCluster(int cluster, int docID){
        if (cluster == 1)
            c1.add(docID);
        if (cluster == 2)
            c2.add(docID);
        if (cluster == 3)
            c3.add(docID);
        if (cluster == 4)
            c4.add(docID);
        if (cluster == 5)
            c5.add(docID);
    }

    public static double getCosineSimScore(HashMap<String, Double> centV, HashMap<String, Double> docV){
        double res = 0.0;
        int lenC = centV.size();
        int lenD = docV.size();
        double dotProduct = 0.0;
        double normCent = 0.0;
        double normDoc = 0.0;

        if (lenD < lenC){
            for (Map.Entry<String, Double> entry : docV.entrySet()){
                String word = entry.getKey();
                if (centV.containsKey(word))
                    dotProduct += centV.get(word) * docV.get(word);
            }
        } else {
            for (Map.Entry<String, Double> entry : centV.entrySet()){
                String word = entry.getKey();
                if (docV.containsKey(word))
                    dotProduct += centV.get(word) * docV.get(word);
            }
        }
        for (Map.Entry<String, Double> entry : docV.entrySet())
            normDoc += entry.getValue();

        for (Map.Entry<String, Double> entry : centV.entrySet())
            normCent += entry.getValue();

        if (Math.sqrt(normCent) != 0 && Math.sqrt(normDoc) != 0)
            res = dotProduct / (Math.sqrt(normCent) * Math.sqrt(normDoc));
        return res;
    }

    public static Double calculatePurity(){
        double res = 0.0;
        int N = documents.size();
        int mostCommonClass = 0;
        for (Set<Integer> cluster : clusters){
            int athleticsClass = 0;
            int cricketClass = 0;
            int footballClass = 0;
            int rugbyClass = 0;
            int tennisClass = 0;
            List<Integer> sumOfClasses = new ArrayList<>();
            for (int id : cluster){
                String classLabel = documents.get(id).getLabel();
                if (classLabel.equals("athletics"))
                    athleticsClass++;
                if (classLabel.equals("cricket"))
                    cricketClass++;
                if (classLabel.equals("football"))
                    footballClass++;
                if (classLabel.equals("rugby"))
                    rugbyClass++;
                if (classLabel.equals("tennis"))
                    tennisClass++;
            }
            sumOfClasses.add(athleticsClass);
            sumOfClasses.add(cricketClass);
            sumOfClasses.add(footballClass);
            sumOfClasses.add(rugbyClass);
            sumOfClasses.add(tennisClass);
            mostCommonClass += Collections.max(sumOfClasses);
        }
        res = mostCommonClass / (double)N ;
        return res;
    }

    private static List<Double> calculateClusterTightness() {
        double t1 = 0.0;
        double t2 = 0.0;
        double t3 = 0.0;
        double t4 = 0.0;
        double t5 = 0.0;
        List<Double> res = new ArrayList<>();
        int resultantCluster = 0;
        double simResult = 0.0;
        for (Map.Entry<Integer, HashMap<String, Double>> curDoc : docVectors.entrySet()){
            List<Double> scores = new ArrayList<>();
            for (Map.Entry<Integer, HashMap<String, Double>> curCent : centroid.entrySet()){
                simResult = getCosineSimScore(curCent.getValue(), curDoc.getValue());
                scores.add(simResult);
            }
            resultantCluster = scores.indexOf(Collections.max(scores)) + 1;
            double distance = 1 - scores.get(resultantCluster - 1);
            if (resultantCluster == 1)
                t1 += Math.pow(distance, 2);

            if (resultantCluster == 2)
                t2 += Math.pow(distance, 2);

            if (resultantCluster == 3)
                t3 += Math.pow(distance, 2);

            if (resultantCluster == 4)
                t4 += Math.pow(distance, 2);

            if (resultantCluster == 5)
                t5 += Math.pow(distance, 2);
        }
        res.add(t1);
        res.add(t2);
        res.add(t3);
        res.add(t4);
        res.add(t5);
        return res;
    }

    public static void writeResults(){
        try {
            FileWriter summary = new FileWriter("clusterSummary.txt");
            FileWriter eval = new FileWriter("clusterEvaluation.txt");
            BufferedWriter writerSummary = new BufferedWriter(summary);
            BufferedWriter writerEval = new BufferedWriter(eval);
            writerSummary.write("classType.docID | Title" + "\n");
            int num = 1;
            for (Set<Integer> cluster : clusters){
                writerSummary.write("******************** Cluster #" + num + " ********************" + "\n") ;
                num++;
                for (int id : cluster){
                    String classType = documents.get(id).getLabel();
                    writerSummary.write(classType + "." + id + " | " + documents.get(id).getTitle() + "\n");
                }
            }


            writerSummary.close();

            writerEval.write("******************** Cluster Evaluation Metrics ********************" + "\n");
            writerEval.write("Purity: " + calculatePurity() + "\n");
            List<Double> distances = calculateClusterTightness();
            writerEval.write("Distance 1: " + distances.get(0) +"\n");
            writerEval.write("Distance 2: " + distances.get(1) +"\n");
            writerEval.write("Distance 3: " + distances.get(2) +"\n");
            writerEval.write("Distance 4: " + distances.get(3) +"\n");
            writerEval.write("Distance 5: " + distances.get(4) +"\n");
            writerEval.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Stop words excluded? ");
        if (scanner.next().equalsIgnoreCase("y"))
            stopWordInput = true;
        System.out.print("Stemming enabled? ");
        if (scanner.next().equalsIgnoreCase("y"))
            stemInput = true;
        InvertedIndex invertedIndex = new InvertedIndex(stopWordInput, stemInput);
        System.out.println("Building inverted index files.....");
        invertedIndex.makeDictionary();
        invertedIndex.makePosting();
        documents = invertedIndex.getDocuments();
        dfValues = invertedIndex.getDf();
        System.out.println("Generating document vectors....");
        setDocumentVector(dfValues);
        System.out.println();
        System.out.println("Performing k-means clustering (5 classes)...");
        System.out.println();
        long startTimes = System.nanoTime();
        kMeans();
        long endTime = System.nanoTime();
        clusters.add(c1);
        clusters.add(c2);
        clusters.add(c3);
        clusters.add(c4);
        clusters.add(c5);

        System.out.println("Cluster 1 Size: " + c1.size());
        System.out.println("Cluster 2 Size: " + c2.size());
        System.out.println("Cluster 3 Size: " + c3.size());
        System.out.println("Cluster 4 Size: " + c4.size());
        System.out.println("Cluster 5 Size: " + c5.size());
        System.out.println();
        System.out.println("Writing Cluster Summary and Evaluation files...");
        writeResults();
        long finalTime = endTime - startTimes;
        System.out.println("<------------------------- End of program ------------------------->");
        System.out.println();
        System.out.println("k-means program time: " + TimeUnit.NANOSECONDS.toMillis(finalTime) + "ms");
    }
}
