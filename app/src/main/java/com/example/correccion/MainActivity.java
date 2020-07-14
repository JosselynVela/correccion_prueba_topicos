package com.example.correccion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.correccion.model.Compras;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private List<Compras> listCompra=new ArrayList<>();
    ArrayAdapter<Compras> arrayAdapterCompra;
    EditText list,detalle;
    ListView listV_Detalle;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

   Compras compraseleccionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main) ;

        list=findViewById(R.id.txtCompra);
        detalle=findViewById(R.id.txtdetalle);
        listV_Detalle=findViewById(R.id.tvdetalle);
        inicializarfirebase();
        listardatos();

        listV_Detalle.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                compraseleccionada = (Compras) parent.getItemAtPosition(position);
                list.setText(compraseleccionada.getCompra());
                detalle.setText(compraseleccionada.getDetalle());
            }
        });


        
    }

    private void listardatos() {
        databaseReference.child("Lista").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCompra.clear();
                for(DataSnapshot objSnapshot:snapshot.getChildren()){
                    Compras c=objSnapshot.getValue(Compras.class);
                    listCompra.add(c);
                    arrayAdapterCompra=new ArrayAdapter<Compras>(MainActivity.this,android.R.layout.simple_list_item_1,listCompra);
                    listV_Detalle.setAdapter(arrayAdapterCompra);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarfirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        String listas=list.getText().toString();
        String det=detalle.getText().toString();

        switch (item.getItemId()){
            case R.id.icon_add:{
                if(listas.equals("") || det.equals("")){
                    validacion();

                }
                else{
                    Compras c=new Compras();
                    c.setId(UUID.randomUUID().toString());
                    c.setCompra(listas);
                    c.setDetalle(det);
                    databaseReference.child("Lista").child(c.getId()).setValue(c);
                    Toast.makeText(this,"Agregar", Toast.LENGTH_LONG).show();
                    limpiar();

                }
                break;

            }
            case R.id.icon_save:{
                 Compras c=new Compras();
                 c.setId(compraseleccionada.getId());
                 c.setCompra(list.getText().toString().trim());
                 c.setDetalle(detalle.getText().toString().trim());
                 databaseReference.child("Lista").child(c.getId()).setValue(c);
                Toast.makeText(this,"Guardar", Toast.LENGTH_LONG).show();
                limpiar();
                break;
            }
            case R.id.icon_delete:{
                Compras c=new Compras();
                c.setId(compraseleccionada.getId());
                databaseReference.child("Lista").child(c.getId()).removeValue();
                Toast.makeText(this,"Eliminar", Toast.LENGTH_LONG).show();
                limpiar();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiar() {
        list.setText("");
        detalle.setText("");
    }

    private void validacion() {
        String nom=list.getText().toString();
        String det=detalle.getText().toString();
        if(nom.equals("")){
            list.setError("Required");
        }
        else if(det.equals("")){
            detalle.setError("Required");
        }
    }
}