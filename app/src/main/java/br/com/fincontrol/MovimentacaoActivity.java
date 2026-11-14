package br.com.fincontrol;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Locale;

import br.com.fincontrol.dao.MovimentacaoDAO;
import br.com.fincontrol.model.Movimentacao;
import br.com.fincontrol.util.FormatadorFinanceiro;

public class MovimentacaoActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "movimentacao_id";

    private static final String[] CATEGORIAS = {
            "Alimentação", "Transporte", "Moradia", "Saúde",
            "Educação", "Lazer", "Salário", "Outros"
    };

    private RadioButton rbDespesa, rbReceita;
    private EditText etDescricao, etValor, etData, etObservacao;
    private Spinner spCategoria;
    private TextView tvTituloTela;

    private MovimentacaoDAO dao;
    private long idEdicao = -1;
    private String dataIso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimentacao);

        tvTituloTela = findViewById(R.id.tvTituloTela);
        rbDespesa = findViewById(R.id.rbDespesa);
        rbReceita = findViewById(R.id.rbReceita);
        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        etData = findViewById(R.id.etData);
        etObservacao = findViewById(R.id.etObservacao);
        spCategoria = findViewById(R.id.spCategoria);
        Button btnSalvar = findViewById(R.id.btnSalvar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        dao = new MovimentacaoDAO(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CATEGORIAS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapter);

        dataIso = FormatadorFinanceiro.hojeIso();
        etData.setText(FormatadorFinanceiro.dataParaExibicao(dataIso));
        etData.setOnClickListener(v -> abrirCalendario());

        idEdicao = getIntent().getLongExtra(EXTRA_ID, -1);
        if (idEdicao != -1) {
            carregarParaEdicao();
        }

        btnSalvar.setOnClickListener(v -> salvar());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void abrirCalendario() {
        String[] p = dataIso.split("-");
        int ano = Integer.parseInt(p[0]);
        int mes = Integer.parseInt(p[1]) - 1;   // DatePicker usa mês de 0 a 11
        int dia = Integer.parseInt(p[2]);

        new DatePickerDialog(this, (view, y, m, d) -> {
            dataIso = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
            etData.setText(FormatadorFinanceiro.dataParaExibicao(dataIso));
        }, ano, mes, dia).show();
    }

    private void carregarParaEdicao() {
        Movimentacao m = dao.buscarPorId(idEdicao);
        if (m == null) {
            finish();
            return;
        }
        tvTituloTela.setText("Editar movimentação");
        etDescricao.setText(m.getDescricao());
        etValor.setText(String.format(Locale.US, "%.2f", m.getValorCentavos() / 100.0));
        etObservacao.setText(m.getObservacao());
        dataIso = m.getData();
        etData.setText(FormatadorFinanceiro.dataParaExibicao(dataIso));

        if (Movimentacao.TIPO_RECEITA.equals(m.getTipo())) {
            rbReceita.setChecked(true);
        } else {
            rbDespesa.setChecked(true);
        }
        int pos = Arrays.asList(CATEGORIAS).indexOf(m.getCategoria());
        if (pos >= 0) spCategoria.setSelection(pos);
    }

    private void salvar() {
        String descricao = etDescricao.getText().toString().trim();
        String valorTexto = etValor.getText().toString().trim();

        // Validação dos campos obrigatórios (RNF08)
        if (descricao.isEmpty()) {
            etDescricao.setError("Informe a descrição");
            etDescricao.requestFocus();
            return;
        }
        if (valorTexto.isEmpty()) {
            etValor.setError("Informe o valor");
            etValor.requestFocus();
            return;
        }
        long centavos;
        try {
            centavos = FormatadorFinanceiro.paraCentavos(valorTexto);
        } catch (NumberFormatException e) {
            etValor.setError("Valor inválido");
            etValor.requestFocus();
            return;
        }
        if (centavos <= 0) {
            etValor.setError("O valor deve ser maior que zero");
            etValor.requestFocus();
            return;
        }

        Movimentacao m = new Movimentacao();
        m.setDescricao(descricao);
        m.setValorCentavos(centavos);
        m.setTipo(rbReceita.isChecked() ? Movimentacao.TIPO_RECEITA : Movimentacao.TIPO_DESPESA);
        m.setCategoria(spCategoria.getSelectedItem().toString());
        m.setData(dataIso);
        m.setObservacao(etObservacao.getText().toString().trim());

        if (idEdicao == -1) {
            dao.inserir(m);
            Toast.makeText(this, "Movimentação salva!", Toast.LENGTH_SHORT).show();
        } else {
            m.setId(idEdicao);
            dao.atualizar(m);
            Toast.makeText(this, "Movimentação atualizada!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}