package com.clyvoelo.painel.service;

import com.clyvoelo.painel.dto.StatusServicoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;

@Service
public class EcossistemaService {

    private final RestClient restClient;
    private final String urlApiDotnet;

    public EcossistemaService(@Value("${clyvo.integracoes.api-dotnet}") String urlApiDotnet) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(10));
        fabrica.setReadTimeout(Duration.ofSeconds(60));
        this.restClient = RestClient.builder().requestFactory(fabrica).build();
        this.urlApiDotnet = urlApiDotnet;
    }

    public List<StatusServicoDto> verificarServicos() {
        return List.of(verificar("API .NET (app do tutor)", urlApiDotnet, "/health/live"));
    }

    private StatusServicoDto verificar(String nome, String urlBase, String caminhoSaude) {
        try {
            restClient.get().uri(urlBase + caminhoSaude).retrieve().toBodilessEntity();
            return new StatusServicoDto(nome, urlBase, true, "Respondendo normalmente");
        } catch (RestClientException ex) {
            return new StatusServicoDto(nome, urlBase, false, "Sem resposta no momento");
        }
    }
}
