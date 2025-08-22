let currentPage = 0;
const pageSize = 50;
let loading = false;
let hasNext = true;
let currentFilter = '';
const tableBody = document.getElementById('table-body');
const loadingIndicator = document.getElementById('loading-indicator');
const backToTopButton = document.getElementById('back-to-top');

// Função para obter o filtro atual da URL ou do campo de input
function getCurrentFilter() {
    const urlParams = new URLSearchParams(window.location.search);
    const filterFromUrl = urlParams.get('filter') || '';
    const filterInput = document.getElementById('filter');
    if (filterInput) {
        filterInput.value = filterFromUrl;
    }
    return filterFromUrl;
}

async function loadOccupations(resetTable = false) {
    if (loading || !hasNext) return;
    
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

        const response = await fetch(`/input/director/occupations/page?page=${currentPage}&size=${pageSize}&filter=${encodeURIComponent(filter)}`);
        const data = await response.json();

        data.content.forEach(occupation => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="text-left">
                    <a href="/input/director/occupations/edit/${occupation.occupationId}" class="row-link">${occupation.occupationId}</a>
                </td>
                <td class="text-left">
                    <a href="/input/director/occupations/edit/${occupation.occupationId}" class="row-link">${occupation.occupationName}</a>
                </td>
                <td class="text-left">
                    <a href="/input/director/occupations/edit/${occupation.occupationId}" class="row-link">${occupation.occupationCBO}</a>
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
        loadOccupations();
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
    currentFilter = getCurrentFilter();
    loadOccupations(true);
});