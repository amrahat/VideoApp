package net.optimusbs.videoapp.UtilityClasses;

import java.sql.Timestamp;

/**
 * Created by AMRahat on 3/13/2017.
 */

public class UtilsMethod {
    public static Long getCurrentTimeStamp(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime();
    }
}
