// Elementos do DOM
const sidebarToggle = document.getElementById('sidebar-toggle');
const sidebar = document.getElementById('sidebar');
const sidebarOverlay = document.getElementById('sidebar-overlay');
const mainContent = document.getElementById('main-content');
const currentYearElement = document.getElementById('current-year');

// Elementos do Modal de Alteração de Senha
const alterarSenhaBtn = document.getElementById('alterarSenhaBtn');
const alterarSenhaModal = document.getElementById('alterarSenhaModal');
const fecharModal = document.getElementById('fecharModal');
const cancelarSenha = document.getElementById('cancelarSenha');
const salvarSenha = document.getElementById('salvarSenha');
const novaSenha = document.getElementById('novaSenha');
const confirmarSenha = document.getElementById('confirmarSenha');
const mensagemErro = document.getElementById('mensagemErro');
const toggleNovaSenha = document.getElementById('toggleNovaSenha');
const toggleConfirmarSenha = document.getElementById('toggleConfirmarSenha');


// Definir o ano atual no footer
currentYearElement.textContent = new Date().getFullYear();

// Função para abrir/fechar o sidebar
function toggleSidebar() {
	const isOpen = sidebar.classList.contains('sidebar-open');

	if (isOpen) {
		// Fechar o sidebar
		sidebar.classList.remove('sidebar-open');
		sidebar.classList.add('sidebar-closed');
		sidebarOverlay.classList.add('hidden');

		// Em telas maiores, ajustar o conteúdo principal
		if (window.innerWidth >= 769) {
			mainContent.classList.remove('sidebar-active');
		}
	} else {
		// Abrir o sidebar
		sidebar.classList.remove('sidebar-closed');
		sidebar.classList.add('sidebar-open');
		sidebarOverlay.classList.remove('hidden');

		// Em telas maiores, ajustar o conteúdo principal
		if (window.innerWidth >= 769) {
			mainContent.classList.add('sidebar-active');
		}
	}
}

// Função para fechar o sidebar
function closeSidebar() {
	sidebar.classList.remove('sidebar-open');
	sidebar.classList.add('sidebar-closed');
	sidebarOverlay.classList.add('hidden');

	// Em telas maiores, ajustar o conteúdo principal
	if (window.innerWidth >= 769) {
		mainContent.classList.remove('sidebar-active');
	}
}

// Funções para o Modal de Alteração de Senha
function abrirModal() {
	alterarSenhaModal.classList.add('show');
	// Limpar campos e mensagens de erro
	novaSenha.value = '';
	confirmarSenha.value = '';
	mensagemErro.style.display = 'none';
}

function fecharModalSenha() {
	alterarSenhaModal.classList.remove('show');
}

function toggleSenhaVisibilidade(inputId, buttonId) {
	const input = document.getElementById(inputId);
	const button = document.getElementById(buttonId);

	if (input.type === 'password') {
		input.type = 'text';
		button.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                <line x1="1" y1="1" x2="23" y2="23"></line>
            </svg>
        `;
	} else {
		input.type = 'password';
		button.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
            </svg>
        `;
	}
}

// Event listeners para o sidebar
sidebarToggle.addEventListener('click', toggleSidebar);
sidebarOverlay.addEventListener('click', closeSidebar);

// Event listeners para o Modal de Alteração de Senha
alterarSenhaBtn.addEventListener('click', function(e) {
	e.preventDefault();
	abrirModal();
});

fecharModal.addEventListener('click', fecharModalSenha);
cancelarSenha.addEventListener('click', fecharModalSenha);
salvarSenha.addEventListener('click', salvarAlteracaoSenha);

// Event listeners para os botões de mostrar/ocultar senha
toggleNovaSenha.addEventListener('click', function() {
	toggleSenhaVisibilidade('novaSenha', 'toggleNovaSenha');
});

toggleConfirmarSenha.addEventListener('click', function() {
	toggleSenhaVisibilidade('confirmarSenha', 'toggleConfirmarSenha');
});

// Fechar o sidebar quando a janela for redimensionada para um tamanho menor
window.addEventListener('resize', function() {
	if (window.innerWidth < 769) {
		mainContent.classList.remove('sidebar-active');
	} else if (sidebar.classList.contains('sidebar-open')) {
		mainContent.classList.add('sidebar-active');
	}
});

function toggleMenu(id) {
	const submenu = document.getElementById(id);
	submenu.classList.toggle('show');
}

function setupModalListeners(modalId, shouldReload = false, redirectUrl = null) {
	const modal = document.getElementById(modalId);
	const okBtn = document.getElementById("okMensagem");
	const closeBtn = document.getElementById("fecharMensagemModal");

	// Remove listeners antigos
	okBtn.onclick = null;
	closeBtn.onclick = null;

	// Novo listener unificado
	const closeHandler = () => {
		modal.classList.remove('show');
		if (shouldReload) {
			setTimeout(() => location.reload(), 300);
		}
		if (redirectUrl) {
			setTimeout(() => window.location.href = redirectUrl, 300);
		}
	};

	okBtn.onclick = closeHandler;
	closeBtn.onclick = closeHandler;
}

