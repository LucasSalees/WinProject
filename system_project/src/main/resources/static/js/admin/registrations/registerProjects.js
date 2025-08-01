//Submit do formulario
const form = document.getElementById('formulario');

form.addEventListener('submit', async function(event) {
    event.preventDefault();
    // Envia via fetch (modal)
    const formData = new FormData(form);
    fetch('/input/admin/projects/save', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw response;
        }
        return response.json();
    })
    .then(data => {
        if (data.status === "success") {
            showMessageModal(data.mensagem, true, {
                redirectUrl: "/input/admin/projects/list"
            });
        } else {
            showMessageModal(data.mensagem || "Erro ao cadastrar projeto.", false);
        }
    })
    .catch(error => {
        if (error.json) {
            error.json().then(err => {
                showMessageModal(err.mensagem || "Erro ao cadastrar projeto.", false);
            }).catch(() => {
                showMessageModal("Erro desconhecido ao processar a resposta do servidor.", false);
            });
        } else {
            console.error("Erro na requisição:", error);
            showMessageModal("Erro de conexão ou resposta inválida do servidor.", false);
        }
    });
});

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

document.addEventListener('DOMContentLoaded', function() {
    
});

const textarea = document.getElementById('comentarios');
const contador = document.getElementById('contador-comentarios');

textarea.addEventListener('input', () => {
    const comprimento = textarea.value.length;
    contador.textContent = `${comprimento}/700`;
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

	document.addEventListener("DOMContentLoaded", function () {
	    const budgetInput = document.getElementById("budgetInput");

	    budgetInput.addEventListener("input", function (e) {
	        let value = e.target.value.replace(/\D/g, "");
	        value = (value / 100).toFixed(2) + '';
	        value = value.replace(".", ",");
	        value = value.replace(/\B(?=(\d{3})+(?!\d))/g, ".");
	        e.target.value = 'R$ ' + value;
	    });
	});

	$(document).ready(function() {
	    $('#gerente-select').change(function() {
	        // Obtém o option selecionado
	        var selectedOption = $(this).find('option:selected');
	        
	        // Obtém os valores dos atributos data
	        var email = selectedOption.data('email');
	        var phone = selectedOption.data('phone');
	        
	        // Preenche os campos
	        $('#email-gerente').val(email || '');
	        $('#telefone-gerente').val(phone || '');
	    });
	});
	
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
