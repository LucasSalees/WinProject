document.addEventListener("DOMContentLoaded", function () {

    // =========================================================================
    // LÓGICA PRINCIPAL DO FORMULÁRIO
    // =========================================================================

    // Submit do formulário de edição de projeto
    const form = document.getElementById('formulario');
    if (form) {
        form.addEventListener('submit', async function(event) {
            event.preventDefault();
            const formData = new FormData(form);

            fetch('/input/admin/projects/save', {
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
                        redirectUrl: "/input/admin/projects/list"
                    });
                }
            })
            .catch(error => {
                if (error.json) {
                    error.json().then(err => {
                        showMessageModal(err.mensagem || "Erro ao editar projeto.", false);
                    }).catch(() => {
                        showMessageModal("Erro desconhecido ao processar a resposta do servidor.", false);
                    });
                } else {
                    showMessageModal("Erro de conexão ou resposta inválida do servidor.", false);
                }
            });
        });
    }

    // Aplicação de máscaras nos campos
    $('#cep-projeto').mask('00000-000');
    $('#telefone-gerente').mask('(00) 00000-0000');
    $('#telefone-gestor').mask('(00) 00000-0000');

    // Lógica da API ViaCEP para preenchimento de endereço
    const cepInput = document.getElementById('cep-projeto');
    if (cepInput) {
        cepInput.addEventListener('focusout', async () => {
            const endereco = document.getElementById('endereco-projeto');
            const bairro = document.getElementById('bairro-projeto');
            const estado = document.getElementById('estado-projeto');
            const cidade = document.getElementById('cidade-projeto');
            
            const limparCamposEndereco = () => {
                endereco.value = '';
                bairro.value = '';
                estado.value = '';
                cidade.value = '';
            };

            try {
                const response = await fetch(`https://viacep.com.br/ws/${$('#cep-projeto'  ).val().replace('-', '')}/json/`);
                if (response.ok) {
                    const data = await response.json();
                    if (data.erro) {
                        limparCamposEndereco();
                        return;
                    }
                    endereco.value = data.logradouro;
                    bairro.value = data.bairro;
                    estado.value = data.uf;
                    cidade.value = data.localidade;
                } else {
                    limparCamposEndereco();
                }
            } catch (error) {
                console.error("Erro ao buscar CEP:", error);
                limparCamposEndereco();
            }
        });
    }

    // Contador de caracteres para o campo de comentários
    const textarea = document.getElementById('comentarios');
    if (textarea) {
        const contador = document.getElementById('contador-comentarios');
        const atualizarContador = () => {
            const comprimento = textarea.value.length;
            contador.textContent = `${comprimento}/700`;
        };
        textarea.addEventListener('input', atualizarContador);
        atualizarContador();
    }

    // Limite de 0 a 100 para o campo de percentual
    const percentualInput = document.getElementById('percentualExecucao');
    if (percentualInput) {
        percentualInput.addEventListener('input', function() {
            if (this.value > 100) this.value = 100;
            else if (this.value < 0) this.value = 0;
        });
    }

    // Lógica da barra de progresso e status do projeto
    const statusSelect = document.getElementById("statusProjeto");
    if (statusSelect && percentualInput) {
        const barraProgresso = document.getElementById("barraProgresso");
        const statusParaPercentual = { "Não iniciado": 0, "Em planejamento": 25, "Em execução": 50, "Atrasado": 75, "Concluído": 100 };
        const percentualParaStatus = (p) => {
            if (p >= 0 && p <= 10) return "Não iniciado";
            if (p > 10 && p <= 25) return "Em planejamento";
            if (p > 25 && p <= 74) return "Em execução";
            if (p >= 75 && p < 100) return "Atrasado";
            if (p === 100) return "Concluído";
            return "";
        };
        const atualizarProgresso = (percentual, status) => {
            percentualInput.value = percentual;
            barraProgresso.style.width = percentual + '%';
            barraProgresso.setAttribute('aria-valuenow', percentual);
            barraProgresso.className = 'progress-bar progress-bar-striped progress-bar-animated';
            const corClasses = { 'Não iniciado': 'progress-bar-vermelha', 'Em planejamento': 'progress-bar-amarela', 'Em execução': 'progress-bar-azul', 'Atrasado': 'progress-bar-laranja', 'Concluído': 'progress-bar-verde' };
            if (corClasses[status]) {
                barraProgresso.classList.add(corClasses[status]);
            }
            statusSelect.value = status;
        };
        statusSelect.addEventListener('change', function() {
            const status = statusSelect.value;
            const percentual = statusParaPercentual[status] ?? 0;
            atualizarProgresso(percentual, status);
        });
        percentualInput.addEventListener('input', function() {
            let val = Math.max(0, Math.min(100, parseInt(percentualInput.value) || 0));
            const status = percentualParaStatus(val);
            atualizarProgresso(val, status);
        });
        const percentualInicial = parseInt(percentualInput.value) || 0;
        const statusAuto = percentualParaStatus(percentualInicial);
        atualizarProgresso(percentualInicial, statusAuto || statusSelect.value);
    }

    // Lógica para calcular a duração do projeto
    const startInput = document.querySelector('[name="projectPlanningStartDate"]');
    const endInput = document.querySelector('[name="projectPlanningEndDate"]');
    const durationInput = document.querySelector('[name="projectDuration"]');
    if (startInput && endInput && durationInput) {
        const calcularDuracao = () => {
            const startDate = new Date(startInput.value);
            const endDate = new Date(endInput.value);
            if (!isNaN(startDate) && !isNaN(endDate) && endDate >= startDate) {
                const diffTime = Math.abs(endDate - startDate);
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
                durationInput.value = diffDays + " dias";
            } else {
                durationInput.value = "";
            }
        };
        startInput.addEventListener("change", calcularDuracao);
        endInput.addEventListener("change", calcularDuracao);
        calcularDuracao();
    }

    // =========================================================================
    // LÓGICA DO MODAL DE EXCLUSÃO (ADICIONADO)
    // =========================================================================
    
    // Torna a função acessível globalmente para ser chamada pelo `onclick` no HTML
    window.openExclusaoModal = function(link) {
        const exclusaoModal = document.getElementById('exclusaoModal');
        if (!exclusaoModal) return;
        
        exclusaoModal.classList.add('show');

        document.getElementById('confirmarExclusaoBtn').onclick = function() {
            fetch(link.href)
                .then(response => {
                    if (!response.ok) throw response;
                    return response.json();
                })
                .then(data => {
                    exclusaoModal.classList.remove('show');
                    showMessageModal(data.mensagem, data.status === "success", {
                        redirectUrl: "/input/admin/projects/list"
                    });
                })
                .catch(error => {
                    exclusaoModal.classList.remove('show');
                    if (error.json) {
                        error.json().then(err => {
                            showMessageModal(err.mensagem || "Erro ao excluir item.", false);
                        }).catch(() => {
                            showMessageModal("Erro inesperado ao processar a exclusão.", false);
                        });
                    } else {
                        showMessageModal("Erro de conexão ou resposta inválida.", false);
                    }
                });
        };
    }

    const fecharExclusaoModalBtn = document.getElementById('fecharExclusaoModal');
    const cancelarExclusaoBtn = document.getElementById('cancelarExclusao');
    const exclusaoModal = document.getElementById('exclusaoModal');

    if (fecharExclusaoModalBtn) {
        fecharExclusaoModalBtn.onclick = () => exclusaoModal.classList.remove('show');
    }
    if (cancelarExclusaoBtn) {
        cancelarExclusaoBtn.onclick = () => exclusaoModal.classList.remove('show');
    }


    // =========================================================================
    // LÓGICA DOS MODAIS DE SELEÇÃO (GENÉRICA E REUTILIZÁVEL)
    // =========================================================================

    function configurarModalDeSelecao(config) {
        const { modalId, campoId, inputId, textoId, filtroInputId, fecharBtnId, linhaClasse, camposFiltro, dataAttrNome, onSelectCallback } = config;
        
        const modal = document.getElementById(modalId);
        if (!modal) return;

        const campo = document.getElementById(campoId);
        const inputValor = document.getElementById(inputId);
        const textoSelecionado = document.getElementById(textoId);
        const tabela = modal.querySelector('table');
        const linhas = tabela.querySelectorAll(`.${linhaClasse}`);

        if (campo) {
            campo.addEventListener('click', () => {
                modal.style.display = 'flex';
                const filtroInput = document.getElementById(filtroInputId);
                if (filtroInput) filtroInput.focus();
            });
        }

        const fecharBtn = document.getElementById(fecharBtnId);
        if (fecharBtn) {
            fecharBtn.addEventListener('click', () => modal.style.display = 'none');
        }

        linhas.forEach(row => {
            row.addEventListener('click', () => {
                const id = row.getAttribute('data-id');
                const nome = row.getAttribute(dataAttrNome || 'data-nome');
                
                if (inputValor) inputValor.value = id;
                if (textoSelecionado) textoSelecionado.innerText = `${id} - ${nome}`;
                
                modal.style.display = 'none';

                if (onSelectCallback) {
                    onSelectCallback(row);
                }
            });
        });

        const filtroInput = document.getElementById(filtroInputId);
        const checkboxesFiltro = modal.querySelectorAll('.campoCheckbox');
        const selecionarTodosCheckbox = modal.querySelector('.selecionarTodos');

        const filtrarTabela = () => {
            if (!filtroInput) return;
            const filtro = filtroInput.value.toLowerCase().trim();
            const linhasTabela = tabela.querySelectorAll('tbody tr');
            linhasTabela.forEach(linha => {
                if (!filtro) {
                    linha.style.display = "";
                    return;
                }
                let mostrarLinha = false;
                for (const [checkboxId, colunaIndex] of Object.entries(camposFiltro)) {
                    const checkbox = document.getElementById(checkboxId);
                    if (checkbox && checkbox.checked) {
                        const cell = linha.querySelector(`td:nth-child(${colunaIndex + 1})`);
                        if (cell && cell.textContent.toLowerCase().includes(filtro)) {
                            mostrarLinha = true;
                            break;
                        }
                    }
                }
                linha.style.display = mostrarLinha ? "" : "none";
            });
        };

        if (filtroInput) filtroInput.addEventListener('input', filtrarTabela);
        checkboxesFiltro.forEach(cb => cb.addEventListener('change', filtrarTabela));

        if (selecionarTodosCheckbox) {
            selecionarTodosCheckbox.addEventListener('change', function () {
                checkboxesFiltro.forEach(checkbox => checkbox.checked = this.checked);
                filtrarTabela();
            });
        }
        
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
    
    document.addEventListener("click", function () {
        document.querySelectorAll(".dropdown-menu.show").forEach(menu => {
            menu.classList.remove("show");
        });
    });

    // --- INICIALIZAÇÃO DE TODOS OS MODAIS DA PÁGINA ---

    // 1. Configuração do Modal de Departamentos
    const emailGerenteInput = document.getElementById('email-gerente');
    const telefoneGerenteInput = document.getElementById('telefone-gerente');
    const limparDepartamentoBtn = document.getElementById('limparDepartamento');
    const userDepartmentInput = document.getElementById('userDepartment');
    const textoDepartamento = document.getElementById('textoDepartamentoSelecionada');

    configurarModalDeSelecao({
        modalId: 'modalListaDepartamentos',
        campoId: 'campoDepartamento',
        inputId: 'userDepartment',
        textoId: 'textoDepartamentoSelecionada',
        filtroInputId: 'filtroDepartamentos',
        fecharBtnId: 'fecharModalListaDepartamento',
        linhaClasse: 'linha-departamento',
        camposFiltro: { 
            'campoCodigoDepartamento': 0, 
            'campoNomeDepartamento': 1,
            'campoGerenteDepartamento': 2
        },
        onSelectCallback: (linhaSelecionada) => {
            const email = linhaSelecionada.getAttribute('data-email');
            const telefone = linhaSelecionada.getAttribute('data-telefone');
            emailGerenteInput.value = email || '';
            telefoneGerenteInput.value = telefone || '';
            if (limparDepartamentoBtn) limparDepartamentoBtn.style.display = 'block';
        }
    });

    if (limparDepartamentoBtn) {
        limparDepartamentoBtn.addEventListener('click', () => {
            userDepartmentInput.value = '';
            textoDepartamento.innerText = 'Selecione um departamento';
            emailGerenteInput.value = '';
            telefoneGerenteInput.value = '';
            limparDepartamentoBtn.style.display = 'none';
        });
    }
    
    if (userDepartmentInput && userDepartmentInput.value) {
        if (limparDepartamentoBtn) limparDepartamentoBtn.style.display = 'block';
    }

    // 2. Configuração do Modal de Siglas
    configurarModalDeSelecao({
        modalId: 'modalListaAcronym',
        campoId: 'campoProjectContractualAcronym',
        inputId: 'projectContractualAcronym',
        textoId: 'textoAcronymSelecionada',
        filtroInputId: 'filtroAcronym',
        fecharBtnId: 'fecharModalListaAcronym',
        linhaClasse: 'linha-acronym',
        dataAttrNome: 'data-sigla',
        camposFiltro: { 
            'campoCodigoAcronym': 0, 
            'campoNomeAcronym': 1,
            'campoAcronym': 2
        }
    });
});
