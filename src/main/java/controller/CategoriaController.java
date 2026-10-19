package controller;


import dtos.CategoriaResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.CategoriaService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping("/lista")
    public ResponseEntity<List<CategoriaResponseDto>> lista(){
        List<CategoriaResponseDto> categorias = categoriaService.listarCategorias().stream().map(CategoriaResponseDto::of).toList();
        return ResponseEntity.ok(categorias);
    }

}
