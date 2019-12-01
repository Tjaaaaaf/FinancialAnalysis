package Interfaces;

import Domein.Business;
import Enums.PropertyName;
import java.util.Map;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public interface IDocumentWrapper {

    String getName();

    int getYear();

    Business getBusiness();

    SimpleStringProperty getNameProperty();

    SimpleBooleanProperty getSelectedProperty();

    Map<PropertyName, String> getPropertiesMap();

}
