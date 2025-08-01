//Submit do formulario
const form = document.getElementById('formulario');

form.addEventListener('submit', async function(event) {
    event.preventDefault();


    // Create FormData directly from form — includes files automatically
    const formData = new FormData(form);

    // Now send with fetch
    fetch('/input/director/departments/save', {
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
                redirectUrl: "/input/director/departments/list"
            });
        }
    })
    .catch(error => {
        error.json().then(err => {
            showMessageModal(err.mensagem || "Erro ao cadastrar departamento.", false);
        });
    });
});

//adiciona as mascaras
$('#DepartmentTel').mask('(00) 00000-0000');