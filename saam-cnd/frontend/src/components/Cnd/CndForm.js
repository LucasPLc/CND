import React, { useState, useEffect } from 'react';

// Este formulário será para CndResultado, mas os campos de cadastro da PEC-4536
// parecem misturar CndCliente e CndResultado.
// Vamos focar nos campos editáveis da CndResultado para PEC-4537 e
// nos campos de cadastro manual de uma CND (que pode gerar uma CndResultado).

const CndForm = ({ initialData = {}, onSubmit, clientes, isEditMode = false }) => {
    const [formData, setFormData] = useState({
        // Campos da CndResultado (editáveis conforme PEC-4537)
        // A PEC-4537 foca em editar dados de uma CND já cadastrada (CndResultado)
        // e menciona "Tipo de Certidão", "Situação Fiscal", etc.
        // A PEC-4536 (criar tela principal) lista campos para "cadastro de uma nova CND"
        // que parecem ser mais para configurar um CndCliente para consulta automática,
        // ou para inserir manualmente um CndResultado.
        // Vamos priorizar os campos de CndResultado que podem ser editados ou inseridos manualmente.

        fkCliente: initialData.fkCliente || '', // ID do CndCliente ao qual este resultado pertence
        situacao: initialData.situacao || '', // Ex: "Negativa", "Positiva com Efeitos de Negativa"
        dataEmissao: initialData.dataEmissao || '', // YYYY-MM-DD
        dataValidade: initialData.dataValidade || '', // YYYY-MM-DD
        codigoControle: initialData.codigoControle || '',
        arquivo: initialData.arquivo || '', // Base64 do PDF (para upload manual, se aplicável)
        statusProcessamento: initialData.statusProcessamento || 'MANUAL', // Default para inserção manual
        // Campos da PEC-4536 que parecem de CndCliente ou configuração de consulta:
        // status (habilita/desabilita consulta) -> CndCliente.statusCliente?
        // tipoCertidao (Municipal, Estadual, Federal) -> Pode ser um campo em CndResultado para categorizar
        // municipio -> Se tipoCertidao for Municipal. Pode ser um campo em CndResultado.
        // orgaoEmissor -> Pode ser um campo em CndResultado.
        // nomeContribuinte -> CndCliente.nome (não temos esse campo, CNPJ é o identificador)
        // cpfOuCnpjContribuinte -> CndCliente.cnpj
        // atividadeEconomicaCNAE -> Poderia ser em CndCliente ou CndResultado
        // observacoes -> Campo genérico, pode ser em CndResultado
        tipoCertidao: initialData.tipoCertidao || 'Federal', // Novo campo para CndResultado
        orgaoEmissor: initialData.orgaoEmissor || 'Receita Federal do Brasil', // Novo campo para CndResultado
        observacoes: initialData.observacoes || '', // Novo campo para CndResultado
    });

    useEffect(() => {
        // Formatar datas para o input type="date" se vierem do backend
        const formattedData = { ...initialData };
        if (initialData.dataEmissao) {
            formattedData.dataEmissao = initialData.dataEmissao.split('T')[0];
        }
        if (initialData.dataValidade) {
            formattedData.dataValidade = initialData.dataValidade.split('T')[0];
        }
        setFormData(prev => ({ ...prev, ...formattedData }));
    }, [initialData]);


    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prevData => ({
            ...prevData,
            [name]: type === 'checkbox' ? checked : value,
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        // Remover campos que não devem ser enviados diretamente se forem apenas para UI
        const dataToSubmit = { ...formData };
        // Se 'arquivo' for um input file, tratar diferentemente (ler como base64)
        onSubmit(dataToSubmit);
    };

    return (
        <form onSubmit={handleSubmit}>
            {/* Campo para selecionar Cliente (fkCliente) */}
            {!isEditMode && clientes && ( // Mostrar seletor de cliente apenas no modo de criação
                <div>
                    <label htmlFor="fkCliente">Cliente Associado:</label>
                    <select
                        id="fkCliente"
                        name="fkCliente"
                        value={formData.fkCliente}
                        onChange={handleChange}
                        required={!isEditMode} // Obrigatório ao criar
                    >
                        <option value="">Selecione um Cliente</option>
                        {clientes.map(cliente => (
                            <option key={cliente.id} value={cliente.id}>
                                {cliente.cnpj} (ID: {cliente.id})
                            </option>
                        ))}
                    </select>
                </div>
            )}
            {isEditMode && formData.fkCliente && <p>Editando CND para Cliente ID: {formData.fkCliente}</p>}


            <div>
                <label htmlFor="tipoCertidao">Tipo de Certidão:</label>
                <select id="tipoCertidao" name="tipoCertidao" value={formData.tipoCertidao} onChange={handleChange}>
                    <option value="Federal">Federal</option>
                    <option value="Estadual">Estadual</option>
                    <option value="Municipal">Municipal</option>
                </select>
            </div>

            <div>
                <label htmlFor="orgaoEmissor">Órgão Emissor:</label>
                <input
                    type="text"
                    id="orgaoEmissor"
                    name="orgaoEmissor"
                    value={formData.orgaoEmissor}
                    onChange={handleChange}
                    placeholder="Ex: Receita Federal, SEFAZ-SP, Prefeitura de..."
                />
            </div>

            <div>
                <label htmlFor="situacao">Situação da Certidão:</label>
                <input
                    type="text"
                    id="situacao"
                    name="situacao"
                    value={formData.situacao}
                    onChange={handleChange}
                    placeholder="Ex: Negativa, Positiva com Efeito de Negativa"
                />
            </div>

            <div>
                <label htmlFor="dataEmissao">Data de Emissão:</label>
                <input
                    type="date"
                    id="dataEmissao"
                    name="dataEmissao"
                    value={formData.dataEmissao}
                    onChange={handleChange}
                />
            </div>

            <div>
                <label htmlFor="dataValidade">Data de Validade:</label>
                <input
                    type="date"
                    id="dataValidade"
                    name="dataValidade"
                    value={formData.dataValidade}
                    onChange={handleChange}
                />
            </div>

            <div>
                <label htmlFor="codigoControle">Código de Controle:</label>
                <input
                    type="text"
                    id="codigoControle"
                    name="codigoControle"
                    value={formData.codigoControle}
                    onChange={handleChange}
                />
            </div>

            {isEditMode && (
                 <div>
                    <label htmlFor="statusProcessamento">Status do Processamento Interno:</label>
                    <input
                        type="text"
                        id="statusProcessamento"
                        name="statusProcessamento"
                        value={formData.statusProcessamento}
                        onChange={handleChange}
                    />
                </div>
            )}

            <div>
                <label htmlFor="observacoes">Observações:</label>
                <textarea
                    id="observacoes"
                    name="observacoes"
                    value={formData.observacoes}
                    onChange={handleChange}
                />
            </div>

            {/* Campo para upload de arquivo PDF (Base64) - Simples por enquanto */}
            {/* <div>
                <label htmlFor="arquivo">Arquivo PDF (Base64):</label>
                <textarea
                    id="arquivo"
                    name="arquivo"
                    value={formData.arquivo}
                    onChange={handleChange}
                    placeholder="Cole o conteúdo Base64 do PDF aqui (se aplicável)"
                    rows="3"
                />
            </div> */}


            <button type="submit" className="button">
                {isEditMode ? 'Salvar Alterações' : 'Cadastrar CND'}
            </button>
        </form>
    );
};

export default CndForm;
