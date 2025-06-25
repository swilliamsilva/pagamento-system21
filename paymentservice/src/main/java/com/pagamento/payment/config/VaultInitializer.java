package com.pagamento.payment.config;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultToken;
import org.springframework.vault.support.VaultTokenRequest;
import org.springframework.vault.support.VaultUnsealStatus;

@Component
@Profile("!test")
public class VaultInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(VaultInitializer.class);

    @Value("${vault.url}")
    private String vaultUrl;

    @Value("${vault.unseal-key}")
    private String unsealKey;

    @Value("${vault.root-token}")
    private String rootToken;

    private final VaultTemplate vaultTemplate;

    public VaultInitializer(VaultTemplate vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            VaultUnsealStatus status = vaultTemplate.opsForSys().getUnsealStatus();

            if (status.isSealed()) {
                logger.info("Vault está selado. Iniciando processo de unseal...");
                vaultTemplate.opsForSys().unseal(unsealKey);
                status = vaultTemplate.opsForSys().getUnsealStatus();

                if (status.isSealed()) {
                    logger.error("Falha ao fazer unseal do Vault. Progresso: {}", status.getProgress());
                    throw new VaultInitException("Falha no unseal do Vault");
                }
                logger.info("Unseal realizado com sucesso");
            }

            VaultEndpoint endpoint = VaultEndpoint.from(vaultUrl);
            VaultOperations authTemplate = new VaultTemplate(endpoint, new TokenAuthentication(rootToken));

            String policyRules = "path \"secret/data/pagamento/*\" {\n" +
                                 "  capabilities = [\"read\", \"list\"]\n" +
                                 "}";

            Map<String, Object> policyRequest = new HashMap<>();
            policyRequest.put("policy", policyRules);
            policyRequest.put("name", "pagamento-policy");
            authTemplate.write("sys/policy/pagamento-policy", policyRequest);

            logger.info("Política 'pagamento-policy' criada/atualizada com sucesso");

            VaultTokenRequest request = VaultTokenRequest.builder()
                .policies(Collections.singletonList("pagamento-policy"))
                .ttl(Duration.ofHours(8760)) // ← Aqui está a correção
                .renewable(true)
                .build();

            VaultToken token = authTemplate.opsForToken().create(request).getToken();
            logger.info("Token de aplicação criado com política 'pagamento-policy'");
            logger.debug("Token: {}", token.getToken());

            Map<String, Object> dbSecret = new HashMap<>();
            dbSecret.put("username", "payment_user");
            dbSecret.put("password", "s3cr3tP@ss");

            Map<String, Object> secretData = Collections.singletonMap("data", dbSecret);
            authTemplate.write("secret/data/pagamento/db", secretData);
            logger.info("Segredo inicial configurado em 'secret/data/pagamento/db'");

        } catch (Exception e) {
            logger.error("Erro crítico na inicialização do Vault. A aplicação não pode iniciar.", e);
            throw new VaultInitException("Falha na inicialização do Vault", e);
        }
    }

    public static class VaultInitException extends RuntimeException {
        public VaultInitException(String message) {
            super(message);
        }

        public VaultInitException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
