package com.project.system.service.input;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.Occupation;
import com.project.system.enums.input.OccupationType;
import com.project.system.repositories.OccupationRepository;

@Service
public class UserOccupationService {

    @Autowired
    private OccupationRepository occupationRepository;
    
    public Page<Occupation> searchOccupationsPaginated(String filter, int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return occupationRepository.searchByFilterPaginated(filter, pageable);
    }

    public Page<Occupation> getAllOccupationsPaginated(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return occupationRepository.findAll(pageable);
    }

    public List<Occupation> getAllOccupations() {
        return occupationRepository.findAll();
    }

    public Optional<Occupation> getOccupationById(Long occupationId) {
        return occupationRepository.findById(occupationId);
    }

    public ResponseEntity<StandardResponseDTO> removeOccupation(Long occupationId) {
        Optional<Occupation> occupationOpt = occupationRepository.findById(occupationId);
        if (occupationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(StandardResponseDTO.error("Profissão não encontrada!"));
        }

        occupationRepository.deleteById(occupationId);
        return ResponseEntity.ok(StandardResponseDTO.success("Profissão removida com sucesso!"));
    }

    public ResponseEntity<StandardResponseDTO> saveEditions(Occupation occupation) {
        try {
            Optional<Occupation> occupationExistsOpt = occupationRepository.findById(occupation.getOccupationId());
            if (occupationExistsOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(StandardResponseDTO.error("Profissão não encontrada."));
            }

            Occupation occupationExists = occupationExistsOpt.get();

            occupationExists.setOccupationName(occupation.getOccupationName());
            occupationExists.setOccupationCBO(occupation.getOccupationCBO());
            occupationExists.setOccupationType(occupation.getOccupationType());
            
            occupationRepository.save(occupationExists);

            return ResponseEntity.ok(StandardResponseDTO.success("Profissão atualizada com sucesso."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(StandardResponseDTO.error("Erro ao editar a profissão."));
        }
    }

    public ResponseEntity<StandardResponseDTO> saveOccupation(Occupation occupation) {
        try {
            occupationRepository.save(occupation);
            return ResponseEntity.ok(StandardResponseDTO.success("Profissão cadastrada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(StandardResponseDTO.error("Erro ao cadastrar profissão: " + e.getMessage()));
        }
    }
    
    // Lista simples
    public List<Occupation> searchOccupations(String filter) {
        return occupationRepository.searchByFilter(filter);
    }

    // Converte o label em português para o nome da enum
    private String mapOccupationLabelToEnumName(String filter) {
        for (OccupationType type : OccupationType.values()) {
            if (type.getLabel().equalsIgnoreCase(filter)) {
                return type.name(); // FAMILY, OCCUPATION ou SYNONYMOUS
            }
        }
        return filter;
    }

    // Lista paginada, usando filtro mapeado
    public Page<Occupation> searchOccupations(String filter, Pageable pageable) {
        String enumFilter = mapOccupationLabelToEnumName(filter);
        return occupationRepository.searchByFilterPaginated(enumFilter, pageable);
    }
}
