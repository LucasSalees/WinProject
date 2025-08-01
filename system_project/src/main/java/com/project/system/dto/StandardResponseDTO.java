package com.project.system.dto;

public class StandardResponseDTO {

    private String status;
    private String message ;

    public StandardResponseDTO(String status, String message ) {
        this.status = status;
        this.message  = message ;
    }

    public static StandardResponseDTO success(String message ) {
        return new StandardResponseDTO("success", message );
    }

    public static StandardResponseDTO error(String message ) {
        return new StandardResponseDTO("error", message );
    }

    public String getStatus() {
        return status;
    }

    public String getMensagem() {
        return message ;
    }
}