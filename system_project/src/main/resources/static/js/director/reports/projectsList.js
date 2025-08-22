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

async function loadProjects(resetTable = false) {
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

        const response = await fetch(`/input/director/reports/pageProject?page=${currentPage}&size=${pageSize}&filter=${encodeURIComponent(filter)}`);
        const data = await response.json();

        data.content.forEach(project => {
            const row = document.createElement('tr');
   
            // As datas vêm como timestamps ou strings, precisam ser formatadas.
            const startDate = project.projectPlanningStartDate ? new Date(project.projectPlanningStartDate).toLocaleDateString('pt-BR') : '';
            const endDate = project.projectPlanningEndDate ? new Date(project.projectPlanningEndDate).toLocaleDateString('pt-BR') : '';

            row.innerHTML = `
			<td class="text-left">
                <a href="/input/director/reports/editProject/${project.projectId}" class="row-link">${project.projectId}</a>
            </td>
            <td class="text-left">
                <a href="/input/director/reports/editProject/${project.projectId}" class="row-link">${project.projectName}</a>
            </td>
            <td class="text-left">
                <a href="/input/director/reports/editProject/${project.projectId}" class="row-link">${project.projectContractualAcronym.acronym}</a>
            </td>
            <td class="text-left">
                <a href="/input/director/reports/editProject/${project.projectId}" class="row-link">${project.projectBusinessVerticalLabel}</a>
            </td>
            <td class="text-left">
                <a href="/input/director/reports/editProject/${project.projectId}" class="row-link">${project.projectPriorityLabel}</a>
            </td>
            <td class="text-left">
                <a href="/input/director/reports/editProject/${project.projectId}" class="row-link">${project.projectStatusLabel}</a>
            </td>
            <td class="text-left">
                <a href="/input/director/reports/editProject/${project.projectId}" class="row-link">${project.projectExecutionPercentage}%</a>
            </td>
            <td class="text-left">
                <a href="/input/director/reports/editProject/${project.projectId}" class="row-link">${startDate}</a>
            </td>
            <td class="text-left">
                <a href="/input/director/reports/editProject/${project.projectId}" class="row-link">${endDate}</a>
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
        loadProjects();
    }

    // Mostra ou esconde o botão Voltar ao Topo
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        backToTopButton.style.display = 'block';
    } else {
        backToTopButton.style.display = 'none';
    }
});

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
    loadProjects(true);
});

// Adiciona a função de rolar para o topo
function topFunction() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
}