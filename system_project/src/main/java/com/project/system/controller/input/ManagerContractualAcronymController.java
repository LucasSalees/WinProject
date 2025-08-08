package com.project.system.controller.input;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.ContractualAcronym;
import com.project.system.entity.User;
import com.project.system.service.input.ManagerContractualAcronymService;
import com.project.system.utils.AuthenticationUtils;

@Controller
public class ManagerContractualAcronymController {

	@Autowired
	private ManagerContractualAcronymService contractualAcronymService;

	@GetMapping("/input/manager/acronyms/register")
	@PreAuthorize("hasAuthority('CONTRACTUAL_ACRONYM_REGISTER')")
	public ModelAndView register(ContractualAcronym acronym, Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		ModelAndView mv = new ModelAndView("/input/manager/acronyms/register");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronym", acronym);
		return mv;
	}

	@GetMapping("/input/manager/acronyms/list")
	@PreAuthorize("hasAuthority('CONTRACTUAL_ACRONYM_LIST')")
	public ModelAndView contractualAcronymList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<ContractualAcronym> acronyms;

		if (filter != null && !filter.trim().isEmpty()) {
			acronyms = contractualAcronymService.searchContractualAcronym(filter);
		} else {
			acronyms = contractualAcronymService.getAllContractualAcronym();
		}

		ModelAndView mv = new ModelAndView("input/manager/acronyms/list");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronymsList", acronyms);
		mv.addObject("filter", filter); // devolve o filtro para manter no input
		return mv;
	}

	@GetMapping("/input/manager/acronyms/print")
	@PreAuthorize("hasAuthority('CONTRACTUAL_ACRONYM_LIST')")
	public ModelAndView printContractualAcronym(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<ContractualAcronym> acronyms;

		if (filter != null && !filter.isEmpty()) {
			acronyms = contractualAcronymService.searchContractualAcronym(filter);
		} else {
			acronyms = contractualAcronymService.getAllContractualAcronym();
		}

		ModelAndView mv = new ModelAndView("input/manager/acronyms/print");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronymsList", acronyms);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/manager/acronyms/print/{acronymId}")
	@PreAuthorize("hasAuthority('CONTRACTUAL_ACRONYM_LIST')")
	public ModelAndView printContractualAcronym(@PathVariable Long acronymId, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		ContractualAcronym acronym = contractualAcronymService.getContractualAcronymById(acronymId)
				.orElseThrow(() -> new RuntimeException("Sigla contratual não encontrada"));

		ModelAndView mv = new ModelAndView("input/manager/acronyms/printOne");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronym", acronym);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/manager/acronyms/edit/{acronymId}")
	@PreAuthorize("hasAuthority('CONTRACTUAL_ACRONYM_EDIT')")
	public ModelAndView editContractualAcronym(@PathVariable("acronymId") Long acronymId, Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		Optional<ContractualAcronym> acronymOpt = contractualAcronymService.getContractualAcronymById(acronymId);
		if (acronymOpt.isEmpty()) {
			return new ModelAndView("redirect:/input/manager/acronyms/list");
		}

		ContractualAcronym acronym = acronymOpt.get();

		ModelAndView mv = new ModelAndView("input/manager/acronyms/edit");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronym", acronym);
		return mv;
	}

	@GetMapping("/input/manager/removeContractualAcronym/{acronymId}")
	@PreAuthorize("hasAuthority('CONTRACTUAL_ACRONYM_DELETE')")
	@ResponseBody
	public ResponseEntity<StandardResponseDTO> removeContractualAcronym(@PathVariable("acronymId") Long acronymId,
			Authentication authentication) {
		return contractualAcronymService.removeContractualAcronym(acronymId);
	}

	@PostMapping("/input/manager/acronyms/edit")
	@PreAuthorize("hasAuthority('CONTRACTUAL_ACRONYM_SAVE_EDIT')")
	@ResponseBody
	public ResponseEntity<StandardResponseDTO> saveEditions(@ModelAttribute("contractualAcronym") ContractualAcronym acronym,
			BindingResult result, Authentication authentication) {
		return contractualAcronymService.saveEditions(acronym);
	}

	@PostMapping("/input/manager/acronyms/save")
	@PreAuthorize("hasAuthority('CONTRACTUAL_ACRONYM_REGISTER')")
	@ResponseBody
	public ResponseEntity<StandardResponseDTO> saveContractualAcronym(@ModelAttribute ContractualAcronym acronym,
			BindingResult result, Authentication authentication) {
		return contractualAcronymService.saveContractualAcronym(acronym);
	}
}
