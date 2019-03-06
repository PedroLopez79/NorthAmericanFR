package cultoftheunicorn.marvel;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.opencv.cultoftheunicorn.marvel.R;

import cultoftheunicorn.marvel.modelo.CheckInCheckOut;

public class CheckinCheckout extends AppCompatActivity {

    String IDEMPLEADO = "";
    String IDPROYECT  = "";
    String NOMBREEMPLEADO = "";

    public static final int SIGNATURE_ACTIVITY = 1;

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
