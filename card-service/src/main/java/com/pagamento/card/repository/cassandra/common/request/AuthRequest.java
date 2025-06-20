/* ========================================================
# Classe: AuthRequest
# Módulo: common - request
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Representa os dados da requisição de login.
# ======================================================== */

package com.pagamento.common.request;

public record AuthRequest(
    String username,
    String password
) {

	public Object password() {
		// TODO Auto-generated method stub
		return null;
	}

	public String username() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object password1() {
		// TODO Auto-generated method stub
		return null;
	}
}

/**
 * 
 *  Testando

Com a aplicação auth-service rodando, você pode fazer:
 * POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "senha123"
}

 * 
 * */
 */