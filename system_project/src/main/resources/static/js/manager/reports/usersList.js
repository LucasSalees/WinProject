let currentPage = 0;
const pageSize = 50;
let loading = false;
let hasNext = true;
let currentFilter = '';
const tableBody = document.getElementById('table-body');
const loadingIndicator = document.getElementById('loading-indicator');
const backToTopButton = document.getElementById('back-to-top');

// Função para obter o filtro atual do campo de input
function getCurrentFilter() {
    const filterInput = document.getElementById('filter');
    // Retorna o valor do campo de input, ou uma string vazia se o input não existir.
    return filterInput ? filterInput.value : '';
}

async function loadUsers(resetTable = false) {
    if (loading || (!hasNext && !resetTable)) {
        return;
    }
    
    loading = true;
    loadingIndicator.style.display = 'block';

    try {
        const filter = getCurrentFilter();
        
        if (filter !== currentFilter) {
            resetTable = true;
            currentFilter = filter;
        }
        
        if (resetTable) {
            tableBody.innerHTML = '';
            currentPage = 0;
            hasNext = true;
        }

        const response = await fetch(`/input/manager/reports/pageUser?page=${currentPage}&size=${pageSize}&filter=${encodeURIComponent(filter)}`);

        // VERIFICAÇÃO ADICIONADA AQUI
        if (!response.ok) {
            console.error('Erro na requisição:', response.statusText);
            // Lança um erro para ser capturado pelo bloco 'catch'
            throw new Error(`Erro na resposta do servidor: ${response.status}`);
        }

        const data = await response.json();
        
        // VERIFICAÇÃO ADICIONADA AQUI TAMBÉM
        if (!data || !Array.isArray(data.content)) {
            console.error('Erro: O formato da resposta da API é inválido.', data);
            // Lança um erro para ser capturado pelo bloco 'catch'
            throw new Error('Formato de dados inesperado da API.');
        }

        data.content.forEach(user => {
            const row = document.createElement('tr');
            
            const userFunction = user.userFunction ? user.userFunction.functionName : '—';
            
            row.innerHTML = `
                <td class="text-left">
                    <a href="/input/manager/reports/editUser/${user.userId}" class="row-link">${user.userId}</a>
                </td>
                <td class="text-left">
                    <a href="/input/manager/reports/editUser/${user.userId}" class="row-link">${user.userName}</a>
                </td>
                <td class="text-left">
                    <a href="/input/manager/reports/editUser/${user.userId}" class="row-link">${user.userEmail}</a>
                </td>
				<td class="text-left">
				    <a href="/input/director/reports/editUser/${user.userId}" class="row-link">${user.userRole}</a>
				</td>
                <td class="text-left">
                    <a href="/input/manager/reports/editUser/${user.userId}" class="row-link">${userFunction}</a>
                </td>
            `;
            tableBody.appendChild(row);
        });

        hasNext = !data.last;
        currentPage++;
        
    } catch (error) {
        console.error('Erro ao carregar usuários:', error);
    } finally {
        loading = false;
        loadingIndicator.style.display = 'none';
    }
}

// Detecta quando o usuário chega perto do fim da página
window.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 300) {
        loadUsers();
    }

    // Mostra ou esconde o botão Voltar ao Topo
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        backToTopButton.style.display = 'block';
    } else {
        backToTopButton.style.display = 'none';
    }
});

// Função para rolar para o topo da página
function topFunction() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
}

// Carrega a primeira página ao abrir
document.addEventListener('DOMContentLoaded', function() {
    // Inicializa o filtro a partir da URL na primeira carga
    const urlParams = new URLSearchParams(window.location.search);
    const filterFromUrl = urlParams.get('filter') || '';
    const filterInput = document.getElementById('filter');
    if (filterInput) {
        filterInput.value = filterFromUrl;
    }
    currentFilter = filterFromUrl;
    loadUsers(true);
});