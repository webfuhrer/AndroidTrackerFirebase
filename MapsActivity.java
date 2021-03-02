package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.security.Permissions;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AccesoFirebase.IRecuperarDatos {

    private GoogleMap mMap;
    private EditText et_nombre_ruta;
    private Spinner spn_rutas;
    private Button btn_parar_grabar;
    private Button btn_mostrar;
    private ArrayList<Ruta> rutas=new ArrayList();
    private ArrayList<Punto> puntos=new ArrayList();
    PolylineOptions polyline_options=new PolylineOptions();
    private boolean estoy_grabando = false;
    LocationManager loc_manager;
    LocationListener oyente_localizacion;
    AccesoFirebase.IRecuperarDatos interfaz_recuperdatos=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AccesoFirebase.pedirRutasFirebase(interfaz_recuperdatos);
        inicializarCampos();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void inicializarCampos() {
        et_nombre_ruta = findViewById(R.id.et_nombre_ruta);
        btn_parar_grabar = findViewById(R.id.btn_grabar_parar);
        spn_rutas = findViewById(R.id.spn_rutas);
        btn_mostrar = findViewById(R.id.btn_mostrar);
        View.OnClickListener oyente_parar_grabar = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //Lo que haya que hacer en el parar/frabar
                if (estoy_grabando) {
                    //Quiero parar
                    loc_manager.removeUpdates(oyente_localizacion);
                    //Cambio el texto del botón (debe poner grabar)
                    btn_parar_grabar.setText(R.string.grabar);
                    //Llamar al método que graba la ruta
                    grabarRutaEnFirebase();
                } else {
                    //Quiero grabar
                    chekearPermiso();
                    //Cambio el texto del botón (debe poner parar)
                    btn_parar_grabar.setText(R.string.parar);
                }
                estoy_grabando = !estoy_grabando;
            }
        };
        View.OnClickListener oyente_mostrar = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lo que haya que hacer en el mostrar
                mostrarRutaSeleccionada();
            }
        };
        btn_mostrar.setOnClickListener(oyente_mostrar);
        btn_parar_grabar.setOnClickListener(oyente_parar_grabar);
        oyente_localizacion = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Dar gritos y avisar a tó el mundo de que unn uevo punto ha llegado a la Tierra
                //Allellujah!!
                Punto p=new Punto(location.getLatitude(), location.getLongitude());
                puntos.add(p);
                //Añado el punto a la PolyLine
                insertarPunto( p);
                //Log.d("PUNTO", location.getLatitude() + ", " + location.getLongitude());
            }
        };
    }

    private void mostrarRutaSeleccionada() {
        Ruta ruta_seleccionada=(Ruta)spn_rutas.getSelectedItem();
        mMap.clear();
        polyline_options=new PolylineOptions();
        ArrayList<Punto> lista_puntos=ruta_seleccionada.getLista_puntos();
        for(Punto p: lista_puntos)
        {
            insertarPunto(p);
        }

    }

    private void insertarPunto(Punto p)
    {
        LatLng x=new LatLng(p.getLat(), p.getLng());
        polyline_options.add(x);
        mMap.addPolyline(polyline_options);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    /******************************************************/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void chekearPermiso() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//Si no tengo permiso, lo pido
            //Si no tengo permiso lo pido
            String[] permisos = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permisos, 99);
        } else {
            //Pido localizaciones directamente
            pedirLocalizaciones();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 99) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pedirLocalizaciones();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void pedirLocalizaciones() {
        loc_manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Ya he pedido permiso, esto es un "trámite"
            return;
        }
        loc_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, oyente_localizacion);
    }
    private void grabarRutaEnFirebase()
    {
        //Aquí debo obtener el nombre de la ruta y los puntos
        String nombre_ruta=et_nombre_ruta.getText().toString();
        Ruta ruta=new Ruta(nombre_ruta, puntos);
        AccesoFirebase.grabarRuta(ruta);
    }

    @Override
    public void recuperarRutas(ArrayList<Ruta> lista_rutas) {
        //1-Relleno mi Spinner con los nombres
        rutas=lista_rutas;//rutas es la variable global
        // que necesito patra cuando alguien quiera ver una ruta
        rellenarSpinner();
        Log.d("RUTA desde main", lista_rutas.toString());
    }

    private void rellenarSpinner() {
        ArrayAdapter<Ruta> adaptador_rutas=new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, rutas);
        spn_rutas.setAdapter(adaptador_rutas);
    }
}
