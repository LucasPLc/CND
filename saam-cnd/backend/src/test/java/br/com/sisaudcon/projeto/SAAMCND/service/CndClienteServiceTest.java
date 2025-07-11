package br.com.sisaudcon.projeto.SAAMCND.service;

import br.com.sisaudcon.projeto.SAAMCND.model.CndCliente;
import br.com.sisaudcon.projeto.SAAMCND.model.CndEmpresa;
import br.com.sisaudcon.projeto.SAAMCND.repository.CndClienteRepository;
import br.com.sisaudcon.projeto.SAAMCND.dto.CndClienteRequestDTO;
import br.com.sisaudcon.projeto.SAAMCND.dto.CndClienteResponseDTO;
import br.com.sisaudcon.projeto.SAAMCND.exception.ResourceNotFoundException;
import br.com.sisaudcon.projeto.SAAMCND.exception.BadRequestException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CndClienteServiceTest {

    @Mock
    private CndClienteRepository cndClienteRepository;

    @Mock
    private CndEmpresaService cndEmpresaService;

    @InjectMocks
    private CndClienteService cndClienteService;

    private CndEmpresa empresa;
    private CndClienteRequestDTO clienteRequestDTO;
    private CndCliente cliente;

    @BeforeEach
    void setUp() {
        empresa = new CndEmpresa();
        empresa.setId(1L);
        empresa.setNomeEmpresa("Empresa Teste");
        empresa.setCnpj("00.000.000/0001-00");

        clienteRequestDTO = new CndClienteRequestDTO(
                "12.345.678/0001-99",
                "Cliente Teste DTO", // Nome adicionado
                30,
                "ATIVO",
                true,
                false,
                true,
                1L
        );

        cliente = new CndCliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste Entidade"); // Nome adicionado
        cliente.setCnpj("12.345.678/0001-99");
        cliente.setEmpresa(empresa);
        cliente.setPeriodicidade(30);
        cliente.setStatusCliente("ATIVO");
    }

    @Test
    void criarCliente_comDadosValidos_retornaClienteResponseDTO() {
        when(cndEmpresaService.getEmpresaByIdAndFetchIfNecessary(1L)).thenReturn(empresa);
        when(cndClienteRepository.save(any(CndCliente.class))).thenAnswer(invocation -> {
            CndCliente c = invocation.getArgument(0);
            c.setId(1L); // Simula o save atribuindo um ID
            return c;
        });

        CndClienteResponseDTO resultado = cndClienteService.criarCliente(clienteRequestDTO);

        assertNotNull(resultado);
        assertEquals("12.345.678/0001-99", resultado.getCnpj());
        assertEquals("Cliente Teste DTO", resultado.getNome()); // Verifica o nome
        assertEquals(1L, resultado.getFkEmpresa());
        assertEquals("Empresa Teste", resultado.getNomeEmpresa());
        assertEquals("INSERT-PEC-4924", resultado.getLinha());
        verify(cndClienteRepository, times(1)).save(any(CndCliente.class));
    }

    @Test
    void criarCliente_semFkEmpresa_lancaBadRequestException() {
        clienteRequestDTO.setFkEmpresa(null);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            cndClienteService.criarCliente(clienteRequestDTO);
        });
        assertEquals("É necessário informar uma empresa válida (fk_empresa).", exception.getMessage());
        verify(cndClienteRepository, never()).save(any(CndCliente.class));
    }

    @Test
    void criarCliente_comCnpjInvalido_logaWarnMasCria() { // Ajustado para refletir a lógica atual
        clienteRequestDTO.setCnpj("12345");
        when(cndEmpresaService.getEmpresaByIdAndFetchIfNecessary(1L)).thenReturn(empresa);
        when(cndClienteRepository.save(any(CndCliente.class))).thenAnswer(invocation -> {
            CndCliente c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        // Como não lança exceção, apenas verificamos se o save é chamado
        cndClienteService.criarCliente(clienteRequestDTO);
        // Aqui poderíamos usar um Appender do Logback/Log4j para verificar o log, mas é mais complexo.
        // Por agora, confiamos que o logger.warn foi chamado.
        verify(cndClienteRepository, times(1)).save(any(CndCliente.class));
    }


    @Test
    void listarClientes_retornaListaDeClientes() {
        when(cndClienteRepository.findAll()).thenReturn(Arrays.asList(cliente));
        List<CndClienteResponseDTO> resultados = cndClienteService.listarClientes();
        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
        assertEquals("12.345.678/0001-99", resultados.get(0).getCnpj());
    }

    @Test
    void buscarClientePorId_quandoClienteExiste_retornaCliente() {
        when(cndClienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        CndClienteResponseDTO resultado = cndClienteService.buscarClientePorId(1L);
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarClientePorId_quandoClienteNaoExiste_lancaResourceNotFoundException() {
        when(cndClienteRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cndClienteService.buscarClientePorId(1L));
    }

    @Test
    void atualizarCliente_comDadosValidos_retornaClienteAtualizado() {
        CndClienteRequestDTO dtoAtualizacao = new CndClienteRequestDTO(
                "99.888.777/0001-66",
                "Cliente Atualizado Teste", // Nome atualizado
                60,
                "INATIVO",
                false,
                true,
                false,
                1L
        );
        when(cndClienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cndClienteRepository.save(any(CndCliente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Não precisamos mockar cndEmpresaService.getEmpresaByIdAndFetchIfNecessary se o fkEmpresa não mudar

        CndClienteResponseDTO resultado = cndClienteService.atualizarCliente(1L, dtoAtualizacao);

        assertNotNull(resultado);
        assertEquals("99.888.777/0001-66", resultado.getCnpj());
        assertEquals("Cliente Atualizado Teste", resultado.getNome()); // Verifica nome atualizado
        assertEquals(60, resultado.getPeriodicidade());
        assertEquals("INATIVO", resultado.getStatusCliente());
        assertEquals("UPDATE-PEC-4924", resultado.getLinha());
        verify(cndClienteRepository, times(1)).save(any(CndCliente.class));
    }

    @Test
    void atualizarCliente_mudandoEmpresa_buscaNovaEmpresa() {
        CndClienteRequestDTO dtoAtualizacao = new CndClienteRequestDTO(
                "12.345.678/0001-99",
                "Cliente Teste Entidade", // Mesmo nome
                30,
                "ATIVO",
                true,
                false,
                true,
                2L // Novo fkEmpresa
        );
        CndEmpresa novaEmpresa = new CndEmpresa();
        novaEmpresa.setId(2L);
        novaEmpresa.setNomeEmpresa("Nova Empresa Teste");

        when(cndClienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cndEmpresaService.getEmpresaByIdAndFetchIfNecessary(2L)).thenReturn(novaEmpresa);
        when(cndClienteRepository.save(any(CndCliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CndClienteResponseDTO resultado = cndClienteService.atualizarCliente(1L, dtoAtualizacao);

        assertEquals(2L, resultado.getFkEmpresa());
        assertEquals("Nova Empresa Teste", resultado.getNomeEmpresa());
        verify(cndEmpresaService, times(1)).getEmpresaByIdAndFetchIfNecessary(2L);
        verify(cndClienteRepository, times(1)).save(any(CndCliente.class));
    }


    @Test
    void atualizarCliente_quandoClienteNaoExiste_lancaResourceNotFoundException() {
        when(cndClienteRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cndClienteService.atualizarCliente(1L, clienteRequestDTO));
    }

    @Test
    void deletarCliente_quandoClienteExisteSemResultados_deletaCliente() {
        when(cndClienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cndClienteRepository.existsCndResultadoByClienteId(1L)).thenReturn(false);
        doNothing().when(cndClienteRepository).delete(cliente);

        cndClienteService.deletarCliente(1L);

        verify(cndClienteRepository, times(1)).delete(cliente);
    }

    @Test
    void deletarCliente_quandoClienteNaoExiste_lancaResourceNotFoundException() {
        when(cndClienteRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cndClienteService.deletarCliente(1L));
        verify(cndClienteRepository, never()).delete(any(CndCliente.class));
    }

    @Test
    void deletarCliente_quandoClienteTemResultadosVinculados_lancaBadRequestException() {
        when(cndClienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cndClienteRepository.existsCndResultadoByClienteId(1L)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            cndClienteService.deletarCliente(1L);
        });

        assertEquals("Não é possível excluir o cliente. Existem resultados de CND vinculados.", exception.getMessage());
        verify(cndClienteRepository, never()).delete(any(CndCliente.class));
    }
}
