package br.com.fincontrol.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.fincontrol.database.DatabaseHelper;
import br.com.fincontrol.model.Movimentacao;

public class MovimentacaoDAO {
    private static final String TABELA = "movimentacao";
    private final DatabaseHelper helper;

    public MovimentacaoDAO(Context context) {
        helper = new DatabaseHelper(context);
    }

    private ContentValues paraValores(Movimentacao m) {
        ContentValues v = new ContentValues();
        v.put("descricao", m.getDescricao());
        v.put("valor_centavos", m.getValorCentavos());
        v.put("tipo", m.getTipo());
        v.put("categoria", m.getCategoria());
        v.put("data", m.getData());
        v.put("observacao", m.getObservacao());
        return v;
    }

    private Movimentacao doCursor(Cursor c) {
        Movimentacao m = new Movimentacao();
        m.setId(c.getLong(c.getColumnIndexOrThrow("id")));
        m.setDescricao(c.getString(c.getColumnIndexOrThrow("descricao")));
        m.setValorCentavos(c.getLong(c.getColumnIndexOrThrow("valor_centavos")));
        m.setTipo(c.getString(c.getColumnIndexOrThrow("tipo")));
        m.setCategoria(c.getString(c.getColumnIndexOrThrow("categoria")));
        m.setData(c.getString(c.getColumnIndexOrThrow("data")));
        m.setObservacao(c.getString(c.getColumnIndexOrThrow("observacao")));
        return m;
    }

    // INSERT
    public long inserir(Movimentacao m) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(TABELA, null, paraValores(m));
    }

    // UPDATE
    public int atualizar(Movimentacao m) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update(TABELA, paraValores(m), "id = ?",
                new String[]{String.valueOf(m.getId())});
    }

    // DELETE
    public int excluir(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(TABELA, "id = ?", new String[]{String.valueOf(id)});
    }

    // SELECT por id
    public Movimentacao buscarPorId(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.query(TABELA, null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? doCursor(c) : null;
        }
    }

    // SELECT lista; tipo = null traz todas
    public List<Movimentacao> listar(String tipo) {
        List<Movimentacao> lista = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String where = tipo == null ? null : "tipo = ?";
        String[] args = tipo == null ? null : new String[]{tipo};
        try (Cursor c = db.query(TABELA, null, where, args, null, null,
                "data DESC, id DESC")) {
            while (c.moveToNext()) {
                lista.add(doCursor(c));
            }
        }
        return lista;
    }

    // Soma em centavos de um tipo em um mês (mes = "yyyy-MM")
    public long somarPorTipo(String tipo, String mes) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.rawQuery(
                "SELECT COALESCE(SUM(valor_centavos), 0) FROM movimentacao "
                        + "WHERE tipo = ? AND data LIKE ?",
                new String[]{tipo, mes + "%"})) {
            return c.moveToFirst() ? c.getLong(0) : 0;
        }
    }
}