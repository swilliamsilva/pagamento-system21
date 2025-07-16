package com.pagamento.pix.common.constants;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import static org.junit.jupiter.api.Assertions.*;

class ApiConstantsTest {

    @Test
    void constantesDevemTerValoresCorretos() {
        assertEquals("${bacen.api.url}", ApiConstants.BACEN_API_URL);
        assertEquals("${bacen.api.key}", ApiConstants.BACEN_API_KEY);
        assertEquals("${vault.endpoint}", ApiConstants.VAULT_ENDPOINT);
        assertEquals("${vault.token}", ApiConstants.VAULT_TOKEN);
        assertEquals("${vault.path}", ApiConstants.VAULT_PATH);
    }

    @Test
    void classeNaoDeveSerInstanciavel() throws NoSuchMethodException {
        // Verificar se a classe é final
        assertTrue(Modifier.isFinal(ApiConstants.class.getModifiers()));
        
        // Obter o construtor
        Constructor<ApiConstants> constructor = ApiConstants.class.getDeclaredConstructor();
        
        // Verificar se o construtor é privado
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        
        // Tentar instanciar via reflexão
        constructor.setAccessible(true);
        
        InvocationTargetException ex = assertThrows(
            InvocationTargetException.class,
            constructor::newInstance
        );
        
        // Verificar se lançou AssertionError
        assertEquals(AssertionError.class, ex.getCause().getClass());
        assertEquals("Não é permitido instanciar esta classe utilitária", ex.getCause().getMessage());
    }

    @Test
    void propriedadesDevemSerConstantes() throws Exception {
        assertTrue(Modifier.isStatic(ApiConstants.class.getField("BACEN_API_URL").getModifiers()));
        assertTrue(Modifier.isFinal(ApiConstants.class.getField("BACEN_API_URL").getModifiers()));
        assertTrue(Modifier.isPublic(ApiConstants.class.getField("BACEN_API_URL").getModifiers()));
    }
}