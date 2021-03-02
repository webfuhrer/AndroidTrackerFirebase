package com.example.tracker;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AccesoFirebase {
    private static FirebaseDatabase database;
    public static void grabarRuta(Ruta r)
    {
        DatabaseReference myRef = database.getReference("rutas");
        myRef.push().setValue(r);
        //myRef.setValue("Hello, World!");
    }
    public static void pedirRutasFirebase(final IRecuperarDatos callback)
    {

        database = FirebaseDatabase.getInstance("https://tracker-5c089-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("rutas");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Ruta> rutas=new ArrayList();
                Iterable<DataSnapshot> datos=dataSnapshot.getChildren();
                while(datos.iterator().hasNext())
                {
                    DataSnapshot d=datos.iterator().next();
                    Ruta r=d.getValue(Ruta.class);
                    rutas.add(r);

                }
                callback.recuperarRutas(rutas);
                //Ya tengo el ArrayList releno, así que se lo pasaré a la clase principal
                //para rellenar el ArrayList de rutas con el que relleno el spinner
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface IRecuperarDatos
    {
        public void recuperarRutas(ArrayList<Ruta> lista_rutas);
    }
}
