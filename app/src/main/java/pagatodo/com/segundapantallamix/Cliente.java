package pagatodo.com.segundapantallamix;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class Cliente extends Activity {

    private final String TAG = "Charlie";
    private TextView tvInfo;
    private Button btnFindDevices;
    private Button btnSync;

    private BluetoothAdapter mBluetoothAdapter;

    private final String MAC_ADDRESS = "A8:9F:BA:7F:F5:5C";
    private BluetoothDevice btDevice;


    //region Broadcasters

    // Create a BroadcastReceiver for ACTION_FOUND
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            msg("entra a broadcastReceiver.. action:"+action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String cadDevice = btDevice.getName() + "\n" + btDevice.getAddress();
                msg("Se encontro device: "+cadDevice);
                if (btDevice.getAddress().equals(MAC_ADDRESS)) {
                    msg("Galaxy tab 4 Encontrado");
                    mBluetoothAdapter.cancelDiscovery();
                    btnFindDevices.setEnabled(false);
                    btnSync.setEnabled(true);
                }
            }else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)){
                msg("action pairing request..");
            }else{
                msg("se encontro una accion e intent no cachada.... action:"+action);
            }
        }
    };
    //Create a Broadcaster for Paired device.
    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    msg("Paired..  sigue mandar comunicar esta cosa..");
                    msg("Paired");
                    Log.d(TAG,"Paired done..  sigue la comunicacion...");
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    msg("Unpaired..  detener comunicacion de esta cosa");
                    Log.d(TAG,"Paired stoped..  ");
                }
            }
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        tvInfo = (TextView) findViewById(R.id.tv_msg);
        btnFindDevices = (Button) findViewById(R.id.btn_find);
        btnSync = (Button) findViewById(R.id.btn_sync);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter;
        filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver , filter);

        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mPairReceiver, intent);
    }

    public void btnFindClicked(View v){
        if (mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.startDiscovery();
            msg("se inicia discovery of new devices..");
        }else{
            msg("Error.  Bluetooth unavaliable");
        }
    }

    public void btnSyncClicked(View v){
        String cad = "Inicia Sync";
        msg(cad);
        new ConnectThread(btDevice, mBluetoothAdapter).start();
    }

    private void msg(String cad){
        Toast.makeText(this,cad,Toast.LENGTH_SHORT).show();
        Log.d(TAG,cad);
        tvInfo.setText(cad);
    }
}


class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final String TAG = "Charlie";
    private BluetoothAdapter mBluetoothAdapter;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter mBluetoothAdapter) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        this.mBluetoothAdapter = mBluetoothAdapter;
        BluetoothSocket tmp = null;
        mmDevice = device;
        Log.d(TAG,"entra al constructor del cliente...");
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(Constantes.getAppUUID());
        } catch (IOException e) {
            Log.d(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        //da cancel discovery pero nunca lo inicialice..
        mBluetoothAdapter.cancelDiscovery();
        Log.d(TAG,"entra al run en el cliente...");
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            Log.d(TAG,"Si se ve esto.. se conecto exitosamente..");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        Log.d(TAG,"conexion exitosa de parte del cliente..");
        //manageMyConnectedSocket(mmSocket);
        cancel();
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}