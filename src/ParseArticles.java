import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ParseArticles {
    private Map<Integer, Document> results = new HashMap<>();

    public Map<Integer,Document> createTextDocuments() {
        pushArticles("/Users/adil1/Documents/F2020Courses/CPS842/A3/athletics", "athletics");
        pushArticles("/Users/adil1/Documents/F2020Courses/CPS842/A3/cricket", "cricket");
        pushArticles("/Users/adil1/Documents/F2020Courses/CPS842/A3/football", "football");
        pushArticles("/Users/adil1/Documents/F2020Courses/CPS842/A3/rugby", "rugby");
        pushArticles("/Users/adil1/Documents/F2020Courses/CPS842/A3/tennis", "tennis");

        return results;
    }

    public void pushArticles(String filename, String classLabel){
        int docNum = 0;
        File directoryPath = new File(filename);
        File[] files = directoryPath.listFiles();
        for (File f : files) {
            try {
                BufferedReader b = new BufferedReader(new FileReader(f));
                String line = b.readLine();
                // set doc id
                docNum = getDocNum(f.getName(), classLabel);
                Document doc = new Document(docNum);
                results.put(docNum, doc);
                // set doc title
                doc.setTitle(line.trim());
                // set abstract
                StringBuilder abstractContent = new StringBuilder();
                line = b.readLine();
                while (line != null) {
                    if (line.length() > 0) abstractContent.append(line);
                    line = b.readLine();
                }
                doc = results.get(docNum);
                doc.setDocAbstract(abstractContent.toString().trim());
                doc.setLabel(classLabel);
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getDocNum(String line, String classLabel) {
        String[] idLine = line.split("\\.");
        if (classLabel.equals("athletics"))
            return 1000 + Integer.parseInt(idLine[0]);
        else if (classLabel.equals("cricket"))
            return 2000 + Integer.parseInt(idLine[0]);
        else if (classLabel.equals("football"))
            return 3000 + Integer.parseInt(idLine[0]);
        else if (classLabel.equals("rugby"))
            return 4000 + Integer.parseInt(idLine[0]);
        else if (classLabel.equals("tennis"))
            return 5000 + Integer.parseInt(idLine[0]);
        else
            return -1;
    }

    public static void main(String[] args)  {
//        Map<Integer,Document> map = new HashMap<>();
//        try {
//            map = createTextDocuments();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(map.get(255).getId());
//        System.out.println(map.get(255).getTitle());
//        System.out.println(map.get(255).getDocAbstract());
    }

}

