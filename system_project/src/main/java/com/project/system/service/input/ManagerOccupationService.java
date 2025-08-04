package com.project.system.service.input;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.Occupation;
import com.project.system.repositories.OccupationRepository;

@Service
public class ManagerOccupationService {

    @Autowired
    private OccupationRepository occupationRepository;

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
            occupationExists.setOccupationEmail(occupation.getOccupationEmail());
            occupationExists.setOccupationTel(occupation.getOccupationTel());

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
    
    public List<Occupation> searchOccupations(String filter) {
        return occupationRepository.searchByFilter(filter);
    }
}
