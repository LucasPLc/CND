package br.com.sisaudcon.projeto.SAAMCND.service;

import br.com.sisaudcon.projeto.SAAMCND.model.CndCliente;
import br.com.sisaudcon.projeto.SAAMCND.model.CndEmpresa;
import br.com.sisaudcon.projeto.SAAMCND.repository.CndClienteRepository;
import br.com.sisaudcon.projeto.SAAMCND.dto.CndClienteRequestDTO;
import br.com.sisaudcon.projeto.SAAMCND.dto.CndClienteResponseDTO;
import br.com.sisaudcon.projeto.SAAMCND.exception.ResourceNotFoundException;
import br.com.sisaudcon.projeto.SAAMCND.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CndClienteService {

    private static final Logger logger = LoggerFactory.getLogger(CndClienteService.class);

    private final CndClienteRepository cndClienteRepository;
    private final CndEmpresaService cndEmpresaService; // Para buscar/criar empresa

    @Autowired
    public CndClienteService(CndClienteRepository cndClienteRepository, CndEmpresaService cndEmpresaService) {
        this.cndClienteRepository = cndClienteRepository;
        this.cndEmpresaService = cndEmpresaService;
    }

    @Transactional
    public CndClienteResponseDTO criarCliente(CndClienteRequestDTO requestDTO) {
        if (requestDTO.getFkEmpresa() == null) {
            throw new BadRequestException("É necessário informar uma empresa válida (fk_empresa).");
        }

        // Validação de CNPJ (simplificada, idealmente usar uma lib)
        if (requestDTO.getCnpj() == null || requestDTO.getCnpj().trim().isEmpty() ||
            !requestDTO.getCnpj().matches("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}") && !requestDTO.getCnpj().matches("\\d{14}")) {
             //throw new BadRequestException("Formato de CNPJ inválido.");
             logger.warn("Formato de CNPJ do cliente {} potencialmente inválido, mas seguindo com o cadastro.", requestDTO.getCnpj());
        }

        // Busca ou cria a empresa associada. A PEC-4924 diz que a API deve obter automaticamente os dados da empresa do SAAM.
        CndEmpresa empresa = cndEmpresaService.getEmpresaByIdAndFetchIfNecessary(requestDTO.getFkEmpresa());

        // Verifica se já existe um cliente com este CNPJ para esta empresa
        // cndClienteRepository.findByCnpjAndEmpresaId(requestDTO.getCnpj(), empresa.getId()).ifPresent(c -> {
        //    throw new BadRequestException("Cliente com CNPJ " + requestDTO.getCnpj() + " já cadastrado para esta empresa.");
        // });


        CndCliente novoCliente = new CndCliente();
        novoCliente.setNome(requestDTO.getNome()); // Adicionado
        novoCliente.setCnpj(requestDTO.getCnpj());
        novoCliente.setPeriodicidade(requestDTO.getPeriodicidade());
        novoCliente.setStatusCliente(requestDTO.getStatusCliente());
        novoCliente.setNacional(requestDTO.getNacional());
        novoCliente.setMunicipal(requestDTO.getMunicipal());
        novoCliente.setEstadual(requestDTO.getEstadual());
        novoCliente.setEmpresa(empresa);
        novoCliente.setLinha("INSERT-PEC-4924"); // Auditoria

        CndCliente clienteSalvo = cndClienteRepository.save(novoCliente);
        logger.info("Cliente {} criado com ID {}", clienteSalvo.getCnpj(), clienteSalvo.getId());
        return new CndClienteResponseDTO(clienteSalvo);
    }

    @Transactional(readOnly = true)
    public List<CndClienteResponseDTO> listarClientes() {
        return cndClienteRepository.findAll().stream()
                .map(CndClienteResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CndClienteResponseDTO buscarClientePorId(Long id) {
        CndCliente cliente = cndClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o ID informado: " + id));
        return new CndClienteResponseDTO(cliente);
    }

    @Transactional(readOnly = true)
    public CndCliente getClienteEntityById(Long id) {
        return cndClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o ID informado: " + id));
    }


    @Transactional
    public CndClienteResponseDTO atualizarCliente(Long id, CndClienteRequestDTO requestDTO) {
        CndCliente clienteExistente = cndClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o ID informado: " + id));

        if (requestDTO.getFkEmpresa() == null) {
            throw new BadRequestException("É necessário informar uma empresa válida (fk_empresa).");
        }

        // Validação de CNPJ (simplificada)
        if (requestDTO.getCnpj() == null || requestDTO.getCnpj().trim().isEmpty() ||
            !requestDTO.getCnpj().matches("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}") && !requestDTO.getCnpj().matches("\\d{14}")) {
            //throw new BadRequestException("Formato de CNPJ inválido.");
            logger.warn("Formato de CNPJ do cliente {} potencialmente inválido, mas seguindo com a atualização.", requestDTO.getCnpj());
        }

        // Verifica se a empresa mudou e se a nova empresa existe
        if (!clienteExistente.getEmpresa().getId().equals(requestDTO.getFkEmpresa())) {
            CndEmpresa novaEmpresa = cndEmpresaService.getEmpresaByIdAndFetchIfNecessary(requestDTO.getFkEmpresa());
            clienteExistente.setEmpresa(novaEmpresa);
        }

        clienteExistente.setNome(requestDTO.getNome()); // Adicionado
        clienteExistente.setCnpj(requestDTO.getCnpj());
        clienteExistente.setPeriodicidade(requestDTO.getPeriodicidade());
        clienteExistente.setStatusCliente(requestDTO.getStatusCliente());
        clienteExistente.setNacional(requestDTO.getNacional());
        clienteExistente.setMunicipal(requestDTO.getMunicipal());
        clienteExistente.setEstadual(requestDTO.getEstadual());
        clienteExistente.setLinha("UPDATE-PEC-4924"); // Auditoria

        CndCliente clienteAtualizado = cndClienteRepository.save(clienteExistente);
        logger.info("Cliente com ID {} atualizado.", clienteAtualizado.getId());
        return new CndClienteResponseDTO(clienteAtualizado);
    }

    @Transactional
    public void deletarCliente(Long id) {
        CndCliente cliente = cndClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o ID informado: " + id));

        // Regra de negócio: Só é permitida se não houver registros vinculados na tabela cnd_resultado.
        if (cndClienteRepository.existsCndResultadoByClienteId(id)) {
            throw new BadRequestException("Não é possível excluir o cliente. Existem resultados de CND vinculados.");
        }

        cndClienteRepository.delete(cliente);
        logger.info("Cliente com ID {} deletado.", id);
    }
}
