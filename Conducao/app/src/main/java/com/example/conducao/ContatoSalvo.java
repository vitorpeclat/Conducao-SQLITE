package com.example.conducao;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ContatoSalvo extends AppCompatActivity {
    private TableLayout tabelaContatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contato_salvo);

        tabelaContatos = findViewById(R.id.tabelaContatos);
        Button btnVoltar = findViewById(R.id.btnVoltarContatos);

        exibirListaContatos();

        btnVoltar.setOnClickListener(v -> finish());
    }

    private void exibirListaContatos() {
        ContatoDAO dao = new ContatoDAO(this);
        Cursor cursor = dao.getTodosEntrevistador();

        if (cursor != null && cursor.moveToFirst()) {
            TableRow cabecalho = new TableRow(this);
            cabecalho.setBackgroundColor(getResources().getColor(android.R.color.black));

            adicionarCelulaCabecalho(cabecalho, "Nome");
            adicionarCelulaCabecalho(cabecalho, "Telefone");
            adicionarCelulaCabecalho(cabecalho, "Entrevistador");
            tabelaContatos.addView(cabecalho);

            do {
                TableRow row = new TableRow(this);
                adicionarCelula(row, cursor.getString(0)); // Nome
                adicionarCelula(row, cursor.getString(1)); // Telefone
                adicionarCelula(row, cursor.getString(2)); // Entrevistador
                tabelaContatos.addView(row);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            TableRow row = new TableRow(this);
            TextView tv = new TextView(this);
            tv.setText("Nenhum contato cadastrado");
            tv.setPadding(8, 8, 8, 8);
            row.addView(tv);
            tabelaContatos.addView(row);
        }
    }

    private void adicionarCelulaCabecalho(TableRow row, String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(8, 8, 8, 8);
        tv.setGravity(Gravity.CENTER);
        row.addView(tv);
    }
    private void adicionarCelula(TableRow row, String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setPadding(8, 8, 8, 8);
        tv.setGravity(Gravity.START);
        row.addView(tv);
    }
}