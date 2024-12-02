package com.stomas.mymqtt;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityFirebase extends AppCompatActivity {

    //Defino variables

    private EditText txtNumeroEquipo, txtNombreEquipo, txtNombreDT, txtApellidoDT;
    private ListView lista;
    private Spinner spIntegrantes;

    //Variable para llamar a la conexin de firestore
    private FirebaseFirestore db;
    //Datos del Spinner
    String[] Integrantes = {"1-2", "3-4", "5-6", "7-8", "9-10"};

    @Override
    protected void onCreate(Bundle savedIntanceState) {
        super.onCreate(savedIntanceState);
        setContentView(R.layout.activity_firebase);
        //traemos al metodo que carga datos
        CargarListaFirestore();

        //Inicializo la base de datos de firestore
        db = FirebaseFirestore.getInstance();

        //Uno las variables con los Id del XML
        txtNumeroEquipo = findViewById(R.id.txtNumeroEquipo);
        txtNombreEquipo = findViewById(R.id.txtNombreEquipo);
        txtNombreDT = findViewById(R.id.txtNombreDT);
        txtApellidoDT = findViewById(R.id.txtApellidoDT);
        spIntegrantes = findViewById(R.id.spIntegrantes);
        lista = findViewById(R.id.lista);

        //poblamos el spinner con los datos de equipos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Integrantes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIntegrantes.setAdapter(adapter);
    }

    //metodo para enviar datos a Firestore
    public void enviarDatosFirestore(View view) {
        //obtenermos los campos de datos ingresados en el formulario
        String numero = txtNumeroEquipo.getText().toString();
        String nombre = txtNombreEquipo.getText().toString();
        String nombreDT = txtNombreDT.getText().toString();
        String apellidoDT = txtApellidoDT.getText().toString();
        String cantidadIntegrantes = spIntegrantes.getSelectedItem().toString();

        //Mapeamos los datos para enviarlos
        Map<String, Object> equipo = new HashMap<>();
        equipo.put("Numero", numero);
        equipo.put("nombre", nombre);
        equipo.put("nombreDT", nombreDT);
        equipo.put("apellidoDT", apellidoDT);
        equipo.put("cantidadIntegrantes", cantidadIntegrantes);
        //Enviamos los datos a la coleccion o "tabla" de firestore
        db.collection("equipos").document(numero).set(equipo).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Informacion Agregada ", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al Ingresar datos", Toast.LENGTH_SHORT).show();
                });
    }

    //boton de evento para cargar lista de datos
    public void CargarLista(View view) {
        CargarListaFirestore();
    }

    //Metodo para cargar Lista
    public void CargarListaFirestore() {
        //Obtenemos la conexion de Fiorestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //hacemos una consulta a la coleccion "Tabla"
        db.collection("equipo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //Verificamos si la consulta fue exitosa
                if (task.isSuccessful()) {
                    List<String> listaEquipos = new ArrayList<>();
                    //Recorro el arraylist de mascotas para mostrarlas en la listView
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String linea = "||" + document.getString("numero") +
                                "||" + document.getString("nombre") +
                                "||" + document.getString("nombreDT") +
                                "||" + document.getString("apellidoDT");
                        listaEquipos.add(linea);
                        //Creo un ArrayAdapter para mostrar los datos en la ListView
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, listaEquipos);
                        lista.setAdapter(adapter);
                    }
                }
            }
        });
    }
}

