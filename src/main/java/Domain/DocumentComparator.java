package Domain;

import java.util.Comparator;

public class DocumentComparator implements Comparator<DocumentWrapper> {

    @Override
    public int compare(DocumentWrapper document1, DocumentWrapper document2) {
        if (document2.getYear() < document1.getYear()) {
            return -1;
        }
        if (document2.getYear() > document1.getYear()) {
            return 1;
        }
        if (document2.getYear() == document1.getYear()) {
            return 0;
        }
        return 0;
    }

}
