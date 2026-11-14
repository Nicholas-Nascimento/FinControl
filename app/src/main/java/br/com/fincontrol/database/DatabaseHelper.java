package br.com.fincontrol.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "fincontrol.db";
    private static final int VERSAO = 1;

    public DatabaseHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE movimentacao ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "descricao TEXT NOT NULL, "
                + "valor_centavos INTEGER NOT NULL, "
                + "tipo TEXT NOT NULL, "
                + "categoria TEXT NOT NULL, "
                + "data TEXT NOT NULL, "
                + "observacao TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS movimentacao");
        onCreate(db);
    }
}