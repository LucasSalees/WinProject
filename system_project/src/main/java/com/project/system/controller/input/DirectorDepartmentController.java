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
import com.project.system.entity.Department;
import com.project.system.entity.User;
import com.project.system.service.input.DirectorDepartmentService;
import com.project.system.utils.AuthenticationUtils;

@Controller
public class DirectorDepartmentController {

    @Autowired
    private DirectorDepartmentService departmentService;

    @GetMapping("/input/director/departments/register")
    @PreAuthorize("hasAuthority('DEPARTMENT_REGISTER')")
    public ModelAndView register(Department department, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        ModelAndView mv = new ModelAndView("/input/director/departments/register");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("department", department);
        return mv;
    }

    @GetMapping("/input/director/departments/list")
    @PreAuthorize("hasAuthority('DEPARTMENT_LIST')")
    public ModelAndView departmentsList(@RequestParam(value = "filter", required = false) String filter,
                                        Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        List<Department> departments;

        if (filter != null && !filter.trim().isEmpty()) {
            departments = departmentService.searchDepartments(filter);
        } else {
            departments = departmentService.getAllDepartments();
        }

        ModelAndView mv = new ModelAndView("input/director/departments/list");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("departmentsList", departments);
        mv.addObject("filter", filter); // devolve o filtro para manter no input
        return mv;
    }
    
    @GetMapping("/input/director/departments/print")
    @PreAuthorize("hasAuthority('DEPARTMENT_LIST')")
    public ModelAndView printDepartments(
            @RequestParam(required = false) String filter,
            Authentication authentication) {

        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        List<Department> departments;

        if (filter != null && !filter.isEmpty()) {
            departments = departmentService.searchDepartments(filter);
        } else {
            departments = departmentService.getAllDepartments();
        }

        ModelAndView mv = new ModelAndView("input/director/departments/print");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("departmentsList", departments);
        mv.addObject("dataAtual", new java.util.Date());
        return mv;
    }
    
    @GetMapping("/input/director/departments/print/{departmentId}")
    @PreAuthorize("hasAuthority('DEPARTMENT_LIST')")
    public ModelAndView printDepartment(
            @PathVariable Long departmentId,
            Authentication authentication) {

        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        Department department = departmentService.getDepartmentById(departmentId)
                                                 .orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

        ModelAndView mv = new ModelAndView("input/director/departments/printOne");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("department", department);
        mv.addObject("dataAtual", new java.util.Date());
        return mv;
    }

    @GetMapping("/input/director/departments/edit/{departmentId}")
    @PreAuthorize("hasAuthority('DEPARTMENT_EDIT')")
    public ModelAndView editDepartment(@PathVariable("departmentId") Long departmentId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        Optional<Department> departmentOpt = departmentService.getDepartmentById(departmentId);
        if (departmentOpt.isEmpty()) {
            return new ModelAndView("redirect:/input/director/departments/list");
        }

        Department department = departmentOpt.get();

        ModelAndView mv = new ModelAndView("/input/director/departments/edit");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("department", department);
        return mv;
    }

    @GetMapping("/input/director/removeDepartment/{departmentId}")
    @PreAuthorize("hasAuthority('DEPARTMENT_DELETE')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> removeDepartment(@PathVariable("departmentId") Long departmentId,
            Authentication authentication) {
        return departmentService.removeDepartment(departmentId);
    }

    @PostMapping("/input/director/departments/edit")
    @PreAuthorize("hasAuthority('DEPARTMENT_SAVE_EDIT')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveEditions(@ModelAttribute("department") Department department,
            BindingResult result, Authentication authentication) {
        return departmentService.saveEditions(department);
    }

    @PostMapping("/input/director/departments/save")
    @PreAuthorize("hasAuthority('USER_REGISTER')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveDepartment(@ModelAttribute Department department, BindingResult result,
            Authentication authentication) {
        return departmentService.saveDepartment(department);
    }
}
