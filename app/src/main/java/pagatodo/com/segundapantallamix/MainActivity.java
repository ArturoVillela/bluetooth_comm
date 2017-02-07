package pagatodo.com.segundapantallamix;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hacemos desde el inicio nuestro device como discoverable..
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 400);
        startActivity(discoverableIntent);
    }

    public void srvBtnClicked(View v){
        Intent intent = new Intent(MainActivity.this,Servidor.class);
        startActivity(intent);
    }

    public void cltBtnClicked(View v){
        Intent intent = new Intent(MainActivity.this,Cliente.class);
        startActivity(intent);
    }




}
