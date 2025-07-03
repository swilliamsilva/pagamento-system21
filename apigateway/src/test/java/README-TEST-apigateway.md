Recomendações para a equipe de suporte e testadores
===================================================
    Manter o container Redis ativo e acessível em localhost:6379 antes de iniciar os testes

        Usar comando: 
        
      #  docker start test-redis ou docker run -d -p 6379:6379 redis

        Caso esteja usando Redis embutido (embedded-redis), validar configurações específicas

    Perfil de Teste (test)

        Execute testes com o profile test ativado para aplicar essas configurações, evitando conflito com ambiente de produção

        Exemplo:
        
        mvn clean test -Dspring.profiles.active=test

    Rate Limiter (limitação de requisições) está desativado por padrão nos testes

        Evita erros HTTP 429 (Too Many Requests) durante execuções de teste automatizado

        Para testar rate limiter, habilite explicitamente via configuração

    Circuit Breaker e Retry desativados para evitar falsos negativos

        Circuit breaker configurado para não abrir o circuito com threshold em 100%

        Retry configurado com 1 tentativa para evitar repetição automática de chamadas

    Cache local e filtros globais desativados para garantir testes isolados e previsíveis

    Logs detalhados ativados para facilitar depuração durante falhas de testes
    
    
    
##    ✅ Pré-condições

Antes de rodar os testes, certifique-se de que o Redis está ativo:

docker ps  # Verifique se o container test-redis está rodando
docker start test-redis  # Caso não esteja

    ⚠️ Caso Redis esteja inativo, o teste EnvironmentSanityTest falhará com uma mensagem clara, indicando como resolver o problema.

▶️ Comando para execução

Execute os testes com o seguinte comando:

mvn clean test -Dspring.profiles.active=test

🚫 Funcionalidades Desativadas nos Testes
Funcionalidade	    Status nos testes	Motivo
Filtros Globais	    ❌ Desativado	    Evita efeitos colaterais nos testes isolados
Cache Local	        ❌ Desativado    	Garante que respostas não venham do cache
Rate Limiter Redis	❌ Desativado    	Impede erros 429 por excesso de requisições
Circuit Breaker	    ✅ Ativado, mas inofensivo	Threshold 100% para nunca abrir o circuito
Retry	            ✅ 1 tentativa	Garante resposta direta sem repetição automática

📌 Observações

    Os testes foram estruturados para garantir ambiente controlado e previsível

    Evite usar banco real ou APIs externas durante os testes

    WireMock está configurado para mockar as dependências externas

    Os filtros de rate limit podem ser testados separadamente em RedisRateLimitingTest, RateLimitingBehaviorTest, e LocalRateLimitingTest