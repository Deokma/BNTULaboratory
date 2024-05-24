package by.bntu.laboratory.models;

import java.util.Base64;
import java.util.Date;

public interface Publications {
    Long id = null;
    String title = null;
    String description = null;
    String content = null;
    Boolean visible = null;
    byte[] cover = new byte[0];
    Date date = null;

}
