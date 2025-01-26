    document.addEventListener('DOMContentLoaded', function () {
        const dropdown = document.getElementById('statusDropdown');
        updateDropdownColor(dropdown);
    });

function updateDropdownColor(dropdown) {

        dropdown.classList.remove('dropdown-green', 'dropdown-yellow', 'dropdown-red');

        switch (dropdown.value) {
            case 'InProcess':
                dropdown.classList.add('dropdown-green');
                break;
            case 'ToReceive':
                dropdown.classList.add('dropdown-yellow');
                break;
            case 'Received':
                dropdown.classList.add('dropdown-red');
                break;
        }
    }

