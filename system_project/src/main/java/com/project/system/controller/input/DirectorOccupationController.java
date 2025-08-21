package com.project.system.controller.input;

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
import com.project.system.entity.Occupation;
import com.project.system.entity.User;
import com.project.system.service.input.DirectorOccupationService;
import com.project.system.utils.AuthenticationUtils;

//Para paginação
import org.springframework.data.domain.Page;

@Controller
public class DirectorOccupationController {

    @Autowired
    private DirectorOccupationService occupationService;

    @GetMapping("/input/director/occupations/register")
    @PreAuthorize("hasAuthority('OCCUPATION_REGISTER')")
    public ModelAndView register(Occupation occupation, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        ModelAndView mv = new ModelAndView("input/director/occupations/register");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("occupation", occupation);
        return mv;
    }
    
    @GetMapping("/input/director/occupations/list")
    @PreAuthorize("hasAuthority('OCCUPATION_LIST')")
    public ModelAndView occupationsList(
            @RequestParam(value = "filter", required = false) String filter,
            Authentication authentication) {

        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        ModelAndView mv = new ModelAndView("input/director/occupations/list");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("filter", filter);
        return mv;
    }
    
    @GetMapping("/input/director/occupations/page")
    @PreAuthorize("hasAuthority('OCCUPATION_LIST')")
    @ResponseBody
    public Page<Occupation> occupationsPage(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {

        if (filter != null && !filter.trim().isEmpty()) {
            return occupationService.searchOccupationsPaginated(filter, page, size);
        } else {
            return occupationService.getAllOccupationsPaginated(page, size);
        }
    }

    @GetMapping("/input/director/occupations/edit/{occupationId}")
    @PreAuthorize("hasAuthority('OCCUPATION_EDIT')")
    public ModelAndView editOccupation(@PathVariable("occupationId") Long occupationId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        Optional<Occupation> occupationOpt = occupationService.getOccupationById(occupationId);
        if (occupationOpt.isEmpty()) {
            return new ModelAndView("redirect:/input/director/occupations/list");
        }

        Occupation occupation = occupationOpt.get();

        ModelAndView mv = new ModelAndView("input/director/occupations/edit");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("occupation", occupation);
        return mv;
    }

    @GetMapping("/input/director/removeOccupation/{occupationId}")
    @PreAuthorize("hasAuthority('OCCUPATION_DELETE')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> removeOccupation(@PathVariable("occupationId") Long occupationId,
            Authentication authentication) {
        return occupationService.removeOccupation(occupationId);
    }

    @PostMapping("/input/director/occupations/edit")
    @PreAuthorize("hasAuthority('OCCUPATION_SAVE_EDIT')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveEditions(@ModelAttribute("occupation") Occupation occupation,
            BindingResult result, Authentication authentication) {
        return occupationService.saveEditions(occupation);
    }

    @PostMapping("/input/director/occupations/save")
    @PreAuthorize("hasAuthority('OCCUPATION_REGISTER')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveOccupation(@ModelAttribute Occupation occupation, BindingResult result,
            Authentication authentication) {
        return occupationService.saveOccupation(occupation);
    }
}

