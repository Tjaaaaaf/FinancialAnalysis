package Util;

import Models.DocumentWrapper;
import java.util.Comparator;

public class DocumentWrapperYearComparator implements Comparator<DocumentWrapper> {

    @Override
    public int compare(DocumentWrapper document1, DocumentWrapper document2) {
        return document1.getYear() - document2.getYear();
    }
}
