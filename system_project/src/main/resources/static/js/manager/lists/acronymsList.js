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
