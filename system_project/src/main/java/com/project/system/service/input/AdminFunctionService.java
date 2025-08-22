package com.project.system.service.input;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.Function;
import com.project.system.repositories.FunctionRepository;

@Service
public class AdminFunctionService {

    @Autowired
    private FunctionRepository functionRepository;
    
    // Método paginado para pegar todas as ocupações
    public Page<Function> getAllFunctionsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return functionRepository.findAll(pageable);
    }

    // Método paginado para buscar com filtro
    public Page<Function> searchFunctionsPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return functionRepository.searchByFilterPaginated(filter, pageable);
    }

    public List<Function> getAllFunctions() {
        return functionRepository.findAll();
    }

    public Optional<Function> getFunctionById(Long functionId) {
        return functionRepository.findById(functionId);
    }

    public ResponseEntity<StandardResponseDTO> removeFunction(Long functionId) {
        Optional<Function> functionOpt = functionRepository.findById(functionId);
        if (functionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(StandardResponseDTO.error("Função não encontrada!"));
        }

        functionRepository.deleteById(functionId);
        return ResponseEntity.ok(StandardResponseDTO.success("Função removida com sucesso!"));
    }

    public ResponseEntity<StandardResponseDTO> saveEditions(Function function) {
        try {
            Optional<Function> functionExistsOpt = functionRepository.findById(function.getFunctionId());
            if (functionExistsOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(StandardResponseDTO.error("Função não encontrada."));
            }

            Function functionExists = functionExistsOpt.get();

            functionExists.setFunctionName(function.getFunctionName());

            functionRepository.save(functionExists);

            return ResponseEntity.ok(StandardResponseDTO.success("Função atualizada com sucesso."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(StandardResponseDTO.error("Erro ao editar a função. " + e.getMessage()));
        }
    }

    public ResponseEntity<StandardResponseDTO> saveFunction(Function function) {
        try {
            functionRepository.save(function);
            return ResponseEntity.ok(StandardResponseDTO.success("Função cadastrada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(StandardResponseDTO.error("Erro ao cadastrar função: " + e.getMessage()));
        }
    }
    
    public List<Function> searchFunctions(String filter) {
        return functionRepository.searchByFilter(filter);
    }
    
}
