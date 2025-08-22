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
    return filterInput ? filterInput.value : '';
}

async function loadAcronyms(resetTable = false) {
    if (loading || (!hasNext && !resetTable)) return;
    
    loading = true;
    loadingIndicator.style.display = 'block';

    try {
        const filter = getCurrentFilter();
        
        // Se o filtro mudou, resetar a tabela
        if (filter !== currentFilter) {
            resetTable = true;
            currentFilter = filter;
        }
        
        if (resetTable) {
            tableBody.innerHTML = '';
            currentPage = 0;
            hasNext = true;
        }

        const response = await fetch(`/input/manager/acronyms/page?page=${currentPage}&size=${pageSize}&filter=${encodeURIComponent(filter)}`);
        const data = await response.json();

        data.content.forEach(acronym => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="text-left">
                    <a href="/input/manager/acronyms/edit/${acronym.acronymId}" class="row-link">${acronym.acronymId}</a>
                </td>
                <td class="text-left">
                    <a href="/input/manager/acronyms/edit/${acronym.acronymId}" class="row-link">${acronym.contractualAcronymName}</a>
                </td>
                <td class="text-left">
                    <a href="/input/manager/acronyms/edit/${acronym.acronymId}" class="row-link">${acronym.acronym}</a>
                </td>
            `;
            tableBody.appendChild(row);
        });

        hasNext = !data.last;
        currentPage++;
        
    } catch (error) {
        console.error('Erro ao carregar ocupações:', error);
    } finally {
        loading = false;
        loadingIndicator.style.display = 'none';
    }
}

// Detecta quando o usuário chega perto do fim da página
window.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 300) {
        loadAcronyms();
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
    document.body.scrollTop = 0; // For Safari
    document.documentElement.scrollTop = 0; // For Chrome, Firefox, IE and Opera
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
    loadAcronyms(true);
});
