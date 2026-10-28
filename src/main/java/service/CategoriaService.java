package service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import modelos.Categoria;
import org.springframework.stereotype.Service;
import repositorios.ICategoriaRepositorio;

import java.util.List;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class CategoriaService {

    private ICategoriaRepositorio categoriaRepository;

    public List<Categoria> listarCategorias(){
        List<Categoria> lista = categoriaRepository.findAll();
        if (lista.isEmpty()){
            throw new EntityNotFoundException("No se ha encontrado ninguna categoria");
        }
        return lista;
    }




}
