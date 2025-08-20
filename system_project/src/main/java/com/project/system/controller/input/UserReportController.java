package com.project.system.controller.input;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.project.system.entity.ContractualAcronym;
import com.project.system.entity.Department;
import com.project.system.entity.Function;
import com.project.system.entity.Occupation;
import com.project.system.entity.Project;
import com.project.system.entity.User;
import com.project.system.enums.input.BrazilianStateUF;
import com.project.system.enums.input.ProjectBusinessVertical;
import com.project.system.enums.input.ProjectPriority;
import com.project.system.enums.input.ProjectStatus;
import com.project.system.enums.input.UserPermission;
import com.project.system.service.input.UserContractualAcronymService;
import com.project.system.service.input.UserDepartmentService;
import com.project.system.service.input.UserFunctionService;
import com.project.system.service.input.UserOccupationService;
import com.project.system.service.input.UserProjectService;
import com.project.system.service.input.UserService;
import com.project.system.utils.AuthenticationUtils;

@Controller
@PreAuthorize("hasRole(\'USER\')")
public class UserReportController {

    @Autowired
    private UserService Service;
    
    @Autowired
    private UserOccupationService occupationService;
    
	@Autowired
	private UserFunctionService functionService;
	
	@Autowired
	private UserDepartmentService departmentService;
	
	@Autowired
	private UserContractualAcronymService contractualAcronymService;

    @Autowired
    private UserProjectService projectService;
    
    /*USUÁRIOS*/
    
    @GetMapping("/input/user/reports/listUser")
    @PreAuthorize("hasAnyAuthority(\'REPORT_USER\')")
    public ModelAndView userList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<User> users;

		if (filter != null && !filter.trim().isEmpty()) {
			users = Service.searchUsers(filter);
		} else {
			users = Service.getAllUsers();
		}

