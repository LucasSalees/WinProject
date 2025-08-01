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
import com.project.system.entity.Function;
import com.project.system.entity.User;
import com.project.system.service.input.DirectorFunctionService;
import com.project.system.utils.AuthenticationUtils;

@Controller
public class DirectorFunctionController {

    @Autowired
    private DirectorFunctionService functionService;

    @GetMapping("/input/director/functions/register")
    @PreAuthorize("hasAuthority('FUNCTION_REGISTER')")
    public ModelAndView register(Function function, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        ModelAndView mv = new ModelAndView("input/director/functions/register");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("function", function);
        return mv;
    }
    
    @GetMapping("/input/director/functions/list")
    @PreAuthorize("hasAuthority('FUNCTION_LIST')")
    public ModelAndView functionsList(@RequestParam(value = "filter", required = false) String filter,
                                        Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        List<Function> functions;

        if (filter != null && !filter.trim().isEmpty()) {
            functions = functionService.searchFunctions(filter);
        } else {
        	functions = functionService.getAllFunctions();
        }

        ModelAndView mv = new ModelAndView("input/director/functions/list");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("functionsList", functions);
        mv.addObject("filter", filter); // devolve o filtro para manter no input
        return mv;
    }
    
    @GetMapping("/input/director/functions/print")
    @PreAuthorize("hasAuthority('DEPARTMENT_LIST')")
    public ModelAndView printFunctions(
            @RequestParam(required = false) String filter,
            Authentication authentication) {

        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        List<Function> functions;

        if (filter != null && !filter.trim().isEmpty()) {
            functions = functionService.searchFunctions(filter);
        } else {
        	functions = functionService.getAllFunctions();
        }

        ModelAndView mv = new ModelAndView("input/director/functions/print");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("functionsList", functions);
        mv.addObject("dataAtual", new java.util.Date());
        return mv;
    }

    @GetMapping("/input/director/functions/edit/{functionId}")
    @PreAuthorize("hasAuthority('FUNCTION_EDIT')")
    public ModelAndView editFunction(@PathVariable("functionId") Long functionId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        Optional<Function> functionOpt = functionService.getFunctionById(functionId);
        if (functionOpt.isEmpty()) {
            return new ModelAndView("redirect:/input/director/functions/list");
        }

        Function function = functionOpt.get();

        ModelAndView mv = new ModelAndView("input/director/functions/edit");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("function", function);
        return mv;
    }

    @GetMapping("/input/director/removeFunction/{functionId}")
    @PreAuthorize("hasAuthority('FUNCTION_DELETE')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> removeFunction(@PathVariable("functionId") Long functionId,
            Authentication authentication) {
        return functionService.removeFunction(functionId);
    }

    @PostMapping("/input/director/functions/edit")
    @PreAuthorize("hasAuthority('FUNCTION_SAVE_EDIT')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveEditions(@ModelAttribute("function") Function function,
            BindingResult result, Authentication authentication) {
        return functionService.saveEditions(function);
    }

    @PostMapping("/input/director/functions/save")
    @PreAuthorize("hasAuthority('FUNCTION_REGISTER')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveFunction(@ModelAttribute Function function, BindingResult result,
            Authentication authentication) {
        return functionService.saveFunction(function);
    }
}
