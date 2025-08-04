//Submit do formulario
const form = document.getElementById('formulario');

form.addEventListener('submit', async function(event) {
    event.preventDefault();
    
    // Envia via fetch (modal)
    const formData = new FormData(form);
    fetch('/input/director/projects/edit', {
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
	            redirectUrl: "/input/director/projects/list"
	        });
	    }
	})
	.catch(error => {
		if (error.json) { // Verifica se a resposta tem um corpo JSON
	    error.json().then(err => {
	        showMessageModal(err.mensagem || "Erro ao editar função.", false);
	    }).catch(() => {
            // Se não conseguir parsear o JSON do erro
            showMessageModal("Erro desconhecido ao processar a resposta do servidor.", false);
            });
	    } else {
            // Erro de rede ou outro erro que não seja uma resposta HTTP
            showMessageModal("Erro de conexão ou resposta inválida do servidor.", false);
        }
	});
});

function openExclusaoModal(link, redirectUrl = null) {
    const exclusaoModal = document.getElementById('exclusaoModal');
    exclusaoModal.classList.add('show');

    document.getElementById('confirmarExclusaoBtn').onclick = function() {
        fetch(link.href)
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
                    redirectUrl: "/input/director/projects/list"
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

//adiciona as mascaras
$('#cep-projeto').mask('00000-000');
$('#telefone-gerente').mask('(00) 00000-0000');
$('#telefone-gestor').mask('(00) 00000-0000');

//Função para completar o endereço automaticamente pegandos os dados da api dos correios "viacep"
const cepInput = document.getElementById('cep-projeto');
const endereco = document.getElementById('endereco-projeto');
const bairro = document.getElementById('bairro-projeto');
const estado = document.getElementById('estado-projeto');
const cidade = document.getElementById('cidade-projeto');

cepInput.addEventListener('focusout', async () => {

	const response = await fetch(`https://viacep.com.br/ws/${$('#cep-projeto').val().replace('-', '')}/json/`);

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
});

function limparCamposEndereco() {
	endereco.value = '';
	bairro.value = '';
	estado.value = '';
	cidade.value = '';
}

const textarea = document.getElementById('comentarios');
const contador = document.getElementById('contador-comentarios');

textarea.addEventListener('input', () => {
    const comprimento = textarea.value.length;
    contador.textContent = `${comprimento}/700`;
});

function atualizarContador() {
    const textarea = document.getElementById('comentarios');
    const contador = document.getElementById('contador-comentarios');
    const comprimento = textarea.value.length;
    contador.textContent = `${comprimento}/700`;
}

// Chame a função quando a página carregar
document.addEventListener('DOMContentLoaded', function() {
    // Atualiza o contador imediatamente
    atualizarContador();
    
    // Mantém o listener para atualizar enquanto digita
    document.getElementById('comentarios').addEventListener('input', atualizarContador);
	
	emailGerenteInput.value = emailGerenteInput.value.trim().split('@')[0];
	emailGestorInput.value = emailGestorInput.value.trim().split('@')[0];
	document.getElementById('formulario').style.display = "block";
});

const input = document.getElementById('percentualExecucao');
input.addEventListener('input', function () {
    if (this.value > 100) {
        this.value = 100;
    } else if (this.value < 0) {
        this.value = 0;
    }
});

document.addEventListener("DOMContentLoaded", function () {
    const statusSelect = document.getElementById("statusProjeto");
    const percentualInput = document.getElementById("percentualExecucao");
    const barraProgresso = document.getElementById("barraProgresso");

    const statusParaPercentual = {
        "Não iniciado": 0,
        "Em planejamento": 25,
        "Em execução": 50,
        "Atrasado": 75,
        "Concluído": 100
    };

    const percentualParaStatus = (percentual) => {
		if (percentual >= 0 && percentual <= 10) return "Não iniciado";
        if (percentual > 10 && percentual <= 25) return "Em planejamento";
        if (percentual > 25 && percentual <= 75) return "Em execução";
        if (percentual > 75 && percentual < 100) return "Atrasado";
        if (percentual === 100) return "Concluído";
        return ""; // fallback
    };

    function atualizarProgresso(percentual, status) {
        percentualInput.value = percentual;
        barraProgresso.style.width = percentual + '%';
        barraProgresso.setAttribute('aria-valuenow', percentual);

        // Remove classes antigas
        barraProgresso.classList.remove(
            'progress-bar-vermelha',
            'progress-bar-amarela',
            'progress-bar-verde',
            'progress-bar-laranja',
            'progress-bar-azul'
        );

        // Aplica a classe de cor conforme o status
        switch (status) {
            case 'Não iniciado':
                barraProgresso.classList.add('progress-bar-vermelha');
                break;
            case 'Em planejamento':
                barraProgresso.classList.add('progress-bar-amarela');
                break;
            case 'Em execução':
                barraProgresso.classList.add('progress-bar-azul');
                break;
            case 'Atrasado':
                barraProgresso.classList.add('progress-bar-laranja');
                break;
            case 'Concluído':
                barraProgresso.classList.add('progress-bar-verde');
                break;
        }

        // Atualiza o status dropdown
        statusSelect.value = status;
    }

    // Quando mudar o STATUS, atualiza o percentual automaticamente
    statusSelect.addEventListener('change', function () {
        const statusSelecionado = statusSelect.value;
        const percentual = statusParaPercentual[statusSelecionado] ?? 0;
        atualizarProgresso(percentual, statusSelecionado);
    });

    // Quando o usuário digitar manualmente o percentual, atualiza status e barra
    percentualInput.addEventListener('input', function () {
        let val = Math.max(0, Math.min(100, parseInt(percentualInput.value) || 0));
        const status = percentualParaStatus(val);
        atualizarProgresso(val, status);
    });

    // Se já estiver preenchido ao carregar (ex: edição), atualiza tudo
    const statusInicial = statusSelect.value;
    const percentualInicial = parseInt(percentualInput.value) || 0;
    const statusAuto = percentualParaStatus(percentualInicial);
    atualizarProgresso(percentualInicial, statusAuto || statusInicial);
});

$(document).ready(function () {
    const departamentoId = $('#userDepartment').val();

    if (departamentoId) {
        // Procura o departamento na lista já renderizada (ex: em um dropdown oculto)
        const option = $('#gerente-select').find('option[value="' + departamentoId + '"]');

        if (option.length) {
            const nome = option.text();
            const email = option.data('email');
            const phone = option.data('phone');

            $('#textoDepartamentoSelecionada').text(nome);
            $('#email-gerente').val(email || '');
            $('#telefone-gerente').val(phone || '');
        }
    }
});

document.addEventListener("DOMContentLoaded", function () {
    const startInput = document.querySelector('[name="projectPlanningStartDate"]');
    const endInput = document.querySelector('[name="projectPlanningEndDate"]');
    const durationInput = document.querySelector('[name="projectDuration"]');

    function calcularDuracao() {
        const startDate = new Date(startInput.value);
        const endDate = new Date(endInput.value);

        if (!isNaN(startDate) && !isNaN(endDate) && endDate >= startDate) {
            const diffTime = Math.abs(endDate - startDate);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; 
            durationInput.value = diffDays + " dias";
        } else {
            durationInput.value = "";
        }
    }

    startInput.addEventListener("change", calcularDuracao);
    endInput.addEventListener("change", calcularDuracao);
});

function imprimirFormulario() {
    try {
        const formatarDataHoraBR = (dataString) => {
            if (!dataString || dataString === 'Não informado') return 'Não informado';
            
            try {
                const data = new Date(dataString);
                if (isNaN(data.getTime())) return 'Data inválida';
                
                const dia = String(data.getDate()).padStart(2, '0');
                const mes = String(data.getMonth() + 1).padStart(2, '0');
                const ano = data.getFullYear();
                
                return `${dia}/${mes}/${ano}`;
            } catch (e) {
                return dataString; // Retorna o valor original se não conseguir formatar
            }
        };

        const formatarMoeda = (valor) => {
            if (!valor || valor === 'Não informado') return 'Não informado';
            return parseFloat(valor).toLocaleString('pt-BR', {style: 'currency', currency: 'BRL'});
        };

        const getValue = (id, isDateTime = false, isCurrency = false) => {
            const el = document.getElementById(id);
            if (!el) {
                // Tenta encontrar pelo name ou outro atributo se não encontrar por ID
                const elByName = document.querySelector(`[name="${id}"]`);
                if (!elByName) return 'Não informado';
                const value = elByName.value || 'Não informado';
                return isDateTime ? formatarDataHoraBR(value) : (isCurrency ? formatarMoeda(value) : value);
            }
            
            const value = el.value || 'Não informado';
            return isDateTime ? formatarDataHoraBR(value) : (isCurrency ? formatarMoeda(value) : value);
        };

        const getSelectedText = (id) => {
            const el = document.getElementById(id);
            if (!el) return 'Não informado';
            return el.options[el.selectedIndex]?.text || 'Não informado';
        };

        const dados = {
            // Informações Básicas
            codigo: getValue('projectId'),
            nome: getValue('projectName'),
            siglaContratual: getValue('projectContractualAcronym'),
            dataCadastro: getValue('projectRegisterDate', true),
            dataAtual: getValue('ProjectCurrentDate', true),
            site: getValue('projectWebsite'),
            verticalNegocio: getSelectedText('projectBusinessVertical'),
            prioridade: getSelectedText('projectPriority'),

            // Cronograma
            dataIniPlan: getValue('projectPlanningStartDate', true),
            dataFimPlan: getValue('projectPlanningEndDate', true),
            duracao: getValue('projectDuration'),
            orcamento: getValue('projectBudget', false, true),

            // Gerente Local
            gerenteNome: getSelectedText('gerente-select'),
            gerenteEmail: getValue('email-gerente'),
            gerenteFone: getValue('telefone-gerente'),

            // Gestor do Cliente
            gestorNome: getValue('projectClientManager'),
            gestorEmail: getValue('projectClientManagerEmail'),
            gestorFone: getValue('projectClientManagerPhone'),

            // Endereço do Cliente
            cep: getValue('projectClientZipCode'),
            endereco: getValue('projectClientAddress'),
            numero: getValue('projectClientAddressNumber'),
            complemento: getValue('projectClientAddressComplement'),
            bairro: getValue('projectClientDistrict'),
            cidade: getValue('projectClientCity'),
            estado: getSelectedText('projectClientState'),

            // Status e Progresso
            status: getSelectedText('statusProjeto'),
            percentual: getValue('percentualExecucao'),

            // Observações
            comentario: getValue('projectComment')
        };

        const baseUrl = window.location.origin;
        const janelaImpressao = window.open('', '_blank', 'width=800,height=600');

        janelaImpressao.document.write(`<!DOCTYPE html>
			<html>
	        <head>
	            <title>WinChurch - Relatório de Projeto</title>
				<style>
				    body {
				        font-family: 'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif;
				        margin: 0;
				        padding: 0;
				        background: #fff;
				        color: #000;
				        line-height: 1.6;
				    }

				    .header {
				        display: flex;
				        align-items: center;
				        justify-content: space-between;
				        padding: 20px 40px;
				        border-bottom: 1.5px solid #ccc;
				        background-color: #f4f4f4;
				    }

				    .header-img img {
				        width: 30px;
				        height: auto;
				        filter: grayscale(100%) contrast(110%);
				    }

				    .header-title {
				        font-size: 20px;
				        font-weight: bold;
				        text-align: center;
				        text-transform: uppercase;
				        flex-grow: 1;
				        color: #000;
				        letter-spacing: 0.6px;
				        position: relative;
				    }

				    .header-title::after {
				        content: "";
				        display: block;
				        width: 50px;
				        height: 2px;
				        background: #000;
				        margin: 6px auto 0;
				    }

				    .report-info {
				        display: flex;
				        justify-content: space-between;
				        padding: 0 40px;
				        margin-top: 10px;
				        font-size: 12px;
				        color: #444;
				    }

				    .user-data {
				        padding: 30px 40px;
				        font-size: 14px;
				    }

				    .user-data h4 {
				        margin-top: 25px;
				        font-size: 15px;
				        border-bottom: 1px solid #ccc;
				        padding-bottom: 5px;
				    }

				    .user-data p {
				        margin: 6px 0;
				    }

				    .footer {
				        text-align: center;
				        font-size: 11px;
				        color: #777;
				        margin-top: 40px;
				        padding: 10px;
				        border-top: 1px solid #ccc;
				    }

				    .footer-text {
				        position: relative;
				        background-color: #fff;
				        display: inline-block;
				        padding: 0 15px;
				        top: -8px;
				    }

				    @media print {
				        @page {
				            size: A4 portrait;
				            margin: 15mm 10mm;
				        }

				        .footer {
				            position: fixed;
				            bottom: 0;
				            left: 0;
				            right: 0;
				            padding: 6px 0;
				            font-size: 10px;
				            color: #999;
				        }

				        .footer:after {
				            content: "Página " counter(page);
				            display: inline-block;
				            margin-left: 10px;
				            color: #bbb;
				        }

				        .report-info {
				            font-size: 10px;
				            padding: 0 30px;
				        }
				    }
				</style>
        </head>
		<body>
				<div class="header">
	                <div class="header-img">
	                    <img src="${baseUrl}/images/input.ico" alt="Logo">
	                </div>
	                <h2 class="header-title">Relatório de Prjeto</h2>
	            </div>

	            <div class="report-info">
	                <div>Emitido em: ${new Date().toLocaleDateString('pt-BR')}</div>
	                <div>WinProject - Sistema de Gestão de Projetos</div>
	            </div>
	
			    <div class="section">
			        <h4>Informações Básicas</h4>
			        <div class="section-grid">
			            <p><strong>Código:</strong> ${dados.codigo}</p>
			            <p><strong>Nome:</strong> ${dados.nome}</p>
			            <p><strong>Sigla Contratual:</strong> ${dados.siglaContratual}</p>
			            <p><strong>Vertical de Negócio:</strong> ${dados.verticalNegocio}</p>
			            <p><strong>Data de Cadastro:</strong> ${dados.dataCadastro}</p>
			            <p><strong>Data Atual:</strong> ${dados.dataAtual}</p>
			            <p><strong>Prioridade:</strong> ${dados.prioridade}</p>
			            <p><strong>Site:</strong> ${dados.site}</p>
			        </div>
			    </div>
	
			    <div class="section">
			        <h4>Cronograma</h4>
			        <div class="section-grid">
			            <p><strong>Início do Planejamento:</strong> ${dados.dataIniPlan}</p>
			            <p><strong>Fim do Planejamento:</strong> ${dados.dataFimPlan}</p>
			            <p><strong>Duração:</strong> ${dados.duracao}</p>
			            <p><strong>Orçamento:</strong> ${dados.orcamento}</p>
			        </div>
			    </div>
	
			    <div class="section">
			        <h4>Gerente Local</h4>
			        <div class="section-grid">
			            <p><strong>Nome:</strong> ${dados.gerenteNome}</p>
			            <p><strong>Email:</strong> ${dados.gerenteEmail}</p>
			            <p><strong>Telefone:</strong> ${dados.gerenteFone}</p>
			        </div>
			    </div>
	
			    <div class="section">
			        <h4>Gestor do Cliente</h4>
			        <div class="section-grid">
			            <p><strong>Nome:</strong> ${dados.gestorNome}</p>
			            <p><strong>Email:</strong> ${dados.gestorEmail}</p>
			            <p><strong>Telefone:</strong> ${dados.gestorFone}</p>
			        </div>
			    </div>
	
			    <div class="section">
			        <h4>Localização do Cliente</h4>
			        <div class="section-grid">
			            <p><strong>CEP:</strong> ${dados.cep}</p>
			            <p><strong>Endereço:</strong> ${dados.endereco}, Nº ${dados.numero}</p>
			            <p><strong>Complemento:</strong> ${dados.complemento}</p>
			            <p><strong>Bairro:</strong> ${dados.bairro}</p>
			            <p><strong>Cidade:</strong> ${dados.cidade}</p>
			            <p><strong>Estado:</strong> ${dados.estado}</p>
			        </div>
			    </div>
	
			    <div class="section">
			        <h4>Status e Progresso</h4>
			        <div class="section-grid">
			            <p><strong>Status:</strong> ${dados.status}</p>
			            <p><strong>Percentual de Execução:</strong> ${dados.percentual}%</p>
			        </div>
			    </div>
	
			    <div class="section">
			        <h4>Observações</h4>
			        <p>${dados.comentario}</p>
			    </div>
	
			    <div class="footer">
			        © ${new Date().getFullYear()} WinProject - Todos os direitos reservados.
			    </div>
		</body>

        </html>`);

        janelaImpressao.document.close();
        setTimeout(() => {
            janelaImpressao.print();
            janelaImpressao.close();
        }, 500);

    } catch (error) {
        console.error('Erro ao imprimir:', error);
        alert('Erro ao gerar a impressão.');
    }
}




document.addEventListener('DOMContentLoaded', () => {
	  document.querySelectorAll('.linha-departamento').forEach(row => {
	    row.addEventListener('click', () => {
	      const id = row.getAttribute('data-id');
	      const nome = row.getAttribute('data-nome');
	      const gerente = row.getAttribute('data-gerente');
	      const email = row.getAttribute('data-email');
	      const telefone = row.getAttribute('data-telefone');

	      document.getElementById('userDepartment').value = id;
	      document.getElementById('textoDepartamentoSelecionada').innerText = `${id} - ${nome} - ${gerente}`;
	      document.getElementById('email-gerente').value = email || '';
	      document.getElementById('telefone-gerente').value = telefone || '';

	      document.getElementById('modalListaDepartamentos').style.display = 'none';
	    });
	  });

	  // Abrir modal ao clicar no campo
	  document.getElementById('campoDepartamento').addEventListener('click', () => {
	    document.getElementById('modalListaDepartamentos').style.display = 'flex';
	  });

	  // Fechar modal
	  document.getElementById('fecharModalListaDepartamento').addEventListener('click', () => {
	    document.getElementById('modalListaDepartamentos').style.display = 'none';
	  });

	  // Controle dos checkboxes de colunas visíveis e filtro da tabela
	  const selecionarTodos = document.getElementById("selecionarTodosDepartamentos");
	  const checkboxes = document.querySelectorAll(".campoCheckbox");

	  selecionarTodos.addEventListener("change", function () {
	    checkboxes.forEach(checkbox => checkbox.checked = this.checked);
	    filtrarTabela();
	  });

	  checkboxes.forEach(checkbox => {
	    checkbox.addEventListener("change", function () {
	      selecionarTodos.checked = [...checkboxes].every(cb => cb.checked);
	      filtrarTabela();
	    });
	  });

	  document.getElementById("filtroDepartamentos").addEventListener("input", filtrarTabela);

	  function filtrarTabela() {
	    const filtro = document.getElementById("filtroDepartamentos").value.toLowerCase().trim();
	    const linhas = document.querySelectorAll("#tabelaDepartamentos tbody tr");

	    linhas.forEach(linha => {
	      let mostrarLinha = false;

	      const campos = {
	        codigo: linha.querySelector("td:nth-child(1)")?.textContent.toLowerCase(),
	        nome: linha.querySelector("td:nth-child(2)")?.textContent.toLowerCase(),
	        gerente: linha.querySelector("td:nth-child(3)")?.textContent.toLowerCase(),
	      };

	      if (!filtro) {
	        mostrarLinha = true;
	      } else {
	        mostrarLinha = (
	          (document.getElementById("campoCodigoDepartamento").checked && campos.codigo.includes(filtro)) ||
	          (document.getElementById("campoNomeDepartamento").checked && campos.nome.includes(filtro)) ||
	          (document.getElementById("campoGerenteDepartamento").checked && campos.gerente.includes(filtro))
	        );
	      }

	      linha.style.display = mostrarLinha ? "" : "none";
	    });
	  }

	  // Dropdown toggle para seleção de campos
	  document.getElementById("dropdownMenuButtonDepartamentos").addEventListener("click", function (event) {
	    event.stopPropagation();
	    const dropdownMenu = document.getElementById("dropdownCamposDepartamentos");
	    dropdownMenu.classList.toggle("show");
	  });

	  document.getElementById("dropdownCamposDepartamentos").addEventListener("click", function (event) {
	    event.stopPropagation();
	  });

	  document.addEventListener("click", function () {
	    const dropdown = document.getElementById("dropdownCamposDepartamentos");
	    dropdown.classList.remove("show");
	  });
	});

