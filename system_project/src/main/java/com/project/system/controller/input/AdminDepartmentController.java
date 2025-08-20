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
import com.project.system.service.input.AdminDepartmentService;
import com.project.system.utils.AuthenticationUtils;

@Controller
public class AdminDepartmentController {

	@Autowired
	private AdminDepartmentService departmentService;

	@GetMapping("/input/admin/departments/register")
	@PreAuthorize("hasAuthority('DEPARTMENT_REGISTER')")
	public ModelAndView register(Department department, Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		ModelAndView mv = new ModelAndView("/input/admin/departments/register");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("department", department);
		return mv;
	}

	@GetMapping("/input/admin/departments/list")
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

		ModelAndView mv = new ModelAndView("input/admin/departments/list");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("departmentsList", departments);
		mv.addObject("filter", filter);
		return mv;
	}

	@GetMapping("/input/admin/departments/edit/{departmentId}")
	@PreAuthorize("hasAuthority('DEPARTMENT_EDIT')")
	public ModelAndView editDepartment(@PathVariable("departmentId") Long departmentId, Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		Optional<Department> departmentOpt = departmentService.getDepartmentById(departmentId);
		if (departmentOpt.isEmpty()) {
			return new ModelAndView("redirect:/input/admin/departments/list");
		}

		Department department = departmentOpt.get();

		ModelAndView mv = new ModelAndView("input/admin/departments/edit");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("department", department);
		return mv;
	}

	@GetMapping("/input/admin/removeDepartment/{departmentId}")
	@PreAuthorize("hasAuthority('DEPARTMENT_DELETE')")
	@ResponseBody
	public ResponseEntity<StandardResponseDTO> removeDepartment(@PathVariable("departmentId") Long departmentId,
			Authentication authentication) {
		return departmentService.removeDepartment(departmentId);
	}

	@PostMapping("/input/admin/departments/edit")
	@PreAuthorize("hasAuthority('DEPARTMENT_SAVE_EDIT')")
	@ResponseBody
	public ResponseEntity<StandardResponseDTO> saveEditions(@ModelAttribute("department") Department department,
			BindingResult result, Authentication authentication) {
		return departmentService.saveEditions(department);
	}

	@PostMapping("/input/admin/departments/save")
	@PreAuthorize("hasAuthority('DEPARTMENT_REGISTER')")
	@ResponseBody
	public ResponseEntity<StandardResponseDTO> saveDepartment(@ModelAttribute Department department,
			BindingResult result, Authentication authentication) {
		return departmentService.saveDepartment(department);
	}
}
