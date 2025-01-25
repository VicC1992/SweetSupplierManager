<script>
  document.addEventListener("DOMContentLoaded", function () {

    const dropdowns = document.querySelectorAll('select[name="productStatus"]');

    dropdowns.forEach(dropdown => {

      const options = dropdown.querySelectorAll('option');

      options.forEach(option => {

        const status = option.getAttribute('data-status');

        if (status == "Activ") {
          option.classList.add('option-active');
        } else if (status == "Inactiv") {
          option.classList.add('option-inactive');
        } else if (status == "Test") {
          option.classList.add('option-pending');
        }
      });
    });
  });
</script>
