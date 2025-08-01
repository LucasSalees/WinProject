document.addEventListener('DOMContentLoaded', function () {
    const emailInput = document.getElementById('email');
    if (emailInput) {
        emailInput.focus();
    }
	
    const inputs = document.querySelectorAll('input[required]');
    inputs.forEach(input => {
        input.addEventListener('blur', function () {
            this.style.borderColor = this.value.trim() === '' ? '#ff0000' : '';
        });
    });

    const loginButton = document.querySelector('.login-button');
    if (loginButton) {
        loginButton.addEventListener('mouseenter', function () {
            this.style.transform = 'translateY(-2px)';
            this.style.boxShadow = '0 4px 8px rgba(0, 61, 166, 0.3)';
        });

        loginButton.addEventListener('mouseleave', function () {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 2px 4px rgba(0, 61, 166, 0.2)';
        });
    }

	const passwordInput = document.getElementById('senha');
	if (passwordInput) {
	    const passwordContainer = passwordInput.parentNode;
	    const toggleIcon = passwordContainer.querySelector('i');

	    if (toggleIcon) {
	        toggleIcon.classList.add('toggle-password-icon');

	        toggleIcon.addEventListener('click', function () {
	            if (passwordInput.type === 'password') {
	                passwordInput.type = 'text';
	                this.classList.remove('fa-eye');
	                this.classList.add('fa-eye-slash');
	            } else {
	                passwordInput.type = 'password';
	                this.classList.remove('fa-eye-slash');
	                this.classList.add('fa-eye');
	            }
	        });
	    }
	}
});