package pagatodo.com.segundapantallamix;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class Servidor extends Activity{

    private final String TAG = "Charlie";

    private TextView tvEstado;
    private Button btnStartService;

    /** The BluetoothAdapter is the gateway to all bluetooth functions **/
    protected BluetoothAdapter bluetoothAdapter = null;

    /** We will write our message to the socket **/
    protected BluetoothSocket socket = null;

    /** The Bluetooth is an external device, which will receive our message **/
    BluetoothDevice blueToothDevice = null;

    private boolean flagConnEstablecida = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servidor);

        tvEstado = (TextView) findViewById(R.id.tv_estado);
        btnStartService = (Button) findViewById(R.id.btn_start_service);
    }

    public void btnStartServiceClicked(View v){
        btnStartService.setEnabled(false);
        msg("Se inicia el servicio");
        new AcceptAsyncTask(this).execute();
    }


    public void msg(String cad){
        Log.d(TAG,cad);
        Toast.makeText(this,cad ,Toast.LENGTH_SHORT).show();
        tvEstado.setText(cad);
    }

}

class AcceptAsyncTask extends AsyncTask<Void, Void, Integer>{
    private final String TAG = "Charlie";
    private final BluetoothServerSocket mmServerSocket;
    private  BluetoothAdapter mBluetoothAdapter = null;
    Servidor actServidor;
    private boolean bConExitosa = false;

    public AcceptAsyncTask(Servidor actServidor) {
        this.actServidor = actServidor;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(Constantes.getBluetoothServerName(), Constantes.getAppUUID());
        } catch (IOException e) {
            Log.d(TAG,"error e: "+e.toString());
        }
        mmServerSocket = tmp;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        int cont = 0;
        while (true && cont++<990000) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.d(TAG,"error: "+e.toString());
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                //manageConnectedSocket(socket);
                Log.d(TAG,"Conexion exitosa.. tenemos el socket..");
                bConExitosa = true;
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"error al cerrar el socket..");
                }
                break;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        actServidor.msg("hilo terminado., conexion exitosa: "+bConExitosa);
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
