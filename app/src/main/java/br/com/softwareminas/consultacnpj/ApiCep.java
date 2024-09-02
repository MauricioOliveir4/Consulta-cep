package br.com.softwareminas.consultacnpj;

public class ApiCep {
    private int cep;
    private String logradouro;
    private String localidade;
    private String estado;
    private String ibge;
    private String ddd;
    private String siafi;
}


 public void setApiCep(ApiCep apiCep){
        this.cep = apiCep.getCep();
        this.logradouro = apiCep.getLogradouro();
        this.localidade = apiCep.getLocalidade();
        this.estado = apiCep.getEstado();
 }


