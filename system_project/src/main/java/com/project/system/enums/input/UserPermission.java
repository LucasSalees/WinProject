package com.project.system.enums.input;

public enum UserPermission {
	
	USER_LIST("Lista"),
	USER_REGISTER("Cadastrar"),
	USER_EDIT("Editar"),
	USER_SAVE_EDIT("Salvar edição"),
	USER_DELETE("Excluir"),
	
	DEPARTMENT_LIST("Lista"),
	DEPARTMENT_REGISTER("Cadastrar"),
	DEPARTMENT_EDIT("Editar"),
	DEPARTMENT_SAVE_EDIT("Salvar edição"),
	DEPARTMENT_DELETE("Excluir"),
	
	OCCUPATION_LIST("Lista"),
	OCCUPATION_REGISTER("Cadastrar"),
	OCCUPATION_EDIT("Editar"),
	OCCUPATION_SAVE_EDIT("Salvar edição"),
	OCCUPATION_DELETE("Excluir"),
	
	FUNCTION_LIST("Lista"),
	FUNCTION_REGISTER("Cadastrar"),
	FUNCTION_EDIT("Editar"),
	FUNCTION_SAVE_EDIT("Salvar edição"),
	FUNCTION_DELETE("Excluir"),
	
	PROJECT_LIST("Lista"),
	PROJECT_REGISTER("Cadastrar"),
	PROJECT_EDIT("Editar"),
	PROJECT_SAVE_EDIT("Salvar edição"),
	PROJECT_DELETE("Excluir"),
	
	CONTRACTUAL_ACRONYM_LIST("Lista"),
	CONTRACTUAL_ACRONYM_REGISTER("Cadastrar"),
	CONTRACTUAL_ACRONYM_EDIT("Editar"),
	CONTRACTUAL_ACRONYM_SAVE_EDIT("Salvar edição"),
	CONTRACTUAL_ACRONYM_DELETE("Excluir"),
    ;

    private final String label;

    UserPermission(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getPermission() {
        return this.name(); 
    }
}
