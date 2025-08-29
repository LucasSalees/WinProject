package com.project.system.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.system.entity.AuditLog;
import com.project.system.service.AuditLogService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private EntityManager entityManager; // Usado para buscar o estado antigo da entidade

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Este "advice" é executado "ao redor" de qualquer método anotado com @Auditable.
     * Ele captura informações antes e depois da execução do método para construir o log.
     */
    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        
        // --- Informações básicas ---
        String action = auditable.action();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String moduleName = getModuleNameFromPackage(joinPoint.getTarget().getClass().getPackage().getName());

        // --- Informações do usuário logado (Exemplo com Spring Security) ---
        String userId = "SYSTEM";
        String userName = "System";
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
            
        } else {
            userName = principal.toString();
        }
        
        Object[] args = joinPoint.getArgs();
        Object entityObject = findEntity(args);
        
        String affectedId = null;
        Object oldValue = null;
        
        if (entityObject != null) {
            affectedId = getEntityId(entityObject);

            if (affectedId != null && !action.contains("CREATE")) {
                try {

                    entityManager.detach(entityObject); 
                    Object oldEntity = entityManager.find(entityObject.getClass(), (Serializable) parseId(entityObject.getClass(), affectedId));
                    if (oldEntity != null) {
                        oldValue = oldEntity;
                    }
                } catch (Exception e) {
                    System.err.println("Audit Aspect: Não foi possível buscar o valor antigo. " + e.getMessage());
                }
            }
        }

        // Executa o método de negócio original (ex: salvar, atualizar, deletar)
        Object returnedObject;
        try {
            returnedObject = joinPoint.proceed();
        } catch (Throwable throwable) {

            throw throwable;
        }

        Object newValue = null;
        if (action.contains("DELETE")) {
            newValue = null; // Em um delete, o novo valor é nulo.
        } else {
            newValue = returnedObject; // Normalmente o método retorna a entidade salva/atualizada.
        }

        // Se o ID afetado ainda não foi encontrado (ex: em um CREATE), tenta pegar do objeto retornado.
        if (affectedId == null && newValue != null) {
            affectedId = getEntityId(newValue);
        }

        // --- Cria e salva o log ---
        try {
            String oldValueJson = (oldValue != null) ? objectMapper.writeValueAsString(oldValue) : null;
            String newValueJson = (newValue != null) ? objectMapper.writeValueAsString(newValue) : null;
            
            String description = String.format("Usuário '%s' executou a ação '%s' no módulo '%s'.", userName, action, moduleName);

            AuditLog log = new AuditLog(action, className, affectedId, description, oldValueJson, newValueJson, moduleName, userId, userName);
            auditLogService.saveLog(log);
        } catch (Exception e) {
            // É crucial que uma falha na auditoria não quebre a aplicação principal.
            System.err.println("ERRO AO SALVAR LOG DE AUDITORIA: " + e.getMessage());
            e.printStackTrace();
        }

        return returnedObject;
    }

    // --- Métodos de Apoio ---

    private String getModuleNameFromPackage(String packageName) {
        // Lógica para extrair o nome do módulo do pacote. Ex: "com.project.system.user" -> "user"
        String[] parts = packageName.split("\\.");
        if (parts.length > 2) {
            return parts[parts.length - 1]; // Pega a última parte do pacote
        }
        return "general";
    }

    private Object findEntity(Object[] args) {
        // Procura por um argumento que seja uma entidade (geralmente o primeiro)
        if (args == null || args.length == 0) return null;
        return Arrays.stream(args)
                .filter(arg -> arg != null && arg.getClass().isAnnotationPresent(jakarta.persistence.Entity.class))
                .findFirst()
                .orElse(null);
    }

    private String getEntityId(Object entity) {
        // Encontra o campo anotado com @Id e retorna seu valor como String
        if (entity == null) return null;
        try {
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    Object idValue = field.get(entity);
                    return idValue != null ? idValue.toString() : null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private Object parseId(Class<?> entityClass, String id) {
        // Converte o ID de String para o tipo correto (Long, Integer, etc.)
        try {
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    Class<?> idType = field.getType();
                    if (idType.equals(Long.class) || idType.equals(long.class)) {
                        return Long.parseLong(id);
                    }
                    if (idType.equals(Integer.class) || idType.equals(int.class)) {
                        return Integer.parseInt(id);
                    }
                    // Adicionar outros tipos se necessário
                    return id;
                }
            }
        } catch(Exception e) {
            System.err.println("Audit Aspect: Erro ao converter ID. " + e.getMessage());
        }
        return id;
    }
}