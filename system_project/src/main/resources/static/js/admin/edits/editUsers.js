const emailInput = document.getElementById('email-usuario');
let score = 0;

// Variável global para controlar se há senha digitada
let senhaDigitada = '';

const form = document.getElementById('formulario');
form.addEventListener('submit', async function(event) {
    event.preventDefault();

    const emailValido = await validarEmail(emailInput);
    if (!emailValido) {
        emailInput.focus();
        return;
    }
    
    // Validação da senha
    const senhaInput = document.getElementById('senha');
    const erroSenha = validarSenha(senhaInput.value);
    
    if (erroSenha) {
        showMessageModal(erroSenha, false);
        senhaInput.focus();
        return;
    }
   
    // Envia via fetch (modal)
    const formData = new FormData(form);
    
    // Se não há senha digitada, remove o campo do FormData para não alterar a senha atual
    if (senhaInput.value === '') {
        formData.delete('novaSenha');
    }

    // --- INÍCIO DA MODIFICAÇÃO PARA AGRUPAR PERMISSÕES ---
    // 1. Pega todos os checkboxes de permissão que estão marcados
    const permissionsCheckboxes = form.querySelectorAll('input[name="permissions"]:checked');

    // 2. Extrai os valores (os nomes das permissões)
    const permissionsValues = Array.from(permissionsCheckboxes).map(cb => cb.value);

    // 3. Converte o array de valores para uma string JSON
    const permissionsJson = JSON.stringify(permissionsValues);

    // 4. Remove os campos "permissions" individuais do FormData
    formData.delete('permissions');

    // 5. Adiciona a string JSON como um único campo chamado "permissionsJson"
    formData.append('permissionsJson', permissionsJson);
    // --- FIM DA MODIFICAÇÃO PARA AGRUPAR PERMISSÕES ---

	fetch('/input/admin/users/edit', {
	    method: 'POST',
	    body: formData,
	    headers: {
	        'X-Requested-With': 'XMLHttpRequest'
	    }
	})

    .then(response => {
        if (!response.ok) throw response; // lança para o catch
        return response.json();
    })
    .then(data => {
        if (data.status === "success") {
            showMessageModal(data.mensagem, true, {
                redirectUrl: "/input/admin/users/list"
            });
        }
    })
    .catch(async error => {
        if (error.json) {
            try {
                const err = await error.json();
                showMessageModal(err.mensagem || "Erro ao editar usuário.", false);
            } catch {
                showMessageModal("Erro desconhecido ao processar a resposta do servidor.", false);
            }
        } else {
            showMessageModal("Erro de conexão ou resposta inválida do servidor.", false);
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const senhaInput = document.getElementById('senha');
    
    // Adiciona o evento de input para verificar a senha enquanto digita
    if (senhaInput) {
        senhaInput.addEventListener('input', function() {
            const senhaDigitada = this.value;
            verificarForcaSenha(senhaDigitada);
        });
    }

    // NÃO cortar o valor do input, assume que vem completo do backend
    const formulario = document.getElementById('formulario');
    if (formulario) {
        formulario.style.display = "block";
    }
});

// Função para verificar a força da senha
function verificarForcaSenha(senha) {
    const forcaText = document.getElementById('forca-text');
    const strengthFill = document.getElementById('strengthFill');
    
    // Reset
    let score = 0;
    let forca = "Muito fraca";
    let cor = "#dc3545"; // Vermelho
    
    // Verifica se há senha digitada
    if (senha.length === 0) {
        forcaText.textContent = "Não informada";
        forcaText.style.color = "#6c757d"; // Cinza
        strengthFill.style.width = "0%";
        strengthFill.style.backgroundColor = "#6c757d";
        return;
    }
    
    // Verificações básicas
    if (senha.length < 8) {
        forcaText.textContent = "Muito fraca (mínimo 8 caracteres)";
        forcaText.style.color = "#dc3545"; // Vermelho
        strengthFill.style.width = "25%";
        strengthFill.style.backgroundColor = "#dc3545";
        return;
    }
    
    if (senha.length > 20) {
        forcaText.textContent = "Muito fraca (máximo 20 caracteres)";
        forcaText.style.color = "#dc3545"; // Vermelho
        strengthFill.style.width = "25%";
        strengthFill.style.backgroundColor = "#dc3545";
        return;
    }
    
    if (senha.match(/.*\s.*/)) {
        forcaText.textContent = "Muito fraca (não pode conter espaços)";
        forcaText.style.color = "#dc3545"; // Vermelho
        strengthFill.style.width = "25%";
        strengthFill.style.backgroundColor = "#dc3545";
        return;
    }
    
    // Calcula a pontuação da senha
    if (senha.match(/[a-z]/)) score++; // Letras minúsculas
    if (senha.match(/[A-Z]/)) score++; // Letras maiúsculas
    if (senha.match(/[0-9]/)) score++; // Números
    if (senha.match(/[^a-zA-Z0-9]/)) score++; // Caracteres especiais
    
    // Determina a força com base na pontuação
    if (score === 1) {
        forca = "Fraca";
        cor = "#ffc107"; // Amarelo
        strengthFill.style.width = "50%";
    } else if (score === 2) {
        forca = "Moderada";
        cor = "#fd7e14"; // Laranja
        strengthFill.style.width = "65%";
    } else if (score === 3) {
        forca = "Forte";
        cor = "#28a745"; // Verde
        strengthFill.style.width = "85%";
    } else if (score >= 4) {
        forca = "Muito forte";
        cor = "#20c997"; // Verde-água
        strengthFill.style.width = "100%";
    } else {
        forca = "Muito fraca";
        cor = "#dc3545"; // Vermelho
        strengthFill.style.width = "25%";
    }
    
    // Atualiza a UI
    forcaText.textContent = forca;
    forcaText.style.color = cor;
    strengthFill.style.backgroundColor = cor;
}

// Função para validar a senha antes do envio do formulário
function validarSenha(senha) {
    if (senha.length === 0) {
        return null; // Senha não foi alterada
    }
    
    if (senha.length < 8) {
        return "A senha não pode ter menos de 8 caracteres!";
    }
    
    if (senha.length > 20) {
        return "A senha não pode ter mais que 20 caracteres!";
    }
    
    if (senha.match(/.*\s.*/)) {
        return "A senha não pode conter espaços!";
    }
    
    if (!verificarForcaSenhaParaEnvio(senha)) {
        return "A senha precisa ser forte ou muito forte! Adicione caracteres especiais e números.";
    }
    
    return null; // Tudo ok
}

// Função auxiliar para verificar a força da senha (para envio)
function verificarForcaSenhaParaEnvio(senha) {
    let score = 0;
    
    if (senha.match(/[a-z]/)) score++; // Letras minúsculas
    if (senha.match(/[A-Z]/)) score++; // Letras maiúsculas
    if (senha.match(/[0-9]/)) score++; // Números
    if (senha.match(/[^a-zA-Z0-9]/)) score++; // Caracteres especiais
    
    return score >= 3;
}

emailInput.addEventListener('input', function() {
    emailCompleto.textContent = this.value.trim();
    validarEmail(this);
});

//adiciona as mascaras
$('#userTel').mask('(00) 00000-0000');

function openExclusaoModal(link, redirectUrl = null) {
    const exclusaoModal = document.getElementById('exclusaoModal');
    exclusaoModal.classList.add('show');

    document.getElementById('confirmarExclusaoBtn').onclick = function() {
        fetch(link.href, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            if (!response.ok) {
                if (response.status === 403) {
                    return response.json().then(err => {
                        throw { 
                            mensagem: err.mensagem || "Acesso negado: você não tem permissão para esta ação.",
                            status: "erro"
                        };
                    });
                }
                throw response;
            }
            return response.json();
        })
        .then(data => {
            exclusaoModal.classList.remove('show');
            showMessageModal(data.mensagem, data.status === "success", {
                redirectUrl: redirectUrl || "/input/admin/users/list"
            });
        })
        .catch(async error => {
            exclusaoModal.classList.remove('show');

            if (error instanceof Response && error.json) {
                try {
                    const err = await error.json();
                    showMessageModal(err.mensagem || "Erro ao excluir item.", false);
                } catch {
                    showMessageModal("Erro inesperado ao processar a exclusão.", false);
                }
            } else {
                showMessageModal(error.mensagem || "Erro ao excluir item.", false);
            }
        });
    };
}

document.getElementById('fecharExclusaoModal').onclick = () => {
    document.getElementById('exclusaoModal').classList.remove('show');
};

document.getElementById('cancelarExclusao').onclick = () => {
    document.getElementById('exclusaoModal').classList.remove('show');
};

// Função para pré-visualizar a foto (comum para cadastro e edição)
function previewPhoto(input) {
    const preview = document.getElementById('photoPreview');
    const btnRemove = document.getElementById('btnRemovePhoto');
    
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
            btnRemove.style.display = 'inline-block';
        };
        reader.readAsDataURL(input.files[0]);
    }
}

