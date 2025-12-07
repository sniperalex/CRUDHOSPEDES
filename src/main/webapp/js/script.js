const API_URL = 'api/hospedes';

// Executa ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    carregarHospedes();
});

// Evento de envio do formulário
document.getElementById('formHospede').addEventListener('submit', async (e) => {
    e.preventDefault(); // Não recarrega a página
    await salvarHospede();
});

// CONSULTAR (GET)
async function carregarHospedes() {
    try {
        const response = await fetch(API_URL);
        
        // Verifica se a resposta foi JSON válido
        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            throw new Error("Resposta do servidor não é JSON.");
        }

        const lista = await response.json();
        
        const tbody = document.getElementById('tabelaCorpo');
        tbody.innerHTML = ''; // Limpa tabela atual

        lista.forEach(h => {
            const tr = document.createElement('tr');
            // Salvamos os dados no atributo 'data-hospede' para recuperar na edição
            // Convertemos para string e escapamos as aspas para não quebrar o HTML
            const dadosJson = JSON.stringify(h).replace(/"/g, '&quot;');
            
            tr.innerHTML = `
                <td>${h.nome}</td>
                <td>${h.cpf}</td>
                <td>${h.email}</td>
                <td>${h.cidade}/${h.estado}</td>
                <td>
                    <button class="btn btn-warning btn-sm" onclick="preencherFormulario(${dadosJson})">Editar</button>
                    <button class="btn btn-danger btn-sm" onclick="inativar(${h.id})">Excluir</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
        
        // Reaplica o filtro caso já tenha algo digitado
        filtrarTabela();

    } catch (erro) {
        console.error('Erro ao carregar:', erro);
        // Não exibe alerta em loop se falhar, apenas loga
    }
}

// CADASTRAR (POST) ou ALTERAR (PUT)
async function salvarHospede() {
    // Monta o objeto com os dados do form
    const id = document.getElementById('hospedeId').value;
    
    const hospede = {
        id: id ? parseInt(id) : null,
        nome: document.getElementById('nome').value,
        cpf: document.getElementById('cpf').value,
        dataNascimento: document.getElementById('dataNascimento').value,
        email: document.getElementById('email').value,
        telefone: document.getElementById('telefone').value,
        cep: document.getElementById('cep').value,
        logradouro: document.getElementById('logradouro').value,
        numero: document.getElementById('numero').value,
        bairro: document.getElementById('bairro').value,
        cidade: document.getElementById('cidade').value,
        estado: document.getElementById('estado').value,
        complemento: document.getElementById('complemento').value,
        ativo: true
    };

    const metodo = id ? 'PUT' : 'POST'; // Se tem ID, é edição (PUT)
    
    try {
        const response = await fetch(API_URL, {
            method: metodo,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(hospede)
        });

        const resultado = await response.json();

        if (response.ok) {
            alert(resultado.mensagem);
            limparFormulario();
            carregarHospedes();
        } else {
            alert('Erro: ' + resultado.erro);
        }
    } catch (erro) {
        console.error('Erro:', erro);
        alert('Erro de comunicação com o servidor.');
    }
}

// INATIVAR (DELETE)
async function inativar(id) {
    if (!confirm('Deseja realmente excluir este hóspede?')) return;

    try {
        const response = await fetch(`${API_URL}?id=${id}`, {
            method: 'DELETE'
        });
        
        const resultado = await response.json();
        
        if (response.ok) {
            alert(resultado.mensagem);
            carregarHospedes();
        } else {
            alert('Erro: ' + resultado.erro);
        }
    } catch (erro) {
        console.error('Erro:', erro);
    }
}

// Preenche o formulário para edição
// A função recebe o objeto direto do clique do botão
window.preencherFormulario = function(hospede) {
    document.getElementById('hospedeId').value = hospede.id || '';
    document.getElementById('nome').value = hospede.nome || '';
    document.getElementById('cpf').value = hospede.cpf || '';
    
    // Converte a data se necessário (garante yyyy-MM-dd)
    if(hospede.dataNascimento) {
        // Se vier como objeto de data complexo do JSON
        if(typeof hospede.dataNascimento === 'object') {
             // Lógica caso o gson serialize diferente, mas geralmente vem String
        }
        document.getElementById('dataNascimento').value = hospede.dataNascimento;
    }
    
    document.getElementById('email').value = hospede.email || '';
    document.getElementById('telefone').value = hospede.telefone || '';
    document.getElementById('cep').value = hospede.cep || '';
    document.getElementById('logradouro').value = hospede.logradouro || '';
    document.getElementById('numero').value = hospede.numero || '';
    document.getElementById('bairro').value = hospede.bairro || '';
    document.getElementById('cidade').value = hospede.cidade || '';
    document.getElementById('estado').value = hospede.estado || '';
    document.getElementById('complemento').value = hospede.complemento || '';
    
    window.scrollTo(0, 0); // Sobe a tela para o form
};

function limparFormulario() {
    document.getElementById('formHospede').reset();
    document.getElementById('hospedeId').value = '';
}

// Função de Filtro (Frontend)
window.filtrarTabela = function() {
    const termo = document.getElementById('filtroInput').value.toLowerCase();
    const linhas = document.querySelectorAll('#tabelaCorpo tr');

    linhas.forEach(linha => {
        const texto = linha.textContent.toLowerCase();
        if (texto.includes(termo)) {
            linha.style.display = '';
        } else {
            linha.style.display = 'none';
        }
    });
};