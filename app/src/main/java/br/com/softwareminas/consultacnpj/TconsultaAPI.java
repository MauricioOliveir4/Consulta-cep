package br.com.softwareminas.consultacnpj;
import android.os.Handler;
import android.os.Message;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.softwareminas.consultacnpj.util.ConexaoHttpClient;


import android.os.Handler;
import android.os.Message;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsultaAPI {
    private final Handler handler;
    private final ExecutorService executorService;

    public ConsultaAPI(Handler handler) {
        this.handler = handler;
        this.executorService = Executors.newSingleThreadExecutor(); // Executor para tarefas em segundo plano
    }
    public class Endereco {
        private String logradouro;
        private String localidade;
        private String estado;
        private String ibge;
        private String ddd;

        // Getters e Setters
        public String getLogradouro() { return logradouro; }
        public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

        public String getLocalidade() { return localidade; }
        public void setLocalidade(String localidade) { this.localidade = localidade; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public String getIbge() { return ibge; }
        public void setIbge(String ibge) { this.ibge = ibge; }

        public String getDdd() { return ddd; }
        public void setDdd(String ddd) { this.ddd = ddd; }
    }

    public void consultarCep(final String cep) {
        executorService.execute(() -> {
            String url = "https://viacep.com.br/ws/" + cep + "/xml";
            try {
                String resposta = ConexaoHttpClient.executaHttpGet(url);
                Endereco endereco = parseXml(resposta);

                // Enviando os dados obtidos para o Handler
                Message message = new Message();
                message.obj = endereco;
                handler.sendMessage(message);

            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            }
        });
    }


    public class ConexaoHttpClient {
        public static String executaHttpGet(String url) throws Exception {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } finally {
                connection.disconnect();
            }
        }
    }

    private Endereco parseXml(String xml) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xml));

        Endereco endereco = new Endereco();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = parser.getName();
                switch (tagName) {
                    case "logradouro":
                        endereco.setLogradouro(parser.nextText());
                        break;
                    case "localidade":
                        endereco.setLocalidade(parser.nextText());
                        break;
                    case "uf":
                        endereco.setEstado(parser.nextText());
                        break;
                    case "ibge":
                        endereco.setIbge(parser.nextText());
                        break;
                    case "ddd":
                        endereco.setDdd(parser.nextText());
                        break;
                }
            }
            eventType = parser.next();
        }
        return endereco;
    }
}
