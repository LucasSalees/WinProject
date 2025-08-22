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
import com.project.system.entity.ContractualAcronym;
import com.project.system.repositories.ContractualAcronymRepository;

@Service
public class ManagerContractualAcronymService {

	
	@Autowired
	private ContractualAcronymRepository contractualAcronymRepository;
	
	// Método paginado para pegar todas as ocupações
    public Page<ContractualAcronym> getAllAcronymsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return contractualAcronymRepository.findAll(pageable);
    }

    // Método paginado para buscar com filtro
    public Page<ContractualAcronym> searchAcronymsPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return contractualAcronymRepository.searchByFilterPaginated(filter, pageable);
    }

	public List<ContractualAcronym> getAllContractualAcronym() {
		return contractualAcronymRepository.findAll();
	}

	public Optional<ContractualAcronym> getContractualAcronymById(Long contractualAcronymId) {
		return contractualAcronymRepository.findById(contractualAcronymId);
	}

	public ResponseEntity<StandardResponseDTO> removeContractualAcronym(Long contractualAcronymId) {
		Optional<ContractualAcronym> contractualAcronymOpt = contractualAcronymRepository.findById(contractualAcronymId);
		if (contractualAcronymOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(StandardResponseDTO.error("Sigla contratual não encontrada!"));
		}

		contractualAcronymRepository.deleteById(contractualAcronymId);
		return ResponseEntity.ok(StandardResponseDTO.success("Sigla contratual removida com sucesso!"));
	}

	public ResponseEntity<StandardResponseDTO> saveEditions(ContractualAcronym contractualAcronym) {
		try {
			Optional<ContractualAcronym> contractualAcronymExistsOpt = contractualAcronymRepository.findById(contractualAcronym.getAcronymId());
			if (contractualAcronymExistsOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(StandardResponseDTO.error("Sigla contratual não encontrado."));
			}

			ContractualAcronym contractualAcronymExists = contractualAcronymExistsOpt.get();

			contractualAcronymExists.setContractualAcronymName(contractualAcronym.getContractualAcronymName());
			contractualAcronymExists.setAcronym(contractualAcronym.getAcronym());

			contractualAcronymRepository.save(contractualAcronymExists);

			return ResponseEntity.ok(StandardResponseDTO.success("Sigla contratual atualizada com sucesso."));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(StandardResponseDTO.error("Erro ao editar o sigla contratual."));
		}
	}

	public ResponseEntity<StandardResponseDTO> saveContractualAcronym(ContractualAcronym contractualAcronym) {
		try {
			contractualAcronymRepository.save(contractualAcronym);
			return ResponseEntity.ok(StandardResponseDTO.success("Sigla contratual cadastrada com sucesso!"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(StandardResponseDTO.error("Erro ao cadastrar sigla contratual: " + e.getMessage()));
		}
	}

	public List<ContractualAcronym> searchContractualAcronym(String filter) {
		return contractualAcronymRepository.searchByFilter(filter);
	}
}
