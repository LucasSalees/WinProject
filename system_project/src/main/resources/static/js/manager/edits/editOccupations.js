//Submit do formulario
const form = document.getElementById('formulario');

//adiciona as mascaras
$('#occupationTel').mask('(00) 00000-0000');

form.addEventListener('submit', async function(event) {
    event.preventDefault();
    
    // Envia via fetch (modal)
    const formData = new FormData(form);
    fetch('/input/manager/occupations/edit', {
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
	            redirectUrl: "/input/manager/occupations/list"
	        });
	    }
	})
	.catch(error => {
		if (error.json) { // Verifica se a resposta tem um corpo JSON
	    error.json().then(err => {
	        showMessageModal(err.mensagem || "Erro ao editar profissão.", false);
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
                    redirectUrl: "/input/manager/occupations/list"
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