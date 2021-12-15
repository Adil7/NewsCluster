import java.io.*;
import java.util.*;

public class InvertedIndex {
    private Map<Integer,Document> documents ;
    private Set<String> dictionary;
    private Set<Integer> pos;
    private Set<String> stopWords = new TreeSet<>();
    private ParseArticles parseArticles;
    private DocumentTermParser documentTermParser;
    private boolean isStopWordsOn;
    private HashMap<String, List<Posting>> postingsMap = new HashMap<>();
    private Map<String,Integer> df = new TreeMap<>();

    public Set<String> getStopWords() {
        return stopWords;
    }

    public void setStopWords(Set<String> stopWords) {
        this.stopWords = stopWords;
    }

    public Map<Integer, Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<Integer, Document> documents) {
        this.documents = documents;
    }

    public Set<String> getDictionary() {
        return dictionary;
    }

    public void setDictionary(Set<String> dictionary) {
        this.dictionary = dictionary;
    }

    public Set<Integer> getPos() {
        return pos;
    }

    public void setPos(Set<Integer> pos) {
        this.pos = pos;
    }

    public DocumentTermParser getDocumentTermParser() {
        return documentTermParser;
    }

    public void setDocumentTermParser(DocumentTermParser documentTermParser) {
        this.documentTermParser = documentTermParser;
    }

    public boolean isStopWordsOn() {
        return isStopWordsOn;
    }

    public void setStopWordsOn(boolean stopWordsOn) {
        isStopWordsOn = stopWordsOn;
    }

    public HashMap<String, List<Posting>> getPostingsMap() {
        return postingsMap;
    }

    public void setPostingsMap(HashMap<String, List<Posting>> postingsMap) {
        this.postingsMap = postingsMap;
    }

    public Map<String, Integer> getDf() {
        return df;
    }

    public void setDf(Map<String, Integer> df) {
        this.df = df;
    }

    public InvertedIndex(boolean isStopWordsOn, boolean isStemmerOn){
        this.isStopWordsOn = isStopWordsOn;
        if (isStopWordsOn) stopWords = makeStopWords();
        parseArticles = new ParseArticles();
        documentTermParser = new DocumentTermParser(isStemmerOn);
        dictionary = new TreeSet<>();
        documents = parseArticles.createTextDocuments();
    }

    private Set<String> makeStopWords() {
        Set<String> res = new HashSet<>();
        try {
            File f = new File("stopwords.txt");
            BufferedReader b = new BufferedReader(new FileReader(f));
            String line = "";
            while ((line = b.readLine()) != null){
                res.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void makeDictionary() {
        for (int id : documents.keySet()){
            Document curDoc = documents.get(id);
            documentTermParser.setDoc(curDoc);
            for (String word : documentTermParser.getWords()){
                if (isStopWordsOn && stopWords.contains(word))
                    continue;
                else
                    dictionary.add(word);
                    int termFrequency = documentTermParser.getTermFrequency(word);
                    curDoc.setTermFreq(word, termFrequency);
                    if (df != null && df.containsKey(word)){
                        df.put(word, df.get(word) + 1);
                    }
                    else if (df != null && !df.containsKey(word))
                        df.put(word, 1);
            }
        }
        setDf(df);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("dictionary.txt"));
            for (Map.Entry<String,Integer> entry : df.entrySet()){
                String term = entry.getKey();
                int numOfTimesFound = entry.getValue();
                writer.write(term + " " + numOfTimesFound + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Total # of documents: " + documents.size());
        System.out.println("Total # of terms    : " + dictionary.size());
    }

    public void makePosting() {
        for (Map.Entry<Integer, Document> entry : documents.entrySet()){
            int id = entry.getKey();
            Document curDoc = entry.getValue();
            documentTermParser.setDoc(curDoc);
            for (String word : documentTermParser.getWords()){
                if (!isStopWordsOn || !stopWords.contains(word)) {
                    List<Posting> postings = new ArrayList<>();
                    pos = documentTermParser.getPositionsOfTerm(word);
                    if (postingsMap != null && postingsMap.containsKey(word)) {
                        postingsMap.get(word).add(new Posting(curDoc.getId(), curDoc.getTermFreq(word), pos));
                    } else {
                        postings.add(new Posting(curDoc.getId(), curDoc.getTermFreq(word), pos));
                        postingsMap.put(word, postings);
                    }
                }
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("posting.txt"));
            for (Map.Entry<String,List<Posting>> post  : postingsMap.entrySet()){
                writer.write(post.getKey() + " ");
                List<Posting> postList = post.getValue();
                for (Posting postObj : postList){
                    writer.write(postObj.toString());
                }
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        InvertedIndex inv = new InvertedIndex(true,true);
//        inv.makeDictionary();
//        inv.makePosting();


    }

}
