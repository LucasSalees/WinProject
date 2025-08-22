//Submit do formulario
const form = document.getElementById('formulario');
const senhaInput = document.getElementById('senha');
let score = 0;

form.addEventListener('submit', async function(event) {
    event.preventDefault();

    const emailValido = await validarEmail(document.getElementById('email-usuario'));
    if (!emailValido) {
        document.getElementById('email-usuario').focus();
        return;
    }

    if (senhaInput.value !== "") {
        calcularScore(senhaInput.value);
        if (score <= 3) {
            senhaInput.focus();
            senhaInput.setCustomValidity('A senha precisa ser forte ou muito forte');
            return;
        }
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

    // Now send with fetch
    fetch('/input/admin/users/save', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) throw response;
        return response.json();
    })
    .then(data => {
        if (data.status === "success") {
            showMessageModal(data.mensagem, true, {
                redirectUrl: "/input/admin/users/list"
            });
        }
    })
    .catch(error => {
        error.json().then(err => {
            showMessageModal(err.mensagem || "Erro ao cadastrar usuário.", false);
        });
    });
});

//adiciona as mascaras
$('#UserDepartmentManager').mask('(00) 00000-0000');

// Função para abrir modal de confirmação de exclusão de foto
function openExclusaoFotoModal() {
    const exclusaoModal = document.getElementById('exclusaoModal');
    exclusaoModal.classList.add('show');

    document.getElementById('confirmarExclusaoBtn').onclick = function() {
        exclusaoModal.classList.remove('show');
        realizarRemocaoFoto();
    };
}

// Função que executa a remoção da foto após confirmação
function realizarRemocaoFoto() {
    const inputFile = document.getElementById('profileImage');
    const preview = document.getElementById('photoPreview');
    const btnRemove = document.getElementById('btnRemovePhoto');
    
    // Cria um FormData para enviar a requisição
    const formData = new FormData();
    formData.append('removePhoto', 'true');
    
    fetch('/removePhotoCadastro', {
        method: 'DELETE',
        body: formData
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
        inputFile.value = '';
        preview.src = '/images/DefaultAvatar.png';
        btnRemove.style.display = 'none';
        showMessageModal(data.mensagem, data.status === "success");
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

// Função para pré-visualizar a foto
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

// ----------- Eventos dos Modais ----------- //
// Modal de Confirmação (Exclusão)
document.getElementById('fecharExclusaoModal').onclick = () => {
    document.getElementById('exclusaoModal').classList.remove('show');
};

document.getElementById('cancelarExclusao').onclick = () => {
    document.getElementById('exclusaoModal').classList.remove('show');
};


function verificacaoDeSenha(event) {
    // Recálcula o score para garantir que está atualizado
    calcularScore(senhaInput.value);

    if (score <= 3) {
        event.preventDefault();
        senhaInput.focus();
        senhaInput.setCustomValidity('A senha precisa ser forte ou muito forte');
    } else {
        senhaInput.setCustomValidity('');
    }
}

// Função para calcular o score
function calcularScore(senha) {
    score = 0;

    //Requisitos
    if (senha.length >= 8) score++;
    if (senha.match(/[a-z]/)) score++;
    if (senha.match(/[A-Z]/)) score++;
    if (senha.match(/[0-9]/)) score++;
    if (senha.match(/[^a-zA-Z0-9]/)) score++;

    return score;
}

//Função para verificação de força de senha digitada
const indicadorDeForca = document.getElementById('forca-text');
const forcas = {
    1: "Muito fraca",
    2: "Fraca",
    3: "Moderada",
    4: "Forte",
    5: "Muito forte"
};

const classes = {
    1: "text-danger",   // vermelho
    2: "text-warning",  // amarelo
    3: "text-info",     // azul claro
    4: "text-primary",  // azul escuro
    5: "text-success"   // verde
};

senhaInput.addEventListener('input', function() {
    const senha = this.value;
    calcularScore(senha);

    if (senha.length > 0) {
        indicadorDeForca.textContent = forcas[score];
        indicadorDeForca.className = classes[score];
    } else {
        indicadorDeForca.innerHTML = 'Muito fraca';
        indicadorDeForca.className = 'text-danger';
    }

    if (score > 3) {
        senhaInput.setCustomValidity('');
    } else {
        senhaInput.setCustomValidity('A senha precisa ser forte ou muito forte');
    }
});

const emailInput = document.getElementById('email-usuario');

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




// Inicializar preview do email ao carregar a página
document.addEventListener('DOMContentLoaded', function () {
    atualizarPreviewEmail();
    document.getElementById('formulario').style.display = "block";
});


async function verificarEmailExistente(email) {
    const usuarioId = document.querySelector('input[name="userId"]')?.value || '';

    try {
        const response = await fetch('/verify-email', {  // Atualizado para novo endpoint
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
    await validarEmail(this);
});

// Função de validação reutilizável
async function validarEmail(emailInput) {
    const emailCompleto = emailInput.value.trim();
    const feedbackElement = document.getElementById('emailFeedback');
    const successElement = document.getElementById('emailSuccess');

    if (emailCompleto === '') {
        emailInput.classList.remove('is-valid', 'is-invalid');
        feedbackElement.style.display = 'none';
        successElement.style.display = 'none';
        emailInput.setCustomValidity('');
        return true;
    }

    const resultado = await verificarEmailExistente(emailCompleto);

    if (resultado.existe) {
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