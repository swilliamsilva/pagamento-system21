public abstract class AbstractBoletoRepositoryTest {
    protected BoletoRepositoryPort repository;
    
    @Test
    abstract void deveSalvarEBuscarBoleto();
}

@SpringBootTest
@ActiveProfiles("jpa")
public class JpaBoletoRepositoryTest extends AbstractBoletoRepositoryTest {
    
    @Autowired
    private BoletoRepositoryPort repository;
    
    @Test
    @Transactional
    @Override
    void deveSalvarEBuscarBoleto() {
        // Implementação específica para JPA
    }
}

@SpringBootTest
@ActiveProfiles("mongo")
public class MongoBoletoRepositoryTest extends AbstractBoletoRepositoryTest {
    
    @Autowired
    private BoletoRepositoryPort repository;
    
    @Test
    @Override
    void deveSalvarEBuscarBoleto() {
        // Implementação específica para MongoDB
    }
}