package com.stomas.mymqtt;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
//Librerias de MQTT y Formulario
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {
    //Variables de la conexion MQTT
    private static String mqttHost = "tcp://ummlando.cloud.shiftr.io:1883"; //Ip del Servidor MQTT
    private static String IdUsuario = "UMMlando"; //Nombre del dispositivo que se conectará

    private static String Topico = "Mensaje"; //Topico al que se suscribirá
    private static String User = "ummlando"; //Usuario
    private static String Pass = "sGYXcCN54ssJUdxx"; //Contraseña o Token

    //Variable que se utilizará pa imprimir los datos del sensor
    private TextView textView;
    private EditText editTextMessage;
    private Button botonEnvio;

    //Libreria MQTT
    private MqttClient mqttClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Enlace de la variable del ID que esta en el activity main donde imprimiremos los datos
        textView = findViewById(R.id.textView);
        editTextMessage =findViewById(R.id.txtMensaje);
        botonEnvio = findViewById(R.id.botonEnvioMensaje);
        try {
            //Creacion de un cliente MQTT
            mqttClient = new MqttClient(mqttHost, IdUsuario,null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());
            //Conexion al servidor MQTT
            mqttClient.connect(options);
            //si se conecta imprimira un mensaje de MQTT
            Toast.makeText(this,"Aplicación conectada al servidor MQTT", Toast.LENGTH_SHORT).show();
            //Manejo de entraga de datos y pérdida de conexión
            mqttClient.setCallback(new MqttCallback() {
                //Metodo en caso de que la conexion se pierda
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT","Conexión perdida");
                }
                //Metodo pa enviar el mensaje a MQTT
                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    runOnUiThread(() -> textView.setText(payload));
                }
                //Metodo para verificar si el envio fue exitoso
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "Entrega completa");
                }
            });
        }catch (MqttException e){
            e.printStackTrace();
        }
        //al dar click en el button enviará el mensaje del topico
        botonEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obterner el mensaje ingresado por el usuario
                String mensaje = editTextMessage.getText().toString();
                try {
                    //verifica si la conexion MQTT está activa
                    if (mqttClient != null && mqttClient.isConnected()){
                        //publicar el mensaje en el topico especificado
                        mqttClient.publish(Topico, mensaje.getBytes(),0,false);
                        //Mostrar en mensaje enviado en el TextView
                        textView.append("\n -"+ mensaje);
                        Toast.makeText(MainActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "Error: no se puedo enviar el mensaje. La conexión MQTT no está activa", Toast.LENGTH_SHORT).show();
                    }
                }catch (MqttException e){
                    e.printStackTrace();
                }
            }
        });
    }
}