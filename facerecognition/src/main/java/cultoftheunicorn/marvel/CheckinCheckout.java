package cultoftheunicorn.marvel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.opencv.cultoftheunicorn.marvel.R;

import java.util.List;

import cultoftheunicorn.marvel.modelo.CheckInCheckOut;
import cultoftheunicorn.marvel.modelo.Empleado;

public class CheckinCheckout extends AppCompatActivity {

    String IDEMPLEADO = "";
    String IDPROYECT  = "";
    String NOMBREEMPLEADO = "";

    public static final int SIGNATURE_ACTIVITY = 1;
    private List<CheckinCheckout> mListCheckInCheckOut;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode) {

            //----------------OBTENER RESULTCODE PARA SABER SI ES REGISTRAR CHECKIN O CHECKOUT----//
            case -100:
                super.onActivityResult(requestCode, resultCode, data);
                String NOMBREEMPLEADO = data.getStringExtra("NOMBREEMPLEADO");
                String FECHA = data.getStringExtra("FECHA");
                String IDPROYECTO = data.getStringExtra("IDPROYECTO");
                String CHECK = data.getStringExtra("CHECK");

                Intent resultIntent = new Intent();
                resultIntent.putExtra("NOMBREEMPLEADO", NOMBREEMPLEADO);
                resultIntent.putExtra("IDEMPLEADO", IDEMPLEADO);
                resultIntent.putExtra("FECHA", String.valueOf(FECHA));
                resultIntent.putExtra("IDPROYECTO", IDPROYECTO);
                resultIntent.putExtra("CHECK", CHECK);
                setResult(-100, resultIntent);
                finish();            // to close this activity
                break;
            //------------------------------------------------------------------------------------//
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_checkout);

        Button CheckInButton = (Button) findViewById(R.id.CheckInButton);
        Button CheckOutButton = (Button) findViewById(R.id.CheckOutButton);

        Intent intent2 = getIntent();
        IDPROYECT = intent2.getStringExtra("IDPROYECTO");
        IDEMPLEADO= intent2.getStringExtra("IDEMPLEADO");
        NOMBREEMPLEADO= intent2.getStringExtra("NOMBREEMPLEADO");

        CheckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CheckinCheckout.this, Recognize.class);
                intent.putExtra("IDPROYECTO", IDPROYECT);
                intent.putExtra("IDEMPLEADO", IDEMPLEADO);
                intent.putExtra("NOMBREEMPLEADO", NOMBREEMPLEADO);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        CheckOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckinCheckout.this, Recognize.class);
                intent.putExtra("IDPROYECTO", IDPROYECT);
                intent.putExtra("IDEMPLEADO", IDEMPLEADO);
                intent.putExtra("NOMBREEMPLEADO", NOMBREEMPLEADO);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

    }

}
