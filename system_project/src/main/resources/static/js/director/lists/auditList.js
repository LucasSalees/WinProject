let currentPage = 0;
const pageSize = 50;
let loading = false;
let hasNext = true;
let currentFilter = '';
const tableBody = document.getElementById('table-body');
const loadingIndicator = document.getElementById('loading-indicator');
const backToTopButton = document.getElementById('back-to-top');

// --- INÍCIO DA CORREÇÃO ---
// Pega os elementos do modal customizado
const detailsModal = document.getElementById('detailsModal');
const fecharDetailsModalBtn = document.getElementById('fecharDetailsModal');
const cancelarDetailsModalBtn = document.getElementById('cancelarDetailsModal');
// --- FIM DA CORREÇÃO ---

const oldValueContent = document.getElementById('oldValueContent');
const newValueContent = document.getElementById('newValueContent');


function getCurrentFilter() {
    const filterInput = document.getElementById('filter');
    return filterInput ? filterInput.value : '';
}

async function loadLogs(resetTable = false) {
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

        const response = await fetch(`/input/director/audits/page?page=${currentPage}&size=${pageSize}&filter=${encodeURIComponent(filter)}`);

        if (!response.ok) {
            throw new Error(`Erro na resposta do servidor: ${response.status}`);
        }

        const data = await response.json();
        
        if (!data || !Array.isArray(data.content)) {
            throw new Error('Formato de dados inesperado da API.');
        }

        data.content.forEach(log => {
            const row = document.createElement('tr');
            
            const timestamp = new Date(log.timestamp).toLocaleString('pt-BR', {
                day: '2-digit', month: '2-digit', year: 'numeric',
                hour: '2-digit', minute: '2-digit', second: '2-digit'
            });

            row.innerHTML = `
                <td class="text-left">${timestamp}</td>
                <td class="text-left">${log.userName || 'N/A'}</td>
                <td class="text-left">${log.action || 'N/A'}</td>
                <td class="text-left">${log.module || 'N/A'}</td>
                <td class="text-left">${log.description || 'N/A'}</td>
                <td class="text-center">
                    <button class="btn btn-sm btn-info" 
                            data-old='${log.oldValue || ''}' 
                            data-new='${log.newValue || ''}' 
                            onclick="showDetails(this)">
                        <i class="fa-solid fa-eye"></i>
                    </button>
                </td>
            `;
            tableBody.appendChild(row);
        });

        hasNext = !data.last;
        currentPage++;
        
    } catch (error) {
        console.error('Erro ao carregar logs:', error);
    } finally {
        loading = false;
        loadingIndicator.style.display = 'none';
    }
}

function showDetails(button) {
    const oldVal = button.getAttribute('data-old');
    const newVal = button.getAttribute('data-new');

    try {
        const oldJson = oldVal ? JSON.stringify(JSON.parse(oldVal), null, 2) : 'Nenhum valor antigo registrado.';
        const newJson = newVal ? JSON.stringify(JSON.parse(newVal), null, 2) : 'Nenhum valor novo registrado.';
        oldValueContent.textContent = oldJson;
        newValueContent.textContent = newJson;
    } catch (e) {
        oldValueContent.textContent = oldVal || 'Nenhum valor antigo registrado.';
        newValueContent.textContent = newVal || 'Nenhum valor novo registrado.';
    }
    
    // --- INÍCIO DA CORREÇÃO ---
    // Mostra o modal customizado
    if (detailsModal) {
        detailsModal.style.display = 'flex';
    }
    // --- FIM DA CORREÇÃO ---
}

// --- INÍCIO DA CORREÇÃO ---
// Funções para fechar o modal customizado
function closeDetailsModal() {
    if (detailsModal) {
        detailsModal.style.display = 'none';
    }
}

if (fecharDetailsModalBtn) {
    fecharDetailsModalBtn.addEventListener('click', closeDetailsModal);
}
if (cancelarDetailsModalBtn) {
    cancelarDetailsModalBtn.addEventListener('click', closeDetailsModal);
}
// Fecha o modal se clicar fora dele
window.addEventListener('click', (event) => {
    if (event.target == detailsModal) {
        closeDetailsModal();
    }
});
// --- FIM DA CORREÇÃO ---


window.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 300) {
        loadLogs();
    }
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        backToTopButton.style.display = 'block';
    } else {
        backToTopButton.style.display = 'none';
    }
});

function topFunction() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
}

document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const filterFromUrl = urlParams.get('filter') || '';
    const filterInput = document.getElementById('filter');
    if (filterInput) {
        filterInput.value = filterFromUrl;
    }
    currentFilter = filterFromUrl;
    loadLogs(true);
});