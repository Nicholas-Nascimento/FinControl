package br.com.fincontrol.model;

public class Movimentacao {
    public static final String TIPO_RECEITA = "RECEITA";
    public static final String TIPO_DESPESA = "DESPESA";

    private long id;
    private String descricao;
    private long valorCentavos;
    private String tipo;
    private String categoria;
    private String data;        // formato yyyy-MM-dd
    private String observacao;

    public Movimentacao() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public long getValorCentavos() { return valorCentavos; }
    public void setValorCentavos(long valorCentavos) { this.valorCentavos = valorCentavos; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}