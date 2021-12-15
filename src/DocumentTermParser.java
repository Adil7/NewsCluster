import java.util.Set;
import java.util.TreeSet;

public class DocumentTermParser {
    private Document d;
    private boolean stemmed;

    public DocumentTermParser(boolean stemmed) {
        this.stemmed = stemmed;
    }

    public void setDoc(Document d) {
        this.d = d;
    }

    public boolean isStemmed() {
        return stemmed;
    }

    public void setStemmed(boolean stemmed) {
        this.stemmed = stemmed;
    }

    public Set<String> getWords(){
        Set<String> result = new TreeSet<>();
        String title = d.getTitle();
        String docAbstract = d.getDocAbstract();
        if (title != null){
            String[] titleTerms = title.split("\\W+");
            for (String s : titleTerms){
                if (s.matches("\\b\\w*([A-Za-z])\\w*\\b")) {
                    if (stemmed) {
                        Stemmer stemmer = new Stemmer();
                        stemmer.add(s.toLowerCase().toCharArray(), s.length());
                        stemmer.stem();
                        result.add(stemmer.toString());
                    } else
                        result.add(s.toLowerCase());
                }
            }
        }
        if (docAbstract != null){
            String[] docAbstractTerms = docAbstract.split("\\W+");
            for (String s : docAbstractTerms){
                if (s.matches("\\b\\w*([A-Za-z])\\w*\\b")) {
                    if (stemmed) {
                        Stemmer stemmer = new Stemmer();
                        stemmer.add(s.toLowerCase().toCharArray(), s.length());
                        stemmer.stem();
                        result.add(stemmer.toString());
                    } else
                        result.add(s.toLowerCase());
                }
            }
        }
        return result;
    }

    public int getTermFrequency(String term){
        int freq = 0;
        String title = d.getTitle();
        String docAbstract = d.getDocAbstract();
        if (title != null){
            String[] titleTerms = title.split("\\W+");
            for (int i = 0; i < titleTerms.length; i++) {
                if (stemmed){
                    Stemmer stemmer = new Stemmer();
                    stemmer.add(titleTerms[i].toLowerCase().toCharArray(),titleTerms[i].length());
                    stemmer.stem();
                    if (stemmer.toString().equalsIgnoreCase(term)) freq++;
                } else
                    if (titleTerms[i].equalsIgnoreCase(term)) freq++;
            }
        }
        if (docAbstract != null){
            String[] docAbstractTerms = docAbstract.split("\\W+");
            for (int i = 0; i < docAbstractTerms.length; i++) {
                if (stemmed){
                    Stemmer stemmer = new Stemmer();
                    stemmer.add(docAbstractTerms[i].toLowerCase().toCharArray(),docAbstractTerms[i].length());
                    stemmer.stem();
                    if (stemmer.toString().equalsIgnoreCase(term)) freq++;
                } else
                if (docAbstractTerms[i].equalsIgnoreCase(term)) freq++;
            }
        }
        return freq;
    }

    public Set<Integer> getPositionsOfTerm (String term){
        Set<Integer> positions = new TreeSet<>();
        String s1 = "", s2 = "";
        if (d.getTitle() != null)
            s1 = d.getTitle();
        if (d.getDocAbstract() != null)
            s2 = d.getDocAbstract();
        String docText = s1 + " " + s2;
        String[] words = docText.split("\\W+");
        for (int i = 0; i < words.length; i++){
            if (stemmed){
                Stemmer stemmer = new Stemmer();
                stemmer.add(words[i].toLowerCase().toCharArray(), words[i].length());
                stemmer.stem();
                if (stemmer.toString().equalsIgnoreCase(term)) positions.add(i);
            } else
                if (words[i].equalsIgnoreCase(term)) positions.add(i);
        }
        return positions;
    }

    public static void main(String[] args) {

        String s = "important";

        Stemmer stemmer = new Stemmer();
        stemmer.add(s.toLowerCase().toCharArray(), s.length());
        stemmer.stem();
        System.out.println(stemmer.toString());

    }
}
