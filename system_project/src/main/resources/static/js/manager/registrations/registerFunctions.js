//Submit do formulario
const form = document.getElementById('formulario');

form.addEventListener('submit', async function(event) {
    event.preventDefault();


    // Create FormData directly from form — includes files automatically
    const formData = new FormData(form);

    // Now send with fetch
    fetch('/input/manager/functions/save', {
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
                redirectUrl: "/input/manager/functions/list"
            });
        }
    })
    .catch(error => {
        error.json().then(err => {
            showMessageModal(err.mensagem || "Erro ao cadastrar função.", false);
        });
    });
});

//adiciona as mascaras
$('#functionTel').mask('(00) 00000-0000');