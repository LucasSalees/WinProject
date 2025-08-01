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

function imprimirFormulario() {
    try {
        const getValue = (id) => {
            const el = document.getElementById(id);
            return el ? el.value || 'Não informado' : 'Não informado';
        };

		const dados = {
		    departmentId: getValue('occupationId'),
		    departmentName: getValue('occupationName'),
		    departmentManager: getValue('occupationManager'),
			departmentEmail: getValue('occupationEmail'),
			departmentTel: getValue('occupationTel')
		};

        const janelaImpressao = window.open('', '_blank', 'width=800,height=600');
        const baseUrl = window.location.origin;

        janelaImpressao.document.write(`
        <html>
        <head>
            <title>WinChurch - Relatório de Profissão</title>
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
                <h2 class="header-title">Relatório de Departamento</h2>
            </div>

            <div class="report-info">
                <div>Emitido em: ${new Date().toLocaleDateString('pt-BR')}</div>
                <div>WinChurch - Sistema de Gestão de Igrejas</div>
            </div>

			<div class="user-data">
               <h4>Informações do Departamento</h4>
               <p><strong>Código:</strong> ${dados.departmentId}</p>
               <p><strong>Nome:</strong> ${dados.departmentName}</p>
               <p><strong>Gerente:</strong> ${dados.departmentManager}</p>
			   <p><strong>Email:</strong> ${dados.departmentEmail}</p>
			   <p><strong>Telefone:</strong> ${dados.departmentTel}</p>
           </div>

            <div class="footer">
                <div class="footer-text">
                    © ${new Date().getFullYear()} WinChurch - Todos os direitos reservados.
                </div>
            </div>
        </body>
        </html>
        `);

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