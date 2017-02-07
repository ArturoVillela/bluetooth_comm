package pagatodo.com.segundapantallamix;

import java.util.UUID;

/**
 * Created by avillela on 01/02/2017.
 */

public class Constantes {

    private static final String NAME = "BlPruebaCharlito";
    private static final UUID MY_UUID = UUID.fromString("de4a24db-4fce-4f0b-a454-41c711f1dce0");

    public static String getBluetoothServerName(){ return NAME;}

    public static UUID getAppUUID(){return MY_UUID;}
}
