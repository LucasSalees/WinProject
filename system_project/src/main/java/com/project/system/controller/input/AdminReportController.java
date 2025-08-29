package com.project.system.controller.input;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.project.system.entity.ContractualAcronym;
import com.project.system.entity.Department;
import com.project.system.entity.Function;
import com.project.system.entity.Occupation;
import com.project.system.entity.Project;
import com.project.system.entity.User;
import com.project.system.enums.input.BrazilianStateUF;
import com.project.system.enums.input.OccupationType;
import com.project.system.enums.input.ProjectBusinessVertical;
import com.project.system.enums.input.ProjectPriority;
import com.project.system.enums.input.ProjectStatus;
import com.project.system.enums.input.UserPermission;
import com.project.system.service.input.AdminContractualAcronymService;
import com.project.system.service.input.AdminDepartmentService;
import com.project.system.service.input.AdminFunctionService;
import com.project.system.service.input.AdminOccupationService;
import com.project.system.service.input.AdminProjectService;
import com.project.system.service.input.AdminService;
import com.project.system.utils.AuthenticationUtils;

@Controller
@PreAuthorize("hasRole(\'ADMIN\')")
public class AdminReportController {

    @Autowired
    private AdminService adminService;
    
    @Autowired
    private AdminOccupationService occupationService;
    
	@Autowired
	private AdminFunctionService functionService;
	
	@Autowired
	private AdminDepartmentService departmentService;
	
	@Autowired
	private AdminContractualAcronymService contractualAcronymService;

    @Autowired
    private AdminProjectService projectService;
    
    /*USUÁRIOS*/
    
    @GetMapping("/input/admin/reports/listUser")
    @PreAuthorize("hasAnyAuthority(\'REPORT_USER\')")
    public ModelAndView userList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		ModelAndView mv = new ModelAndView("input/admin/reports/listUser");
		
