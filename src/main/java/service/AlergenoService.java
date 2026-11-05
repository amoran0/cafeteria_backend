package service;

import excepciones.AlergenoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import modelos.Alergeno;
import org.springframework.stereotype.Service;
import repositorios.IAlergenoRepositorio;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlergenoService {

    private final IAlergenoRepositorio alergenoRepositorio;

    public List<Alergeno> buscarPorIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> idsUnicos = ids.stream().distinct().toList();
        List<Alergeno> alergenos = alergenoRepositorio.findAllById(idsUnicos);
        if (alergenos.size() != idsUnicos.size()) {
            throw new AlergenoNoEncontradoException(
                    "Uno o varios de los alérgenos indicados no existen"
            );
        }
        return alergenos;
    }
}