// Função para abrir modal de confirmação de exclusão de foto (edição)
function openExclusaoFotoModal() {
    const userId = document.getElementById('id-usuario').value;
    const photoPath = document.getElementById('photoPreview').src.split('/').pop();
    
    // Se for a imagem padrão, não faz nada
    if (photoPath === 'DefaultAvatar.png' || photoPath === 'images/DefaultAvatar.png') {
        return;
    }

    const exclusaoModal = document.getElementById('exclusaoModal');
    exclusaoModal.classList.add('show');

    document.getElementById('confirmarExclusaoBtn').onclick = function() {
        exclusaoModal.classList.remove('show');
        excluirFoto(photoPath, userId);
    };
}

// Função para excluir foto (específica para edição)
function excluirFoto(fileName, userId) {
    if (!fileName || fileName === "DefaultAvatar.png") return;

    fetch(`/removePhoto?fileName=${encodeURIComponent(fileName)}&userId=${userId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            if (response.status === 403) {
                return response.json().then(err => {
                    throw { 
                        mensagem: err.mensagem || "Acesso negado: você não tem permissão para esta ação.",
                        status: "erro"
                    };
                });
            }
            throw response;
        }
        return response.json();
    })
    .then(data => {
        showMessageModal(data.mensagem, data.status === "success");
        document.getElementById("photoPreview").src = "/images/DefaultAvatar.png";
        document.getElementById("profileImage").value = "";
        document.getElementById("btnRemovePhoto").style.display = 'none';
    })
    .catch(error => {
        if (error.json) {
            error.json().then(err => {
                showMessageModal(err.mensagem || "Erro ao remover foto.", false);
            }).catch(() => {
                showMessageModal("Erro inesperado ao processar a remoção da foto.", false);
            });
        } else {
            showMessageModal(error.mensagem || "Erro ao remover foto.", false);
        }
    });
}

// Mostrar botão de excluir se já houver foto ao carregar a página
document.addEventListener('DOMContentLoaded', function() {
    const photoPreview = document.getElementById('photoPreview');
    const btnRemove = document.getElementById('btnRemovePhoto');
    
    // Verifica se a imagem não é a padrão
    if (!photoPreview.src.includes('DefaultAvatar.png')) {
        btnRemove.style.display = 'inline-block';
    }
});

// Função para mostrar/ocultar senha
function togglePassword() {
    const senhaInput = document.getElementById('senha');
    const eyeIcon = document.querySelector('.input-with-icon i');

    if (senhaInput.type === 'password') {
        senhaInput.type = 'text';
        eyeIcon.classList.remove('fa-eye');
        eyeIcon.classList.add('fa-eye-slash');
    } else {
        senhaInput.type = 'password';
        eyeIcon.classList.remove('fa-eye-slash');
        eyeIcon.classList.add('fa-eye');
    }
}

// Funções para seleção rápida de dias (atualizadas para o novo nome allowedDays)
function selectAll() {
    document.querySelectorAll('input[name="allowedDays"]').forEach(cb => cb.checked = true);
}

function disableAll() {
    document.querySelectorAll('input[name="allowedDays"]').forEach(cb => cb.checked = false);
}

function weekdaySelect() {
    disableAll();
    ['segunda', 'terca', 'quarta', 'quinta', 'sexta'].forEach(id => {
        document.getElementById(id).checked = true;
    });
}

function weekendSelect() {
    disableAll();
    ['sabado', 'domingo'].forEach(id => {
        document.getElementById(id).checked = true;
    });
}

function resetarFormulario() {
    if (confirm('Tem certeza que deseja resetar o formulário? Todas as alterações não salvas serão perdidas.')) {
        location.reload();
    }
}

async function verificarEmailExistente(email) {
    const usuarioId = document.querySelector('input[name="userId"]')?.value || '';

    try {
        const response = await fetch('/verify-email', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `email=${encodeURIComponent(email)}&id=${encodeURIComponent(usuarioId)}`
        });

        return await response.json();
    } catch (error) {
        console.error('Erro ao verificar email:', error);
        return { existe: false };
    }
}

// Validação ao sair do campo (blur)
document.getElementById('email-usuario').addEventListener('blur', async function() {
    const emailInput = this;
    await validarEmail(emailInput);
});

async function validarEmail(emailInput) {
    const email = emailInput.value.trim();
    const feedbackElement = document.getElementById('emailFeedback');
    const successElement = document.getElementById('emailSuccess');
    const userId = document.querySelector('input[name="userId"]')?.value || '';

    if (email === '') {
        emailInput.classList.remove('is-valid', 'is-invalid');
        feedbackElement.style.display = 'none';
        successElement.style.display = 'none';
        emailInput.setCustomValidity('');
        return true;
    }

    try {
        const response = await fetch('/verify-email', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `email=${encodeURIComponent(email)}&id=${encodeURIComponent(userId)}`
        });
        const result = await response.json();

        if (result.existe) {
            emailInput.classList.remove('is-valid');
            emailInput.classList.add('is-invalid');
            feedbackElement.style.display = 'block';
            successElement.style.display = 'none';
            emailInput.setCustomValidity('Email já cadastrado');
            return false;
        } else {
            emailInput.classList.remove('is-invalid');
            emailInput.classList.add('is-valid');
            feedbackElement.style.display = 'none';
            successElement.style.display = 'block';
            emailInput.setCustomValidity('');
            return true;
        }
    } catch (error) {
        console.error('Erro ao verificar email:', error);
        emailInput.classList.remove('is-invalid');
        feedbackElement.style.display = 'none';
        successElement.style.display = 'none';
        emailInput.setCustomValidity('');
        return true;
    }
}

document.addEventListener("DOMContentLoaded", function () {

    /**
     * Função genérica e reutilizável para configurar a lógica de um modal de seleção com filtro.
     * @param {string} modalId - O ID do elemento do modal (ex: 'modalListaProfissoes').
     * @param {string} campoId - O ID do campo de texto que abre o modal (ex: 'campoProfissao').
     * @param {string} inputId - O ID do input hidden que armazenará o valor selecionado (ex: 'userOccupation').
     * @param {string} textoId - O ID do span que mostrará o texto selecionado (ex: 'textoProfissaoSelecionada').
     * @param {string} filtroInputId - O ID do campo de texto para o filtro (ex: 'filtro').
     * @param {string} fecharBtnId - O ID do botão para fechar o modal.
     * @param {string} linhaClasse - A classe CSS das linhas clicáveis da tabela (ex: 'linha-profissao').
     * @param {object} camposFiltro - Um objeto mapeando o ID do checkbox do filtro para a coluna da tabela (ex: { campoCodigo: 0, campoNome: 1 }).
     */
    function configurarModalDeSelecao(modalId, campoId, inputId, textoId, filtroInputId, fecharBtnId, linhaClasse, camposFiltro) {
        const modal = document.getElementById(modalId);
        if (!modal) return; // Se o modal não existir na página, não faz nada.

        const campo = document.getElementById(campoId);
        const inputValor = document.getElementById(inputId);
        const textoSelecionado = document.getElementById(textoId);
        const filtroInput = document.getElementById(filtroInputId);
        const fecharBtn = document.getElementById(fecharBtnId);
        const tabela = modal.querySelector('table'); // Pega a tabela específica DENTRO do modal
        const linhas = tabela.querySelectorAll(`.${linhaClasse}`);
        const checkboxesFiltro = modal.querySelectorAll('.campoCheckbox');
        const selecionarTodosCheckbox = modal.querySelector('.selecionarTodos');

        // 1. Abrir o modal
        campo.addEventListener('click', () => {
            modal.style.display = 'flex';
            filtroInput.focus(); // Foca no campo de busca ao abrir
        });

        // 2. Fechar o modal
        fecharBtn.addEventListener('click', () => {
            modal.style.display = 'none';
        });

        // 3. Selecionar um item na tabela
        linhas.forEach(row => {
            row.addEventListener('click', () => {
                const id = row.getAttribute('data-id');
                const nome = row.getAttribute('data-nome');
                
                inputValor.value = id;
                textoSelecionado.innerText = `${id} - ${nome}`;
                modal.style.display = 'none';
            });
        });

        // 4. Filtrar a tabela
        function filtrarTabela() {
            const filtro = filtroInput.value.toLowerCase().trim();
            const linhasTabela = tabela.querySelectorAll('tbody tr');
            
            const camposAtivos = {};
            checkboxesFiltro.forEach(cb => {
                if (cb.checked) {
                    // Extrai o nome do campo do ID do checkbox (ex: 'campoCodigoProfissao' -> 'campoCodigo')
                    const nomeCampo = cb.id.replace(modalId.replace('modalLista', ''), '');
                    camposAtivos[nomeCampo] = true;
                }
            });

            linhasTabela.forEach(linha => {
                if (!filtro) {
                    linha.style.display = ""; // Mostra a linha se o filtro estiver vazio
                    return;
                }

                let mostrarLinha = false;
                for (const [checkboxId, colunaIndex] of Object.entries(camposFiltro)) {
                    const checkbox = document.getElementById(checkboxId);
                    if (checkbox && checkbox.checked) {
                        const cell = linha.querySelector(`td:nth-child(${colunaIndex + 1})`);
                        if (cell && cell.textContent.toLowerCase().includes(filtro)) {
                            mostrarLinha = true;
                            break; // Encontrou uma correspondência, não precisa verificar outras colunas
                        }
                    }
                }
                linha.style.display = mostrarLinha ? "" : "none";
            });
        }

        filtroInput.addEventListener('input', filtrarTabela);
        checkboxesFiltro.forEach(cb => cb.addEventListener('change', filtrarTabela));

        // 5. Lógica do "Selecionar Todos"
        if (selecionarTodosCheckbox) {
            selecionarTodosCheckbox.addEventListener('change', function () {
                checkboxesFiltro.forEach(checkbox => checkbox.checked = this.checked);
                filtrarTabela();
            });

            checkboxesFiltro.forEach(checkbox => {
                checkbox.addEventListener('change', function () {
                    selecionarTodosCheckbox.checked = [...checkboxesFiltro].every(cb => cb.checked);
                });
            });
        }
        
        // 6. Lógica do Dropdown de seleção de campos
        const dropdownButton = modal.querySelector('.dropdown-toggle');
        const dropdownMenu = modal.querySelector('.dropdown-menu');
        if(dropdownButton && dropdownMenu) {
            dropdownButton.addEventListener('click', (event) => {
                event.stopPropagation();
                dropdownMenu.classList.toggle('show');
            });
            
            dropdownMenu.addEventListener('click', (event) => event.stopPropagation());
        }
    }
    
    // Fechar dropdowns ao clicar fora
    document.addEventListener("click", function () {
        document.querySelectorAll(".dropdown-menu.show").forEach(menu => {
            menu.classList.remove("show");
        });
    });


    // --- INICIALIZAÇÃO DOS MODAIS ---
    // Cada modal é configurado com seus próprios IDs e classes, evitando conflitos.

    // Configura o Modal de Profissões
    configurarModalDeSelecao(
        'modalListaProfissoes',
        'campoProfissao',
        'userOccupation',
        'textoProfissaoSelecionada',
        'filtroProfissao', // ID único para o input de filtro
        'fecharModalListaProfissao', // ID único para o botão de fechar
        'linha-profissao',
        { 
            'campoCodigoProfissao': 0, // ID único para o checkbox + índice da coluna
            'campoNomeProfissao': 1    // ID único para o checkbox + índice da coluna
        }
    );

    // Configura o Modal de Funções
    configurarModalDeSelecao(
        'modalListaFuncoes',
        'campoFuncao',
        'userFunction',
        'textoFuncaoSelecionada',
        'filtroFuncoes',
        'fecharModalListaFuncao',
        'linha-funcao',
        { 
            'campoCodigoFuncao': 0, 
            'campoNomeFuncao': 1 
        }
    );

    // Configura o Modal de Departamentos
    configurarModalDeSelecao(
        'modalListaDepartamentos',
        'campoDepartamento',
        'userDepartment',
        'textoDepartamentoSelecionada',
        'filtroDepartamentos',
        'fecharModalListaDepartamento',
        'linha-departamento',
        { 
            'campoCodigoDepartamento': 0, 
            'campoNomeDepartamento': 1 
        }
    );
    
    // Carrega o nome da profissão, função e departamento salvos ao carregar a página
    function carregarTextoInicial(inputId, textoId) {
        const valorSalvo = document.getElementById(inputId).value;
        const elementoTexto = document.getElementById(textoId);
        
        if (valorSalvo && elementoTexto.innerText.includes('Selecione')) {
            const nomeSalvo = elementoTexto.getAttribute('data-nome');
            if (nomeSalvo) {
                elementoTexto.innerText = `${valorSalvo} - ${nomeSalvo}`;
            }
        }
    }
    
    carregarTextoInicial('userOccupation', 'textoProfissaoSelecionada');
    carregarTextoInicial('userFunction', 'textoFuncaoSelecionada');
    carregarTextoInicial('userDepartment', 'textoDepartamentoSelecionada');
});


