import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import CndForm from '../components/Cnd/CndForm';
import { getCndResultadoById, createCndResultado, updateCndResultado, getClientes } from '../services/apiService'; // Adicionar create e update para CndResultado

const CndFormPage = () => {
    const { id } = useParams(); // ID do CndResultado para edição
    const navigate = useNavigate();
    const isEditMode = Boolean(id);

    const [initialData, setInitialData] = useState({});
    const [clientes, setClientes] = useState([]);
    const [loading, setLoading] = useState(isEditMode); // Só carrega dados se for edição
    const [error, setError] = useState(null);

    useEffect(() => {
        // Buscar lista de clientes para o select no modo de criação
        const fetchClientesList = async () => {
            try {
                const data = await getClientes(); // Assumindo que getClientes retorna todos os clientes
                setClientes(data);
            } catch (err) {
                console.error("Erro ao buscar lista de clientes:", err);
                setError("Falha ao carregar lista de clientes para seleção.");
            }
        };

        if (!isEditMode) {
            fetchClientesList();
        }
    }, [isEditMode]);


    useEffect(() => {
        if (isEditMode && id) {
            setLoading(true);
            getCndResultadoById(id)
                .then(data => {
                    // Ajustar o fkCliente para o formulário
                    const formData = { ...data, fkCliente: data.fkCliente || (data.cliente ? data.cliente.id : '') };
                    setInitialData(formData);
                    setLoading(false);
                })
                .catch(err => {
                    setError('Falha ao carregar dados da CND para edição: ' + (err.response?.data?.message || err.message));
                    setLoading(false);
                    console.error(err);
                });
        }
    }, [id, isEditMode]);

    const handleSubmit = async (formData) => {
        setError(null);
        try {
            let response;
            // Ajustar o payload para o backend, especialmente fkCliente
            const payload = { ...formData };
            if (payload.fkCliente && typeof payload.fkCliente === 'string' && payload.fkCliente) {
                 payload.fkCliente = parseInt(payload.fkCliente, 10);
            }


            if (isEditMode) {
                // Adicionar a linha de auditoria para edição
                payload.linha = `UPDATE-PEC-4537-FRONTEND`;
                response = await updateCndResultado(id, payload); // updateCndResultado a ser criado no apiService
                alert('CND atualizada com sucesso!');
            } else {
                 // Adicionar a linha de auditoria para criação
                payload.linha = `INSERT-PEC-4536-FRONTEND`;
                response = await createCndResultado(payload); // createCndResultado a ser criado no apiService
                alert('CND cadastrada com sucesso!');
            }
            navigate('/cnds'); // Redireciona para a lista após sucesso
        } catch (err) {
            setError('Erro ao salvar CND: ' + (err.response?.data?.message || JSON.stringify(err.response?.data?.errors) || err.message));
            console.error(err);
        }
    };

    if (loading && isEditMode) return <p>Carregando formulário...</p>;
    // Não mostrar erro fatal aqui, pois o formulário pode ser usado para criar mesmo se a lista de clientes falhar

    return (
        <div>
            <h2>{isEditMode ? 'Editar Certidão (Resultado CND)' : 'Cadastrar Nova Certidão (Resultado CND)'}</h2>
            {error && <p style={{ color: 'red' }}>Erro: {error}</p>}
            <CndForm
                initialData={initialData}
                onSubmit={handleSubmit}
                clientes={clientes} // Passa a lista de clientes para o formulário
                isEditMode={isEditMode}
            />
        </div>
    );
};

export default CndFormPage;
