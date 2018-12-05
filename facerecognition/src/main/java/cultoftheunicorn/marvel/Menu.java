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

public class Menu extends AppCompatActivity {

    public static final int SIGNATURE_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Button btnpersonal = (Button) findViewById(R.id.btnpersonal);
        Button btnregistrapersonal = (Button) findViewById(R.id.btnregistrapersonal);

        btnpersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.putExtra("usuarioid", usuarioid);
                //intent.putExtra("nombreusuario", nombreusuario);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        /*btnregistrapersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BitacoraServicio.class);
                intent.putExtra("usuarioid", usuarioid);
                intent.putExtra("nombreusuario", nombreusuario);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });*/
    }

}
