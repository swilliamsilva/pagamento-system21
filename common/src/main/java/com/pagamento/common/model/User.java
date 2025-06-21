// ==========================
// MODEL: User.java
// ==========================
package com.pagamento.common.model;

/**
 * Representa um usuário do sistema.
 * Contém informações essenciais como ID, nome, e-mail e documento pessoal.
 */
public class User implements Identifiable {
    private String id;
    private String nome;
    private String email;
    private String documento;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
}
