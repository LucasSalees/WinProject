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
    location.reload();
});

// Filtrar tabela
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
                codigo: linha.querySelector("td:nth-child(1)")?.textContent.toLowerCase(),
                nome: linha.querySelector("td:nth-child(2)")?.textContent.toLowerCase(),
                sigla: linha.querySelector("td:nth-child(3)")?.textContent.toLowerCase(),
                vertical: linha.querySelector("td:nth-child(4)")?.textContent.toLowerCase(),
                prioridade: linha.querySelector("td:nth-child(5)")?.textContent.toLowerCase(),
                statusProjeto: linha.querySelector("td:nth-child(6)")?.textContent.toLowerCase(),
                statusExecucao: linha.querySelector("td:nth-child(7)")?.textContent.toLowerCase(),
                planejInicio: linha.querySelector("td:nth-child(8)")?.textContent.toLowerCase(),
                planejFim: linha.querySelector("td:nth-child(9)")?.textContent.toLowerCase()
            };

            mostrarLinha = (
                (document.getElementById("campoCodigo").checked && campos.codigo?.includes(filtro)) ||
                (document.getElementById("campoNomeProjeto").checked && campos.nome?.includes(filtro)) ||
                (document.getElementById("campoSiglaContratual").checked && campos.sigla?.includes(filtro)) ||
                (document.getElementById("campoVerticalNegocio").checked && campos.vertical?.includes(filtro)) ||
                (document.getElementById("campoPrioridade").checked && campos.prioridade?.includes(filtro)) ||
                (document.getElementById("campoStatsProjeto").checked && campos.statusProjeto?.includes(filtro)) ||
                (document.getElementById("campoStatsExecucao").checked && campos.statusExecucao?.includes(filtro)) ||
                (document.getElementById("campoPlanejInicio").checked && campos.planejInicio?.includes(filtro)) ||
                (document.getElementById("campoPlanejFim").checked && campos.planejFim?.includes(filtro))
            );
        }

        linha.style.display = mostrarLinha ? "" : "none";
    });
}

// Toggle do dropdown
document.getElementById("dropdownMenuButton").addEventListener("click", function (event) {
    event.stopPropagation();
    const dropdownMenu = document.getElementById("dropdownCampos");
    dropdownMenu.classList.toggle("show");
});

document.getElementById("dropdownCampos").addEventListener("click", function (event) {
    event.stopPropagation();
});

document.addEventListener("click", function () {
    const dropdown = document.getElementById("dropdownCampos");
    dropdown.classList.remove("show");
});
