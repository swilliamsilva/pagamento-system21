/* ========================================================
# Classe: User
# Módulo: common
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Representa um usuário do sistema.
# ======================================================== */

package com.pagamento.common.model;

public class User {
    private String id;
    private String nome;
    private String email;
    private String documento;

    // Getters e Setters (Java 21 ainda exige com POJO clássico)
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
}
