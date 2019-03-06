package cultoftheunicorn.marvel;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.cultoftheunicorn.marvel.R;

public class principalMenu extends AppCompatActivity {

    public static final int SIGNATURE_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_menu);

        Button administra = (Button) findViewById(R.id.btnadministra);
        Button campo = (Button) findViewById(R.id.btncampo);

        administra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(principalMenu.this, MainActivity2.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        campo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(principalMenu.this, personalProyecto2.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

    }

}
