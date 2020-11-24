let userId = sessionStorage.getItem("userId");
let isRated = sessionStorage.getItem("isRated");
document.addEventListener('DOMContentLoaded', function () {
    console.log(isRated);
    if (isRated === "true") {
        document.getElementById("rating-button").innerText = "Rerating";
    }
    document.getElementById('rating-button').addEventListener('click', function () {
        console.log("rating userId:"+userId);
        if (userId) {
            document.querySelector('.bg-modal').style.display = 'flex';
        } else {
            setTimeout(function () {
                alert("Please login to rate!");
                }, 10);
        }
    });

    document.querySelector('.close').addEventListener('click', function () {
        document.querySelector('.bg-modal').style.display = 'none';
    });
});