package com.example.appfirebase

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.appfirebase.ui.theme.AppFirebaseTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppFirebaseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(padding = innerPadding, db = db)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppFirebaseTheme {
        Greeting("Android")
    }
}

@Composable
fun App(padding: PaddingValues = PaddingValues(16.dp), db: FirebaseFirestore) {

    val clientes = remember {
        mutableStateListOf<HashMap<String, String>>()
    }

    fun fetch() {
        clientes.clear()
        db.collection("clientes").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.data
                    val map = hashMapOf<String, String>(
                        "id" to document.id,
                        "nome" to data["nome"].toString(),
                        "telefone" to data["telefone"].toString()
                    )
                    clientes.add(map)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("READ", "Error getting documents.", exception)
            }

    }


    var nome by remember {
        mutableStateOf("")
    }

    var telefone by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
        , verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = "App Firebase Firestore")
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(0.3f)) {
                Text("Nome:")
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = nome,
                    onValueChange = { nome = it })
            }

        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(0.3f)) {
                Text("Telefone:")
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions =  KeyboardOptions(
                        keyboardType = KeyboardType.Phone,

                    ),
                    value = telefone,
                    onValueChange = { telefone = it })
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                val data = hashMapOf(
                    "nome" to nome,
                    "telefone" to telefone
                )

                val collection = db.collection("clientes")
                collection.add(data)
                    .addOnSuccessListener {
                        nome = ""
                        telefone = ""
                        Log.d("CREATE", "DocumentSnapshot added with ID: ${it.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("CREATE", "Error adding document", e)
                    }
                    fetch()
                }) {
                Text(text = "Salvar")
            }
        }
        Spacer(modifier = Modifier.size(20.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(clientes) { item ->
                Spacer(modifier = Modifier.size(20.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                        Text("Nome:")
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(item["nome"] ?: "")
                    }
                }
                Row (modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                        Text("Telefone:")
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(item["telefone"] ?: "")
                    }

                }
            }
        }
    }

    LaunchedEffect(Unit) {
        fetch()
    }

}