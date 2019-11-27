package fr.exemple.localisation;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager lm;
    private TextView latitude;
    private TextView reponseLatitude;
    private TextView longitude;
    private TextView reponseLongitude;
    private Button btnacq;
    private Button btnSave;
    private Button btnreset;
    private Button btnAProposDe;
    private Button btnExit;
    private int code=1;
    private static final String[] Localisation= {Manifest.permission.ACCESS_COARSE_LOCATION
            ,Manifest.permission.ACCESS_FINE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init()
    {
        latitude= findViewById(R.id.latitude);
        longitude= findViewById(R.id.longitude);
        reponseLatitude= findViewById(R.id.repLatitude);
        reponseLongitude= findViewById(R.id.repLongitude);
        btnacq= findViewById(R.id.button);
        btnSave= findViewById(R.id.buttonSave);
        btnreset= findViewById(R.id.buttonReset);
        btnAProposDe= findViewById(R.id.buttonAProposDe);
        btnExit= findViewById(R.id.buttonExit);
        checkWriteStockage();
        permissionsLocalisation();
    }

    //méthode vérification permission stockage
    private void checkWriteStockage()
    {
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
        {
           Toast.makeText(MainActivity.this,"Vous avez déja accordé cette permission",Toast.LENGTH_SHORT).show();
        }
        else {
            requestStorage();
        }
    }

    // méthode permission stockage
    private void requestStorage()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE));
        {
            new AlertDialog.Builder(MainActivity.this)
                  .setTitle("Titre")
                   .setMessage("Cette permission est nécessaire")
                  .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                              ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},code);
                      }
                  })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==code )
        {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this,"Permission Autorisé",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this,"Permission non autorisée",Toast.LENGTH_SHORT).show();
            }
        }
    }

    // méthode permission Localisation
private void permissionsLocalisation()
{
    int permissionCoarse=ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION);
    int permissionFine=ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);
    if(permissionCoarse!=PackageManager.PERMISSION_GRANTED && permissionFine!=PackageManager.PERMISSION_GRANTED)
    {
        ActivityCompat.requestPermissions(MainActivity.this,
                Localisation,1);
    }
}
    @Override
    // @SuppressWarnings("MissingPermission")
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0,this);
        }
        if(lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,10000,0,this);
        }
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10000,0,this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (lm!=null)
        {
            lm.removeUpdates(this);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(final Location location) {
        final double rlatitude=  location.getLatitude();
        final double rlongitude= location.getLongitude();


        Toast.makeText(this,"New Data",Toast.LENGTH_LONG).show();

        btnacq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reponseLatitude.setText("Latitude: N  "+rlatitude);
                reponseLongitude.setText("Longitude: E "+rlongitude);
            }
        });

        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reponseLatitude.setText("ICI REP LATITUDE");
                reponseLongitude.setText("ICI REP LONGITUDE");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                try {
                    // création fichier sauvegarde dans le répertoire Download du stockage interne
                    File extStore = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    String fileName="Localisation.csv";
                    String path = extStore.getAbsolutePath() + "/" + fileName;
                    String data = reponseLatitude.getText().toString()+" "+reponseLongitude.getText().toString();
                    File myFile = new File(path);
                    FileWriter fw = new FileWriter(myFile,true);
                    fw.write(data);
                    fw.write("\r\n");
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btnAProposDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // intent vers autre activité
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);

            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}