package com.pagamento.pix.common.constants;

public final class ApiConstants {

    private ApiConstants() {
        // Construtor privado para prevenir instanciação
        throw new AssertionError("Não é permitido instanciar esta classe utilitária");
    }

    public static final String BACEN_API_URL = "${bacen.api.url}";
    public static final String BACEN_API_KEY = "${bacen.api.key}";
    public static final String VAULT_ENDPOINT = "${vault.endpoint}";
    public static final String VAULT_TOKEN = "${vault.token}";
    public static final String VAULT_PATH = "${vault.path}";
    
    // Adicione outras constantes conforme necessário
}