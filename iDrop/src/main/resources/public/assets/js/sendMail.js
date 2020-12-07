document.addEventListener('DOMContentLoaded', function () {
    const form = {
        name: document.getElementById("name"),
        email: document.getElementById("email"),
        subject: document.getElementById("subject"),
        content: document.getElementById("content"),
        submit: document.getElementById("sendMail")
    }


    form.submit.addEventListener('click', function () {
        emailjs.init('user_coa5QTQ6yXeVAM6W3o1SZ');

        emailjs.send("service_3xzx03g", "template_ufj2aaf", {
            subject: form.subject.value,
            name: form.name.value,
            email: form.email.value,
            message: form.content.value,
        }).then(r  => console.log(r));

        document.getElementById("modal_content").innerText = "email being sent";
        $('#myModal').modal('show');

        console.log("send email");
    })
})