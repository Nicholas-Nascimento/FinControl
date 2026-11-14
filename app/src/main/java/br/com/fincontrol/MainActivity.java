package br.com.fincontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import br.com.fincontrol.dao.MovimentacaoDAO;
import br.com.fincontrol.model.Movimentacao;
import br.com.fincontrol.util.FormatadorFinanceiro;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS = "fincontrol_prefs";
    private static final String CHAVE_LIMITE = "limite_centavos";

    private TextView tvSaldo, tvReceitas, tvDespesas, tvLimite, tvPercentual, tvAviso;
    private ProgressBar pbOrcamento;
    private MovimentacaoDAO dao;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSaldo = findViewById(R.id.tvSaldo);
        tvReceitas = findViewById(R.id.tvReceitas);
        tvDespesas = findViewById(R.id.tvDespesas);
        tvLimite = findViewById(R.id.tvLimite);
        tvPercentual = findViewById(R.id.tvPercentual);
        tvAviso = findViewById(R.id.tvAviso);
        pbOrcamento = findViewById(R.id.pbOrcamento);

        dao = new MovimentacaoDAO(this);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        Button btnNova = findViewById(R.id.btnNova);
        Button btnHistorico = findViewById(R.id.btnHistorico);
        Button btnLimite = findViewById(R.id.btnLimite);

        btnNova.setOnClickListener(v ->
                startActivity(new Intent(this, MovimentacaoActivity.class)));
        btnHistorico.setOnClickListener(v ->
                startActivity(new Intent(this, HistoricoActivity.class)));
        btnLimite.setOnClickListener(v -> abrirDialogLimite());
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarResumo(); // recalcula ao voltar de outras telas
    }

    private void atualizarResumo() {
        String mes = FormatadorFinanceiro.mesAtual();
        long receitas = dao.somarPorTipo(Movimentacao.TIPO_RECEITA, mes);
        long despesas = dao.somarPorTipo(Movimentacao.TIPO_DESPESA, mes);
        long saldo = receitas - despesas;
        long limite = prefs.getLong(CHAVE_LIMITE, 0);

        tvReceitas.setText(FormatadorFinanceiro.formatar(receitas));
        tvDespesas.setText(FormatadorFinanceiro.formatar(despesas));
        tvSaldo.setText(FormatadorFinanceiro.formatar(saldo));

        if (limite > 0) {
            int percentual = (int) (despesas * 100 / limite);
            tvLimite.setText("Limite: " + FormatadorFinanceiro.formatar(limite));
            pbOrcamento.setProgress(Math.min(percentual, 100));
            tvPercentual.setText(percentual + "% do limite utilizado");
            tvAviso.setVisibility(despesas > limite ? View.VISIBLE : View.GONE);
        } else {
            tvLimite.setText("Limite: não definido");
            pbOrcamento.setProgress(0);
            tvPercentual.setText("Defina um limite para acompanhar seus gastos");
            tvAviso.setVisibility(View.GONE);
        }
    }

    private void abrirDialogLimite() {
        EditText etLimite = new EditText(this);
        etLimite.setHint("Ex.: 3000,00");
        etLimite.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(this)
                .setTitle("Limite mensal de gastos")
                .setView(etLimite)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    try {
                        long centavos = FormatadorFinanceiro.paraCentavos(etLimite.getText().toString());
                        if (centavos <= 0) {
                            Toast.makeText(this, "Informe um valor maior que zero", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        prefs.edit().putLong(CHAVE_LIMITE, centavos).apply();
                        Toast.makeText(this, "Limite salvo!", Toast.LENGTH_SHORT).show();
                        atualizarResumo();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}