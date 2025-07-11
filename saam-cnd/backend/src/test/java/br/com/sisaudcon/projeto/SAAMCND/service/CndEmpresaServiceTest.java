package br.com.sisaudcon.projeto.SAAMCND.service;

import br.com.sisaudcon.projeto.SAAMCND.model.CndEmpresa;
import br.com.sisaudcon.projeto.SAAMCND.repository.CndEmpresaRepository;
import br.com.sisaudcon.projeto.SAAMCND.exception.ResourceNotFoundException;
import br.com.sisaudcon.projeto.SAAMCND.exception.ServicoExternoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.boot.web.client.RestTemplateBuilder; // Se fosse usar RestTemplate real

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CndEmpresaServiceTest {

    @Mock
    private CndEmpresaRepository cndEmpresaRepository;

    // @Mock
    // private RestTemplateBuilder restTemplateBuilder; // Se fosse usar RestTemplate real

    @InjectMocks
    private CndEmpresaService cndEmpresaService;

    private CndEmpresa empresaExistente;
    private CndEmpresa empresaMockSaam;

    @BeforeEach
    void setUp() {
        empresaExistente = new CndEmpresa();
        empresaExistente.setId(1L);
        empresaExistente.setCnpj("00.000.000/0001-00");
        empresaExistente.setNomeEmpresa("Empresa Existente Localmente");

        empresaMockSaam = new CndEmpresa();
        empresaMockSaam.setId(2L); // ID diferente para o mock do SAAM
        empresaMockSaam.setCnpj("11.111.111/0001-11");
        empresaMockSaam.setNomeEmpresa("Empresa Mockada SAAM");
        empresaMockSaam.setStatusEmpresa("ATIVO_SAAM");

        // Se o RestTemplate fosse real, precisaria mockar suas chamadas
        // RestTemplate mockRestTemplate = mock(RestTemplate.class);
        // when(restTemplateBuilder.build()).thenReturn(mockRestTemplate);
    }

    @Test
    void getEmpresaByIdAndFetchIfNecessary_quandoEmpresaExisteLocalmente_retornaEmpresaLocal() {
        when(cndEmpresaRepository.findById(1L)).thenReturn(Optional.of(empresaExistente));

        CndEmpresa resultado = cndEmpresaService.getEmpresaByIdAndFetchIfNecessary(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Empresa Existente Localmente", resultado.getNomeEmpresa());
        verify(cndEmpresaRepository, times(1)).findById(1L);
        verify(cndEmpresaRepository, never()).save(any(CndEmpresa.class)); // Não deve tentar salvar
    }

    @Test
    void getEmpresaByIdAndFetchIfNecessary_quandoEmpresaNaoExisteLocalmenteMasExisteNoSaam_retornaEmpresaDoSaamESalva() {
        Long idEmpresaSaam = 2L;
        // Configura o mock para simular que a empresa não existe localmente
        when(cndEmpresaRepository.findById(idEmpresaSaam)).thenReturn(Optional.empty());

        // Configura o mock para simular que a empresa é encontrada no SAAM (método fetchEmpresaFromSaamMock é privado, então testamos o efeito)
        // O método getEmpresaByIdAndFetchIfNecessary internamente chama o mock.
        // Para este teste, vamos assumir que o fetchEmpresaFromSaamMock retornaria empresaMockSaam para id 2L.
        // E que após isso, ele tenta salvar.
        when(cndEmpresaRepository.save(any(CndEmpresa.class))).thenAnswer(invocation -> {
            CndEmpresa empresaSalva = invocation.getArgument(0);
            empresaSalva.setId(idEmpresaSaam); // Simula o ID sendo atribuído ou mantido
            return empresaSalva;
        });


        CndEmpresa resultado = cndEmpresaService.getEmpresaByIdAndFetchIfNecessary(idEmpresaSaam);

        assertNotNull(resultado);
        assertEquals(idEmpresaSaam, resultado.getId());
        assertEquals("Empresa Mockada SAAM (ID: " + idEmpresaSaam + ")", resultado.getNomeEmpresa()); // Nome do mock do SAAM
        assertEquals("INSERT-PEC-4924-SAAM_FETCH", resultado.getLinha());
        verify(cndEmpresaRepository, times(1)).findById(idEmpresaSaam);
        verify(cndEmpresaRepository, times(1)).save(any(CndEmpresa.class));
    }

    @Test
    void getEmpresaByIdAndFetchIfNecessary_quandoEmpresaNaoExisteLocalmenteNemNoSaam_lancaResourceNotFoundException() {
        Long idInexistente = 3L;
        when(cndEmpresaRepository.findById(idInexistente)).thenReturn(Optional.empty());
        // O método fetchEmpresaFromSaamMock retornará null para este ID, simulando não encontrar no SAAM

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            cndEmpresaService.getEmpresaByIdAndFetchIfNecessary(idInexistente);
        });

        assertEquals("Empresa não encontrada no SAAM para o ID informado: " + idInexistente, exception.getMessage());
        verify(cndEmpresaRepository, times(1)).findById(idInexistente);
        verify(cndEmpresaRepository, never()).save(any(CndEmpresa.class));
    }

    @Test
    void getEmpresaByIdAndFetchIfNecessary_quandoFalhaComunicacaoSaam_lancaServicoExternoException() {
        Long idErroSaam = 99L; // ID que simula erro no mock
        when(cndEmpresaRepository.findById(idErroSaam)).thenReturn(Optional.empty());

        ServicoExternoException exception = assertThrows(ServicoExternoException.class, () -> {
            cndEmpresaService.getEmpresaByIdAndFetchIfNecessary(idErroSaam);
        });

        assertTrue(exception.getMessage().contains("Erro simulado na comunicação com o SAAM"));
        verify(cndEmpresaRepository, times(1)).findById(idErroSaam);
        verify(cndEmpresaRepository, never()).save(any(CndEmpresa.class));
    }


    @Test
    void findById_quandoEmpresaExiste_retornaEmpresa() {
        when(cndEmpresaRepository.findById(1L)).thenReturn(Optional.of(empresaExistente));
        CndEmpresa resultado = cndEmpresaService.findById(1L);
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void findById_quandoEmpresaNaoExiste_lancaResourceNotFoundException() {
        when(cndEmpresaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cndEmpresaService.findById(1L));
    }

    @Test
    void saveEmpresa_defineLinhaSeNulaOuVazia_eSalva() {
        CndEmpresa empresaNova = new CndEmpresa();
        empresaNova.setCnpj("22.222.222/0001-22");
        empresaNova.setNomeEmpresa("Nova Empresa");

        when(cndEmpresaRepository.save(any(CndEmpresa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CndEmpresa salva = cndEmpresaService.save(empresaNova);

        assertNotNull(salva);
        assertEquals("SAVE-GENERIC-PEC-XXXX", salva.getLinha());
        verify(cndEmpresaRepository, times(1)).save(empresaNova);
    }

    @Test
    void saveEmpresa_mantemLinhaExistente_eSalva() {
        CndEmpresa empresaComLinha = new CndEmpresa();
        empresaComLinha.setCnpj("33.333.333/0001-33");
        empresaComLinha.setNomeEmpresa("Empresa com Linha");
        empresaComLinha.setLinha("EXISTING-LINE");

        when(cndEmpresaRepository.save(any(CndEmpresa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CndEmpresa salva = cndEmpresaService.save(empresaComLinha);

        assertNotNull(salva);
        assertEquals("EXISTING-LINE", salva.getLinha());
        verify(cndEmpresaRepository, times(1)).save(empresaComLinha);
    }
}
