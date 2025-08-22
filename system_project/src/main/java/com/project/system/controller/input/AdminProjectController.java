package com.project.system.controller.input;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.project.system.entity.Project;
import com.project.system.entity.User;
import com.project.system.enums.input.BrazilianStateUF;
import com.project.system.enums.input.ProjectBusinessVertical;
import com.project.system.enums.input.ProjectPriority;
import com.project.system.enums.input.ProjectStatus;
import com.project.system.service.input.AdminProjectService;
import com.project.system.service.input.AdminService;
import com.project.system.repositories.ContractualAcronymRepository;
import com.project.system.repositories.DepartmentRepository;
import com.project.system.utils.AuthenticationUtils;

@Controller
public class AdminProjectController {

    @Autowired
    private AdminProjectService projectService;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private ContractualAcronymRepository contratualAcronymRepository;

    @GetMapping("/input/admin/projects/register")
    @PreAuthorize("hasAuthority('PROJECT_REGISTER')")
    public ModelAndView register(Project project, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        
        // Define datas padrão para projeto novo
        if (project.getProjectRegisterDate() == null) {
            project.setProjectRegisterDate(LocalDate.now());
        }
        if (project.getProjectCurrentDate() == null) {
            project.setProjectCurrentDate(LocalDate.now());
        }

        ModelAndView mv = new ModelAndView("input/admin/projects/register");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("project", project);
        mv.addObject("departments", departmentRepository.findAll());
        mv.addObject("acronyms", contratualAcronymRepository.findAll());
        
        mv.addObject("brazilianStates", BrazilianStateUF.values());
        mv.addObject("projectPriorities", ProjectPriority.values());
        mv.addObject("projectBusinessVerticals", ProjectBusinessVertical.values());
        mv.addObject("projectStatuses", ProjectStatus.values());
        
        return mv;
    }

    @GetMapping("/input/admin/projects/list")
    @PreAuthorize("hasAuthority('PROJECT_LIST')")
    public ModelAndView projectsList(@RequestParam(value = "filter", required = false) String filter,
            Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        ModelAndView mv = new ModelAndView("input/admin/projects/list");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("filter", filter);
        return mv;
    }
    
    @GetMapping("/input/admin/projects/page")
    @PreAuthorize("hasAuthority('PROJECT_LIST')")
    @ResponseBody
    public Page<Project> projectsPage(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {

        if (filter != null && !filter.trim().isEmpty()) {
            return projectService.searchProjectsPaginated(filter, page, size);
        } else {
            return projectService.getAllProjectsPaginated(page, size);
        }
    }

    @GetMapping("/input/admin/projects/edit/{projectId}")
    @PreAuthorize("hasAuthority('PROJECT_EDIT')")
    public ModelAndView editProject(@PathVariable("projectId") Long projectId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) {
            return new ModelAndView("redirect:/input/admin/projects/list");
        }

        Project project = projectOpt.get();
        project.setProjectCurrentDate(LocalDate.now());

        ModelAndView mv = new ModelAndView("input/admin/projects/edit");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("project", project);
        mv.addObject("departments", adminService.getAllDepartments());
        mv.addObject("acronyms", adminService.getAllAcronyms());
        
        // Adiciona os enums antes do return
        mv.addObject("brazilianStates", BrazilianStateUF.values());
        mv.addObject("projectPriorities", ProjectPriority.values());
        mv.addObject("projectBusinessVerticals", ProjectBusinessVertical.values());
        mv.addObject("projectStatuses", ProjectStatus.values());
        
        return mv;
    }

    @GetMapping("/input/admin/removeProject/{projectId}")
    @PreAuthorize("hasAuthority('PROJECT_DELETE')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> removeProject(@PathVariable("projectId") Long projectId,
            Authentication authentication) {
        return projectService.removeProject(projectId);
    }

    @PostMapping("/input/admin/projects/edit")
    @PreAuthorize("hasAuthority('PROJECT_SAVE_EDIT')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveEditions(@ModelAttribute("project") Project project,
            BindingResult result, Authentication authentication) {
        return projectService.saveEditions(project, result);
    }

    @PostMapping("/input/admin/projects/save")
    @PreAuthorize("hasAuthority('PROJECT_REGISTER')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveProject(@ModelAttribute Project project, BindingResult result,
            Authentication authentication) {
        return projectService.saveProject(project, result);
    }
}
