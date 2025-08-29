package com.project.system.enums.input;

public enum UserPermission {
	
	// Permissões de usuários
	USER_LIST("Lista"),
	USER_REGISTER("Cadastrar"),
	USER_EDIT("Editar"),
	USER_SAVE_EDIT("Salvar edição"),
	USER_DELETE("Excluir"),
	
	// Permissões de departamentos
	DEPARTMENT_LIST("Lista"),
	DEPARTMENT_REGISTER("Cadastrar"),
	DEPARTMENT_EDIT("Editar"),
	DEPARTMENT_SAVE_EDIT("Salvar edição"),
	DEPARTMENT_DELETE("Excluir"),
	
	// Permissões de ocupações (profissões)
	OCCUPATION_LIST("Lista"),
	OCCUPATION_REGISTER("Cadastrar"),
	OCCUPATION_EDIT("Editar"),
	OCCUPATION_SAVE_EDIT("Salvar edição"),
	OCCUPATION_DELETE("Excluir"),
	
	// Permissões de funções
	FUNCTION_LIST("Lista"),
	FUNCTION_REGISTER("Cadastrar"),
	FUNCTION_EDIT("Editar"),
	FUNCTION_SAVE_EDIT("Salvar edição"),
	FUNCTION_DELETE("Excluir"),
	
	// Permissões de projetos
	PROJECT_LIST("Lista"),
	PROJECT_REGISTER("Cadastrar"),
	PROJECT_EDIT("Editar"),
	PROJECT_SAVE_EDIT("Salvar edição"),
	PROJECT_DELETE("Excluir"),
	
	// Permissões de siglas contratuais
	CONTRACTUAL_ACRONYM_LIST("Lista"),
	CONTRACTUAL_ACRONYM_REGISTER("Cadastrar"),
	CONTRACTUAL_ACRONYM_EDIT("Editar"),
	CONTRACTUAL_ACRONYM_SAVE_EDIT("Salvar edição"),
	CONTRACTUAL_ACRONYM_DELETE("Excluir"),
	
	// Permissões para relatórios
	REPORT_OCCUPATION("Profissões"),
	REPORT_FUNCTION("Funções"),
	REPORT_DEPARTMENT("Departamentos"),
	REPORT_USER("Usuários"),
	REPORT_CONTRACTUAL_ACRONYM("Siglas Contratuais"),
	REPORT_PROJECT("Projetos"),
    
    // *** PERMISSÃO ADICIONADA AQUI ***
    AUDIT_VIEW("Visualizar Auditoria");

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