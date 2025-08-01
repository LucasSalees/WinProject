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

	document.getElementById('btnImprimir').addEventListener('click', function(e) {
	    e.preventDefault();

	    // Obtém a tabela e linhas visíveis (seu código existente)
	    const tabela = document.getElementById('tabela');
	    const linhasVisiveis = Array.from(tabela.querySelectorAll('tbody tr')).filter(tr => 
	        tr.style.display !== 'none'
	    );

	    // Configuração de paginação (seu código existente)
	    const maxItensPorPagina = 42;
	    const paginas = [];

	    // Processamento das páginas (seu código existente)
	    if (linhasVisiveis.length <= maxItensPorPagina) {
	        // Cria uma página única
	        const novaTabela = document.createElement('table');
	        novaTabela.innerHTML = `
	            <thead>${tabela.querySelector('thead').innerHTML}</thead>
	            <tbody></tbody>
	        `;
	        const tbody = novaTabela.querySelector('tbody');

	        linhasVisiveis.forEach(linha => {
	            const linhaClonada = linha.cloneNode(true);
	            const links = linhaClonada.querySelectorAll('a');
	            links.forEach(link => {
	                const texto = document.createTextNode(link.textContent);
	                link.parentNode.replaceChild(texto, link);
	            });
	            tbody.appendChild(linhaClonada);
	        });

	        paginas.push(novaTabela);
	    } else {
	        // Divide em múltiplas páginas
	        for (let i = 0; i < linhasVisiveis.length; i += maxItensPorPagina) {
	            const novaTabela = document.createElement('table');
	            novaTabela.innerHTML = `
	                <thead>${tabela.querySelector('thead').innerHTML}</thead>
	                <tbody></tbody>
	            `;
	            const tbody = novaTabela.querySelector('tbody');

	            linhasVisiveis.slice(i, i + maxItensPorPagina).forEach(linha => {
	                const linhaClonada = linha.cloneNode(true);
	                const links = linhaClonada.querySelectorAll('a');
	                links.forEach(link => {
	                    const texto = document.createTextNode(link.textContent);
	                    link.parentNode.replaceChild(texto, link);
	                });
	                tbody.appendChild(linhaClonada);
	            });

	            paginas.push(novaTabela);
	        }
	    }

	    // Cria a janela para impressão
	    const janelaImpressao = window.open('', '_blank', 'width=800,height=600');
	    
	    // URL base para as imagens - ajuste conforme sua estrutura
	    const baseUrl = window.location.origin;
	    
	    janelaImpressao.document.write(`
			<html>
			<head>
			    <title>WinChurch - Relatório de Usuários</title>
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
			            width: 90px;
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

			        table {
			            width: 92%;
			            margin: 25px auto;
			            border-collapse: collapse;
			            font-size: 12px;
			            color: #000;
			        }

			        th {
			            background-color: #eaeaea;
			            font-weight: 600;
			            text-transform: uppercase;
			            text-align: left;
			            padding: 10px 8px;
			            border: 1px solid #ccc;
			            font-size: 11px;
			        }

			        td {
			            padding: 9px 8px;
			            border: 1px solid #ccc;
			            vertical-align: top;
			            word-break: break-word;
			        }

			        tr:nth-child(even) {
			            background-color: #f9f9f9;
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

			        .page-break {
			            page-break-after: always;
			            height: 0;
			            visibility: hidden;
			        }

			        @media print {
			            @page {
			                size: A4 portrait;
			                margin: 15mm 10mm;
			            }

			            body {
			                margin: 0;
			                padding: 0;
			            }

			            .header {
			                padding: 12px 0;
			                border-bottom: 1px solid #aaa;
			            }

			            .header-img img {
			                width: 70px;
			            }

			            .header-title {
			                font-size: 16px;
			            }

			            table {
			                font-size: 10px;
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
			                margin-top: 0;
			                padding: 0 30px;
			            }
			        }
			    </style>
			</head>
			<body>
			    ${paginas.map((tabela, index) => `
			        <div class="header">
			            <div class="header-img">
			                <img src="${baseUrl}/images/input.ico" alt="Logo">
			            </div>
			            <h2 class="header-title">Relatório de Usuários${paginas.length > 1 ? `- Página ${index + 1}` : ''}</h2>
			        </div>

			        <div class="report-info">
			            <div>Emitido em: ${new Date().toLocaleDateString('pt-BR')}</div>
			            <div>WinChurch - Sistema de Gestão de Igrejas</div>
			        </div>

			        <div class="content">
			            ${tabela.outerHTML}
			        </div>

			        <div class="footer">
			            <div class="footer-text">
			                © ${new Date().getFullYear()} WinChurch - Todos os direitos reservados.
			            </div>
			        </div>

			        ${index < paginas.length - 1 ? '<div class="page-break"></div>' : ''}
			    `).join('')}
			</body>
			</html>
	    `);
	    janelaImpressao.document.close();
	    janelaImpressao.focus();
	    
	    // Adiciona um pequeno delay para garantir que a imagem seja carregada
	    setTimeout(() => {
	        janelaImpressao.print();
	        // janelaImpressao.close(); // Opcional: fechar após impressão
	    }, 500);
	});