		ModelAndView mv = new ModelAndView("input/user/reports/listUser");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("usersList", users);
		mv.addObject("filter", filter);
		return mv;
	}
    
	@GetMapping("/input/user/reports/editUser/{id}")
    @PreAuthorize("hasAuthority(\'USER_EDIT\')")
    public ModelAndView editUserForm(@PathVariable Long id, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("input/user/reports/editUser");

        Optional<User> userOpt = Service.getUserById(id);
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
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("departments", Service.getAllDepartments());
        mv.addObject("occupations", Service.getAllOccupations());
        mv.addObject("functions", Service.getAllFunctions());
        mv.addObject("projects", Service.getAllProjects());
        mv.addObject("acronyms", Service.getAllAcronyms());

        return mv;
    }
    
    @GetMapping("/input/user/reports/printUser")
	@PreAuthorize("hasAuthority(\'REPORT_USER\')")
	public ModelAndView printUsers(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<User> users;

		if (filter != null && !filter.isEmpty()) {
			users = Service.searchUsers(filter);
		} else {
			users = Service.getAllUsers();
		}

		ModelAndView mv = new ModelAndView("input/user/reports/printUser");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("usersList", users);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

    @GetMapping("/input/user/reports/printOneUser/{userId}")
    @PreAuthorize("hasAuthority('REPORT_USER')")
    public ModelAndView printUser(@PathVariable Long userId, Authentication authentication) {

        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        User user = Service.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ModelAndView mv = new ModelAndView("input/user/reports/printOneUser");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("user", user); 
        mv.addObject("dataAtual", new java.util.Date());
        return mv;
    }
    
    /*USUÁRIOS*/
    
    /*PROFISSÕES*/
	
	@GetMapping("/input/user/reports/listOccupation")
    @PreAuthorize("hasAuthority('REPORT_OCCUPATION')")
    public ModelAndView occupationsList(@RequestParam(value = "filter", required = false) String filter,
                                        Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        List<Occupation> occupations;

        if (filter != null && !filter.trim().isEmpty()) {
            occupations = occupationService.searchOccupations(filter);
        } else {
            occupations = occupationService.getAllOccupations();
        }

        ModelAndView mv = new ModelAndView("input/user/reports/listOccupation");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("occupationsList", occupations);
        mv.addObject("filter", filter); // devolve o filtro para manter no input
        return mv;
    }

    @GetMapping("/input/user/reports/editOccupation/{occupationId}")
    @PreAuthorize("hasAuthority('REPORT_OCCUPATION')")
    public ModelAndView editOccupation(@PathVariable("occupationId") Long occupationId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        Optional<Occupation> occupationOpt = occupationService.getOccupationById(occupationId);
        if (occupationOpt.isEmpty()) {
            return new ModelAndView("redirect:/input/user/reports/listOccupation");
        }

        Occupation occupation = occupationOpt.get();

        ModelAndView mv = new ModelAndView("input/user/reports/editOccupation");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("occupation", occupation);
        return mv;
    }
	
	@GetMapping("/input/user/reports/printOccupation")
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

        ModelAndView mv = new ModelAndView("input/user/reports/printOccupation");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("occupationsList", occupations);
        mv.addObject("dataAtual", new java.util.Date());
        return mv;
    }
    
    @GetMapping("/input/user/reports/printOneOccupation/{occupationId}")
    @PreAuthorize("hasAuthority('REPORT_OCCUPATION')")
    public ModelAndView printOccupation(@PathVariable Long occupationId, Authentication authentication) {

        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        Occupation occupation = occupationService.getOccupationById(occupationId)
                                                 .orElseThrow(() -> new RuntimeException("Profissão não encontrada"));

        ModelAndView mv = new ModelAndView("input/user/reports/printOneOccupation");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("occupation", occupation);
        mv.addObject("dataAtual", new java.util.Date());
        return mv;
    }
    
    /*PROFISSÕES*/
    
    /*FUNÇÕES*/
    
    @GetMapping("/input/user/reports/listFunction")
	@PreAuthorize("hasAuthority('REPORT_FUNCTION')")
	public ModelAndView functionsList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<Function> functions;

		if (filter != null && !filter.trim().isEmpty()) {
			functions = functionService.searchFunctions(filter);
		} else {
			functions = functionService.getAllFunctions();
		}

		ModelAndView mv = new ModelAndView("input/user/reports/listFunction");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("functionsList", functions);
		mv.addObject("filter", filter); // devolve o filtro para manter no input
		return mv;
	}

	@GetMapping("/input/user/reports/editFunction/{functionId}")
	@PreAuthorize("hasAuthority('REPORT_FUNCTION')")
	public ModelAndView editFunction(@PathVariable("functionId") Long functionId, Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		Optional<Function> functionOpt = functionService.getFunctionById(functionId);
		if (functionOpt.isEmpty()) {
			return new ModelAndView("redirect:/input/director/reports/listFunction");
		}

		Function function = functionOpt.get();

		ModelAndView mv = new ModelAndView("input/user/reports/editFunction");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("function", function);
		return mv;
	}
    
	@GetMapping("/input/user/reports/printFunction")
	@PreAuthorize("hasAuthority('REPORT_FUNCTION')")
	public ModelAndView printFunctions(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<Function> functions;

		if (filter != null && !filter.trim().isEmpty()) {
			functions = functionService.searchFunctions(filter);
		} else {
			functions = functionService.getAllFunctions();
		}

		ModelAndView mv = new ModelAndView("input/user/reports/printFunction");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("functionsList", functions);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/user/reports/printOneFunction/{functionId}")
	@PreAuthorize("hasAuthority('REPORT_FUNCTION')")
	public ModelAndView printFunction(@PathVariable Long functionId, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		Function function = functionService.getFunctionById(functionId)
				.orElseThrow(() -> new RuntimeException("Função não encontrada"));

		ModelAndView mv = new ModelAndView("input/user/reports/printOneFunction");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("function", function);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}
	
	/*FUNÇÕES*/
	
	/*DEPARTAMENTOS*/
	
	@GetMapping("/input/user/reports/listDepartment")
	@PreAuthorize("hasAuthority('REPORT_DEPARTMENT')")
	public ModelAndView departmentsList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<Department> departments;

		if (filter != null && !filter.trim().isEmpty()) {
			departments = departmentService.searchDepartments(filter);
		} else {
			departments = departmentService.getAllDepartments();
		}

		ModelAndView mv = new ModelAndView("input/user/reports/listDepartment");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("departmentsList", departments);
		mv.addObject("filter", filter); // devolve o filtro para manter no input
		return mv;
	}
	
	@GetMapping("/input/user/reports/editDepartment/{departmentId}")
	@PreAuthorize("hasAuthority('REPORT_DEPARTMENT')")
	public ModelAndView editDepartment(@PathVariable("departmentId") Long departmentId, Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		Optional<Department> departmentOpt = departmentService.getDepartmentById(departmentId);
		if (departmentOpt.isEmpty()) {
			return new ModelAndView("redirect:/input/user/reports/listDepartment");
		}

		Department department = departmentOpt.get();

		ModelAndView mv = new ModelAndView("input/user/reports/editDepartment");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("department", department);
		return mv;
	}
	
	@GetMapping("/input/user/reports/printDepartment")
	@PreAuthorize("hasAuthority('REPORT_DEPARTMENT')")
	public ModelAndView printDepartments(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<Department> departments;

		if (filter != null && !filter.isEmpty()) {
			departments = departmentService.searchDepartments(filter);
		} else {
			departments = departmentService.getAllDepartments();
		}

		ModelAndView mv = new ModelAndView("input/user/reports/printDepartment");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("departmentsList", departments);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/user/reports/printOneDepartment/{departmentId}")
	@PreAuthorize("hasAuthority('REPORT_DEPARTMENT')")
	public ModelAndView printDepartment(@PathVariable Long departmentId, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		Department department = departmentService.getDepartmentById(departmentId)
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		ModelAndView mv = new ModelAndView("input/user/reports/printOneDepartment");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("department", department);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}
	
	/*DEPARTAMENTOS*/
	
	/*SIGLAS CONTRATUAIS*/
	
	@GetMapping("/input/user/reports/listAcronym")
	@PreAuthorize("hasAuthority('REPORT_CONTRACTUAL_ACRONYM')")
	public ModelAndView contractualAcronymList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<ContractualAcronym> acronyms;

		if (filter != null && !filter.trim().isEmpty()) {
			acronyms = contractualAcronymService.searchContractualAcronym(filter);
		} else {
			acronyms = contractualAcronymService.getAllContractualAcronym();
		}

		ModelAndView mv = new ModelAndView("input/user/reports/listAcronym");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronymsList", acronyms);
		mv.addObject("filter", filter);
		return mv;
	}
	
	@GetMapping("/input/user/reports/editAcronym/{acronymId}")
	@PreAuthorize("hasAuthority('REPORT_CONTRACTUAL_ACRONYM')")
	public ModelAndView editContractualAcronym(@PathVariable("acronymId") Long acronymId, Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		Optional<ContractualAcronym> acronymOpt = contractualAcronymService.getContractualAcronymById(acronymId);
		if (acronymOpt.isEmpty()) {
			return new ModelAndView("redirect:/input/director/reports/listAcronym");
		}

		ContractualAcronym acronym = acronymOpt.get();

		ModelAndView mv = new ModelAndView("input/user/reports/editAcronym");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronym", acronym);
		return mv;
	}
	
	@GetMapping("/input/user/reports/printAcronym")
	@PreAuthorize("hasAuthority('REPORT_CONTRACTUAL_ACRONYM')")
	public ModelAndView printContractualAcronym(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<ContractualAcronym> acronyms;

		if (filter != null && !filter.isEmpty()) {
			acronyms = contractualAcronymService.searchContractualAcronym(filter);
		} else {
			acronyms = contractualAcronymService.getAllContractualAcronym();
		}

		ModelAndView mv = new ModelAndView("input/user/reports/printAcronym");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronymsList", acronyms);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/user/reports/printOneAcronym/{acronymId}")
	@PreAuthorize("hasAuthority('REPORT_CONTRACTUAL_ACRONYM')")
	public ModelAndView printContractualAcronym(@PathVariable Long acronymId, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		ContractualAcronym acronym = contractualAcronymService.getContractualAcronymById(acronymId)
				.orElseThrow(() -> new RuntimeException("Sigla contratual não encontrada"));

		ModelAndView mv = new ModelAndView("input/user/reports/printOneAcronym");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("acronym", acronym);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	/*SIGLAS CONTRATUAIS*/
	
	/*PROJETOS*/
	
    @GetMapping("/input/user/reports/listProject")
	@PreAuthorize("hasAuthority('REPORT_PROJECT')")
	public ModelAndView projectsList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<Project> projects;

		if (filter != null && !filter.trim().isEmpty()) {
			projects = projectService.searchProjects(filter);
		} else {
			projects = projectService.getAllProjects();
		}

		ModelAndView mv = new ModelAndView("input/user/reports/listProject");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("projectsList", projects);
		mv.addObject("filter", filter); // devolve o filtro para manter no input
		return mv;
	}
    
    @GetMapping("/input/user/reports/editProject/{projectId}")
    @PreAuthorize("hasAuthority('REPORT_PROJECT')")
    public ModelAndView editProject(@PathVariable("projectId") Long projectId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) {
            return new ModelAndView("redirect:/input/user/reports/listProject");
        }

        Project project = projectOpt.get();
        project.setProjectCurrentDate(LocalDate.now());

        ModelAndView mv = new ModelAndView("input/user/reports/editProject");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("project", project);
        mv.addObject("departments", Service.getAllDepartments());
        mv.addObject("acronyms", Service.getAllAcronyms());
        
        // Adiciona os enums antes do return
        mv.addObject("brazilianStates", BrazilianStateUF.values());
        mv.addObject("projectPriorities", ProjectPriority.values());
        mv.addObject("projectBusinessVerticals", ProjectBusinessVertical.values());
        mv.addObject("projectStatuses", ProjectStatus.values());
        
        return mv;
    }
    
	@GetMapping("/input/user/reports/printProject")
	@PreAuthorize("hasAuthority('REPORT_PROJECT')")
	public ModelAndView printProjects(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<Project> projects;

		if (filter != null && !filter.isEmpty()) {
			projects = projectService.searchProjects(filter);
		} else {
			projects = projectService.getAllProjects();
		}

		ModelAndView mv = new ModelAndView("input/user/reports/printProject");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("projectsList", projects);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/user/reports/printOneProject/{projectId}")
	@PreAuthorize("hasAuthority('REPORT_PROJECT')")
	public ModelAndView printProject(@PathVariable Long projectId, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		Project project = projectService.getProjectById(projectId)
				.orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

		ModelAndView mv = new ModelAndView("input/user/reports/printOneProject");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("projects", project);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}
	
	/*PROJETOS*/
	
}