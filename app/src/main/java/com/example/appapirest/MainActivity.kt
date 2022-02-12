package com.example.appapirest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        //Ocultar la barra de acciones
        val actionBar = supportActionBar
        actionBar?.hide()

        //  Ocultar la barra de progreso
        progressBar1.visibility= View.GONE

        //Cargar los datos del usuario
        val preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE)
        editText.setText(preferencias.getString("usuario", ""))
        editText2.setText(preferencias.getString("contrasena", ""))

        //Si hay datos activar recordar
        if (preferencias.getString("usuario", "")!="")
            checkBox.isChecked=true

        //si pulsamos en el boton de registrar

        button2.setOnClickListener {
            val intento2=Intent(this,Registrarme::class.java)
            startActivity(intento2)
        }

        //Si pulsamos en el boton de entrar

        button.setOnClickListener {
            progressBar1.visibility= View.VISIBLE;
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(this)
            val url: String = "https://incuse-hisses.000webhostapp.com/logina.php?usuario=${editText.text}&contrasena=${editText2.text}"

            //textView.text=url.toString()
            // Request a string response from the provided URL.

            val stringReq = StringRequest(Request.Method.GET, url,

                Response.Listener<String> { response ->
                    progressBar1.visibility= View.GONE;
                    var strResp = response.toString()
                    val jsonObj: JSONObject = JSONObject(strResp)
                    val jsonArray: JSONArray = jsonObj.getJSONArray("records")
                    if (jsonArray.length()==0)

                    {
                        Alerta("Usuarios","No esta registrado este usuario!")
                    }

                    else
                    {
                        if(checkBox.isChecked)
                        {
                            val editor = preferencias.edit()
                            editor.putString("usuario", editText.text.toString())
                            editor.putString("contrasena", editText2.text.toString())
                            editor.commit()
                        }else

                        {
                            val editor = preferencias.edit()
                            editor.putString("usuario", "")
                            editor.putString("contrasena", "")
                            editor.commit()
                        }

                        val intento1 = Intent(this, Inicio::class.java)
                        startActivity(intento1)

                    }

                },

                Response.ErrorListener {
                    progressBar1.visibility= View.GONE;
                    Alerta("Red","Problemas con el Internet")
                })
            queue.add(stringReq)
        }
    }

    fun Alerta(Titulo:String, Mensaje:String)
    {
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage(Mensaje)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(Titulo)
        // show alert dialog
        alert.show()
    }
}