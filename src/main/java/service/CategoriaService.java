package service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import modelos.Categoria;
import org.hibernate.action.internal.EntityActionVetoException;
import org.springframework.stereotype.Service;
import repositorios.ICategoriaRepositorio;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final ICategoriaRepositorio categoriaRepository;

    public List<Categoria> listarCategorias(){
        List<Categoria> lista = categoriaRepository.findAll();
        if (lista.isEmpty()){
            throw new EntityNotFoundException("No se ha encontrado ninguna categoria");
        }
        return lista;
    }




}