        mv.addObject("occupationType", OccupationType.values());
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("filter", filter);
		return mv;
	}
    
	@GetMapping("/input/admin/reports/pageUser")
    @PreAuthorize("hasAuthority('REPORT_USER')")
    @ResponseBody
    public Page<User> usersPage(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {

        if (filter != null && !filter.trim().isEmpty()) {
            return adminService.searchUsersPaginated(filter, page, size);
        } else {
            return adminService.getAllUsersPaginated(page, size);
        }
    }
    
	@GetMapping("/input/admin/reports/editUser/{id}")
    @PreAuthorize("hasAuthority(\'USER_EDIT\')")
    public ModelAndView editUserForm(@PathVariable Long id, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("input/admin/reports/editUser");

        Optional<User> userOpt = adminService.getUserById(id);
        if (userOpt.isEmpty()) {

            return new ModelAndView("redirect:/error");
        }
        
        User userToEdit = userOpt.get();

        Map<UserPermission, Boolean> permissionsMap = Arrays.stream(UserPermission.values())
            .collect(Collectors.toMap(
                permission -> permission,
                permission -> userToEdit.getPermissions().contains(permission)
            ));

        mv.addObject("user", userToEdit);
        mv.addObject("userPermissions", userToEdit.getPermissions());

        mv.addObject("permissionsMap", permissionsMap);
        mv.addObject("allPermissions", UserPermission.values());
        mv.addObject("occupationType", OccupationType.values());
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("departments", adminService.getAllDepartments());
        mv.addObject("occupations", adminService.getAllOccupations());
        mv.addObject("functions", adminService.getAllFunctions());
        mv.addObject("projects", adminService.getAllProjects());
        mv.addObject("acronyms", adminService.getAllAcronyms());

        return mv;
    }
    
    @GetMapping("/input/admin/reports/printUser")
	@PreAuthorize("hasAuthority(\'REPORT_USER\')")
	public ModelAndView printUsers(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<User> users;

		if (filter != null && !filter.isEmpty()) {
			users = adminService.searchUsers(filter);
		} else {
			users = adminService.getAllUsers();
		}

		ModelAndView mv = new ModelAndView("input/admin/reports/printUser");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("usersList", users);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

    @GetMapping("/input/admin/reports/printOneUser/{userId}")
    @PreAuthorize("hasAuthority('REPORT_USER')")
    public ModelAndView printUser(@PathVariable Long userId, Authentication authentication) {

        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        User user = adminService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ModelAndView mv = new ModelAndView("input/admin/reports/printOneUser");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("user", user); 
        mv.addObject("dataAtual", new java.util.Date());
        return mv;
    }
    
    /*USUÁRIOS*/
    
    /*PROFISSÕES*/
	
	@GetMapping("/input/admin/reports/listOccupation")
    @PreAuthorize("hasAuthority('REPORT_OCCUPATION')")
    public ModelAndView occupationsList(@RequestParam(value = "filter", required = false) String filter,
                                        Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        ModelAndView mv = new ModelAndView("input/admin/reports/listOccupation");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("filter", filter); // devolve o filtro para manter no input
        return mv;
    }
	
    @GetMapping("/input/admin/reports/pageOccupation")
    @PreAuthorize("hasAuthority('REPORT_OCCUPATION')")
    @ResponseBody
    public Page<Occupation> occupationsPage(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size,
            @RequestParam(value = "sortBy", defaultValue = "occupationCBO") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {

        if (filter != null && !filter.trim().isEmpty()) {
            return occupationService.searchOccupationsPaginated(filter, page, size, sortBy, sortDirection);
        } else {
            return occupationService.getAllOccupationsPaginated(page, size, sortBy, sortDirection);
        }
    }

    @GetMapping("/input/admin/reports/editOccupation/{occupationId}")
    @PreAuthorize("hasAuthority('REPORT_OCCUPATION')")
    public ModelAndView editOccupation(@PathVariable("occupationId") Long occupationId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        Optional<Occupation> occupationOpt = occupationService.getOccupationById(occupationId);
        if (occupationOpt.isEmpty()) {
            return new ModelAndView("redirect:/input/admin/reports/listOccupation");
        }

        Occupation occupation = occupationOpt.get();

        ModelAndView mv = new ModelAndView("input/admin/reports/editOccupation");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("occupation", occupation);
        return mv;
    }
	
	@GetMapping("/input/admin/reports/printOccupation")
    @PreAuthorize("hasAuthority('REPORT_OCCUPATION')")
    public ModelAndView printOccupations(
            @RequestParam(required = false) String filter,
            Authentication authentication) {

        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        List<Occupation> occupations;

        if (filter != null && !filter.isEmpty()) {
        	occupations = occupationService.searchOccupations(filter);
        } else {
        	occupations = occupationService.getAllOccupations();
        }

        ModelAndView mv = new ModelAndView("input/admin/reports/printOccupation");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("occupationsList", occupations);
        mv.addObject("dataAtual", new java.util.Date());
        return mv;
    }
    
    @GetMapping("/input/admin/reports/printOneOccupation/{occupationId}")
    @PreAuthorize("hasAuthority('REPORT_OCCUPATION')")
    public ModelAndView printOccupation(@PathVariable Long occupationId, Authentication authentication) {

        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        Occupation occupation = occupationService.getOccupationById(occupationId)
                                                 .orElseThrow(() -> new RuntimeException("Profissão não encontrada"));

        ModelAndView mv = new ModelAndView("input/admin/reports/printOneOccupation");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("occupation", occupation);
        mv.addObject("dataAtual", new java.util.Date());
        return mv;
    }
    
    /*PROFISSÕES*/
    
    /*FUNÇÕES*/
    
    @GetMapping("/input/admin/reports/listFunction")
	@PreAuthorize("hasAuthority('REPORT_FUNCTION')")
	public ModelAndView functionsList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		ModelAndView mv = new ModelAndView("input/admin/reports/listFunction");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("filter", filter); // devolve o filtro para manter no input
		return mv;
	}
    
	@GetMapping("/input/admin/reports/pageFunction")
    @PreAuthorize("hasAuthority('REPORT_FUNCTION')")
    @ResponseBody
    public Page<Function> functionsPage(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {

        if (filter != null && !filter.trim().isEmpty()) {
            return functionService.searchFunctionsPaginated(filter, page, size);
        } else {
            return functionService.getAllFunctionsPaginated(page, size);
        }
    }

	@GetMapping("/input/admin/reports/editFunction/{functionId}")
	@PreAuthorize("hasAuthority('REPORT_FUNCTION')")
	public ModelAndView editFunction(@PathVariable("functionId") Long functionId, Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		Optional<Function> functionOpt = functionService.getFunctionById(functionId);
		if (functionOpt.isEmpty()) {
			return new ModelAndView("redirect:/input/admin/reports/listFunction");
		}

		Function function = functionOpt.get();

		ModelAndView mv = new ModelAndView("input/admin/reports/editFunction");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("function", function);
		return mv;
	}
    
	@GetMapping("/input/admin/reports/printFunction")
	@PreAuthorize("hasAuthority('REPORT_FUNCTION')")
	public ModelAndView printFunctions(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<Function> functions;

		if (filter != null && !filter.trim().isEmpty()) {
			functions = functionService.searchFunctions(filter);
		} else {
			functions = functionService.getAllFunctions();
		}

		ModelAndView mv = new ModelAndView("input/admin/reports/printFunction");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("functionsList", functions);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/admin/reports/printOneFunction/{functionId}")
	@PreAuthorize("hasAuthority('REPORT_FUNCTION')")
	public ModelAndView printFunction(@PathVariable Long functionId, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		Function function = functionService.getFunctionById(functionId)
				.orElseThrow(() -> new RuntimeException("Função não encontrada"));

		ModelAndView mv = new ModelAndView("input/admin/reports/printOneFunction");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("function", function);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}
	
	/*FUNÇÕES*/
	
	/*DEPARTAMENTOS*/
	
	@GetMapping("/input/admin/reports/listDepartment")
	@PreAuthorize("hasAuthority('REPORT_DEPARTMENT')")
	public ModelAndView departmentsList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		ModelAndView mv = new ModelAndView("input/admin/reports/listDepartment");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("filter", filter); // devolve o filtro para manter no input
		return mv;
	}
	
	@GetMapping("/input/admin/reports/pageDepartment")
    @PreAuthorize("hasAuthority('REPORT_DEPARTMENT')")
    @ResponseBody
    public Page<Department> departmentsPage(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {

        if (filter != null && !filter.trim().isEmpty()) {
            return departmentService.searchDepartmentsPaginated(filter, page, size);
        } else {
            return departmentService.getAllDepartmentsPaginated(page, size);
        }
    }
	
	@GetMapping("/input/admin/reports/editDepartment/{departmentId}")
	@PreAuthorize("hasAuthority('REPORT_DEPARTMENT')")
	public ModelAndView editDepartment(@PathVariable("departmentId") Long departmentId, Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		Optional<Department> departmentOpt = departmentService.getDepartmentById(departmentId);
		if (departmentOpt.isEmpty()) {
			return new ModelAndView("redirect:/input/admin/reports/listDepartment");
		}

		Department department = departmentOpt.get();

		ModelAndView mv = new ModelAndView("input/admin/reports/editDepartment");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("department", department);
		return mv;
	}
	
	@GetMapping("/input/admin/reports/printDepartment")
	@PreAuthorize("hasAuthority('REPORT_DEPARTMENT')")
	public ModelAndView printDepartments(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<Department> departments;

		if (filter != null && !filter.isEmpty()) {
			departments = departmentService.searchDepartments(filter);
		} else {
			departments = departmentService.getAllDepartments();
		}

		ModelAndView mv = new ModelAndView("input/admin/reports/printDepartment");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("departmentsList", departments);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/admin/reports/printOneDepartment/{departmentId}")
	@PreAuthorize("hasAuthority('REPORT_DEPARTMENT')")
	public ModelAndView printDepartment(@PathVariable Long departmentId, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		Department department = departmentService.getDepartmentById(departmentId)
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		ModelAndView mv = new ModelAndView("input/admin/reports/printOneDepartment");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("department", department);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}
	
	/*DEPARTAMENTOS*/
	
	/*SIGLAS CONTRATUAIS*/
	
	@GetMapping("/input/admin/reports/listAcronym")
	@PreAuthorize("hasAuthority('REPORT_CONTRACTUAL_ACRONYM')")
	public ModelAndView contractualAcronymList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		ModelAndView mv = new ModelAndView("input/admin/reports/listAcronym");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("filter", filter);
		return mv;
	}
	
	@GetMapping("/input/admin/reports/pageAcronym")
    @PreAuthorize("hasAuthority('REPORT_CONTRACTUAL_ACRONYM')")
    @ResponseBody
    public Page<ContractualAcronym> acronymsPage(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {

        if (filter != null && !filter.trim().isEmpty()) {
            return contractualAcronymService.searchAcronymsPaginated(filter, page, size);
        } else {
            return contractualAcronymService.getAllAcronymsPaginated(page, size);
        }
    }
	
	@GetMapping("/input/admin/reports/editAcronym/{acronymId}")
	@PreAuthorize("hasAuthority('REPORT_CONTRACTUAL_ACRONYM')")
	public ModelAndView editContractualAcronym(@PathVariable("acronymId") Long acronymId, Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		Optional<ContractualAcronym> acronymOpt = contractualAcronymService.getContractualAcronymById(acronymId);
		if (acronymOpt.isEmpty()) {
			return new ModelAndView("redirect:/input/admin/reports/listAcronym");
		}

		ContractualAcronym acronym = acronymOpt.get();

		ModelAndView mv = new ModelAndView("input/admin/reports/editAcronym");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronym", acronym);
		return mv;
	}
	
	@GetMapping("/input/admin/reports/printAcronym")
	@PreAuthorize("hasAuthority('REPORT_CONTRACTUAL_ACRONYM')")
	public ModelAndView printContractualAcronym(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<ContractualAcronym> acronyms;

		if (filter != null && !filter.isEmpty()) {
			acronyms = contractualAcronymService.searchContractualAcronym(filter);
		} else {
			acronyms = contractualAcronymService.getAllContractualAcronym();
		}

		ModelAndView mv = new ModelAndView("input/admin/reports/printAcronym");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronymsList", acronyms);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/admin/reports/printOneAcronym/{acronymId}")
	@PreAuthorize("hasAuthority('REPORT_CONTRACTUAL_ACRONYM')")
	public ModelAndView printContractualAcronym(@PathVariable Long acronymId, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		ContractualAcronym acronym = contractualAcronymService.getContractualAcronymById(acronymId)
				.orElseThrow(() -> new RuntimeException("Sigla contratual não encontrada"));

		ModelAndView mv = new ModelAndView("input/admin/reports/printOneAcronym");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronym", acronym);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	/*SIGLAS CONTRATUAIS*/
	
	/*PROJETOS*/
	
    @GetMapping("/input/admin/reports/listProject")
	@PreAuthorize("hasAuthority('REPORT_PROJECT')")
	public ModelAndView projectsList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		ModelAndView mv = new ModelAndView("input/admin/reports/listProject");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("filter", filter); // devolve o filtro para manter no input
		return mv;
	}
    
	@GetMapping("/input/admin/reports/pageProject")
    @PreAuthorize("hasAuthority('REPORT_PROJECT')")
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
    
    @GetMapping("/input/admin/reports/editProject/{projectId}")
    @PreAuthorize("hasAuthority('REPORT_PROJECT')")
    public ModelAndView editProject(@PathVariable("projectId") Long projectId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) {
            return new ModelAndView("redirect:/input/admin/reports/listProject");
        }

        Project project = projectOpt.get();
        project.setProjectCurrentDate(LocalDate.now());

        ModelAndView mv = new ModelAndView("input/admin/reports/editProject");
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
    
	@GetMapping("/input/admin/reports/printProject")
	@PreAuthorize("hasAuthority('REPORT_PROJECT')")
	public ModelAndView printProjects(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<Project> projects;

		if (filter != null && !filter.isEmpty()) {
			projects = projectService.searchProjects(filter);
		} else {
			projects = projectService.getAllProjects();
		}

		ModelAndView mv = new ModelAndView("input/admin/reports/printProject");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("projectsList", projects);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/admin/reports/printOneProject/{projectId}")
	@PreAuthorize("hasAuthority('REPORT_PROJECT')")
	public ModelAndView printProject(@PathVariable Long projectId, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		Project project = projectService.getProjectById(projectId)
				.orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

		ModelAndView mv = new ModelAndView("input/admin/reports/printOneProject");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("projects", project);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}
	
	/*PROJETOS*/
	
}