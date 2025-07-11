import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { getClientes, deleteCliente, createCliente } from '../services/apiService';

const ClientePage = () => {
    const [clientes, setClientes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [formData, setFormData] = useState({
        nome: '',
        cnpj: ''
    });

    const fetchClientes = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await getClientes();
            setClientes(data);
        } catch (err) {
            setError('Falha ao carregar clientes: ' + (err.response?.data?.message || err.message));
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchClientes();
    }, [fetchClientes]);

    const handleDeleteCliente = async (id) => {
        if (window.confirm(`Tem certeza de que deseja excluir o cliente ID ${id}?`)) {
            try {
                await deleteCliente(id);
                alert(`Cliente ID ${id} excluído com sucesso.`);
                fetchClientes();
            } catch (err) {
                setError('Falha ao excluir cliente: ' + (err.response?.data?.message || err.message));
                console.error(err);
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await createCliente(formData);
            alert('Cliente criado com sucesso!');
            setShowForm(false);
            setFormData({ nome: '', cnpj: '' });
            fetchClientes(); // Recarregar a lista
        } catch (err) {
            setError('Erro ao criar cliente: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    if (loading) return <p>Carregando clientes...</p>;

    return (
        <div>
            <h2>Gerenciamento de Clientes</h2>
            
            {error && <p style={{ color: 'red' }}>Erro: {error}</p>}

            <button onClick={() => setShowForm(!showForm)} className="button">
                {showForm ? 'Cancelar' : 'Novo Cliente'}
            </button>

            {showForm && (
                <form onSubmit={handleSubmit} style={{ margin: '20px 0', padding: '15px', border: '1px solid #ccc' }}>
                    <h3>Novo Cliente</h3>
                    <div>
                        <label htmlFor="nome">Nome:</label>
                        <input
                            type="text"
                            id="nome"
                            name="nome"
                            value={formData.nome}
                            onChange={handleInputChange}
                            required
                        />
                    </div>
                    <div>
                        <label htmlFor="cnpj">CNPJ:</label>
                        <input
                            type="text"
                            id="cnpj"
                            name="cnpj"
                            value={formData.cnpj}
                            onChange={handleInputChange}
                            required
                        />
                    </div>
                    <button type="submit" className="button">Criar Cliente</button>
                </form>
            )}

            {clientes.length === 0 && !loading && !error && (
                <p>Nenhum cliente encontrado.</p>
            )}

            {clientes.length > 0 && (
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nome</th>
                            <th>CNPJ</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        {clientes.map(cliente => (
                            <tr key={cliente.id}>
                                <td>{cliente.id}</td>
                                <td>{cliente.nome}</td>
                                <td>{cliente.cnpj}</td>
                                <td>
                                    <Link to={`/clientes/editar/${cliente.id}`} className="button edit">
                                        Editar
                                    </Link>
                                    <button 
                                        onClick={() => handleDeleteCliente(cliente.id)} 
                                        className="button delete"
                                    >
                                        Excluir
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default ClientePage; 