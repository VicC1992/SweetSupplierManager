document.addEventListener('DOMContentLoaded', function () {
    const dropdowns = document.querySelectorAll('.statusDropdown');
    dropdowns.forEach(updateDropdownColor);

    dropdowns.forEach(dropdown => {
        dropdown.addEventListener('change', function() {
            updateDropdownColor(dropdown);
        });
    });
});

function updateDropdownColor(dropdown) {

        dropdown.classList.remove('dropdown-green', 'dropdown-yellow', 'dropdown-red');

        switch (dropdown.value) {
            case 'Activ':
                dropdown.classList.add('dropdown-green');
                break;
            case 'Test':
                dropdown.classList.add('dropdown-yellow');
                break;
            case 'Inactiv':
                dropdown.classList.add('dropdown-red');
                break;
        }
    }