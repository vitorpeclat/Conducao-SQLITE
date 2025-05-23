package com.example.conducao;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class ContatoDAO extends SQLiteOpenHelper {
    //CONTATO
    public static final String NOME_BANCO = "bdcontatos";
    public static final int VERSAO_BANCO = 1;
    public static final String TABELA_CONTATO = "contato";
    public static final String COLUNA_ID = "id";
    public static final String COLUNA_NOME = "nome";
    public static final String COLUNA_TELEFONE = "telefone";
    public static final String COLUNA_FKENTREVISTADOR_ID = "entrevistador_id";
    //ENTREVISTADORES
    public static final String TABELA_ENTREVISTADORES = "entrevistadores";
    public static final String COLUNA_ENTREVISTADOR_ID = "id";
    public static final String COLUNA_ENTREVISTADOR_NOME = "nome";
    public static final String COLUNA_ENTREVISTADOR_CONTAGEM = "contagem";
    //ADMIN
    public static final String TABELA_ADMINISTRADORES = "administradores";
    public static final String COLUNA_ADMIN_ID = "id";
    public static final String COLUNA_ADMIN_NOME = "nome";
    public static final String COLUNA_ADMIN_SENHA = "senha";
    //LINHAS
    public static final String TABELA_LINHAS = "linhas";
    public static final String COLUNA_LINHA_ID = "id";
    public static final String COLUNA_LINHA_NOME = "nome";
    public static final String COLUNA_LINHA_CONTADOR_ORIGEM = "contador_origem";
    public static final String COLUNA_LINHA_CONTADOR_DESTINO = "contador_destino";
    //ESTAÇÕES
    public static final String TABELA_ESTACOES = "estacoes";
    public static final String COLUNA_ESTACAO_ID = "id";
    public static final String COLUNA_ESTACAO_NOME = "nome";
    public static final String COLUNA_FKLINHA_ID = "linha_id";
    public ContatoDAO(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABELA_CONTATO + " (" +
                COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUNA_NOME + " TEXT, " +
                COLUNA_TELEFONE + " TEXT, " +
                COLUNA_FKENTREVISTADOR_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUNA_FKENTREVISTADOR_ID + ") REFERENCES " +
                TABELA_ENTREVISTADORES + "(" + COLUNA_ENTREVISTADOR_ID + "))"
        );

        db.execSQL("CREATE TABLE " + TABELA_ENTREVISTADORES + " (" +
                COLUNA_ENTREVISTADOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUNA_ENTREVISTADOR_NOME + " TEXT, " +
                COLUNA_ENTREVISTADOR_CONTAGEM + " INTEGER DEFAULT 0)"
        );

        db.execSQL("CREATE TABLE " + TABELA_ADMINISTRADORES + " (" +
                COLUNA_ADMIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUNA_ADMIN_NOME + " TEXT, " +
                COLUNA_ADMIN_SENHA + " TEXT)"
        );

        db.execSQL("CREATE TABLE " + TABELA_LINHAS + " (" +
                COLUNA_LINHA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUNA_LINHA_NOME + " TEXT, " +
                COLUNA_LINHA_CONTADOR_ORIGEM + " INTEGER DEFAULT 0, " +
                COLUNA_LINHA_CONTADOR_DESTINO + " INTEGER DEFAULT 0)"
        );

        db.execSQL("CREATE TABLE " + TABELA_ESTACOES + " (" +
                COLUNA_ESTACAO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUNA_ESTACAO_NOME + " TEXT, " +
                COLUNA_FKLINHA_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUNA_FKLINHA_ID + ") REFERENCES " +
                TABELA_LINHAS + "(" + COLUNA_LINHA_ID + "))"
        );

        insertEntrevistador(db, "Vitor");
        insertEntrevistador(db, "Maria");
        insertLinhasEstacoes(db);
        insertAdmin(db, "admin", "admin");
    }

    private void insertEntrevistador(SQLiteDatabase db, String nome) {
        ContentValues values = new ContentValues();
        values.put(COLUNA_ENTREVISTADOR_NOME, nome);
        db.insert(TABELA_ENTREVISTADORES, null, values);
    }
    private void insertLinhasEstacoes(SQLiteDatabase db) {
        ContentValues linhaAzul = new ContentValues();
        linhaAzul.put(COLUNA_LINHA_NOME, "Azul");
        long linhaAzulId = db.insert(TABELA_LINHAS, null, linhaAzul);
        insertEstacao(db, "Jabaquara", linhaAzulId);
        insertEstacao(db, "Liberdade", linhaAzulId);

        ContentValues linhaVermelha = new ContentValues();
        linhaVermelha.put(COLUNA_LINHA_NOME, "Vermelha");
        long linhaVermelhaId = db.insert(TABELA_LINHAS, null, linhaVermelha);
        insertEstacao(db, "República", linhaVermelhaId);
        insertEstacao(db, "Anhangabau", linhaVermelhaId);
    }
    private void insertEstacao(SQLiteDatabase db, String nome, long linhaId) {
        ContentValues values = new ContentValues();
        values.put(COLUNA_ESTACAO_NOME, nome);
        values.put(COLUNA_FKLINHA_ID, linhaId);
        db.insert(TABELA_ESTACOES, null, values);
    }
    private void insertAdmin(SQLiteDatabase db, String nome, String senha) {
        ContentValues values = new ContentValues();
        values.put(COLUNA_ADMIN_NOME, nome);
        values.put(COLUNA_ADMIN_SENHA, senha);
        db.insert(TABELA_ADMINISTRADORES, null, values);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_CONTATO);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_ENTREVISTADORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_ADMINISTRADORES);
        onCreate(db);
    }

    public void salvarContato(Contato c, long entrevistadorId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COLUNA_NOME, c.getNome());
        valores.put(COLUNA_TELEFONE, c.getTelefone());
        valores.put(COLUNA_FKENTREVISTADOR_ID, entrevistadorId);

        db.insert(TABELA_CONTATO, null, valores);
        ContadorEntrevistador(db, entrevistadorId);
        db.close();
    }
    public void ContadorOrigem(long estacaoId) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABELA_LINHAS +
                        " SET " + COLUNA_LINHA_CONTADOR_ORIGEM + " = " +
                        COLUNA_LINHA_CONTADOR_ORIGEM + " + 1 " +
                        " WHERE " + COLUNA_LINHA_ID + " = (" +
                        "SELECT " + COLUNA_FKLINHA_ID + " FROM " + TABELA_ESTACOES +
                        " WHERE " + COLUNA_ESTACAO_ID + " = ?)",
                new String[]{String.valueOf(estacaoId)});
        db.close();
    }
    public void ContadorDestino(long estacaoId) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABELA_LINHAS +
                        " SET " + COLUNA_LINHA_CONTADOR_DESTINO + " = " +
                        COLUNA_LINHA_CONTADOR_DESTINO + " + 1 " +
                        " WHERE " + COLUNA_LINHA_ID + " = (" +
                        "SELECT " + COLUNA_FKLINHA_ID + " FROM " + TABELA_ESTACOES +
                        " WHERE " + COLUNA_ESTACAO_ID + " = ?)",
                new String[]{String.valueOf(estacaoId)});
        db.close();
    }
    private void ContadorEntrevistador(SQLiteDatabase db, long entrevistadorId) {
        db.execSQL("UPDATE " + TABELA_ENTREVISTADORES +
                        " SET " + COLUNA_ENTREVISTADOR_CONTAGEM + " = " +
                        COLUNA_ENTREVISTADOR_CONTAGEM + " + 1 " +
                        " WHERE " + COLUNA_ENTREVISTADOR_ID + " = ?",
                new String[]{String.valueOf(entrevistadorId)});
    }

    public List<Estacao> getTodasEstacoes() {
        List<Estacao> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT e." + COLUNA_ESTACAO_ID + ", e." + COLUNA_ESTACAO_NOME + ", l." + COLUNA_LINHA_NOME +
                " FROM " + TABELA_ESTACOES + " e " +
                "JOIN " + TABELA_LINHAS + " l ON e." + COLUNA_FKLINHA_ID + " = l." + COLUNA_LINHA_ID;

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Estacao estacao = new Estacao();
            estacao.setId(cursor.getLong(0));
            estacao.setNome(cursor.getString(1));
            estacao.setLinha(cursor.getString(2));
            lista.add(estacao);
        }
        cursor.close();
        return lista;
    }
    public List<Entrevistador> getEntrevistadores() {
        List<Entrevistador> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nome FROM " + TABELA_ENTREVISTADORES, null);

        while (cursor.moveToNext()) {
            Entrevistador e = new Entrevistador();
            e.setId(cursor.getLong(0));
            e.setNome(cursor.getString(1));
            lista.add(e);
        }
        cursor.close();
        return lista;
    }
    public int getTotalRegistros() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABELA_CONTATO, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            db.close();
        }
    }
    public Cursor getEstatisticasOrigem() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT l." + COLUNA_LINHA_NOME + ", l." + COLUNA_LINHA_CONTADOR_ORIGEM +
                " FROM " + TABELA_LINHAS + " l ORDER BY l." + COLUNA_LINHA_CONTADOR_ORIGEM + " DESC";
        return db.rawQuery(query, null);
    }
    public Cursor getEstatisticasDestino() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT l." + COLUNA_LINHA_NOME + ", " + COLUNA_LINHA_CONTADOR_DESTINO + " as total " +
                "FROM " + TABELA_LINHAS + " l " +
                "ORDER BY total DESC";
        return db.rawQuery(query, null);
    }
    public Cursor getEstatisticasEntrevistadores() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT e." + COLUNA_ENTREVISTADOR_NOME + ", e." + COLUNA_ENTREVISTADOR_CONTAGEM +
                " FROM " + TABELA_ENTREVISTADORES + " e ORDER BY e." + COLUNA_ENTREVISTADOR_CONTAGEM + " DESC";
        return db.rawQuery(query, null);
    }
    public Cursor getTodosEntrevistador() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT c." + COLUNA_NOME + ", c." + COLUNA_TELEFONE + ", e." + COLUNA_ENTREVISTADOR_NOME +
                " FROM " + TABELA_CONTATO + " c " +
                "JOIN " + TABELA_ENTREVISTADORES + " e ON c." + COLUNA_FKENTREVISTADOR_ID + " = e." + COLUNA_ENTREVISTADOR_ID +
                " ORDER BY c." + COLUNA_ID + " DESC";
        return db.rawQuery(query, null);
    }
    public void limparTodosDados() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABELA_CONTATO, null, null);
            db.execSQL("UPDATE " + TABELA_LINHAS + " SET " +
                    COLUNA_LINHA_CONTADOR_ORIGEM + " = 0, " +
                    COLUNA_LINHA_CONTADOR_DESTINO + " = 0");
            db.execSQL("UPDATE " + TABELA_ENTREVISTADORES + " SET " +
                    COLUNA_ENTREVISTADOR_CONTAGEM + " = 0");
        } finally {
            db.close();
        }
    }
    public boolean isAdmin(String nome, String senha) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_ADMINISTRADORES +
                        " WHERE " + COLUNA_ADMIN_NOME + " = ? AND " +
                        COLUNA_ADMIN_SENHA + " = ?",
                new String[]{nome, senha}
        );
        boolean resultado = cursor.getCount() > 0;
        cursor.close();
        return resultado;
    }

    public static class Entrevistador {
        private long id;
        private String nome;
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String toString() { return nome; }
    }
    public static class Estacao {
        private long id;
        private String nome;
        private String linha;
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getLinha() { return linha; }
        public void setLinha(String linha) { this.linha = linha; }
        public String toString() {
            return nome + " (" + linha + ")";
        }
}
}
