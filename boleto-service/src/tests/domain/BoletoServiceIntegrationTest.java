@SpringBootTest
@ActiveProfiles("test")
public class BoletoServiceIntegrationTest {

    @Autowired
    private BoletoServicePort boletoService;

    @Autowired
    private BoletoRepositoryPort repository;

    @Test
    @Transactional
    void deveEmitirConsultarECancelarBoleto() {
        // Emitir
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente A", "Empresa X", 1500.00, 
            LocalDate.now(), LocalDate.now().plusDays(30)
        );
        Boleto boleto = boletoService.emitirBoleto(request);
        
        // Consultar
        Boleto consultado = boletoService.consultarBoleto(boleto.getId());
        assertNotNull(consultado);
        assertEquals(BoletoStatus.EMITIDO, consultado.getStatus());
        
        // Cancelar
        Boleto cancelado = boletoService.cancelarBoleto(boleto.getId());
        assertEquals(BoletoStatus.CANCELADO, cancelado.getStatus());
    }

    @Test
    void deveGerarPDFECodigos() {
        // Emitir boleto
        BoletoRequestDTO request = new BoletoRequestDTO(...);
        Boleto boleto = boletoService.emitirBoleto(request);
        
        // Gerar PDF
        byte[] pdf = boletoService.gerarPDF(boleto.getId());
        assertTrue(pdf.length > 0);
        
        // Gerar códigos
        String codigoBarras = boletoService.gerarCodigoBarras(boleto.getId());
        assertNotNull(codigoBarras);
        
        String qrCode = boletoService.gerarQRCode(boleto.getId());
        assertNotNull(qrCode);
    }
}