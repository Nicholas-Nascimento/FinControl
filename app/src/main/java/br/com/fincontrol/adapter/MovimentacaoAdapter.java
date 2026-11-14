package br.com.fincontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

import br.com.fincontrol.R;
import br.com.fincontrol.model.Movimentacao;
import br.com.fincontrol.util.FormatadorFinanceiro;

public class MovimentacaoAdapter extends ArrayAdapter<Movimentacao> {

    public MovimentacaoAdapter(Context context, List<Movimentacao> lista) {
        super(context, 0, lista);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_movimentacao, parent, false);
        }
        Movimentacao m = getItem(position);

        TextView tvDescricao = convertView.findViewById(R.id.tvItemDescricao);
        TextView tvDetalhe = convertView.findViewById(R.id.tvItemDetalhe);
        TextView tvValor = convertView.findViewById(R.id.tvItemValor);

        boolean receita = Movimentacao.TIPO_RECEITA.equals(m.getTipo());

        tvDescricao.setText(m.getDescricao());
        tvDetalhe.setText(m.getCategoria() + " • "
                + FormatadorFinanceiro.dataParaExibicao(m.getData()));
        tvValor.setText((receita ? "+ " : "- ")
                + FormatadorFinanceiro.formatar(m.getValorCentavos()));
        tvValor.setTextColor(ContextCompat.getColor(getContext(),
                receita ? R.color.receita : R.color.despesa));

        return convertView;
    }
}