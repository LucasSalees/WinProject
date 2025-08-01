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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.Function;
import com.project.system.entity.User;
import com.project.system.service.input.AdminFunctionService;
import com.project.system.utils.AuthenticationUtils;

@Controller
public class AdminFunctionController {

    @Autowired
    private AdminFunctionService functionService;

    @GetMapping("/input/admin/functions/register")
    @PreAuthorize("hasAuthority('FUNCTION_REGISTER')")
    public ModelAndView register(Function function, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        ModelAndView mv = new ModelAndView("input/admin/functions/register");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("function", function);
        return mv;
    }

    @GetMapping("/input/admin/functions/list")
    @PreAuthorize("hasAuthority('FUNCTION_LIST')")
    public ModelAndView functionsList(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        List<Function> functions = functionService.getAllFunctions();

        ModelAndView mv = new ModelAndView("input/admin/functions/list");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("functionsList", functions);
        return mv;
    }

    @GetMapping("/input/admin/functions/edit/{functionId}")
    @PreAuthorize("hasAuthority('FUNCTION_EDIT')")
    public ModelAndView editFunction(@PathVariable("functionId") Long functionId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        Optional<Function> functionOpt = functionService.getFunctionById(functionId);
        if (functionOpt.isEmpty()) {
            return new ModelAndView("redirect:/input/admin/functions/list");
        }

        Function function = functionOpt.get();

        ModelAndView mv = new ModelAndView("input/admin/functions/edit");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("function", function);
        return mv;
    }

    @GetMapping("/input/admin/removeFunction/{functionId}")
    @PreAuthorize("hasAuthority('FUNCTION_DELETE')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> removeFunction(@PathVariable("functionId") Long functionId,
            Authentication authentication) {
        return functionService.removeFunction(functionId);
    }

    @PostMapping("/input/admin/functions/edit")
    @PreAuthorize("hasAuthority('FUNCTION_SAVE_EDIT')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveEditions(@ModelAttribute("function") Function function,
            BindingResult result, Authentication authentication) {
        return functionService.saveEditions(function);
    }

    @PostMapping("/input/admin/functions/save")
    @PreAuthorize("hasAuthority('FUNCTION_REGISTER')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveFunction(@ModelAttribute Function function, BindingResult result,
            Authentication authentication) {
        return functionService.saveFunction(function);
    }
}
