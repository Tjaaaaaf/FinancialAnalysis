package Util;

public interface Exportable<T> {

    String excelFormat(T object);

    String excelheaders();
}
