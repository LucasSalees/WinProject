let currentPage = 0;
const pageSize = 50;
let loading = false;
let hasNext = true;
let currentFilter = '';
let currentSortBy = '';
let currentSortDirection = '';

const tableBody = document.getElementById('table-body');
const loadingIndicator = document.getElementById('loading-indicator');
const backToTopButton = document.getElementById('back-to-top');
const cboHeader = document.querySelector('th[onclick*="cbo"]');

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

async function loadOccupations(resetTable = false, sortBy = 'occupationCBO', sortDirection = 'asc') {
    if (loading && !resetTable) return;
    
    // Se a ordenação ou o filtro mudou, resetar a tabela
    const filter = getCurrentFilter();
    if (filter !== currentFilter || sortBy !== currentSortBy || sortDirection !== currentSortDirection) {
        resetTable = true;
        currentFilter = filter;
        currentSortBy = sortBy;
        currentSortDirection = sortDirection;
        hasNext = true; // Necessário resetar para novas buscas
    }
    
    if (!hasNext) return;

    loading = true;
    loadingIndicator.style.display = 'block';

    try {
        if (resetTable) {
            tableBody.innerHTML = '';
            currentPage = 0;
        }

        const response = await fetch(`/input/manager/occupations/page?page=${currentPage}&size=${pageSize}&filter=${encodeURIComponent(currentFilter)}&sortBy=${currentSortBy}&sortDirection=${currentSortDirection}`);
        const data = await response.json();

		data.content.forEach(occupation => {
            const row = document.createElement('tr');
            row.innerHTML = `
			    <td class="text-left">
			        <a href="/input/manager/occupations/edit/${occupation.occupationId}" class="row-link">${occupation.occupationId}</a>
			    </td>
			    <td class="text-left">
			        <a href="/input/manager/occupations/edit/${occupation.occupationId}" class="row-link">${occupation.occupationName}</a>
			    </td>
			    <td class="text-left">
			        <a href="/input/manager/occupations/edit/${occupation.occupationId}" class="row-link">${occupation.occupationCBO}</a>
			    </td>
			    <td class="text-left">
			        <a href="/input/manager/occupations/edit/${occupation.occupationId}" class="row-link">${occupation.occupationType}</a> <!-- label em português -->
			    </td>
			`;;
            tableBody.appendChild(row);
        });

        hasNext = !data.last;
        currentPage++;
        
        // Atualiza os ícones de ordenação
        updateSortIcons();
    } catch (error) {
        console.error('Erro ao carregar ocupações:', error);
    } finally {
        loading = false;
        loadingIndicator.style.display = 'none';
    }
}

function updateSortIcons() {
    document.querySelectorAll('th[onclick*="cbo"]').forEach(th => {
        const icon = th.querySelector('i');
        icon.className = 'fa fa-sort'; // Reset all icons

        if (th.getAttribute('data-sort-by') === 'occupationCBO') {
            if (currentSortDirection === 'asc') {
                icon.className = 'fa fa-sort-asc';
            } else if (currentSortDirection === 'desc') {
                icon.className = 'fa fa-sort-desc';
            }
        }
    });
}

// Alterna a direção da ordenação ao clicar no cabeçalho
cboHeader.onclick = function() {
    const newSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';
    loadOccupations(true, 'occupationCBO', newSortDirection);
};

// Detecta quando o usuário chega perto do fim da página
window.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 300) {
        loadOccupations(false, currentSortBy, currentSortDirection);
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
    // Chame com a ordenação padrão de CBO, se desejar
    loadOccupations(true, 'occupationCBO', 'asc'); 
});