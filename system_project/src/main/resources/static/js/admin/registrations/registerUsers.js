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

    // Create FormData directly from form — includes files automatically
    const formData = new FormData(form);

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

emailInput.addEventListener('input', function() {
    atualizarPreviewEmail();
    validarEmail(this);  // se quiser validar ao digitar
});


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

document.addEventListener('DOMContentLoaded', () => {
   document.querySelectorAll('.linha-profissao').forEach(row => {
       row.addEventListener('click', () => {
           const id = row.getAttribute('data-id');
           const nome = row.getAttribute('data-nome');
           document.getElementById('userOccupation').value = id;
           document.getElementById('textoProfissaoSelecionada').innerText = `${id} - ${nome}`;
           document.getElementById('modalListaProfissoes').style.display = 'none';
       });
   });

   document.getElementById('campoProfissao').addEventListener('click', () => {
       document.getElementById('modalListaProfissoes').style.display = 'flex';
   });

   document.getElementById('fecharModalLista').addEventListener('click', () => {
       document.getElementById('modalListaProfissoes').style.display = 'none';
   });
});
   
document.addEventListener("DOMContentLoaded", function () {
   const selecionarTodos = document.getElementById("selecionarTodos");
   const checkboxes = document.querySelectorAll(".campoCheckbox");

   // Marcar ou desmarcar todos os campos ao clicar no "Selecionar Todos"
   selecionarTodos.addEventListener("change", function () {
       checkboxes.forEach(checkbox => checkbox.checked = this.checked);
       filtrarTabela(); // refiltra após mudança
   });

   // Se algum for desmarcado, desmarcar o "Selecionar Todos"
   checkboxes.forEach(checkbox => {
       checkbox.addEventListener("change", function () {
           selecionarTodos.checked = [...checkboxes].every(cb => cb.checked);
           filtrarTabela(); // refiltra após mudança
       });
   });

   // Ativar filtro ao digitar
   document.getElementById("filtro").addEventListener("input", filtrarTabela);

   function filtrarTabela() {
       const filtro = document.getElementById("filtro").value.toLowerCase().trim();
       const linhas = document.querySelectorAll("#tabela tbody tr");

       linhas.forEach(linha => {
           let mostrarLinha = false;

           // Obter os campos da linha
           const campos = {
               codigo: linha.querySelector("td:nth-child(1)")?.textContent.toLowerCase(),
               nome: linha.querySelector("td:nth-child(2)")?.textContent.toLowerCase(),
           };

           // ✅ Se o campo de busca estiver vazio, mostrar tudo
           if (!filtro) {
               mostrarLinha = true;
           } else {
               // Verifica se o texto aparece nos campos selecionados
               mostrarLinha = (
                   (document.getElementById("campoCodigo").checked && campos.codigo?.includes(filtro)) ||
                   (document.getElementById("campoNome").checked && campos.nome?.includes(filtro))
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
       event.stopPropagation();
   });

   // Fechar dropdown ao clicar fora
   document.addEventListener("click", function () {
       const dropdown = document.getElementById("dropdownCampos");
       dropdown.classList.remove("show");
   });
});

document.addEventListener('DOMContentLoaded', () => {
   document.querySelectorAll('.linha-funcao').forEach(row => {
       row.addEventListener('click', () => {
           const id = row.getAttribute('data-id');
           const nome = row.getAttribute('data-nome');
           document.getElementById('userFunction').value = id;
           document.getElementById('textoFuncaoSelecionada').innerText = `${id} - ${nome}`;
           document.getElementById('modalListaFuncoes').style.display = 'none';
       });
   });

   document.getElementById('campoFuncao').addEventListener('click', () => {
       document.getElementById('modalListaFuncoes').style.display = 'flex';
   });

   document.getElementById('fecharModalListaFuncao').addEventListener('click', () => {
       document.getElementById('modalListaFuncoes').style.display = 'none';
   });
});
   
document.addEventListener("DOMContentLoaded", function () {
   const selecionarTodos = document.getElementById("selecionarTodosFuncoes");
   const checkboxes = document.querySelectorAll(".campoCheckbox");

   // Marcar ou desmarcar todos os campos ao clicar no "Selecionar Todos"
   selecionarTodos.addEventListener("change", function () {
       checkboxes.forEach(checkbox => checkbox.checked = this.checked);
       filtrarTabela(); // refiltra após mudança
   });

   // Se algum for desmarcado, desmarcar o "Selecionar Todos"
   checkboxes.forEach(checkbox => {
       checkbox.addEventListener("change", function () {
           selecionarTodos.checked = [...checkboxes].every(cb => cb.checked);
           filtrarTabela(); // refiltra após mudança
       });
   });

   // Ativar filtro ao digitar
   document.getElementById("filtroFuncoes").addEventListener("input", filtrarTabela);

   function filtrarTabela() {
       const filtro = document.getElementById("filtroFuncoes").value.toLowerCase().trim();
       const linhas = document.querySelectorAll("#tabela tbody tr");

       linhas.forEach(linha => {
           let mostrarLinha = false;

           // Obter os campos da linha
           const campos = {
               codigo: linha.querySelector("td:nth-child(1)")?.textContent.toLowerCase(),
               nome: linha.querySelector("td:nth-child(2)")?.textContent.toLowerCase(),
           };

           // ✅ Se o campo de busca estiver vazio, mostrar tudo
           if (!filtro) {
               mostrarLinha = true;
           } else {
               // Verifica se o texto aparece nos campos selecionados
               mostrarLinha = (
                   (document.getElementById("campoCodigoFuncao").checked && campos.codigo?.includes(filtro)) ||
                   (document.getElementById("campoNomeFuncao").checked && campos.nome?.includes(filtro))
               );
           }

           linha.style.display = mostrarLinha ? "" : "none";
       });
   }

   // Toggle do dropdown
   document.getElementById("dropdownMenuButtonFuncoes").addEventListener("click", function (event) {
       event.stopPropagation(); // Impede que o clique propague para o document
       const dropdownMenu = document.getElementById("dropdownCamposFuncoes");
       dropdownMenu.classList.toggle("show");
   });

   // Impede que cliques dentro do menu fechem o dropdown
   document.getElementById("dropdownCamposFuncoes").addEventListener("click", function (event) {
       event.stopPropagation();
   });

   // Fechar dropdown ao clicar fora
   document.addEventListener("click", function () {
       const dropdown = document.getElementById("dropdownCamposFuncoes");
       dropdown.classList.remove("show");
   });
});



document.addEventListener('DOMContentLoaded', () => {
   document.querySelectorAll('.linha-departamento').forEach(row => {
       row.addEventListener('click', () => {
           const id = row.getAttribute('data-id');
           const nome = row.getAttribute('data-nome');
           document.getElementById('userDepartment').value = id;
           document.getElementById('textoDepartamentoSelecionada').innerText = `${id} - ${nome}`;
           document.getElementById('modalListaDepartamentos').style.display = 'none';
       });
   });

   document.getElementById('campoDepartamento').addEventListener('click', () => {
       document.getElementById('modalListaDepartamentos').style.display = 'flex';
   });

   document.getElementById('fecharModalListaDepartamento').addEventListener('click', () => {
       document.getElementById('modalListaDepartamentos').style.display = 'none';
   });
});
   
document.addEventListener("DOMContentLoaded", function () {
   const selecionarTodos = document.getElementById("selecionarTodosDepartamentos");
   const checkboxes = document.querySelectorAll(".campoCheckbox");

   // Marcar ou desmarcar todos os campos ao clicar no "Selecionar Todos"
   selecionarTodos.addEventListener("change", function () {
       checkboxes.forEach(checkbox => checkbox.checked = this.checked);
       filtrarTabela(); // refiltra após mudança
   });

   // Se algum for desmarcado, desmarcar o "Selecionar Todos"
   checkboxes.forEach(checkbox => {
       checkbox.addEventListener("change", function () {
           selecionarTodos.checked = [...checkboxes].every(cb => cb.checked);
           filtrarTabela(); // refiltra após mudança
       });
   });

   // Ativar filtro ao digitar
   document.getElementById("filtroDepartamentos").addEventListener("input", filtrarTabela);

   function filtrarTabela() {
       const filtro = document.getElementById("filtroDepartamentos").value.toLowerCase().trim();
       const linhas = document.querySelectorAll("#tabela tbody tr");

       linhas.forEach(linha => {
           let mostrarLinha = false;

           // Obter os campos da linha
           const campos = {
               codigo: linha.querySelector("td:nth-child(1)")?.textContent.toLowerCase(),
               nome: linha.querySelector("td:nth-child(2)")?.textContent.toLowerCase(),
           };

           // ✅ Se o campo de busca estiver vazio, mostrar tudo
           if (!filtro) {
               mostrarLinha = true;
           } else {
               // Verifica se o texto aparece nos campos selecionados
               mostrarLinha = (
                   (document.getElementById("campoCodigoDepartamento").checked && campos.codigo?.includes(filtro)) ||
                   (document.getElementById("campoNomeDepartamento").checked && campos.nome?.includes(filtro))
               );
           }

           linha.style.display = mostrarLinha ? "" : "none";
       });
   }

   // Toggle do dropdown
   document.getElementById("dropdownMenuButtonDepartamentos").addEventListener("click", function (event) {
       event.stopPropagation(); // Impede que o clique propague para o document
       const dropdownMenu = document.getElementById("dropdownCamposDepartamentos");
       dropdownMenu.classList.toggle("show");
   });

   // Impede que cliques dentro do menu fechem o dropdown
   document.getElementById("dropdownCamposDepartamentos").addEventListener("click", function (event) {
       event.stopPropagation();
   });

   // Fechar dropdown ao clicar fora
   document.addEventListener("click", function () {
       const dropdown = document.getElementById("dropdownCamposDepartamentos");
       dropdown.classList.remove("show");
   });
});














   
   
