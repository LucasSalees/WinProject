    document.addEventListener("DOMContentLoaded", function () {
        const selecionarTodos = document.getElementById("selecionarTodos");
        const checkboxes = document.querySelectorAll(".campoCheckbox");

        // Marcar ou desmarcar todos os campos ao clicar no "Selecionar Todos"
        selecionarTodos.addEventListener("change", function () {
            checkboxes.forEach(checkbox => checkbox.checked = this.checked);
        });

        // Se algum for desmarcado, desmarcar o "Selecionar Todos"
        checkboxes.forEach(checkbox => {
            checkbox.addEventListener("change", function () {
                selecionarTodos.checked = [...checkboxes].every(cb => cb.checked);
            });
        });
    });

    // Recarregar a página para remover os filtros
    document.getElementById("btnRemoverFiltros").addEventListener("click", function () {
        location.reload(); // Recarrega a página para limpar os filtros
    });

    document.getElementById("filtro").addEventListener("input", filtrarTabela);

	function filtrarTabela() {
	    const filtro = document.getElementById("filtro").value.toLowerCase();
	    const linhas = document.querySelectorAll("#tabela tbody tr");

	    linhas.forEach(linha => {
	        let mostrarLinha = false;

	        if (!filtro) {
	            mostrarLinha = true;
	        } else {
	            const campos = {
	                nome: linha.querySelector("td:nth-child(1)")?.textContent.toLowerCase(),
	                email: linha.querySelector("td:nth-child(2)")?.textContent.toLowerCase(),
	                tipo: linha.querySelector("td:nth-child(3)")?.textContent.toLowerCase(),
	                funcao: linha.querySelector("td:nth-child(4)")?.textContent.toLowerCase(),
	                bloqueio: linha.querySelector("td:nth-child(5)")?.textContent.toLowerCase()
	            };

	            mostrarLinha = (
	                (document.getElementById("campoNomeUsuario").checked && campos.nome?.includes(filtro)) ||
	                (document.getElementById("campoEmail").checked && campos.email?.includes(filtro)) ||
	                (document.getElementById("campoTipo").checked && campos.tipo?.includes(filtro)) ||
	                (document.getElementById("campoFuncao").checked && campos.funcao?.includes(filtro)) ||
	                (document.getElementById("campoBloqueio").checked && campos.bloqueio?.includes(filtro))
	            );
	        }

	        linha.style.display = mostrarLinha ? "" : "none";
	    });
	}
	
	// Toggle do dropdown
	document.getElementById("dropdownMenuButton").addEventListener("click", function (event) {
	    event.stopPropagation(); // Impede que o clique propague para o document
	    const dropdownMenu = document.getElementById("dropdownCampos");
	    dropdownMenu.classList.toggle("show");
	});

	// Impede que cliques dentro do menu fechem o dropdown
	document.getElementById("dropdownCampos").addEventListener("click", function (event) {
	    event.stopPropagation(); // Essencial para evitar que o clique feche o menu
	});

	// Fechar dropdown ao clicar fora
	document.addEventListener("click", function () {
	    const dropdown = document.getElementById("dropdownCampos");
	    dropdown.classList.remove("show");
	});