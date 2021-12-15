import java.util.Set;

public class Posting {
    private int ID;
    private int docFreq;
    private Set<Integer> pos;

    public Posting(int ID, int docFreq, Set<Integer> pos) {
        this.ID = ID;
        this.docFreq = docFreq;
        this.pos = pos;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int num : pos)
            res.append(num).append(" ");
        return ID + " " + docFreq + " " + res;
    }
}
