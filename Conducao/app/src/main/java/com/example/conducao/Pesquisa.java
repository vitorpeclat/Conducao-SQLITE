package com.example.conducao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class Pesquisa extends AppCompatActivity {
    private Button btSalvar, btResultados;
    private EditText edNome, edTelefone;
    private Spinner spinnerEntrevistadores, spinnerOrigem, spinnerDestino;
    private long entrevistadorById;
    private ContatoDAO.Estacao Origem, Destino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pesquisa);

        btSalvar = findViewById(R.id.btSalvar);
        btResultados = findViewById(R.id.btResultados);
        edNome = findViewById(R.id.edNome);
        edTelefone = findViewById(R.id.edTelefone);
        spinnerEntrevistadores = findViewById(R.id.spinnerEntrevistadores);
        spinnerOrigem = findViewById(R.id.spinnerOrigem);
        spinnerDestino = findViewById(R.id.spinnerDestino);

        carregarEstacoes();
        carregarEntrevistadores();

        btSalvar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (entrevistadorById <= 0) {
                    Toast.makeText(Pesquisa.this, "Selecione um entrevistador", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Origem == null || Destino == null) {
                    Toast.makeText(Pesquisa.this, "Selecione origem e destino", Toast.LENGTH_SHORT).show();
                    return;
                }

                Contato c = new Contato();
                c.setNome(edNome.getText().toString());
                c.setTelefone(edTelefone.getText().toString());

                ContatoDAO dao = new ContatoDAO(Pesquisa.this);
                dao.salvarContato(c, entrevistadorById);

                dao.ContadorOrigem(Origem.getId());
                dao.ContadorDestino(Destino.getId());

                Toast.makeText(Pesquisa.this, "Contato gravado com sucesso", Toast.LENGTH_SHORT).show();
                limparCampos();
            }
        });
        btResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAdmin();
            }
        });
    }

    private void carregarEntrevistadores() {
        ContatoDAO dao = new ContatoDAO(this);
        List<ContatoDAO.Entrevistador> entrevistadores = dao.getEntrevistadores();
        ArrayAdapter<ContatoDAO.Entrevistador> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                entrevistadores
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEntrevistadores.setAdapter(adapter);

        spinnerEntrevistadores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ContatoDAO.Entrevistador entrevistador = (ContatoDAO.Entrevistador) parent.getItemAtPosition(position);
                entrevistadorById = entrevistador.getId();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                entrevistadorById = 0;
            }
        });
    }
    private void carregarEstacoes() {
        ContatoDAO dao = new ContatoDAO(this);
        List<ContatoDAO.Estacao> estacoes = dao.getTodasEstacoes();
        ArrayAdapter<ContatoDAO.Estacao> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                estacoes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerOrigem.setAdapter(adapter);
        spinnerDestino.setAdapter(adapter);

        spinnerOrigem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Origem = (ContatoDAO.Estacao) parent.getItemAtPosition(position);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                Origem = null;
            }
        });

        spinnerDestino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Destino = (ContatoDAO.Estacao) parent.getItemAtPosition(position);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                Destino = null;
            }
        });
    }

    private void loginAdmin() {
        View view = getLayoutInflater().inflate(R.layout.login_admin, null);
        EditText edUsuario = view.findViewById(R.id.edUsuario);
        EditText edSenha = view.findViewById(R.id.edSenha);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Login Administrador")
                .setView(view)
                .setPositiveButton("Entrar", (dialog, which) -> {
                    String usuario = edUsuario.getText().toString();
                    String senha = edSenha.getText().toString();

                    ContatoDAO dao = new ContatoDAO(Pesquisa.this);
                    boolean valido = dao.isAdmin(usuario, senha);
                    if (valido) {
                        Intent intent = new Intent(Pesquisa.this, MenuAdmin.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Usuário ou senha inválidos!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void limparCampos() {
        edNome.setText("");
        edTelefone.setText("");
    }
}