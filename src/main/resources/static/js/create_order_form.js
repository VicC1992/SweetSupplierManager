document.addEventListener("DOMContentLoaded", function () {
        const form = document.querySelector("form");
        const nameInput = document.getElementById("name");

        form.addEventListener("submit", function (event) {
            const pattern = /^Order for: \d{2}\/\d{2}\/\d{4}$/;
            if (!pattern.test(nameInput.value)) {
                alert("Order name must follow the format: 'Order for: DD/MM/YYYY'");
                event.preventDefault();
            }
        });
    });