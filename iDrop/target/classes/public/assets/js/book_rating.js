userId = sessionStorage.getItem("userId");
isRated = sessionStorage.getItem("isRated");

document.addEventListener('DOMContentLoaded', function () {
    if (isRated === "true") {
        document.getElementById("rating-button").innerText = "Rerating";
    }

    document.getElementById('rating-button').addEventListener('click', function () {
        if (userId) {
            document.querySelector('.bg-modal').style.display = 'flex';
        } else {
            document.getElementById("modal_content").innerText = "Please login to rate!";
            $('#myModal').modal('show');
        }
    });

    document.querySelector('.rating_close').addEventListener('click', function () {
        document.querySelector('.bg-modal').style.display = 'none';
    });
});