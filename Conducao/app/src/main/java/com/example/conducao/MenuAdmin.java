package com.example.conducao;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MenuAdmin extends AppCompatActivity {
    private ContatoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);

        dao = new ContatoDAO(this);

        Button btnExibirEstatisticas = findViewById(R.id.btnExibirEstatisticas);
        Button btnVerContatos = findViewById(R.id.btnVerContatos);
        Button btnLimparDados = findViewById(R.id.btnLimparDados);
        Button btnVoltar = findViewById(R.id.btnVoltar);

        btnExibirEstatisticas.setOnClickListener(v -> mostrarPopupEstatisticas());
        btnVerContatos.setOnClickListener(v -> startActivity(new Intent(this, ContatoSalvo.class)));
        btnLimparDados.setOnClickListener(v -> confirmarLimpezaDados());
        btnVoltar.setOnClickListener(v -> finish());

    }

    private void mostrarPopupEstatisticas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Estatísticas Completas");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        try {
            adicionarTitulo(layout, "Entrevistas por Entrevistador");
            exibirEstatisticasEntrevistadores(layout);
            adicionarTitulo(layout, "\nEstatísticas por Origem/Destino");
            exibirEstatisticasLinhas(layout);

        } catch (Exception e) {
            adicionarTexto(layout, "Erro ao carregar estatísticas");
            e.printStackTrace();
        }

        builder.setPositiveButton("Fechar", null);
        builder.show();
    }
    private void exibirEstatisticasEntrevistadores(LinearLayout layout) {
        Cursor cursor = dao.getEstatisticasEntrevistadores();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String nome = cursor.getString(0);
                int quantidade = cursor.getInt(1);

                adicionarTexto(layout, String.format("%s: %d entrevistas", nome, quantidade));

            } while (cursor.moveToNext());
            cursor.close();
        } else {
            adicionarTexto(layout, "Nenhum dado de entrevistadores disponível");
        }
    }
    private void exibirEstatisticasLinhas(LinearLayout layout) {
        int totalRegistros = dao.getTotalRegistros();

        if (totalRegistros == 0) {
            adicionarTexto(layout, "Nenhum dado disponível");
            return;
        }

        adicionarSubTitulo(layout, "Origem:");
        exibirDadosLinhas(layout, dao.getEstatisticasOrigem(), totalRegistros);

        adicionarSubTitulo(layout, "Destino:");
        exibirDadosLinhas(layout, dao.getEstatisticasDestino(), totalRegistros);
    }

    private void exibirDadosLinhas(LinearLayout layout, Cursor cursor, int totalRegistros) {
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String linha = cursor.getString(0);
                int quantidade = cursor.getInt(1);
                double percentual = (quantidade * 100.0) / totalRegistros;
                adicionarTexto(layout, String.format("%s: %d (%.2f%%)", linha, quantidade, percentual));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            adicionarTexto(layout, "Nenhum dado disponível");
        }
    }

    // Métodos auxiliares
    private void adicionarTitulo(LinearLayout layout, String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextSize(18);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(0, 16, 0, 8);
        layout.addView(tv);
    }
    private void adicionarSubTitulo(LinearLayout layout, String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextSize(16);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(0, 8, 0, 4);
        layout.addView(tv);
    }
    private void adicionarTexto(LinearLayout layout, String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextSize(16);
        tv.setPadding(0, 4, 0, 4);
        layout.addView(tv);
    }
    private void confirmarLimpezaDados() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("Tem certeza que deseja apagar TODOS os dados?\nIsso inclui contatos e estatísticas de entrevistadores.")
                .setPositiveButton("Apagar", (dialog, which) -> limparDados())
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void limparDados() {
        try {
            dao.limparTodosDados();
            Toast.makeText(this, "Todos os dados foram apagados", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao apagar dados", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}