function showMessageModal(message, isSuccess = false, options = {}) {
	const mensagemModal = document.getElementById("mensagemModal");
	const mensagemTexto = document.getElementById("mensagemTexto");

	mensagemTexto.textContent = message;
	mensagemTexto.style.color = isSuccess ? "green" : "red";

	// Configura os listeners com opções
	setupModalListeners(
		"mensagemModal",
		options.shouldReload || false,
		options.redirectUrl || null
	);

	mensagemModal.classList.add('show');
}

function salvarAlteracaoSenha() {
    const form = document.getElementById("formAlterarSenha");
    const formData = new FormData(form);
    const params = new URLSearchParams(formData);

    fetch("/changeMyPassword", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "X-Requested-With": "XMLHttpRequest"
        },
        body: params.toString()
    })
    .then(async response => {
        const json = await response.json();
        if (response.ok) {
            fecharModalSenha();
            showMessageModal(json.mensagem, true, { shouldReload: true });
        } else {
            showMessageModal(json.mensagem, false);
        }
    })
    .catch(error => {
        console.error("Erro:", error);
        showMessageModal("Erro inesperado ao alterar senha!", false);
    });
}


// Verificação de primeiro acesso ao carregar a página
function checkFirstAccess() {
    fetch('/check-first-access')
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao verificar primeiro acesso');
            }
            return response.json();
        })
        .then(isFirstAccess => {
            if (isFirstAccess) {
                showPasswordChangeModal(true);
                
                // Adiciona mensagem de primeiro acesso
                const primeiroAcessoAlert = document.createElement('div');
                primeiroAcessoAlert.className = 'alert alert-warning mb-3';
                primeiroAcessoAlert.innerHTML = '<strong>ATENÇÃO:</strong> Este é seu primeiro acesso. Você <strong>DEVE</strong> alterar sua senha para continuar.';
                
                const modalBody = alterarSenhaModal.querySelector('.modal-body');
                if (modalBody) {
                    modalBody.insertBefore(primeiroAcessoAlert, modalBody.firstChild);
                }
            }
        })
        .catch(error => {
            console.error('Erro:', error);
        });
}

// Função para mostrar o modal de alteração de senha
function showPasswordChangeModal(isFirstAccess = false) {
    if (!alterarSenhaModal) return;
    
    // Usando Bootstrap Modal API se disponível
    if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
        const modalInstance = new bootstrap.Modal(alterarSenhaModal);
        
        // Configura para não fechar no primeiro acesso
        if (isFirstAccess) {
            modalInstance._config.backdrop = 'static';
            modalInstance._config.keyboard = false;
        }
        
        modalInstance.show();
    } else {
        // Fallback para manipulação manual
        alterarSenhaModal.classList.add('show');
        document.body.classList.add('modal-open');
    }
    
    // Limpar campos
    if (novaSenha) novaSenha.value = '';
    if (confirmarSenha) confirmarSenha.value = '';
    
    // Esconder mensagem de erro se existir
    if (mensagemErro) {
        mensagemErro.style.display = 'none';
        mensagemErro.textContent = '';
    }
    
    // Comportamento específico para primeiro acesso
    if (isFirstAccess) {
        // Impede fechar o modal se for primeiro acesso
        if (fecharModal) fecharModal.style.display = 'none';
        if (cancelarSenha) cancelarSenha.style.display = 'none';
        
		
		const modalTitle = alterarSenhaModal.querySelector('.modal-title');
		if (modalTitle) {
		    modalTitle.innerHTML = '<span style="font-weight: bold; color: #dc3545;">Atenção:</span> Este é seu primeiro acesso. <br> Você <strong style="font-weight: bold; color: #dc3545;">deve</strong> alterar sua senha para continuar.';
		    modalTitle.classList.add('text-danger'); // Adiciona classe de destaque, se estiver usando Bootstrap
		}

    } else {
        // Restaura botões se não for primeiro acesso
        if (fecharModal) fecharModal.style.display = 'block';
        if (cancelarSenha) cancelarSenha.style.display = 'block';
        
        // Restaura o título padrão
        const modalTitle = alterarSenhaModal.querySelector('.modal-title');
        if (modalTitle) {
            modalTitle.textContent = 'Alterar senha';
            modalTitle.style.color = '';
        }
    }
}

// Inicialização quando o DOM estiver pronto
document.addEventListener("DOMContentLoaded", function() {
    // Esconder mensagem de erro inicialmente
    if (mensagemErro) {
        mensagemErro.style.display = 'none';
    }
    
    // Verificar primeiro acesso
    checkFirstAccess();
});



