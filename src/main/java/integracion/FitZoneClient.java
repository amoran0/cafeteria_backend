package integracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** RF-08: consulta a FitZone si un DNI es de un socio activo. */
@Component
public class FitZoneClient {

    private final RestClient restClient;

    public FitZoneClient(@Value("${fitzone.url:http://localhost:8081}") String url) {
        this.restClient = RestClient.create(url);
    }

    public boolean esSocioActivo(String dni) {
        try {
            SocioFitZone socio = restClient.get()
                    .uri("/api/v1/socios/{dni}", dni)
                    .retrieve()
                    .body(SocioFitZone.class);
            return socio != null && socio.activo();
        } catch (RestClientException e) {
            return false; // si FitZone no responde o no existe el socio, no hay descuento
        }
    }

    public record SocioFitZone(boolean activo) {
    }
}
