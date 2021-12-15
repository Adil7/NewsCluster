import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Document {
    private int id;
    private String title;
    private String docAbstract;
    private String date;
    private String author;
    private String label;
    private Map<String,Integer> termFreq = new HashMap<>();
    private Map<String,Integer> docFreq = new HashMap<>();
    private Map<String, Double> weights = new HashMap<>();
    public Document() {
    }

    public Document(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int docId) {
        this.id = docId;
    }

    public String getLabel() {
        if (label == null) return "";
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        if (title == null) return "";
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocAbstract() {
        if (docAbstract == null) return "";
        return docAbstract;
    }

    public void setDocAbstract(String docAbstract) {
        this.docAbstract = docAbstract;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTermFreq(String term) {
        if (termFreq != null && termFreq.containsKey(term))
            return termFreq.get(term);
        else
            return 0;
    }

    public void setTermFreq(String term, int freq) {
        if (termFreq == null) {
            termFreq = new HashMap<>();
        }
        termFreq.put(term, freq);
    }

    public Set<String> getTerms(){
        if (termFreq != null)
            return termFreq.keySet();
        else return new HashSet<>();
    }

    public void setWeight(String word, double v) {
        weights.put(word, v);
    }

    public Double getWeightValue(String word){
        if (weights != null && weights.containsKey(word))
            return weights.get(word);
        else
            return 0.0;
    }
}
