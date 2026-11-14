package br.com.fincontrol;

import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.com.fincontrol.adapter.MovimentacaoAdapter;
import br.com.fincontrol.dao.MovimentacaoDAO;
import br.com.fincontrol.model.Movimentacao;

public class HistoricoActivity extends AppCompatActivity {

    private ListView lvHistorico;
    private TextView tvVazio;
    private RadioGroup rgFiltro;

    private MovimentacaoDAO dao;
    private MovimentacaoAdapter adapter;
    private final List<Movimentacao> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        lvHistorico = findViewById(R.id.lvHistorico);
        tvVazio = findViewById(R.id.tvVazio);
        rgFiltro = findViewById(R.id.rgFiltro);

        dao = new MovimentacaoDAO(this);
        adapter = new MovimentacaoAdapter(this, lista);
        lvHistorico.setAdapter(adapter);

        rgFiltro.setOnCheckedChangeListener((group, checkedId) -> carregar());
        lvHistorico.setOnItemClickListener((parent, view, position, id) ->
                mostrarOpcoes(lista.get(position)));

        Button btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregar(); // atualiza após editar/cadastrar
    }

    private void carregar() {
        String tipo = null;
        int selecionado = rgFiltro.getCheckedRadioButtonId();
        if (selecionado == R.id.rbReceitas) {
            tipo = Movimentacao.TIPO_RECEITA;
        } else if (selecionado == R.id.rbDespesas) {
            tipo = Movimentacao.TIPO_DESPESA;
        }

        lista.clear();
        lista.addAll(dao.listar(tipo));
        adapter.notifyDataSetChanged();
        tvVazio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void mostrarOpcoes(Movimentacao m) {
        String[] opcoes = {"Editar", "Excluir"};
        new AlertDialog.Builder(this)
                .setTitle(m.getDescricao())
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) {
                        Intent i = new Intent(this, MovimentacaoActivity.class);
                        i.putExtra(MovimentacaoActivity.EXTRA_ID, m.getId());
                        startActivity(i);
                    } else {
                        confirmarExclusao(m);
                    }
                })
                .show();
    }

    private void confirmarExclusao(Movimentacao m) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir movimentação")
                .setMessage("Deseja realmente excluir \"" + m.getDescricao() + "\"?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    dao.excluir(m.getId());
                    Toast.makeText(this, "Movimentação excluída", Toast.LENGTH_SHORT).show();
                    carregar();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}